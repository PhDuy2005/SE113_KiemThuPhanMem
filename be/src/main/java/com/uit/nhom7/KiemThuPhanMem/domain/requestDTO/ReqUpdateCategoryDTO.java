package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import lombok.Data;

@Data
public class ReqUpdateCategoryDTO {
    private String categoryName;
    private String categoryImage;
    private String categoryDescription;
    private java.util.UUID parentId;
}
