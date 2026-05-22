package com.uit.nhom7.KiemThuPhanMem.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uit.nhom7.KiemThuPhanMem.domain.table.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
