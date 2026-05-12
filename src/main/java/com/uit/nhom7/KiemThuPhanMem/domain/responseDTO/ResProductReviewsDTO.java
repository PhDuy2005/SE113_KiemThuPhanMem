package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResProductReviewsDTO {
    private UUID productId;
    private double averageRating;
    private long totalReviews;
    private List<ResReviewDTO> reviewList;
    private String message;
}
