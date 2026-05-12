package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResReviewDTO {
    private UUID id;
    private UUID userId;
    private String reviewerName;
    private UUID productId;
    private Integer ratingStars;
    private String reviewComment;
    private String status;
    private Instant createdAt;
    private String message;
}
