package com.uit.nhom7.KiemThuPhanMem.domain.table;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_method")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {
    public static final String ONLINE_TYPE = "ONLINE";
    public static final String CASH_TYPE = "CASH";

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(unique = true)
    private String name;

    private String type;
}
