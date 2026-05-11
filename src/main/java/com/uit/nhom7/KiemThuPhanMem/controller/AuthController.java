package com.uit.nhom7.KiemThuPhanMem.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqChangePasswordDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqForgotPasswordDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqLoginDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqRegisterDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqResetPasswordDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateProfileDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResAuthActionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResLoginDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResUserDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.service.UserService;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${se113.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    public AuthController(SecurityUtil securityUtil, UserService userService) {
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/register")
    @ApiMessage("Dang ky tai khoan")
    public ResponseEntity<ResAuthActionDTO> register(@Valid @RequestBody ReqRegisterDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.register(request));
    }

    @GetMapping("/verify")
    @ApiMessage("Xac thuc dang ky")
    public ResponseEntity<ResAuthActionDTO> verifyRegistration(@RequestParam("token") String token) {
        return ResponseEntity.ok(this.userService.verifyRegistration(token));
    }

    @PostMapping("/login")
    @ApiMessage("Dang nhap")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        System.out.println(">>>AUTH MODULE: Login attempt for email: " + loginDTO.getEmail());

        User currentUserDB = this.userService.handleFindByEmail(loginDTO.getEmail());
        if (currentUserDB == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (this.userService.isLoginTemporarilyLocked(currentUserDB)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (!this.userService.matchesPassword(loginDTO.getPassword(), currentUserDB)) {
            int failedAttempts = this.userService.increaseFailedLoginAttempts(currentUserDB);
            if (failedAttempts >= MAX_FAILED_LOGIN_ATTEMPTS) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!this.userService.isUserActive(currentUserDB)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        this.userService.resetFailedLoginAttempts(currentUserDB);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUserDB.getEmail(), null, java.util.List.of()));

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUserDB.getId(),
                currentUserDB.getEmail(),
                currentUserDB.getUserFullName());
        resLoginDTO.setUser(userLogin);

        if (currentUserDB.getRole() != null) {
            resLoginDTO.setRole(new ResLoginDTO.Role(
                    currentUserDB.getRole().getId(),
                    currentUserDB.getRole().getName()));
        }

        String accessToken = securityUtil.createAccessToken(loginDTO.getEmail(), resLoginDTO);
        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getEmail(), resLoginDTO);

        resLoginDTO.setAccessToken(accessToken);
        this.userService.updateUserRefreshToken(refreshToken, loginDTO.getEmail());

        ResponseCookie resCookies = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        System.out.println(">>>AUTH MODULE: Login successful for email: " + loginDTO.getEmail());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(resLoginDTO);
    }

    @PostMapping("/forgot-password")
    @ApiMessage("Quen mat khau")
    public ResponseEntity<ResAuthActionDTO> forgotPassword(@Valid @RequestBody ReqForgotPasswordDTO request) {
        return ResponseEntity.ok(this.userService.forgotPassword(request));
    }

    @GetMapping("/reset-password/validate")
    @ApiMessage("Kiem tra token dat lai mat khau")
    public ResponseEntity<ResAuthActionDTO> validateResetToken(@RequestParam("token") String token) {
        return ResponseEntity.ok(this.userService.validateResetToken(token));
    }

    @PostMapping("/reset-password")
    @ApiMessage("Dat lai mat khau")
    public ResponseEntity<ResAuthActionDTO> resetPassword(@Valid @RequestBody ReqResetPasswordDTO request) {
        return ResponseEntity.ok(this.userService.resetPassword(request));
    }

    @PutMapping("/profile")
    @ApiMessage("Cap nhat thong tin ca nhan")
    public ResponseEntity<ResUserDTO> updateProfile(@Valid @RequestBody ReqUpdateProfileDTO request) {
        return ResponseEntity.ok(this.userService.updateCurrentUserProfile(request));
    }

    @PutMapping("/change-password")
    @ApiMessage("Doi mat khau")
    public ResponseEntity<ResAuthActionDTO> changePassword(@Valid @RequestBody ReqChangePasswordDTO request) {
        return ResponseEntity.ok(this.userService.changeCurrentUserPassword(request));
    }

    @GetMapping("/account")
    @ApiMessage("Lay thong tin tai khoan")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        System.out.println(">>>AUTH MODULE: Fetching account information");

        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUserDB = this.userService.handleFindByEmail(email);
        if (currentUserDB == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!this.userService.isUserActive(currentUserDB)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUserDB.getId(),
                currentUserDB.getEmail(),
                currentUserDB.getUserFullName());

        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        userGetAccount.setUser(userLogin);

        if (currentUserDB.getRole() != null) {
            userGetAccount.setRole(new ResLoginDTO.Role(
                    currentUserDB.getRole().getId(),
                    currentUserDB.getRole().getName()));
        }
        return ResponseEntity.ok(userGetAccount);
    }

    @GetMapping("/refresh")
    @ApiMessage("Lay token moi bang refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "No cookies") String refreshToken)
            throws BadRequestException {
        System.out.println(">>>AUTH MODULE: Refresh token attempt");
        if (refreshToken.equals("No cookies")) {
            System.out.println(">>>AUTH MODULE: No refresh token provided");
            throw new BadRequestException("No refresh token provided");
        }
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();

        User currentUser = this.userService.handleFindByEmailAndRefreshToken(email, refreshToken);
        if (currentUser == null) {
            System.out.println(">>>AUTH MODULE: Invalid refresh token for email: " + email);
            throw new BadRequestException("Invalid refresh token");
        }
        if (!this.userService.isUserActive(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getUserFullName());

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        resLoginDTO.setUser(userLogin);

        if (currentUser.getRole() != null) {
            resLoginDTO.setRole(new ResLoginDTO.Role(
                    currentUser.getRole().getId(),
                    currentUser.getRole().getName()));
        }

        String accessToken = securityUtil.createAccessToken(email, resLoginDTO);
        String newRefreshToken = this.securityUtil.createRefreshToken(currentUser.getEmail(), resLoginDTO);

        resLoginDTO.setAccessToken(accessToken);
        this.userService.updateUserRefreshToken(newRefreshToken, currentUser.getEmail());

        ResponseCookie resCookies = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        System.out.println(">>>AUTH MODULE: Refresh token successful for email: " + email);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(resLoginDTO);
    }

    @PostMapping("/logout")
    @ApiMessage("Dang xuat")
    public ResponseEntity<Void> logout() throws BadRequestException {
        System.out.println(">>>AUTH MODULE: Logout attempt");

        String email = SecurityUtil.getCurrentUserLogin().orElse(null);
        if (email == null) {
            throw new BadRequestException("No user logged in");
        }

        this.userService.handleLogOutUser(email);

        ResponseCookie deleteCookies = ResponseCookie.from("refresh_token", null)
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();

        System.out.println(">>>AUTH MODULE: Logout successful for email: " + email);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookies.toString())
                .build();
    }
}
