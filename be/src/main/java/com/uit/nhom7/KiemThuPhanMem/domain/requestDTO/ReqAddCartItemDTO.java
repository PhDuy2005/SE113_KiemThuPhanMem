package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqAddCartItemDTO {
    @NotNull(message = "Product id must not be empty")
    private UUID productId;

    @NotNull(message = "Quantity must not be empty")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;
}
