package com.uit.nhom7.KiemThuPhanMem.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uit.nhom7.KiemThuPhanMem.domain.table.ReviewResponse;

@Repository
public interface ReviewResponseRepository extends JpaRepository<ReviewResponse, UUID> {
    List<ReviewResponse> findByReviewIdOrderByCreatedAtAsc(UUID reviewId);

    boolean existsByReviewId(UUID reviewId);
}
