package com.uit.nhom7.KiemThuPhanMem.service.cart_service;

import static org.mockito.Mockito.mock;

import com.uit.nhom7.KiemThuPhanMem.repository.CartItemRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.CartRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductImageRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.service.CartService;

public abstract class CartServiceTestBase {

    protected static class Fixture {
        public final CartRepository cartRepository;
        public final CartItemRepository cartItemRepository;
        public final InventoryRepository inventoryRepository;
        public final ProductImageRepository productImageRepository;
        public final ProductRepository productRepository;
        public final UserRepository userRepository;
        public final CartService cartService;

        public Fixture() {
            this.cartRepository = mock(CartRepository.class);
            this.cartItemRepository = mock(CartItemRepository.class);
            this.inventoryRepository = mock(InventoryRepository.class);
            this.productImageRepository = mock(ProductImageRepository.class);
            this.productRepository = mock(ProductRepository.class);
            this.userRepository = mock(UserRepository.class);

            this.cartService = new CartService(
                    this.cartRepository,
                    this.cartItemRepository,
                    this.inventoryRepository,
                    this.productImageRepository,
                    this.productRepository,
                    this.userRepository
            );
        }
    }
}
