package com.uit.nhom7.KiemThuPhanMem.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uit.nhom7.KiemThuPhanMem.domain.table.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    @Query("""
            select review
            from Review review
            join fetch review.user
            join fetch review.product
            where review.product.id = :productId
              and upper(review.status) = upper(:status)
            order by review.createdAt desc
            """)
    List<Review> findByProductIdAndStatusWithUser(
            @Param("productId") UUID productId,
            @Param("status") String status);
}
