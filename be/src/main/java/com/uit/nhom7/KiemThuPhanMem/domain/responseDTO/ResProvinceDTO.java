package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResProvinceDTO {
    private String code;
    private String name;
    private String nameWithType;
    private String slug;
    private String type;
}
