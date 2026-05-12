package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.time.Instant;
import java.util.List;
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
    private String productName;
    private Integer ratingStars;
    private String reviewComment;
    private String status;
    private boolean responded;
    private String violationReason;
    private String violationDescription;
    private Instant hiddenAt;
    private Instant createdAt;
    private List<ResReviewResponseDTO> responses;
    private String message;
}
