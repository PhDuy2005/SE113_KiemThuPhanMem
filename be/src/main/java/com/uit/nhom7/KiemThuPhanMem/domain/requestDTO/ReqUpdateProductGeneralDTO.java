package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import java.util.UUID;
import lombok.Data;

@Data
public class ReqUpdateProductGeneralDTO {
    private String name;
    private String description;
    private UUID categoryId;
    private String brand;
    private String status;
}
