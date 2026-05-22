package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResShippingFeeDTO {
    private UUID id;
    private String provinceCode;
    private String province;
    private BigDecimal shippingFee;
    private Instant createdAt;
    private Instant updatedAt;
    private String message;
}
