package com.uit.nhom7.KiemThuPhanMem.controller;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqLoginDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResLoginDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.service.UserService;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${se113.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil, UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    @ApiMessage("Đăng nhập")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        System.out.println(">>>AUTH MODULE: Login attempt for email: " + loginDTO.getEmail());
        
        // Authenticate user
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getPassword());

        // Xác thực người dùng => cán việt hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Nap thông tin (nếu xử lý thành công) vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // securityUtil.createAccessToken(authentication);
        ResLoginDTO resLoginDTO = new ResLoginDTO();

        User currentUserDB = this.userService.handleFindByEmail(loginDTO.getEmail());
        if (currentUserDB == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUserDB.getId(),
                currentUserDB.getEmail(),
                currentUserDB.getName());
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

        // set cookies
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

    @GetMapping("/account")
    @ApiMessage("Lấy thông tin tài khoản")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        System.out.println(">>>AUTH MODULE: Fetching account information");

        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUserDB = this.userService.handleFindByEmail(email);
        if (currentUserDB == null) {
            // return;
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUserDB.getId(),
                currentUserDB.getEmail(),
                currentUserDB.getName());

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
    @ApiMessage("Lấy token mới bằng refresh token")
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

        // Find user by email and refresh token
        User currentUser = this.userService.handleFindByEmailAndRefreshToken(email, refreshToken);
        if (currentUser == null) {
            System.out.println(">>>AUTH MODULE: Invalid refresh token for email: " + email);
            throw new BadRequestException("Invalid refresh token");
        }

        // Build response DTO
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getName());

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        resLoginDTO.setUser(userLogin);

        if (currentUser.getRole() != null) {
            resLoginDTO.setRole(new ResLoginDTO.Role(
                    currentUser.getRole().getId(),
                    currentUser.getRole().getName()));
        }

        // Create new tokens
        String accessToken = securityUtil.createAccessToken(email, resLoginDTO);
        String newRefreshToken = this.securityUtil.createRefreshToken(currentUser.getEmail(), resLoginDTO);

        resLoginDTO.setAccessToken(accessToken);
        this.userService.updateUserRefreshToken(newRefreshToken, currentUser.getEmail());

        // set cookies
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
    @ApiMessage("Đăng xuất")
    public ResponseEntity<Void> logout() throws BadRequestException {
        System.out.println(">>>AUTH MODULE: Logout attempt");

        String email = SecurityUtil.getCurrentUserLogin().orElse(null);
        if (email == null) {
            throw new BadRequestException("No user logged in");
        }

        this.userService.handleLogOutUser(email);

        // Clear refresh token cookie
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