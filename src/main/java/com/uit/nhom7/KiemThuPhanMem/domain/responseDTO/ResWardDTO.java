package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResWardDTO {
    private String code;
    private String parentCode;
    private String name;
    private String nameWithType;
    private String pathWithType;
    private String slug;
    private String type;
}
