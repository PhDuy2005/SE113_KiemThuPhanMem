package com.uit.nhom7.KiemThuPhanMem.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
}
