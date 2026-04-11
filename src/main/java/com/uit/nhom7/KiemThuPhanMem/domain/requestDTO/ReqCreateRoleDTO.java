package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new Role
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqCreateRoleDTO {

    @NotBlank(message = "Tên role không được để trống")
    private String name;

    private String description;
}