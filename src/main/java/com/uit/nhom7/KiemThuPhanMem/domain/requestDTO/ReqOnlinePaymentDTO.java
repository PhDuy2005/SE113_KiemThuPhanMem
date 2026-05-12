package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqOnlinePaymentDTO {
    @NotNull(message = "Order id must not be empty")
    private UUID orderId;

    @NotNull(message = "Payment method id must not be empty")
    private UUID paymentMethodId;

    @NotNull(message = "Total amount must not be empty")
    private BigDecimal totalAmount;
}
