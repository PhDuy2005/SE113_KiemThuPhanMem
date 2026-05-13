package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResRevenueReportDTO {
    private BigDecimal totalRevenue;
    private long completedOrderCount;
    private List<RevenuePoint> chartData;
    private String message;

    @Data
    @Builder
    public static class RevenuePoint {
        private LocalDate date;
        private BigDecimal value;
        private long orderCount;
    }
}
