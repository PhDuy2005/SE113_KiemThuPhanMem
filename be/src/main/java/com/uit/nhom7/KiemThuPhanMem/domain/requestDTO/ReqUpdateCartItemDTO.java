package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

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
public class ReqUpdateCartItemDTO {
    @NotNull(message = "New quantity must not be empty")
    @Min(value = 1, message = "New quantity must be greater than 0")
    private Integer newQuantity;

    private Integer oldQuantity;
}
