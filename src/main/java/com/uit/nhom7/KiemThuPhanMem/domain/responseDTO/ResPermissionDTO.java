package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Permission information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResPermissionDTO {

    private Long id;

    private String name;

    private String apiPath;

    private String method;

    private String module;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}