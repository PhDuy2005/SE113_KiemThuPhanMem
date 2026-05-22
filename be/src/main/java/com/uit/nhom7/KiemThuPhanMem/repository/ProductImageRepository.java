package com.uit.nhom7.KiemThuPhanMem.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uit.nhom7.KiemThuPhanMem.domain.table.ProductImage;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
    List<ProductImage> findByProductIdOrderByPrimaryImageDescCreatedAtAsc(UUID productId);
    List<ProductImage> findByProductIdIn(List<UUID> productIds);
}
