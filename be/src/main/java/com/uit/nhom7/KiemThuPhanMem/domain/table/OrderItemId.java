package com.uit.nhom7.KiemThuPhanMem.domain.table;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemId implements Serializable {
    @Column(name = "order_id", columnDefinition = "BINARY(16)")
    private UUID orderId;

    @Column(name = "product_id", columnDefinition = "BINARY(16)")
    private UUID productId;
}
