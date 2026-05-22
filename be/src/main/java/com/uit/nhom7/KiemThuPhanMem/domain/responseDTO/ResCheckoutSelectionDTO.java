package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResCheckoutSelectionDTO {
    private List<UUID> selectedProductIds;
    private BigDecimal tempTotalPrice;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal totalPrice;
    private String checkoutUrl;
}
