package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqApplyVoucherDTO {
    @NotBlank(message = "Voucher code must not be empty")
    private String voucherCode;

    @NotNull(message = "Total order amount must not be empty")
    private BigDecimal totalOrderAmount;
}
