package com.uit.nhom7.KiemThuPhanMem.domain.table;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    @Id
    @Column(name = "product_id", columnDefinition = "BINARY(16)")
    private UUID productId;

    @Min(value = 0, message = "Quantity must not be negative")
    private Integer quantity;

    @Min(value = 0, message = "Reserved quantity must not be negative")
    @Column(name = "reserved_quantity")
    private Integer reservedQuantity;

    public int getAvailableQuantity() {
        int currentQuantity = quantity == null ? 0 : quantity;
        int currentReservedQuantity = reservedQuantity == null ? 0 : reservedQuantity;
        return Math.max(0, currentQuantity - currentReservedQuantity);
    }
}
