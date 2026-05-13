package com.uit.nhom7.KiemThuPhanMem.service;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResAuditLogDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.AuditLog;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.AuditLogRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

import jakarta.persistence.criteria.Predicate;

@Service
public class AuditLogService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String BUSINESS_ADMIN_ROLE = "BUSINESS_ADMIN";
    private static final String MSG112 = "No audit logs found";

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ResultPaginationDTO getAuditLogs(
            UUID userId,
            String actionType,
            Instant startTimestamp,
            Instant endTimestamp,
            int pageNumber,
            int pageSize) {
        getCurrentBusinessAdmin();
        if (startTimestamp != null && endTimestamp != null && endTimestamp.isBefore(startTimestamp)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "End timestamp must not be before start timestamp");
        }

        Pageable pageable = PageRequest.of(
                Math.max(pageNumber - 1, 0),
                pageSize <= 0 ? 10 : pageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> logs = auditLogRepository.findAll(
                buildSpecification(userId, actionType, startTimestamp, endTimestamp),
                pageable);

        ResultPaginationDTO.Meta meta = ResultPaginationDTO.Meta.builder()
                .page(pageable.getPageNumber() + 1)
                .pageSize(pageable.getPageSize())
                .totalPages(logs.getTotalPages())
                .totalItems(logs.getTotalElements())
                .build();

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(logs.getContent().stream()
                .map(this::toDTO)
                .toList());
        result.setMessage(logs.isEmpty() ? MSG112 : null);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(
            String actionType,
            String targetType,
            String targetId,
            String oldValue,
            String newValue,
            String detail) {
        String actorEmail = SecurityUtil.getCurrentUserLogin().orElse("system");
        User actor = userRepository.findByEmail(actorEmail).orElse(null);
        auditLogRepository.save(AuditLog.builder()
                .actor(actor)
                .actorEmail(actorEmail)
                .actionType(normalizeActionType(actionType))
                .targetType(cleanNullable(targetType))
                .targetId(cleanNullable(targetId))
                .oldValue(cleanNullable(oldValue))
                .newValue(cleanNullable(newValue))
                .detail(cleanNullable(detail))
                .build());
    }

    private Specification<AuditLog> buildSpecification(
            UUID userId,
            String actionType,
            Instant startTimestamp,
            Instant endTimestamp) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (userId != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("actor").get("id"), userId));
            }
            if (actionType != null && !actionType.isBlank()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("actionType"), normalizeActionType(actionType)));
            }
            if (startTimestamp != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startTimestamp));
            }
            if (endTimestamp != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endTimestamp));
            }
            return predicate;
        };
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

    private ResAuditLogDTO toDTO(AuditLog auditLog) {
        return ResAuditLogDTO.builder()
                .id(auditLog.getId())
                .timestamp(auditLog.getCreatedAt())
                .actorUserId(auditLog.getActor() == null ? null : auditLog.getActor().getId())
                .actorEmail(auditLog.getActorEmail())
                .actionType(auditLog.getActionType())
                .targetType(auditLog.getTargetType())
                .targetId(auditLog.getTargetId())
                .oldValue(auditLog.getOldValue())
                .newValue(auditLog.getNewValue())
                .detail(auditLog.getDetail())
                .build();
    }

    private String normalizeActionType(String actionType) {
        if (actionType == null || actionType.isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Action type is required");
        }
        return actionType.trim().toUpperCase(Locale.ROOT);
    }

    private String cleanNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
