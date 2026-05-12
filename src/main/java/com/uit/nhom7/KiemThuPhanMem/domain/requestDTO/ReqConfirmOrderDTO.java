package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqConfirmOrderDTO {
    @NotEmpty(message = "You must select at least one product")
    private List<UUID> selectedProductIds;

    @NotNull(message = "Shipping address id must not be empty")
    private UUID shippingAddressId;

    @NotNull(message = "Payment method id must not be empty")
    private UUID paymentMethodId;

    private String voucherCode;
}
