package com.uit.nhom7.KiemThuPhanMem.service.product_management_service;

import static org.mockito.Mockito.mock;

import com.uit.nhom7.KiemThuPhanMem.repository.CategoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductImageRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.service.ProductManagementService;

public abstract class ProductManagementServiceTestBase {

    protected static class Fixture {
        public final CategoryRepository categoryRepository;
        public final InventoryRepository inventoryRepository;
        public final ProductImageRepository productImageRepository;
        public final ProductRepository productRepository;
        public final UserRepository userRepository;
        public final ProductManagementService productManagementService;

        public Fixture() {
            this.categoryRepository = mock(CategoryRepository.class);
            this.inventoryRepository = mock(InventoryRepository.class);
            this.productImageRepository = mock(ProductImageRepository.class);
            this.productRepository = mock(ProductRepository.class);
            this.userRepository = mock(UserRepository.class);

            this.productManagementService = new ProductManagementService(
                    this.categoryRepository,
                    this.inventoryRepository,
                    this.productImageRepository,
                    this.productRepository,
                    this.userRepository
            );
        }
    }
}
