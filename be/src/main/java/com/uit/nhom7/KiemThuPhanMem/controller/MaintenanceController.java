package com.uit.nhom7.KiemThuPhanMem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqEnableMaintenanceDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResMaintenanceDTO;
import com.uit.nhom7.KiemThuPhanMem.service.MaintenanceService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/business/system/maintenance")
public class MaintenanceController {
    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    @ApiMessage("Business admin xem trang thai bao tri")
    public ResponseEntity<ResMaintenanceDTO> getMaintenanceStatus() {
        return ResponseEntity.ok(maintenanceService.getMaintenanceStatus());
    }

    @PatchMapping("/enable")
    @ApiMessage("Business admin bat che do bao tri va sao luu tu dong")
    public ResponseEntity<ResMaintenanceDTO> enableMaintenance(@RequestBody ReqEnableMaintenanceDTO request) {
        return ResponseEntity.ok(maintenanceService.enableMaintenance(request));
    }
}
