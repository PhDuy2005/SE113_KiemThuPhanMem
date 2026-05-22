package com.uit.nhom7.KiemThuPhanMem.repository;

import java.util.UUID;
import java.util.Optional;
import java.util.List;
import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uit.nhom7.KiemThuPhanMem.domain.table.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByIdAndUserId(UUID id, UUID userId);

    Page<Order> findByUserId(UUID userId, Pageable pageable);

    Page<Order> findByStatusIgnoreCase(String status, Pageable pageable);

    Page<Order> findByStatusIgnoreCaseAndUserId(String status, UUID userId, Pageable pageable);

    Page<Order> findByUserPhoneNumberContaining(String phoneNumber, Pageable pageable);

    List<Order> findByUserIdAndStatusIgnoreCase(UUID userId, String status);

    List<Order> findByStatusIgnoreCaseAndCompletedAtBetween(String status, Instant startDate, Instant endDate);

    List<Order> findByCreatedAtBetween(Instant startDate, Instant endDate);

    List<Order> findByStatusIgnoreCaseAndCreatedAtBetween(String status, Instant startDate, Instant endDate);
}
