package com.uit.nhom7.KiemThuPhanMem.service.cart_service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.table.Cart;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00018_clearCart extends CartServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

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
    @DisplayName("FUNC00018_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.cartService.clearCart())
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00018_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.cartService.clearCart())
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00018_UTCID03 - Fails because user is not ACTIVE")
    void TC3_NotActive() {
        User user = User.builder().id(userId).accountStatus("PENDING").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.cartService.clearCart())
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00018_UTCID04 - Fails because user does not have a cart")
    void TC4_CartNotFound() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.cartService.clearCart())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cart not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00018_UTCID05 - Clears the cart successfully")
    void TC5_Success() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Cart cart = Cart.builder().id(cartId).build();
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        fixture.cartService.clearCart();

        verify(fixture.cartItemRepository).deleteByCartId(cartId);
    }
}
