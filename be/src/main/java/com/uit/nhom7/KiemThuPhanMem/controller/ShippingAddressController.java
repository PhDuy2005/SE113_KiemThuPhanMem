package com.uit.nhom7.KiemThuPhanMem.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqShippingAddressDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResShippingAddressDTO;
import com.uit.nhom7.KiemThuPhanMem.service.ShippingAddressService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/addresses")
public class ShippingAddressController {
    private final ShippingAddressService shippingAddressService;

    public ShippingAddressController(ShippingAddressService shippingAddressService) {
        this.shippingAddressService = shippingAddressService;
    }

    @PostMapping
    @ApiMessage("Them dia chi giao hang moi")
    public ResponseEntity<ResShippingAddressDTO> createAddress(
            @Valid @RequestBody ReqShippingAddressDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shippingAddressService.createAddress(request));
    }

    @PutMapping("/default")
    @ApiMessage("Cap nhat dia chi mac dinh")
    public ResponseEntity<ResShippingAddressDTO> updateDefaultAddress(
            @Valid @RequestBody ReqShippingAddressDTO request) {
        return ResponseEntity.ok(shippingAddressService.updateDefaultAddress(request));
    }

    @PutMapping("/{addressId}/default")
    @ApiMessage("Dat dia chi mac dinh")
    public ResponseEntity<ResShippingAddressDTO> setDefaultAddress(@PathVariable UUID addressId) {
        return ResponseEntity.ok(shippingAddressService.setDefaultAddress(addressId));
    }
}
