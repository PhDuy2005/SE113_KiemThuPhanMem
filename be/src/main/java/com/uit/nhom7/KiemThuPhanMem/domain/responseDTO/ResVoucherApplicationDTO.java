package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResVoucherApplicationDTO {
    private String voucherCode;
    private BigDecimal discountAmount;
    private BigDecimal finalTotal;
    private String message;
}
