package com.uit.nhom7.KiemThuPhanMem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqOnlinePaymentDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOnlinePaymentDTO;
import com.uit.nhom7.KiemThuPhanMem.service.PaymentService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/online")
    @ApiMessage("Khoi tao thanh toan truc tuyen")
    public ResponseEntity<ResOnlinePaymentDTO> initializeOnlinePayment(
            @Valid @RequestBody ReqOnlinePaymentDTO request) {
        return ResponseEntity.ok(paymentService.initializeOnlinePayment(request));
    }
}
