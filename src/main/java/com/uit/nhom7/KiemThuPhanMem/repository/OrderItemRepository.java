package com.uit.nhom7.KiemThuPhanMem.repository;

import java.util.List;
import java.util.UUID;
import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uit.nhom7.KiemThuPhanMem.domain.table.OrderItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.OrderItemId;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
    @Query("""
            select item
            from OrderItem item
            join fetch item.product
            where item.order.id = :orderId
            """)
    List<OrderItem> findByOrderIdWithProduct(@Param("orderId") UUID orderId);

    @Query("""
            select item
            from OrderItem item
            join fetch item.product
            join fetch item.order
            where upper(item.order.status) = upper(:status)
              and item.order.createdAt between :startDate and :endDate
            """)
    List<OrderItem> findByOrderStatusAndOrderCreatedAtBetweenWithProduct(
            @Param("status") String status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);
}
