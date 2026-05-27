package com.uit.nhom7.KiemThuPhanMem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.service.VoucherManagementService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/vouchers")
public class VoucherController {
    private final VoucherManagementService voucherManagementService;

    public VoucherController(VoucherManagementService voucherManagementService) {
        this.voucherManagementService = voucherManagementService;
    }

    @GetMapping
    @ApiMessage("Lay danh sach voucher cong khai")
    public ResponseEntity<ResultPaginationDTO> getPublicVouchers(
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(voucherManagementService.getPublicVouchers(pageNumber, pageSize));
    }
}
