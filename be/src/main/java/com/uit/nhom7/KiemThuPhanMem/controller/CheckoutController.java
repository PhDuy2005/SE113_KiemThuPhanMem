package com.uit.nhom7.KiemThuPhanMem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqApplyVoucherDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCheckoutSelectionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqConfirmOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqSelectPaymentMethodDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCheckoutSelectionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResPaymentMethodSelectionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResVoucherApplicationDTO;
import com.uit.nhom7.KiemThuPhanMem.service.CheckoutService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/selection")
    @ApiMessage("Chon mat hang de thanh toan")
    public ResponseEntity<ResCheckoutSelectionDTO> calculateSelection(
            @Valid @RequestBody ReqCheckoutSelectionDTO request) {
        return ResponseEntity.ok(checkoutService.calculateSelection(request));
    }

    @PostMapping("/voucher")
    @ApiMessage("Ap dung ma giam gia")
    public ResponseEntity<ResVoucherApplicationDTO> applyVoucher(
            @Valid @RequestBody ReqApplyVoucherDTO request) {
        return ResponseEntity.ok(checkoutService.applyVoucher(request));
    }

    @PostMapping("/payment-method")
    @ApiMessage("Chon phuong thuc thanh toan")
    public ResponseEntity<ResPaymentMethodSelectionDTO> selectPaymentMethod(
            @Valid @RequestBody ReqSelectPaymentMethodDTO request) {
        return ResponseEntity.ok(checkoutService.selectPaymentMethod(request));
    }

    @PostMapping("/confirm")
    @ApiMessage("Xac nhan don hang")
    public ResponseEntity<ResOrderDTO> confirmOrder(@Valid @RequestBody ReqConfirmOrderDTO request) {
        return ResponseEntity.ok(checkoutService.confirmOrder(request));
    }
}
