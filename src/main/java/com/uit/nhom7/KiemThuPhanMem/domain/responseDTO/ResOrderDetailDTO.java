package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResOrderDetailDTO {
    private UUID orderId;
    private String status;
    private BigDecimal totalProductAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String shippingAddressSnapshot;
    private String paymentMethod;
    private String paymentStatus;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ResOrderItemDTO> items;
}
