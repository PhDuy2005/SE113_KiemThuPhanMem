package com.uit.nhom7.KiemThuPhanMem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateShippingFeeDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResShippingFeeDTO;
import com.uit.nhom7.KiemThuPhanMem.service.ShippingFeeConfigService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/business/shipping-fees")
public class ShippingFeeConfigController {
    private final ShippingFeeConfigService shippingFeeConfigService;

    public ShippingFeeConfigController(ShippingFeeConfigService shippingFeeConfigService) {
        this.shippingFeeConfigService = shippingFeeConfigService;
    }

    @GetMapping
    @ApiMessage("Business admin lay danh sach cau hinh phi giao hang theo province")
    public ResponseEntity<List<ResShippingFeeDTO>> getShippingFees() {
        return ResponseEntity.ok(shippingFeeConfigService.getShippingFees());
    }

    @PutMapping
    @ApiMessage("Business admin cap nhat phi giao hang theo province")
    public ResponseEntity<List<ResShippingFeeDTO>> updateShippingFees(
            @RequestBody List<ReqUpdateShippingFeeDTO> request) {
        return ResponseEntity.ok(shippingFeeConfigService.updateShippingFees(request));
    }
}
