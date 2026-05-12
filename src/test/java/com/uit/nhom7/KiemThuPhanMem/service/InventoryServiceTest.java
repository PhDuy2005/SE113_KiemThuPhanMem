package com.uit.nhom7.KiemThuPhanMem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResInventoryStockDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

class InventoryServiceTest {
    @Test
    void getStockStatusShouldReturnInStockWhenAvailableQuantityIsPositive() {
        InventoryRepository inventoryRepository = Mockito.mock(InventoryRepository.class);
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        InventoryService inventoryService = new InventoryService(inventoryRepository, productRepository);
        UUID productId = UUID.randomUUID();

        when(productRepository.findByIdAndStatusIgnoreCase(productId, Product.ACTIVE_STATUS))
                .thenReturn(Optional.of(activeProduct(productId)));
        when(inventoryRepository.findById(productId))
                .thenReturn(Optional.of(Inventory.builder()
                        .productId(productId)
                        .quantity(10)
                        .reservedQuantity(3)
                        .build()));

        ResInventoryStockDTO result = inventoryService.getStockStatus(productId);

        assertThat(result.getAvailableQuantity()).isEqualTo(7);
        assertThat(result.getStockStatus()).isEqualTo("IN_STOCK");
    }

    @Test
    void getStockStatusShouldReturnOutOfStockWhenNoQuantityIsAvailable() {
        InventoryRepository inventoryRepository = Mockito.mock(InventoryRepository.class);
        ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        InventoryService inventoryService = new InventoryService(inventoryRepository, productRepository);
        UUID productId = UUID.randomUUID();

        when(productRepository.findByIdAndStatusIgnoreCase(productId, Product.ACTIVE_STATUS))
                .thenReturn(Optional.of(activeProduct(productId)));
        when(inventoryRepository.findById(productId))
                .thenReturn(Optional.of(Inventory.builder()
                        .productId(productId)
                        .quantity(5)
                        .reservedQuantity(5)
                        .build()));

        ResInventoryStockDTO result = inventoryService.getStockStatus(productId);

        assertThat(result.getAvailableQuantity()).isZero();
        assertThat(result.getStockStatus()).isEqualTo("OUT_OF_STOCK");
    }

    @Test
    void getStockStatusShouldRejectUnknownProduct() {
        InventoryService inventoryService = new InventoryService(
                Mockito.mock(InventoryRepository.class),
                Mockito.mock(ProductRepository.class));

        assertThatThrownBy(() -> inventoryService.getStockStatus(UUID.randomUUID()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Product not found");
    }

    private Product activeProduct(UUID productId) {
        return Product.builder()
                .id(productId)
                .name("Product")
                .price(BigDecimal.TEN)
                .status(Product.ACTIVE_STATUS)
                .build();
    }
}
