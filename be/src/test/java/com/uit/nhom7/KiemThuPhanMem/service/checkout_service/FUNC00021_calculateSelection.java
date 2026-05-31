package com.uit.nhom7.KiemThuPhanMem.service.checkout_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCheckoutSelectionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCheckoutSelectionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Cart;
import com.uit.nhom7.KiemThuPhanMem.domain.table.CartItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00021_calculateSelection extends CheckoutServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    private final UUID userId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final UUID cartId = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private final UUID product1Id = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID product2Id = UUID.fromString("22222222-2222-2222-2222-222222222222");

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
    @DisplayName("FUNC00021_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        ReqCheckoutSelectionDTO request = new ReqCheckoutSelectionDTO();
        request.setSelectedProductIds(List.of(product1Id, product2Id));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.checkoutService.calculateSelection(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00021_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        ReqCheckoutSelectionDTO request = new ReqCheckoutSelectionDTO();
        request.setSelectedProductIds(List.of(product1Id, product2Id));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.checkoutService.calculateSelection(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00021_UTCID03 - Fails because user is not ACTIVE")
    void TC3_NotActive() {
        ReqCheckoutSelectionDTO request = new ReqCheckoutSelectionDTO();
        request.setSelectedProductIds(List.of(product1Id, product2Id));

        User user = User.builder().id(userId).accountStatus("PENDING").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.checkoutService.calculateSelection(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00021_UTCID04 - Fails because user does not have a cart")
    void TC4_CartNotFound() {
        ReqCheckoutSelectionDTO request = new ReqCheckoutSelectionDTO();
        request.setSelectedProductIds(List.of(product1Id, product2Id));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.checkoutService.calculateSelection(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cart not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00021_UTCID05 - Fails because selectedProductIds is empty or null")
    void TC5_SelectionEmpty() {
        ReqCheckoutSelectionDTO request = new ReqCheckoutSelectionDTO();
        request.setSelectedProductIds(List.of());

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Cart cart = Cart.builder().id(cartId).build();
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> fixture.checkoutService.calculateSelection(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must select at least one product")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00021_UTCID06 - Fails because some product IDs are not in the cart")
    void TC6_InvalidProducts() {
        ReqCheckoutSelectionDTO request = new ReqCheckoutSelectionDTO();
        request.setSelectedProductIds(List.of(product1Id, product2Id));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Cart cart = Cart.builder().id(cartId).build();
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        
        // Mock only returns 1 product, but 2 were selected
        Product product1 = Product.builder().id(product1Id).price(BigDecimal.valueOf(100.0)).build();
        CartItem item1 = CartItem.builder().product(product1).quantity(2).build();
        
        when(fixture.cartItemRepository.findSelectedByCartIdWithProduct(cartId, request.getSelectedProductIds()))
                .thenReturn(List.of(item1));

        assertThatThrownBy(() -> fixture.checkoutService.calculateSelection(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Selected products are invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00021_UTCID07 - Calculates temp total successfully")
    void TC7_Success() {
        ReqCheckoutSelectionDTO request = new ReqCheckoutSelectionDTO();
        request.setSelectedProductIds(List.of(product1Id, product2Id));

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Cart cart = Cart.builder().id(cartId).build();
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        
        Product product1 = Product.builder().id(product1Id).price(BigDecimal.valueOf(100.0)).build();
        CartItem item1 = CartItem.builder().product(product1).quantity(2).build(); // Total 200.0
        
        Product product2 = Product.builder().id(product2Id).price(BigDecimal.valueOf(50.0)).build();
        CartItem item2 = CartItem.builder().product(product2).quantity(1).build(); // Total 50.0
        
        when(fixture.cartItemRepository.findSelectedByCartIdWithProduct(cartId, request.getSelectedProductIds()))
                .thenReturn(List.of(item1, item2));

        ResCheckoutSelectionDTO result = fixture.checkoutService.calculateSelection(request);

        assertThat(result).isNotNull();
        assertThat(result.getSelectedProductIds()).containsExactlyInAnyOrder(product1Id, product2Id);
        assertThat(result.getTempTotalPrice().doubleValue()).isEqualTo(250.0);
        assertThat(result.getShippingFee().doubleValue()).isEqualTo(0.0);
        assertThat(result.getDiscountAmount().doubleValue()).isEqualTo(0.0);
        assertThat(result.getTotalPrice().doubleValue()).isEqualTo(250.0);
        assertThat(result.getCheckoutUrl()).isEqualTo("/checkout");
    }
}
