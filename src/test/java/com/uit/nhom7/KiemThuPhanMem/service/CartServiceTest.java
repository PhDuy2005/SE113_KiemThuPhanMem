package com.uit.nhom7.KiemThuPhanMem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqAddCartItemDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateCartItemDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCartItemActionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Cart;
import com.uit.nhom7.KiemThuPhanMem.domain.table.CartItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.CartItemRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.CartRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

class CartServiceTest {
    @Test
    void addItemShouldCreateCartAndAddProductWhenStockIsEnough() {
        Fixture fixture = new Fixture();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        User user = activeUser(userId);
        Product product = activeProduct(productId);
        Cart cart = Cart.builder().id(cartId).user(user).build();

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.productRepository.findByIdAndStatusIgnoreCase(productId, Product.ACTIVE_STATUS))
                .thenReturn(Optional.of(product));
        when(fixture.inventoryRepository.findById(productId))
                .thenReturn(Optional.of(Inventory.builder()
                        .productId(productId)
                        .quantity(10)
                        .reservedQuantity(2)
                        .build()));
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(fixture.cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(fixture.cartItemRepository.findByCartIdAndProductId(cartId, productId)).thenReturn(Optional.empty());
        when(fixture.cartItemRepository.save(any(CartItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(fixture.cartItemRepository.getTotalItemsCount(cartId)).thenReturn(3);

        ResCartItemActionDTO result = fixture.cartService.addItem(ReqAddCartItemDTO.builder()
                .productId(productId)
                .quantity(3)
                .build());

        assertThat(result.getCartId()).isEqualTo(cartId);
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getQuantity()).isEqualTo(3);
        assertThat(result.getCartBadgeCount()).isEqualTo(3);
        assertThat(result.getAvailableQuantity()).isEqualTo(8);
        SecurityContextHolder.clearContext();
    }

    @Test
    void addItemShouldRejectWhenExistingQuantityWouldExceedStock() {
        Fixture fixture = new Fixture();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        User user = activeUser(userId);
        Product product = activeProduct(productId);
        Cart cart = Cart.builder().id(cartId).user(user).build();
        CartItem existingItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(7)
                .build();

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.productRepository.findByIdAndStatusIgnoreCase(productId, Product.ACTIVE_STATUS))
                .thenReturn(Optional.of(product));
        when(fixture.inventoryRepository.findById(productId))
                .thenReturn(Optional.of(Inventory.builder()
                        .productId(productId)
                        .quantity(8)
                        .reservedQuantity(0)
                        .build()));
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(fixture.cartItemRepository.findByCartIdAndProductId(cartId, productId))
                .thenReturn(Optional.of(existingItem));

        assertThatThrownBy(() -> fixture.cartService.addItem(ReqAddCartItemDTO.builder()
                .productId(productId)
                .quantity(2)
                .build()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Insufficient stock. Available quantity: 8");
        SecurityContextHolder.clearContext();
    }

    @Test
    void addItemShouldRequireLogin() {
        Fixture fixture = new Fixture();
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> fixture.cartService.addItem(ReqAddCartItemDTO.builder()
                .productId(UUID.randomUUID())
                .quantity(1)
                .build()))
                .isInstanceOf(BusinessException.class)
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void updateItemQuantityShouldSetNewQuantityAndRecalculateTotal() {
        Fixture fixture = new Fixture();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        User user = activeUser(userId);
        Product product = activeProduct(productId);
        Cart cart = Cart.builder().id(cartId).user(user).build();
        CartItem existingItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(fixture.cartItemRepository.findByCartIdAndProductId(cartId, productId))
                .thenReturn(Optional.of(existingItem));
        when(fixture.inventoryRepository.findById(productId))
                .thenReturn(Optional.of(Inventory.builder()
                        .productId(productId)
                        .quantity(10)
                        .reservedQuantity(0)
                        .build()));
        when(fixture.cartItemRepository.save(existingItem)).thenReturn(existingItem);
        when(fixture.cartItemRepository.getTotalItemsCount(cartId)).thenReturn(5);
        when(fixture.cartItemRepository.findByCartIdWithProduct(cartId)).thenReturn(List.of(existingItem));

        ResCartItemActionDTO result = fixture.cartService.updateItemQuantity(
                productId,
                ReqUpdateCartItemDTO.builder().newQuantity(5).oldQuantity(2).build());

        assertThat(result.getQuantity()).isEqualTo(5);
        assertThat(result.getCartBadgeCount()).isEqualTo(5);
        assertThat(result.getTotalCartPrice()).isEqualByComparingTo(BigDecimal.valueOf(50));
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateItemQuantityShouldAdjustToAvailableQuantityAndRejectWhenExceedsStock() {
        Fixture fixture = new Fixture();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        User user = activeUser(userId);
        Product product = activeProduct(productId);
        Cart cart = Cart.builder().id(cartId).user(user).build();
        CartItem existingItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(fixture.cartItemRepository.findByCartIdAndProductId(cartId, productId))
                .thenReturn(Optional.of(existingItem));
        when(fixture.inventoryRepository.findById(productId))
                .thenReturn(Optional.of(Inventory.builder()
                        .productId(productId)
                        .quantity(3)
                        .reservedQuantity(0)
                        .build()));

        assertThatThrownBy(() -> fixture.cartService.updateItemQuantity(
                productId,
                ReqUpdateCartItemDTO.builder().newQuantity(5).oldQuantity(2).build()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Insufficient stock. Available quantity: 3");
        assertThat(existingItem.getQuantity()).isEqualTo(3);
        SecurityContextHolder.clearContext();
    }

    @Test
    void removeItemShouldDeleteCartItemAndRecalculateCart() {
        Fixture fixture = new Fixture();
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        User user = activeUser(userId);
        Product product = activeProduct(productId);
        Cart cart = Cart.builder().id(cartId).user(user).build();
        CartItem existingItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(fixture.cartItemRepository.findByCartIdAndProductId(cartId, productId))
                .thenReturn(Optional.of(existingItem));
        when(fixture.inventoryRepository.findById(productId))
                .thenReturn(Optional.of(Inventory.builder()
                        .productId(productId)
                        .quantity(10)
                        .reservedQuantity(0)
                        .build()));
        when(fixture.cartItemRepository.getTotalItemsCount(cartId)).thenReturn(0);
        when(fixture.cartItemRepository.findByCartIdWithProduct(cartId)).thenReturn(List.of());

        ResCartItemActionDTO result = fixture.cartService.removeItem(productId);

        assertThat(result.getQuantity()).isZero();
        assertThat(result.getCartBadgeCount()).isZero();
        assertThat(result.getTotalCartPrice()).isEqualByComparingTo(BigDecimal.ZERO);
        SecurityContextHolder.clearContext();
    }

    private void authenticate(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null));
    }

    private User activeUser(UUID userId) {
        return User.builder()
                .id(userId)
                .email("customer@example.com")
                .password("encoded")
                .accountStatus("ACTIVE")
                .build();
    }

    private Product activeProduct(UUID productId) {
        return Product.builder()
                .id(productId)
                .name("Product")
                .price(BigDecimal.TEN)
                .status(Product.ACTIVE_STATUS)
                .build();
    }

    private static class Fixture {
        private final CartRepository cartRepository = Mockito.mock(CartRepository.class);
        private final CartItemRepository cartItemRepository = Mockito.mock(CartItemRepository.class);
        private final InventoryRepository inventoryRepository = Mockito.mock(InventoryRepository.class);
        private final ProductRepository productRepository = Mockito.mock(ProductRepository.class);
        private final UserRepository userRepository = Mockito.mock(UserRepository.class);
        private final CartService cartService = new CartService(
                cartRepository,
                cartItemRepository,
                inventoryRepository,
                productRepository,
                userRepository);
    }
}
