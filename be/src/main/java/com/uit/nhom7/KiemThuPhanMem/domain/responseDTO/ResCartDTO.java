package com.uit.nhom7.KiemThuPhanMem.domain.responseDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResCartDTO {
    private UUID userId;
    private List<CartItemDto> items;
    private BigDecimal totalPrice;
    private int totalItemsCount;

    @Data
    @Builder
    public static class CartItemDto {
        private UUID productId;
        private int quantity;
        private Instant createdAt;
        private Instant updatedAt;
        private CartProductDto product;
    }

    @Data
    @Builder
    public static class CartProductDto {
        private UUID id;
        private String name;
        private String brand;
        private BigDecimal price;
        private List<CartProductImageDto> images;
    }

    @Data
    @Builder
    public static class CartProductImageDto {
        private UUID id;
        private String imageUrl;
        private boolean isPrimary;
    }
}
