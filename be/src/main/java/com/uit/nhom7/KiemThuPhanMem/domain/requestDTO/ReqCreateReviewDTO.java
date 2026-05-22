package com.uit.nhom7.KiemThuPhanMem.domain.requestDTO;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReqCreateReviewDTO {
    @NotNull(message = "Product id is required")
    private UUID productId;

    @NotNull(message = "Order id is required")
    private UUID orderId;

    private Integer ratingStars;

    private String reviewComment;
}
