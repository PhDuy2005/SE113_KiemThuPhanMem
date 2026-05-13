package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqShippingAddressDTO {
    private String provinceCode;

    private String province;

    private String wardCode;

    private String ward;

    private String detail;
}
