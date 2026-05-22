package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Data;

@Data
public class ReqCreateVoucherDTO {
    private String voucherCode;
    private BigDecimal discountValue;
    private String discountType;
    private Integer quantity;
    private Instant startDate;
    private Instant endDate;
    private BigDecimal minOrderAmount;
}
