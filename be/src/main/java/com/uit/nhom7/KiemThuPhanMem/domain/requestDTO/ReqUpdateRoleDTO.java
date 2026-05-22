package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing Role
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqUpdateRoleDTO {

    @NotNull(message = "ID không được để trống")
    private Long id;

    private String name;

    private String description;

    private Boolean active;
}