package com.uit.nhom7.KiemThuPhanMem.domain.table;

import java.time.Instant;

import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "system_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfig {
    @Id
    @Column(name = "config_key", length = 100)
    private String key;

    @Column(name = "config_value", columnDefinition = "TEXT", nullable = false)
    private String value;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        createdBy = SecurityUtil.getCurrentUserLogin().orElse("system");
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        updatedBy = SecurityUtil.getCurrentUserLogin().orElse("system");
    }
}
