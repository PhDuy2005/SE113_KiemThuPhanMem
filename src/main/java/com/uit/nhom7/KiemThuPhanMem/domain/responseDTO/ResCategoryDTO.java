package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResCategoryDTO {
    private UUID id;
    private String categoryName;
    private String categoryImage;
    private String categoryDescription;
    private Instant createdAt;
    private Instant updatedAt;
    private String message;
}
