package com.uit.nhom7.KiemThuPhanMem.service;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateStaffDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResRoleDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResUserDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.RoleRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class StaffManagementService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String LOCKED_ACCOUNT_STATUS = "LOCKED";
    private static final String BUSINESS_ADMIN_ROLE = "BUSINESS_ADMIN";
    private static final String STAFF_ROLE = "STAFF";
    private static final String MSG1 = "Required field is missing";
    private static final String MSG2 = "Email format is invalid";
    private static final String MSG93 = "Staff account created successfully";
    private static final String MSG94 = "Account already exists";
    private static final String MSG97 = "Staff account locked successfully";
    private static final String PASSWORD_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#$%";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public StaffManagementService(
            EmailService emailService,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            UserRepository userRepository) {
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResUserDTO createStaff(ReqCreateStaffDTO request) {
        getCurrentBusinessAdmin();
        validateCreateStaffRequest(request);

        String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BusinessException(HttpStatus.CONFLICT, MSG94);
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "Role not found"));
        if (role.getName() == null || !STAFF_ROLE.equals(role.getName().trim().toUpperCase(Locale.ROOT))) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Only STAFF role can be assigned to staff accounts");
        }

        String tempPassword = generateRandomPassword();
        User staff = User.builder()
                .email(normalizedEmail)
                .userFullName(request.getFullName().trim())
                .password(passwordEncoder.encode(tempPassword))
                .accountStatus(ACTIVE_ACCOUNT_STATUS)
                .failedLoginAttempts(0)
                .role(role)
                .build();

        User savedStaff = userRepository.save(staff);
        emailService.sendStaffLoginDetails(savedStaff.getEmail(), savedStaff.getUserFullName(), tempPassword);

        return toUserDTO(savedStaff, MSG93);
    }

    @Transactional
    public ResUserDTO lockStaff(UUID staffId) {
        getCurrentBusinessAdmin();
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Staff account not found"));
        if (staff.getRole() == null
                || staff.getRole().getName() == null
                || !STAFF_ROLE.equals(staff.getRole().getName().trim().toUpperCase(Locale.ROOT))) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Only staff accounts can be locked by this action");
        }

        staff.setAccountStatus(LOCKED_ACCOUNT_STATUS);
        staff.setRefreshToken(null);
        staff.setLockedUntil(null);
        return toUserDTO(userRepository.save(staff), MSG97);
    }

    @Transactional(readOnly = true)
    public ResultPaginationDTO getStaff(int pageNumber, int pageSize) {
        getCurrentBusinessAdmin();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                Math.max(pageNumber - 1, 0),
                pageSize <= 0 ? 20 : pageSize,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        org.springframework.data.domain.Page<User> staffPage = userRepository.findByRoleNameIgnoreCase(STAFF_ROLE, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(staffPage.getTotalPages());
        meta.setTotalItems(staffPage.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(staffPage.getContent().stream()
                .map(staff -> toUserDTO(staff, null))
                .toList());
        return result;
    }

    private void validateCreateStaffRequest(ReqCreateStaffDTO request) {
        if (request == null
                || request.getEmail() == null || request.getEmail().isBlank()
                || request.getFullName() == null || request.getFullName().isBlank()
                || request.getRoleId() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG1);
        }
        if (!EMAIL_PATTERN.matcher(request.getEmail().trim()).matches()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG2);
        }
    }

    private User getCurrentBusinessAdmin() {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "You must login first"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "User session is invalid"));
        if (user.getAccountStatus() == null
                || !ACTIVE_ACCOUNT_STATUS.equals(user.getAccountStatus().trim().toUpperCase(Locale.ROOT))) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "User account is not active");
        }
        String roleName = user.getRole() == null || user.getRole().getName() == null
                ? ""
                : user.getRole().getName().trim().toUpperCase(Locale.ROOT);
        if (!BUSINESS_ADMIN_ROLE.equals(roleName)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Only business admin can perform this action");
        }
        return user;
    }

    private String generateRandomPassword() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            password.append(PASSWORD_CHARS.charAt(SECURE_RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return password.toString();
    }

    private ResUserDTO toUserDTO(User user, String message) {
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
                .accountStatus(user.getAccountStatus())
                .role(roleDTO)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .message(message)
                .build();
    }
}
