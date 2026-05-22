package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResBestSellingProductsReportDTO {
    private List<BestSellingProduct> rankingList;
    private String message;

    @Data
    @Builder
    public static class BestSellingProduct {
        private int rank;
        private UUID productId;
        private String productName;
        private long totalSold;
        private BigDecimal revenue;
    }
}
