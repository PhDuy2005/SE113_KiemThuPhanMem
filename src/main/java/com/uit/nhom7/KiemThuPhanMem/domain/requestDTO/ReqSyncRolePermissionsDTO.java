package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class ReqSyncRolePermissionsDTO {
    private List<RolePermissionSetting> rolePermissions;

    @Data
    public static class RolePermissionSetting {
        private Long roleId;
        private Set<Long> permissionIds;
    }
}
