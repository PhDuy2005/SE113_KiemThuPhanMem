package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResCartItemActionDTO {
    private UUID cartId;
    private UUID productId;
    private int quantity;
    private int cartBadgeCount;
    private int availableQuantity;
    private BigDecimal totalCartPrice;
    private String message;
}
