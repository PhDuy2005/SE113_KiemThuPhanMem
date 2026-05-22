package com.uit.nhom7.KiemThuPhanMem.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = """
            select review
            from Review review
            join fetch review.user
            join fetch review.product
            """,
            countQuery = "select count(review) from Review review")
    Page<Review> findAllWithUserAndProduct(Pageable pageable);

    @Query(value = """
            select review
            from Review review
            join fetch review.user
            join fetch review.product
            where review.rating = :rating
            """,
            countQuery = "select count(review) from Review review where review.rating = :rating")
    Page<Review> findByRatingWithUserAndProduct(@Param("rating") Integer rating, Pageable pageable);

    @Query("SELECT r.product.id, AVG(r.rating) FROM Review r WHERE r.product.id IN :productIds AND UPPER(r.status) = 'APPROVED' GROUP BY r.product.id")
    List<Object[]> getAverageRatingForProducts(@Param("productIds") List<UUID> productIds);
}
