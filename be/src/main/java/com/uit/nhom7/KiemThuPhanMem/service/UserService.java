package com.uit.nhom7.KiemThuPhanMem.service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqChangePasswordDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqForgotPasswordDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqLoginDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqRegisterDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqResetPasswordDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateProfileDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateUserDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResAuthActionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResLoginDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResRoleDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResUserDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.RoleRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;
import com.uit.nhom7.KiemThuPhanMem.util.error.IdInvalidException;

@Service
@Validated
public class UserService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String PENDING_ACCOUNT_STATUS = "PENDING";
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;
    private static final int LOGIN_LOCK_MINUTES = 15;
    private static final int RESET_TOKEN_VALID_MINUTES = 30;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecurityUtil securityUtil;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            SecurityUtil securityUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.securityUtil = securityUtil;
    }

    /**
     * Convert User entity to ResUserDTO
     */
    private ResUserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }

        ResRoleDTO roleDTO = null;
        if (user.getRole() != null) {
            roleDTO = ResRoleDTO.builder()
                    .id(user.getRole().getId())
                    .name(user.getRole().getName())
                    .description(user.getRole().getDescription())
                    .active(user.getRole().isActive())
                    .build();
        }

        return ResUserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getUserFullName())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .dateOfBirth(user.getDateOfBirth())
                .accountStatus(user.getAccountStatus())
                .role(roleDTO)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .build();
    }

    /**
     * Convert User entity to ResLoginDTO
     */
    private ResLoginDTO convertToLoginDTO(User user) {
        if (user == null) {
            return null;
        }

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                user.getId(),
                user.getEmail(),
                user.getUserFullName(),
                user.getPhoneNumber(),
                user.getAvatarUrl(),
                user.getDateOfBirth());

        ResLoginDTO.Role roleDTO = null;
        if (user.getRole() != null) {
            roleDTO = new ResLoginDTO.Role(user.getRole().getId(), user.getRole().getName());
        }

        return ResLoginDTO.builder()
                .user(userLogin)
                .role(roleDTO)
                .build();
    }

    /**
     * Find user by email
     */
    @Transactional(readOnly = true)
    public User handleFindByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public static class AuthResult {
        private final ResLoginDTO resLoginDTO;
        private final String refreshToken;

        public AuthResult(ResLoginDTO resLoginDTO, String refreshToken) {
            this.resLoginDTO = resLoginDTO;
            this.refreshToken = refreshToken;
        }

        public ResLoginDTO getResLoginDTO() {
            return resLoginDTO;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }

    @Transactional
    public AuthResult handleLogin(ReqLoginDTO loginDTO) {
        User currentUserDB = handleFindByEmail(loginDTO.getEmail());
        if (currentUserDB == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Email or password incorrect");
        }
        if (isLoginTemporarilyLocked(currentUserDB)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Account is temporarily locked");
        }
        if (!matchesPassword(loginDTO.getPassword(), currentUserDB)) {
            int failedAttempts = increaseFailedLoginAttempts(currentUserDB);
            if (failedAttempts >= MAX_FAILED_LOGIN_ATTEMPTS) {
                throw new BusinessException(HttpStatus.FORBIDDEN, "Account locked due to too many failed attempts");
            }
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Email or password incorrect");
        }
        if (!isUserActive(currentUserDB)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Account is not active");
        }

        resetFailedLoginAttempts(currentUserDB);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUserDB.getEmail(), null, java.util.List.of()));

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUserDB.getId(),
                currentUserDB.getEmail(),
                currentUserDB.getUserFullName(),
                currentUserDB.getPhoneNumber(),
                currentUserDB.getAvatarUrl(),
                currentUserDB.getDateOfBirth());
        resLoginDTO.setUser(userLogin);

        if (currentUserDB.getRole() != null) {
            resLoginDTO.setRole(new ResLoginDTO.Role(
                    currentUserDB.getRole().getId(),
                    currentUserDB.getRole().getName()));
        }

        String accessToken = securityUtil.createAccessToken(loginDTO.getEmail(), resLoginDTO);
        String refreshToken = securityUtil.createRefreshToken(loginDTO.getEmail(), resLoginDTO);

        resLoginDTO.setAccessToken(accessToken);
        updateUserRefreshToken(refreshToken, loginDTO.getEmail());

        return new AuthResult(resLoginDTO, refreshToken);
    }

    @Transactional
    public ResAuthActionDTO register(ReqRegisterDTO request) {
        validatePasswordConfirmation(request.getPassword(), request.getConfirmPassword());

        String normalizedEmail = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Account already exists");
        }

        Role customerRole = roleRepository.findByName("CUSTOMER");
        String verificationToken = UUID.randomUUID().toString();
        User user = User.builder()
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .accountStatus(PENDING_ACCOUNT_STATUS)
                .failedLoginAttempts(0)
                .verificationToken(verificationToken)
                .role(customerRole)
                .build();

        userRepository.save(user);
        emailService.sendRegistrationVerification(normalizedEmail, verificationToken);

        return ResAuthActionDTO.builder()
                .message("Registration created. Please verify your email.")
                .token(verificationToken)
                .build();
    }

    @Transactional
    public ResAuthActionDTO verifyRegistration(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Invalid verification link"));

        user.setAccountStatus(ACTIVE_ACCOUNT_STATUS);
        user.setVerificationToken(null);
        userRepository.save(user);

        return ResAuthActionDTO.builder()
                .message("Registration verified successfully")
                .build();
    }

    @Transactional
    public ResAuthActionDTO forgotPassword(ReqForgotPasswordDTO request) {
        User user = userRepository.findByEmail(normalizeEmail(request.getEmail()))
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Email does not exist"));

        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordTokenExpiresAt(Instant.now().plusSeconds(RESET_TOKEN_VALID_MINUTES * 60L));
        userRepository.save(user);
        emailService.sendPasswordReset(user.getEmail(), resetToken);

        return ResAuthActionDTO.builder()
                .message("Password reset email sent")
                .token(resetToken)
                .build();
    }

    @Transactional(readOnly = true)
    public ResAuthActionDTO validateResetToken(String token) {
        getValidResetTokenUser(token);
        return ResAuthActionDTO.builder()
                .message("Reset token is valid")
                .build();
    }

    @Transactional
    public ResAuthActionDTO resetPassword(ReqResetPasswordDTO request) {
        validatePasswordConfirmation(request.getNewPassword(), request.getConfirmPassword());

        User user = getValidResetTokenUser(request.getToken());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiresAt(null);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);

        return ResAuthActionDTO.builder()
                .message("Password reset successfully")
                .build();
    }

    @Transactional
    public ResUserDTO updateCurrentUserProfile(ReqUpdateProfileDTO request) {
        User user = getCurrentActiveUser();
        user.setUserFullName(request.getFullName().trim());
        user.setPhoneNumber(request.getPhoneNumber().trim());
        return convertToDTO(userRepository.save(user));
    }

    @Transactional
    public ResAuthActionDTO changeCurrentUserPassword(ReqChangePasswordDTO request) {
        validatePasswordConfirmation(request.getNewPassword(), request.getConfirmPassword());

        User user = getCurrentActiveUser();
        if (!matchesPassword(request.getCurrentPassword(), user)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResAuthActionDTO.builder()
                .message("Password changed successfully")
                .build();
    }

    /**
     * Find user by email and refresh token
     */
    @Transactional(readOnly = true)
    public User handleFindByEmailAndRefreshToken(String email, String refreshToken) {
        return userRepository.findByEmailAndRefreshToken(email, refreshToken).orElse(null);
    }

    /**
     * Authentication and refresh flows should only proceed for ACTIVE users.
     */
    public boolean isUserActive(User user) {
        return user != null
                && user.getAccountStatus() != null
                && ACTIVE_ACCOUNT_STATUS.equals(user.getAccountStatus().trim().toUpperCase(Locale.ROOT));
    }

    public boolean isLoginTemporarilyLocked(User user) {
        if (user == null || user.getLockedUntil() == null) {
            return false;
        }
        if (user.getLockedUntil().isAfter(Instant.now())) {
            return true;
        }
        user.setLockedUntil(null);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
        return false;
    }

    public boolean matchesPassword(String rawPassword, User user) {
        return user != null
                && rawPassword != null
                && user.getPassword() != null
                && passwordEncoder.matches(rawPassword, user.getPassword());
    }

    @Transactional
    public int increaseFailedLoginAttempts(User user) {
        Objects.requireNonNull(user, "user must not be null");
        user.setFailedLoginAttempts(getFailedLoginAttempts(user) + 1);
        user.setLastFailedAt(Instant.now());
        if (user.getFailedLoginAttempts() >= MAX_FAILED_LOGIN_ATTEMPTS) {
            user.setLockedUntil(Instant.now().plusSeconds(LOGIN_LOCK_MINUTES * 60L));
        }
        userRepository.save(user);
        return user.getFailedLoginAttempts();
    }

    @Transactional
    public void resetFailedLoginAttempts(User user) {
        Objects.requireNonNull(user, "user must not be null");
        if (getFailedLoginAttempts(user) == 0 && user.getLockedUntil() == null) {
            return;
        }

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }

    private int getFailedLoginAttempts(User user) {
        return user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts();
    }

    /**
     * Get user by ID and convert to DTO
     */
    @Transactional(readOnly = true)
    public ResUserDTO handleFetchUserById(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        return convertToDTO(user);
    }

    /**
     * Get user entity by ID (internal)
     */
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Get all users with filter and pagination
     */
    @Transactional(readOnly = true)
    public ResultPaginationDTO handleGetAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageUsers = userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(pageUsers.getTotalPages());
        meta.setTotalItems(pageUsers.getTotalElements());

        rs.setMeta(meta);

        List<ResUserDTO> userDTOs = pageUsers.getContent().stream()
                .map(this::convertToDTO)
                .toList();
        rs.setResult(userDTOs);

        return rs;
    }

    /**
     * Update user refresh token
     */
    @Transactional
    public void updateUserRefreshToken(String refreshToken, String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        }
    }

    /**
     * Handle user logout
     */
    @Transactional
    public void handleLogOutUser(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            user.setRefreshToken(null);
            userRepository.save(user);
        }
    }

    /**
     * Delete user by ID
     */
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("User with id " + id + " does not exist"));
        userRepository.deleteById(id);
    }

    @Transactional
    public ResUserDTO updateUser(UUID id, ReqUpdateUserDTO request) {
        getCurrentActiveBusinessAdmin();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "User not found"));

        if (request.getFullName() != null) {
            user.setUserFullName(request.getFullName().trim());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber().trim());
        }
        if (request.getAccountStatus() != null) {
            user.setAccountStatus(request.getAccountStatus().trim());
        }
        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Role not found"));
            user.setRole(role);
        } else if (request.getRoleName() != null && !request.getRoleName().isBlank()) {
            String normalizedRoleName = request.getRoleName().trim().toUpperCase(Locale.ROOT);
            Role role = roleRepository.findByName(normalizedRoleName);
            if (role == null) {
                role = roleRepository.findByName(request.getRoleName().trim());
            }
            if (role != null) {
                user.setRole(role);
            }
        }

        return convertToDTO(userRepository.save(user));
    }

    private User getCurrentActiveBusinessAdmin() {
        User user = getCurrentActiveUser();
        String roleName = user.getRole() == null || user.getRole().getName() == null
                ? ""
                : user.getRole().getName().trim().toUpperCase(Locale.ROOT);
        if (!"BUSINESS_ADMIN".equals(roleName)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Only business admin can perform this action");
        }
        return user;
    }

    private User getCurrentActiveUser() {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "You must login first"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "User session is invalid"));
        if (!isUserActive(user)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "User account is not active");
        }
        return user;
    }

    private User getValidResetTokenUser(String token) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Invalid or expired reset link"));
        if (user.getResetPasswordTokenExpiresAt() == null
                || user.getResetPasswordTokenExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Invalid or expired reset link");
        }
        return user;
    }

    private void validatePasswordConfirmation(String password, String confirmPassword) {
        if (!Objects.equals(password, confirmPassword)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Password confirmation does not match");
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
