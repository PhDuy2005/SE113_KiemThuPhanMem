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

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateVoucherDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResVoucherDTO;
import com.uit.nhom7.KiemThuPhanMem.service.VoucherManagementService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/business/vouchers")
public class VoucherManagementController {
    private final VoucherManagementService voucherManagementService;

    public VoucherManagementController(VoucherManagementService voucherManagementService) {
        this.voucherManagementService = voucherManagementService;
    }

    @PostMapping
    @ApiMessage("Business admin tao voucher kem dieu kien")
    public ResponseEntity<ResVoucherDTO> createVoucher(@RequestBody ReqCreateVoucherDTO request) {
        ResVoucherDTO voucher = voucherManagementService.createVoucher(request);
        return ResponseEntity
                .created(URI.create("/api/v1/business/vouchers/" + voucher.getId()))
                .body(voucher);
    }

    @PatchMapping("/{voucherId}/stop")
    @ApiMessage("Business admin dung voucher khan cap")
    public ResponseEntity<ResVoucherDTO> emergencyStopVoucher(@PathVariable UUID voucherId) {
        return ResponseEntity.ok(voucherManagementService.emergencyStopVoucher(voucherId));
    }
}
