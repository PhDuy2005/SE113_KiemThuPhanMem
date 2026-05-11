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
public class ResProductDTO {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String status;
    private String brand;
    private UUID categoryId;
}
