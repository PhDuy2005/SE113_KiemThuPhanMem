package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResReviewResponseDTO {
    private UUID id;
    private UUID reviewId;
    private UUID staffId;
    private String staffName;
    private String content;
    private Instant createdAt;
    private String message;
}
