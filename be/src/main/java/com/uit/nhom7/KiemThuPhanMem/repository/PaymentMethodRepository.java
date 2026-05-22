package com.uit.nhom7.KiemThuPhanMem.repository;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uit.nhom7.KiemThuPhanMem.domain.table.PaymentMethod;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {
    Optional<PaymentMethod> findByNameIgnoreCase(String name);
}
