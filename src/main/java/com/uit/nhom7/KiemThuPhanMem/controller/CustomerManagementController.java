package com.uit.nhom7.KiemThuPhanMem.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResUserDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.service.CustomerManagementService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/business/customers")
public class CustomerManagementController {
    private final CustomerManagementService customerManagementService;

    public CustomerManagementController(CustomerManagementService customerManagementService) {
        this.customerManagementService = customerManagementService;
    }

    @GetMapping
    @ApiMessage("Business admin quan ly danh sach khach hang")
    public ResponseEntity<ResultPaginationDTO> getCustomers(
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(customerManagementService.getCustomers(pageNumber, pageSize));
    }

    @PatchMapping("/{customerId}/block-fraud")
    @ApiMessage("Business admin chan tai khoan dat hang gia mao")
    public ResponseEntity<ResUserDTO> blockFraudCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(customerManagementService.blockFraudCustomer(customerId));
    }
}
