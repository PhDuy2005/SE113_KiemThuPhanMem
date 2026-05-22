package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResAuditLogDTO {
    private UUID id;
    private Instant timestamp;
    private UUID actorUserId;
    private String actorEmail;
    private String actionType;
    private String targetType;
    private String targetId;
    private String oldValue;
    private String newValue;
    private String detail;
}
