package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResVoucherDTO {
    private UUID id;
    private String voucherCode;
    private String discountType;
    private BigDecimal discountValue;
    private Integer quantity;
    private Integer usedCount;
    private BigDecimal minOrderAmount;
    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
    private String message;
}
