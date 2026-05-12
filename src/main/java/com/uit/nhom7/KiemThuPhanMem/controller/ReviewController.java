package com.uit.nhom7.KiemThuPhanMem.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateReviewDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProductReviewsDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResReviewDTO;
import com.uit.nhom7.KiemThuPhanMem.service.ReviewService;
import com.uit.nhom7.KiemThuPhanMem.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    @ApiMessage("Danh gia san pham da mua")
    public ResponseEntity<ResReviewDTO> createReview(@Valid @RequestBody ReqCreateReviewDTO request) {
        return ResponseEntity.ok(reviewService.createReview(request));
    }

    @GetMapping("/products/{productId}/reviews")
    @ApiMessage("Doc danh gia san pham")
    public ResponseEntity<ResProductReviewsDTO> getProductReviews(@PathVariable UUID productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }
}
