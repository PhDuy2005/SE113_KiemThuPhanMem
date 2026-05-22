package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

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
public class ReqChangeOrderAddressDTO {
    @NotNull(message = "New address id must not be empty")
    private UUID newAddressId;
}
