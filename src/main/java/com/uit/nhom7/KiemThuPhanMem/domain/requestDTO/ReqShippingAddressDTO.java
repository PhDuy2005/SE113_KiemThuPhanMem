package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqShippingAddressDTO {
    @NotBlank(message = "Province must not be empty")
    private String province;

    @NotBlank(message = "Ward must not be empty")
    private String ward;

    @NotBlank(message = "Detail must not be empty")
    private String detail;
}
