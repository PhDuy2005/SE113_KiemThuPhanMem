package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResMaintenanceDTO {
    private String status;
    private String backupFileName;
    private Long backupSizeBytes;
    private String checksumSha256;
    private Instant updatedAt;
    private String message;
}
