package com.uit.nhom7.KiemThuPhanMem.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateStaffDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResUserDTO;
import com.uit.nhom7.KiemThuPhanMem.service.StaffManagementService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/business/staff")
public class StaffManagementController {
    private final StaffManagementService staffManagementService;

    public StaffManagementController(StaffManagementService staffManagementService) {
        this.staffManagementService = staffManagementService;
    }

    @PostMapping
    @ApiMessage("Business admin tao tai khoan nhan vien")
    public ResponseEntity<ResUserDTO> createStaff(@RequestBody ReqCreateStaffDTO request) {
        ResUserDTO staff = staffManagementService.createStaff(request);
        return ResponseEntity
                .created(URI.create("/api/v1/business/staff/" + staff.getId()))
                .body(staff);
    }

    @PatchMapping("/{staffId}/lock")
    @ApiMessage("Business admin khoa tai khoan nhan vien")
    public ResponseEntity<ResUserDTO> lockStaff(@PathVariable UUID staffId) {
        return ResponseEntity.ok(staffManagementService.lockStaff(staffId));
    }
}
