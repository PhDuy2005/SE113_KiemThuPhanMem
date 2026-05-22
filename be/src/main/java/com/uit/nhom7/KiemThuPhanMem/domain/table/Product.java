package com.uit.nhom7.KiemThuPhanMem.domain.table;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.UuidV7Generator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    public static final String ACTIVE_STATUS = "ACTIVE";
    public static final String DISCONTINUED_STATUS = "DISCONTINUED";
    public static final String OUT_OF_STOCK_STATUS = "OUT_OF_STOCK";

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotBlank(message = "Product name must not be empty")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Product price must not be empty")
    private BigDecimal price;

    @NotBlank(message = "Product status must not be empty")
    private String status;

    private String brand;

    @Column(name = "normalized_name")
    private String normalizedName;

    @Column(name = "category_id", columnDefinition = "BINARY(16)")
    private UUID categoryId;

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
        if (id == null) {
            id = UuidV7Generator.generate();
        }
        if (status == null || status.isBlank()) {
            status = ACTIVE_STATUS;
        }
        normalizedName = normalizeForSearch(name);
        createdAt = Instant.now();
        createdBy = SecurityUtil.getCurrentUserLogin().orElse("system");
    }

    @PreUpdate
    protected void onUpdate() {
        normalizedName = normalizeForSearch(name);
        updatedAt = Instant.now();
        updatedBy = SecurityUtil.getCurrentUserLogin().orElse("system");
    }

    private String normalizeForSearch(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd');
        return normalized.replaceAll("[^a-z0-9]+", " ").trim().replaceAll("\\s+", " ");
    }
}
