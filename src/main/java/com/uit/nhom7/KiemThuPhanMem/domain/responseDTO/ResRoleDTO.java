package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Role information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResRoleDTO {

    private Long id;

    private String name;

    private String description;

    private boolean active;

    private List<ResPermissionDTO> permissions;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;
}