package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqCheckoutSelectionDTO {
    @NotEmpty(message = "You must select at least one product")
    private List<UUID> selectedProductIds;
}
