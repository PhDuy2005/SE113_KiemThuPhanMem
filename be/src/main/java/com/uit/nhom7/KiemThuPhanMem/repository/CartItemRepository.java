package com.uit.nhom7.KiemThuPhanMem.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uit.nhom7.KiemThuPhanMem.domain.table.CartItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.CartItemId;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {
    @Query("""
            select item
            from CartItem item
            where item.cart.id = :cartId
              and item.product.id = :productId
            """)
    Optional<CartItem> findByCartIdAndProductId(
            @Param("cartId") UUID cartId,
            @Param("productId") UUID productId);

    @Query("""
            select item
            from CartItem item
            join fetch item.product
            where item.cart.id = :cartId
            """)
    List<CartItem> findByCartIdWithProduct(@Param("cartId") UUID cartId);

    @Query("""
            select item
            from CartItem item
            join fetch item.product
            where item.cart.id = :cartId
              and item.product.id in :productIds
            """)
    List<CartItem> findSelectedByCartIdWithProduct(
            @Param("cartId") UUID cartId,
            @Param("productIds") List<UUID> productIds);

    @Modifying
    @Query("""
            delete from CartItem item
            where item.cart.id = :cartId
              and item.product.id in :productIds
            """)
    int deleteByCartIdAndProductIdIn(
            @Param("cartId") UUID cartId,
            @Param("productIds") List<UUID> productIds);

    @Query("""
            select coalesce(sum(item.quantity), 0)
            from CartItem item
            where item.cart.id = :cartId
            """)
    int getTotalItemsCount(@Param("cartId") UUID cartId);

    @Modifying
    @Query("""
            delete from CartItem item
            where item.cart.id = :cartId
            """)
    int deleteByCartId(@Param("cartId") UUID cartId);
}
