package com.uit.nhom7.KiemThuPhanMem.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

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

    @GetMapping
    @ApiMessage("Lay danh sach dia chi giao hang")
    public ResponseEntity<List<ResShippingAddressDTO>> getAddresses() {
        return ResponseEntity.ok(shippingAddressService.getAddresses());
    }

    @PutMapping("/{addressId}")
    @ApiMessage("Cap nhat dia chi giao hang")
    public ResponseEntity<ResShippingAddressDTO> updateAddress(
            @PathVariable UUID addressId,
            @Valid @RequestBody ReqShippingAddressDTO request) {
        return ResponseEntity.ok(shippingAddressService.updateAddress(addressId, request));
    }

    @DeleteMapping("/{addressId}")
    @ApiMessage("Xoa dia chi giao hang")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID addressId) {
        shippingAddressService.deleteAddress(addressId);
        return ResponseEntity.ok().build();
    }
}
