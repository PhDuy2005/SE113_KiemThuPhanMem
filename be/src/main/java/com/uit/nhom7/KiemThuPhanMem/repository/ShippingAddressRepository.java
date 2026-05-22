package com.uit.nhom7.KiemThuPhanMem.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uit.nhom7.KiemThuPhanMem.domain.table.ShippingAddress;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, UUID> {
    Optional<ShippingAddress> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

    Optional<ShippingAddress> findByUserIdAndDefaultAddressTrueAndDeletedAtIsNull(UUID userId);

    List<ShippingAddress> findByUserIdAndDeletedAtIsNull(UUID userId);

    boolean existsByUserIdAndDeletedAtIsNull(UUID userId);

    @Modifying
    @Query("""
            update ShippingAddress address
            set address.defaultAddress = false
            where address.user.id = :userId
              and address.defaultAddress = true
              and address.deletedAt is null
            """)
    int clearDefaultByUserId(@Param("userId") UUID userId);
}
