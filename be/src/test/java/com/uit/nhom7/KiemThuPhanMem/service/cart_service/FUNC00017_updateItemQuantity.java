package com.uit.nhom7.KiemThuPhanMem.service.cart_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
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

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateCartItemDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCartItemActionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Cart;
import com.uit.nhom7.KiemThuPhanMem.domain.table.CartItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00017_updateItemQuantity extends CartServiceTestBase {

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
    @DisplayName("FUNC00017_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        ReqUpdateCartItemDTO request = new ReqUpdateCartItemDTO();
        request.setNewQuantity(10);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.cartService.updateItemQuantity(productId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00017_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        ReqUpdateCartItemDTO request = new ReqUpdateCartItemDTO();
        request.setNewQuantity(10);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.cartService.updateItemQuantity(productId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00017_UTCID03 - Fails because user is not ACTIVE")
    void TC3_NotActive() {
        ReqUpdateCartItemDTO request = new ReqUpdateCartItemDTO();
        request.setNewQuantity(10);

        User user = User.builder().id(userId).accountStatus("PENDING").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.cartService.updateItemQuantity(productId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00017_UTCID04 - Fails because user does not have a cart")
    void TC4_CartNotFound() {
        ReqUpdateCartItemDTO request = new ReqUpdateCartItemDTO();
        request.setNewQuantity(10);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.cartService.updateItemQuantity(productId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cart not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00017_UTCID05 - Fails because product is not in the cart")
    void TC5_ItemNotFound() {
        ReqUpdateCartItemDTO request = new ReqUpdateCartItemDTO();
        request.setNewQuantity(10);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Cart cart = Cart.builder().id(cartId).build();
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        
        when(fixture.cartItemRepository.findByCartIdAndProductId(cartId, productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.cartService.updateItemQuantity(productId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cart item not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00017_UTCID06 - Fails due to insufficient stock > 0, auto-updates to max available")
    void TC6_FailAutoUpdate() {
        ReqUpdateCartItemDTO request = new ReqUpdateCartItemDTO();
        request.setNewQuantity(10);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Cart cart = Cart.builder().id(cartId).build();
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        
        CartItem cartItem = CartItem.builder().quantity(5).build();
        when(fixture.cartItemRepository.findByCartIdAndProductId(cartId, productId)).thenReturn(Optional.of(cartItem));
        
        Inventory inventory = Inventory.builder().quantity(5).reservedQuantity(0).build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));

        assertThatThrownBy(() -> fixture.cartService.updateItemQuantity(productId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Insufficient stock. Available quantity: 5")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(fixture.cartItemRepository).save(cartItem);
        assertThat(cartItem.getQuantity()).isEqualTo(5); // Verify it auto-updated the item to the max available stock
    }

    @Test
    @DisplayName("FUNC00017_UTCID07 - Fails due to insufficient stock (0 available), auto-deletes item")
    void TC7_FailAutoDelete() {
        ReqUpdateCartItemDTO request = new ReqUpdateCartItemDTO();
        request.setNewQuantity(10);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Cart cart = Cart.builder().id(cartId).build();
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        
        CartItem cartItem = CartItem.builder().quantity(5).build();
        when(fixture.cartItemRepository.findByCartIdAndProductId(cartId, productId)).thenReturn(Optional.of(cartItem));
        
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.cartService.updateItemQuantity(productId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Insufficient stock. Available quantity: 0")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(fixture.cartItemRepository).delete(cartItem); // Verify it auto-deleted the item
    }

    @Test
    @DisplayName("FUNC00017_UTCID08 - Updates cart item quantity successfully")
    void TC8_Success() {
        ReqUpdateCartItemDTO request = new ReqUpdateCartItemDTO();
        request.setNewQuantity(10);

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Cart cart = Cart.builder().id(cartId).build();
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        
        CartItem cartItem = CartItem.builder().quantity(5).build();
        when(fixture.cartItemRepository.findByCartIdAndProductId(cartId, productId)).thenReturn(Optional.of(cartItem));
        
        Inventory inventory = Inventory.builder().quantity(20).reservedQuantity(0).build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        
        CartItem savedCartItem = CartItem.builder().quantity(10).build();
        when(fixture.cartItemRepository.save(any(CartItem.class))).thenReturn(savedCartItem);
        
        when(fixture.cartItemRepository.getTotalItemsCount(cartId)).thenReturn(1);
        
        Product product = Product.builder().price(BigDecimal.valueOf(100.0)).build();
        CartItem cartItemWithProduct = CartItem.builder().product(product).quantity(10).build();
        when(fixture.cartItemRepository.findByCartIdWithProduct(cartId)).thenReturn(List.of(cartItemWithProduct));

        ResCartItemActionDTO result = fixture.cartService.updateItemQuantity(productId, request);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("Cart item quantity updated successfully");
        assertThat(result.getCartId()).isEqualTo(cartId);
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getQuantity()).isEqualTo(10);
        assertThat(result.getCartBadgeCount()).isEqualTo(1);
        assertThat(result.getAvailableQuantity()).isEqualTo(20);
        assertThat(result.getTotalCartPrice().doubleValue()).isEqualTo(1000.0);
    }
}
