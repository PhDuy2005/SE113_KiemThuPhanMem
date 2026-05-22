package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResOrderStatusTimelineDTO {
    private UUID orderId;
    private String currentStatus;
    private List<StatusStep> timeline;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatusStep {
        private String statusName;
        private Instant updatedAt;
        private String description;
    }
}
