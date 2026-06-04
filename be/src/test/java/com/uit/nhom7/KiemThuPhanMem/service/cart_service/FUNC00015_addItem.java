package com.uit.nhom7.KiemThuPhanMem.service.cart_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqAddCartItemDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCartItemActionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Cart;
import com.uit.nhom7.KiemThuPhanMem.domain.table.CartItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.CartItemId;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00015_addItem extends CartServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    private final UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID userId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final UUID cartId = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
        mockedSecurityUtil = mockStatic(SecurityUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtil.close();
    }

    private User getActiveUser() {
        return User.builder()
                .id(userId)
                .email("user@example.com")
                .accountStatus("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("FUNC00015_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        ReqAddCartItemDTO request = new ReqAddCartItemDTO();
        request.setProductId(productId);
        request.setQuantity(10);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.cartService.addItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00015_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        ReqAddCartItemDTO request = new ReqAddCartItemDTO();
        request.setProductId(productId);
        request.setQuantity(10);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.cartService.addItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00015_UTCID03 - Fails because user is not ACTIVE")
    void TC3_NotActive() {
        ReqAddCartItemDTO request = new ReqAddCartItemDTO();
        request.setProductId(productId);
        request.setQuantity(10);

        User user = User.builder().id(userId).accountStatus("PENDING").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.cartService.addItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00015_UTCID04 - Fails because active product does not exist")
    void TC4_ProductNotFound() {
        ReqAddCartItemDTO request = new ReqAddCartItemDTO();
        request.setProductId(productId);
        request.setQuantity(10);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        when(fixture.productRepository.findByIdAndStatusIgnoreCase(productId, Product.ACTIVE_STATUS))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.cartService.addItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Product not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00015_UTCID05 - Fails because requested quantity exceeds stock")
    void TC5_InsufficientStock() {
        ReqAddCartItemDTO request = new ReqAddCartItemDTO();
        request.setProductId(productId);
        request.setQuantity(10);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Product product = Product.builder().id(productId).build();
        when(fixture.productRepository.findByIdAndStatusIgnoreCase(productId, Product.ACTIVE_STATUS))
                .thenReturn(Optional.of(product));
        
        Inventory inventory = Inventory.builder().quantity(5).reservedQuantity(0).build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        
        Cart cart = Cart.builder().id(cartId).build();
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        
        CartItem cartItem = CartItem.builder().quantity(0).build();
        when(fixture.cartItemRepository.findByCartIdAndProductId(cartId, productId)).thenReturn(Optional.of(cartItem));

        assertThatThrownBy(() -> fixture.cartService.addItem(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Insufficient stock. Available quantity: 5")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00015_UTCID06 - Adds item successfully when cart and cartItem do not exist")
    void TC6_SuccessEmptyDBs() {
        ReqAddCartItemDTO request = new ReqAddCartItemDTO();
        request.setProductId(productId);
        request.setQuantity(0);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Product product = Product.builder().id(productId).build();
        when(fixture.productRepository.findByIdAndStatusIgnoreCase(productId, Product.ACTIVE_STATUS))
                .thenReturn(Optional.of(product));
        
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.empty());
        
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        Cart cart = Cart.builder().id(cartId).build();
        when(fixture.cartRepository.save(any(Cart.class))).thenReturn(cart);
        
        when(fixture.cartItemRepository.findByCartIdAndProductId(cartId, productId)).thenReturn(Optional.empty());
        
        CartItem savedCartItem = CartItem.builder()
                .id(new CartItemId(cartId, productId))
                .cart(cart)
                .product(product)
                .quantity(0)
                .build();
        when(fixture.cartItemRepository.save(any(CartItem.class))).thenReturn(savedCartItem);
        
        when(fixture.cartItemRepository.getTotalItemsCount(cartId)).thenReturn(0);
        when(fixture.cartItemRepository.findByCartIdWithProduct(cartId)).thenReturn(List.of());

        ResCartItemActionDTO result = fixture.cartService.addItem(request);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Product added to cart successfully");
        assertThat(result.getCartId()).isEqualTo(cartId);
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getQuantity()).isEqualTo(0);
        assertThat(result.getCartBadgeCount()).isEqualTo(0);
        assertThat(result.getAvailableQuantity()).isEqualTo(0);
        assertThat(result.getTotalCartPrice().doubleValue()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("FUNC00015_UTCID07 - Adds item successfully when cart and cartItem already exist")
    void TC7_SuccessPresentDBs() {
        ReqAddCartItemDTO request = new ReqAddCartItemDTO();
        request.setProductId(productId);
        request.setQuantity(10);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Product product = Product.builder().id(productId).price(BigDecimal.valueOf(100.0)).build();
        when(fixture.productRepository.findByIdAndStatusIgnoreCase(productId, Product.ACTIVE_STATUS))
                .thenReturn(Optional.of(product));
        
        Inventory inventory = Inventory.builder().quantity(20).reservedQuantity(0).build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        
        Cart cart = Cart.builder().id(cartId).build();
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        
        CartItem cartItem = CartItem.builder().quantity(5).build();
        when(fixture.cartItemRepository.findByCartIdAndProductId(cartId, productId)).thenReturn(Optional.of(cartItem));
        
        CartItem savedCartItem = CartItem.builder()
                .quantity(15) // 5 + 10
                .build();
        when(fixture.cartItemRepository.save(any(CartItem.class))).thenReturn(savedCartItem);
        
        when(fixture.cartItemRepository.getTotalItemsCount(cartId)).thenReturn(1);
        
        CartItem cartItemWithProduct = CartItem.builder()
                .quantity(15)
                .product(product)
                .build();
        when(fixture.cartItemRepository.findByCartIdWithProduct(cartId)).thenReturn(List.of(cartItemWithProduct));

        ResCartItemActionDTO result = fixture.cartService.addItem(request);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Product added to cart successfully");
        assertThat(result.getCartId()).isEqualTo(cartId);
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getQuantity()).isEqualTo(15);
        assertThat(result.getCartBadgeCount()).isEqualTo(1);
        assertThat(result.getAvailableQuantity()).isEqualTo(20);
        assertThat(result.getTotalCartPrice().doubleValue()).isEqualTo(1500.0);
    }
}
