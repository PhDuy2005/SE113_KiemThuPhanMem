package com.uit.nhom7.KiemThuPhanMem.service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqSyncRolePermissionsDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResPermissionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResRoleDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResRolePermissionMatrixDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Permission;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.PermissionRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.RoleRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class RolePermissionMatrixService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String BUSINESS_ADMIN_ROLE = "BUSINESS_ADMIN";
    private static final String MSG109 = "Role permissions updated successfully";
    private static final String MSG110 = "Invalid role or permission configuration";

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public RolePermissionMatrixService(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            AuditLogService auditLogService) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public ResRolePermissionMatrixDTO getMatrix() {
        getCurrentBusinessAdmin();
        return buildMatrix(null);
    }

    @Transactional
    public ResRolePermissionMatrixDTO syncRolePermissions(ReqSyncRolePermissionsDTO request) {
        getCurrentBusinessAdmin();
        if (request == null || request.getRolePermissions() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG110);
        }

        Map<Long, Set<Long>> oldValueByRole = roleRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Role::getId,
                        role -> role.getPermissions() == null
                                ? Set.of()
                                : role.getPermissions().stream()
                                        .map(Permission::getId)
                                        .collect(Collectors.toSet())));
        Set<Long> submittedRoleIds = new HashSet<>();
        for (ReqSyncRolePermissionsDTO.RolePermissionSetting setting : request.getRolePermissions()) {
            if (setting == null || setting.getRoleId() == null || !submittedRoleIds.add(setting.getRoleId())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, MSG110);
            }

            Role role = roleRepository.findById(setting.getRoleId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, MSG110));
            Set<Long> permissionIds = setting.getPermissionIds() == null
                    ? Set.of()
                    : setting.getPermissionIds();
            List<Permission> permissions = permissionRepository.findAllById(permissionIds);
            if (permissions.size() != permissionIds.size()) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, MSG110);
            }

            role.setPermissions(new HashSet<>(permissions));
            roleRepository.save(role);
        }

        refreshAuthorizationCache();
        auditLogService.record(
                "UPDATE",
                "ROLE_PERMISSION_MATRIX",
                "ROLE_PERMISSION_MATRIX",
                oldValueByRole.toString(),
                request.getRolePermissions().toString(),
                MSG109);
        return buildMatrix(MSG109);
    }

    private ResRolePermissionMatrixDTO buildMatrix(String message) {
        List<Role> roles = roleRepository.findAll();
        List<Permission> permissions = permissionRepository.findAll();

        List<ResRoleDTO> roleDTOs = roles.stream()
                .map(this::toRoleDTO)
                .toList();
        List<ResPermissionDTO> permissionDTOs = permissions.stream()
                .map(this::toPermissionDTO)
                .toList();
        List<ResRolePermissionMatrixDTO.RolePermissionSelection> selections = roles.stream()
                .map(role -> ResRolePermissionMatrixDTO.RolePermissionSelection.builder()
                        .roleId(role.getId())
                        .permissionIds(role.getPermissions() == null
                                ? Set.of()
                                : role.getPermissions().stream()
                                        .map(Permission::getId)
                                        .collect(java.util.stream.Collectors.toSet()))
                        .build())
                .toList();

        return ResRolePermissionMatrixDTO.builder()
                .roles(roleDTOs)
                .permissions(permissionDTOs)
                .rolePermissions(selections)
                .message(message)
                .build();
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

    private ResRoleDTO toRoleDTO(Role role) {
        return ResRoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .active(role.isActive())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .createdBy(role.getCreatedBy())
                .updatedBy(role.getUpdatedBy())
                .build();
    }

    private ResPermissionDTO toPermissionDTO(Permission permission) {
        return ResPermissionDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .apiPath(permission.getApiPath())
                .method(permission.getMethod())
                .module(permission.getModule())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .createdBy(permission.getCreatedBy())
                .updatedBy(permission.getUpdatedBy())
                .build();
    }

    private void refreshAuthorizationCache() {
        // JWT-based authorization has no local in-memory role-permission cache to refresh.
    }
}
