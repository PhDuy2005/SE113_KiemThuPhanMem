package com.uit.nhom7.KiemThuPhanMem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqSyncRolePermissionsDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResRolePermissionMatrixDTO;
import com.uit.nhom7.KiemThuPhanMem.service.RolePermissionMatrixService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/business/role-permissions")
public class RolePermissionMatrixController {
    private final RolePermissionMatrixService rolePermissionMatrixService;

    public RolePermissionMatrixController(RolePermissionMatrixService rolePermissionMatrixService) {
        this.rolePermissionMatrixService = rolePermissionMatrixService;
    }

    @GetMapping
    @ApiMessage("Business admin lay ma tran role-permission")
    public ResponseEntity<ResRolePermissionMatrixDTO> getMatrix() {
        return ResponseEntity.ok(rolePermissionMatrixService.getMatrix());
    }

    @PutMapping
    @ApiMessage("Business admin cap nhat ma tran role-permission")
    public ResponseEntity<ResRolePermissionMatrixDTO> syncRolePermissions(
            @RequestBody ReqSyncRolePermissionsDTO request) {
        return ResponseEntity.ok(rolePermissionMatrixService.syncRolePermissions(request));
    }
}
