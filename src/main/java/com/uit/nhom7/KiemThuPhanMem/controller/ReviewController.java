package com.uit.nhom7.KiemThuPhanMem.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateReviewDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqHideReviewDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqReplyReviewDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProductReviewsDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResReviewDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResReviewResponseDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
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

    @GetMapping("/staff/reviews")
    @ApiMessage("Staff xem phan hoi moi nhat")
    public ResponseEntity<ResultPaginationDTO> getLatestFeedbacksForStaff(
            @RequestParam(value = "starFilter", required = false) Integer starFilter,
            @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "DESC") String sortOrder) {
        return ResponseEntity.ok(reviewService.getLatestFeedbacksForStaff(
                starFilter, pageNumber, pageSize, sortBy, sortOrder));
    }

    @PostMapping("/staff/reviews/{reviewId}/responses")
    @ApiMessage("Staff phan hoi danh gia")
    public ResponseEntity<ResReviewResponseDTO> replyReview(
            @PathVariable UUID reviewId,
            @RequestBody ReqReplyReviewDTO request) {
        return ResponseEntity.ok(reviewService.replyReview(reviewId, request));
    }

    @PatchMapping("/staff/reviews/{reviewId}/hide")
    @ApiMessage("Staff an danh gia vi pham")
    public ResponseEntity<ResReviewDTO> hideReview(
            @PathVariable UUID reviewId,
            @RequestBody ReqHideReviewDTO request) {
        return ResponseEntity.ok(reviewService.hideReview(reviewId, request));
    }
}
