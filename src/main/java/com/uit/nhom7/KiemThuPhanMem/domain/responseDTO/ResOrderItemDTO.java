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
public class ResOrderItemDTO {
    private UUID productId;
    private String productName;
    private BigDecimal price;
    private int quantity;
    private BigDecimal lineTotal;
}
