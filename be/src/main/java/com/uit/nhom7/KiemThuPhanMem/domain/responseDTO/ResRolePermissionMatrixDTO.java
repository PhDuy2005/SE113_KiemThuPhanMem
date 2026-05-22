package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.util.List;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResRolePermissionMatrixDTO {
    private List<ResRoleDTO> roles;
    private List<ResPermissionDTO> permissions;
    private List<RolePermissionSelection> rolePermissions;
    private String message;

    @Data
    @Builder
    public static class RolePermissionSelection {
        private Long roleId;
        private Set<Long> permissionIds;
    }
}
