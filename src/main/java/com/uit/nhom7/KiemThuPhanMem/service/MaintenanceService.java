package com.uit.nhom7.KiemThuPhanMem.service;

import java.time.Instant;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqEnableMaintenanceDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResMaintenanceDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.SystemConfig;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.SystemConfigRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.service.DatabaseBackupService.BackupResult;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class MaintenanceService {
    public static final String SYSTEM_STATUS_KEY = "System_Status";
    public static final String ACTIVE_STATUS = "ACTIVE";
    public static final String MAINTENANCE_STATUS = "MAINTENANCE";

    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String BUSINESS_ADMIN_ROLE = "BUSINESS_ADMIN";
    private static final String MSG113 = "Maintenance mode enabled and database backup completed successfully";
    private static final String MSG114 = "Emergency rollback: backup failed, maintenance mode was not enabled";

    private final AuditLogService auditLogService;
    private final DatabaseBackupService databaseBackupService;
    private final SystemConfigRepository systemConfigRepository;
    private final UserRepository userRepository;

    public MaintenanceService(
            AuditLogService auditLogService,
            DatabaseBackupService databaseBackupService,
            SystemConfigRepository systemConfigRepository,
            UserRepository userRepository) {
        this.auditLogService = auditLogService;
        this.databaseBackupService = databaseBackupService;
        this.systemConfigRepository = systemConfigRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ResMaintenanceDTO getMaintenanceStatus() {
        getCurrentBusinessAdmin();
        SystemConfig config = getSystemStatusConfig();
        return ResMaintenanceDTO.builder()
                .status(config.getValue())
                .updatedAt(config.getUpdatedAt() == null ? config.getCreatedAt() : config.getUpdatedAt())
                .build();
    }

    @Transactional
    public ResMaintenanceDTO enableMaintenance(ReqEnableMaintenanceDTO request) {
        getCurrentBusinessAdmin();
        if (request == null || !request.isConfirmed()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Maintenance confirmation is required");
        }

        String oldStatus = getSystemStatus();
        try {
            BackupResult backupResult = databaseBackupService.backupToLocalStorage();
            SystemConfig statusConfig = getSystemStatusConfig();
            statusConfig.setValue(MAINTENANCE_STATUS);
            SystemConfig savedConfig = systemConfigRepository.save(statusConfig);
            auditLogService.record(
                    "UPDATE",
                    "SYSTEM_STATUS",
                    SYSTEM_STATUS_KEY,
                    oldStatus,
                    MAINTENANCE_STATUS,
                    "Maintenance enabled. Backup=%s, checksum=%s".formatted(
                            backupResult.fileName(), backupResult.checksumSha256()));
            return ResMaintenanceDTO.builder()
                    .status(savedConfig.getValue())
                    .backupFileName(backupResult.fileName())
                    .backupSizeBytes(backupResult.sizeBytes())
                    .checksumSha256(backupResult.checksumSha256())
                    .updatedAt(savedConfig.getUpdatedAt() == null ? Instant.now() : savedConfig.getUpdatedAt())
                    .message(MSG113)
                    .build();
        } catch (RuntimeException ex) {
            rollbackMaintenanceStatus(oldStatus);
            auditLogService.record(
                    "UPDATE",
                    "SYSTEM_STATUS",
                    SYSTEM_STATUS_KEY,
                    oldStatus,
                    ACTIVE_STATUS,
                    MSG114 + ": " + ex.getMessage());
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, MSG114);
        }
    }

    @Transactional(readOnly = true)
    public boolean isMaintenanceMode() {
        return MAINTENANCE_STATUS.equalsIgnoreCase(getSystemStatus());
    }

    private void rollbackMaintenanceStatus(String oldStatus) {
        SystemConfig statusConfig = getSystemStatusConfig();
        statusConfig.setValue(oldStatus == null || oldStatus.isBlank() ? ACTIVE_STATUS : oldStatus);
        systemConfigRepository.save(statusConfig);
    }

    private String getSystemStatus() {
        return getSystemStatusConfig().getValue();
    }

    private SystemConfig getSystemStatusConfig() {
        return systemConfigRepository.findById(SYSTEM_STATUS_KEY)
                .orElseGet(() -> systemConfigRepository.save(SystemConfig.builder()
                        .key(SYSTEM_STATUS_KEY)
                        .value(ACTIVE_STATUS)
                        .build()));
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
}
