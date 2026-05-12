package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import lombok.Data;

@Data
public class ReqCreateCategoryDTO {
    private String categoryName;
    private String categoryImage;
    private String categoryDescription;
}
