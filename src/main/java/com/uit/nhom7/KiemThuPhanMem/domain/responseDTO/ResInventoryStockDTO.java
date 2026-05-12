package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResInventoryStockDTO {
    private UUID productId;
    private int availableQuantity;
    private String stockStatus;
}
