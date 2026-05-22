package com.uit.nhom7.KiemThuPhanMem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProvinceDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResWardDTO;
import com.uit.nhom7.KiemThuPhanMem.service.AddressDataService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/address-data")
public class AddressDataController {
    private final AddressDataService addressDataService;

    public AddressDataController(AddressDataService addressDataService) {
        this.addressDataService = addressDataService;
    }

    @GetMapping("/provinces")
    @ApiMessage("Lay danh sach tinh thanh Viet Nam")
    public ResponseEntity<List<ResProvinceDTO>> getProvinces() {
        return ResponseEntity.ok(addressDataService.getProvinces());
    }

    @GetMapping("/provinces/{provinceCode}/wards")
    @ApiMessage("Lay danh sach phuong xa theo tinh thanh")
    public ResponseEntity<List<ResWardDTO>> getWards(@PathVariable String provinceCode) {
        return ResponseEntity.ok(addressDataService.getWardsByProvinceCode(provinceCode));
    }
}
