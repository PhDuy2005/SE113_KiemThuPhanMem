package com.uit.nhom7.KiemThuPhanMem.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByStatusIgnoreCase(String status);
}
