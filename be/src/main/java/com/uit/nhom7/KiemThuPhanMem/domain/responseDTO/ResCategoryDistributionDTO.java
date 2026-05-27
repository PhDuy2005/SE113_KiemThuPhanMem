package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResCategoryDistributionDTO {
    private List<CategoryPoint> distribution;
    private String message;

    @Data
    @Builder
    public static class CategoryPoint {
        private String name;
        private double value;
    }
}
