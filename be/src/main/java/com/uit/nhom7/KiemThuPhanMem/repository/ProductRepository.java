package com.uit.nhom7.KiemThuPhanMem.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByStatusIgnoreCase(String status);

    Optional<Product> findByIdAndStatusIgnoreCase(UUID id, String status);

    List<Product> findByStatusIgnoreCase(String status, Sort sort);

    List<Product> findByStatusIgnoreCaseAndCategoryIdIn(String status, List<UUID> categoryIds, Sort sort);

    @Modifying
    @Query("update Product product set product.categoryId = :newCategoryId where product.categoryId = :oldCategoryId")
    int updateCategory(
            @Param("oldCategoryId") UUID oldCategoryId,
            @Param("newCategoryId") UUID newCategoryId);

    @Modifying
    @Query("update Product product set product.price = :newPrice where product.id = :productId")
    int updatePrice(@Param("productId") UUID productId, @Param("newPrice") java.math.BigDecimal newPrice);

    @Modifying
    @Query("update Product product set product.status = :status where product.id = :productId")
    int updateStatus(@Param("productId") UUID productId, @Param("status") String status);
}
