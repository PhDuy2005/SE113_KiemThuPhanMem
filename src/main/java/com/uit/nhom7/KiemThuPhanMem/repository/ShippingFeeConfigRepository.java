package com.uit.nhom7.KiemThuPhanMem.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uit.nhom7.KiemThuPhanMem.domain.table.ShippingFeeConfig;

public interface ShippingFeeConfigRepository extends JpaRepository<ShippingFeeConfig, UUID> {
    List<ShippingFeeConfig> findAllByOrderByProvinceAsc();

    Optional<ShippingFeeConfig> findByProvinceCode(String provinceCode);

    Optional<ShippingFeeConfig> findByProvinceKey(String provinceKey);
}
