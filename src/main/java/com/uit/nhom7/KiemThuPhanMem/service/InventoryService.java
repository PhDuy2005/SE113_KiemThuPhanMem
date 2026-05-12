package com.uit.nhom7.KiemThuPhanMem.service;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResInventoryStockDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class InventoryService {
    private static final String IN_STOCK = "IN_STOCK";
    private static final String OUT_OF_STOCK = "OUT_OF_STOCK";

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public ResInventoryStockDTO getStockStatus(UUID productId) {
        productRepository.findByIdAndStatusIgnoreCase(productId, Product.ACTIVE_STATUS)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found"));

        int availableQuantity = inventoryRepository.findById(productId)
                .map(Inventory::getAvailableQuantity)
                .orElse(0);

        return ResInventoryStockDTO.builder()
                .productId(productId)
                .availableQuantity(availableQuantity)
                .stockStatus(availableQuantity > 0 ? IN_STOCK : OUT_OF_STOCK)
                .build();
    }
}
