package com.uit.nhom7.KiemThuPhanMem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqApplyVoucherDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCheckoutSelectionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqConfirmOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResCheckoutSelectionDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResVoucherApplicationDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Cart;
import com.uit.nhom7.KiemThuPhanMem.domain.table.CartItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Order;
import com.uit.nhom7.KiemThuPhanMem.domain.table.OrderItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Payment;
import com.uit.nhom7.KiemThuPhanMem.domain.table.PaymentMethod;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.ShippingAddress;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Voucher;
import com.uit.nhom7.KiemThuPhanMem.repository.CartItemRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.CartRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderItemRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.PaymentMethodRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.PaymentRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ShippingAddressRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.VoucherRepository;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

class CheckoutServiceTest {
    @Test
    void calculateSelectionShouldRequireLogin() {
        Fixture fixture = new Fixture();
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> fixture.service.calculateSelection(selectionRequest(List.of(UUID.randomUUID()))))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void calculateSelectionShouldRejectWhenUserSessionIsInvalid() {
        Fixture fixture = new Fixture();
        authenticate("missing@example.com");
        when(fixture.userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.calculateSelection(selectionRequest(List.of(UUID.randomUUID()))))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        SecurityContextHolder.clearContext();
    }

    @Test
    void calculateSelectionShouldRejectWhenUserIsInactive() {
        Fixture fixture = new Fixture();
        User user = user("user@example.com", "PENDING");

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.service.calculateSelection(selectionRequest(List.of(UUID.randomUUID()))))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
        SecurityContextHolder.clearContext();
    }

    @Test
    void calculateSelectionShouldRejectWhenCartDoesNotExist() {
        Fixture fixture = new Fixture();
        User user = activeUser();

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.cartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.calculateSelection(selectionRequest(List.of(UUID.randomUUID()))))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cart not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
        SecurityContextHolder.clearContext();
    }

    @Test
    void calculateSelectionShouldRejectWhenSelectionIsEmpty() {
        Fixture fixture = new Fixture();
        User user = activeUser();
        Cart cart = cartFor(user);

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> fixture.service.calculateSelection(selectionRequest(List.of())))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must select at least one product")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        SecurityContextHolder.clearContext();
    }

    @Test
    void calculateSelectionShouldRejectWhenSelectedProductsAreInvalid() {
        Fixture fixture = new Fixture();
        User user = activeUser();
        Cart cart = cartFor(user);
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(fixture.cartItemRepository.findSelectedByCartIdWithProduct(cart.getId(), List.of(productId1, productId2)))
                .thenReturn(List.of(cartItem(cart, product(productId1, "P1", "100"), 2)));

        assertThatThrownBy(() -> fixture.service.calculateSelection(selectionRequest(List.of(productId1, productId2))))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Selected products are invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        SecurityContextHolder.clearContext();
    }

    @Test
    void calculateSelectionShouldReturnTempTotalSuccessfully() {
        Fixture fixture = new Fixture();
        User user = activeUser();
        Cart cart = cartFor(user);
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        List<UUID> selectedIds = List.of(productId1, productId2);

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(fixture.cartItemRepository.findSelectedByCartIdWithProduct(cart.getId(), selectedIds))
                .thenReturn(List.of(
                        cartItem(cart, product(productId1, "P1", "100"), 2),
                        cartItem(cart, product(productId2, "P2", "50"), 1)));

        ResCheckoutSelectionDTO result = fixture.service.calculateSelection(selectionRequest(selectedIds));

        assertThat(result.getSelectedProductIds()).containsExactlyElementsOf(selectedIds);
        assertThat(result.getTempTotalPrice()).isEqualByComparingTo("250");
        assertThat(result.getShippingFee()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getTotalPrice()).isEqualByComparingTo("250");
        assertThat(result.getCheckoutUrl()).isEqualTo("/checkout");
        SecurityContextHolder.clearContext();
    }

    @Test
    void applyVoucherShouldRequireLogin() {
        Fixture fixture = new Fixture();
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> fixture.service.applyVoucher(applyVoucherRequest("VOUCHER10", "100000")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void applyVoucherShouldRejectWhenSessionIsInvalid() {
        Fixture fixture = new Fixture();
        authenticate("missing@example.com");
        when(fixture.userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.applyVoucher(applyVoucherRequest("VOUCHER10", "100000")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        SecurityContextHolder.clearContext();
    }

    @Test
    void applyVoucherShouldRejectWhenAccountStatusIsNull() {
        Fixture fixture = new Fixture();
        User user = user("user@example.com", null);

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.service.applyVoucher(applyVoucherRequest("VOUCHER10", "100000")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
        SecurityContextHolder.clearContext();
    }

    @Test
    void applyVoucherShouldRejectWhenVoucherDoesNotExist() {
        Fixture fixture = new Fixture();
        User user = activeUser();

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.applyVoucher(applyVoucherRequest("VOUCHER10", "100000")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        SecurityContextHolder.clearContext();
    }

    @Test
    void applyVoucherShouldRejectWhenVoucherIsInactive() {
        Fixture fixture = new Fixture();
        User user = activeUser();
        Voucher voucher = activeVoucher("VOUCHER10", Voucher.PERCENT_TYPE, "10");
        voucher.setActive(false);

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        assertThatThrownBy(() -> fixture.service.applyVoucher(applyVoucherRequest("VOUCHER10", "100000")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher");
        SecurityContextHolder.clearContext();
    }

    @Test
    void applyVoucherShouldRejectWhenVoucherStartDateIsInFuture() {
        Fixture fixture = new Fixture();
        User user = activeUser();
        Voucher voucher = activeVoucher("VOUCHER10", Voucher.PERCENT_TYPE, "10");
        voucher.setStartDate(Instant.now().plus(1, ChronoUnit.DAYS));

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        assertThatThrownBy(() -> fixture.service.applyVoucher(applyVoucherRequest("VOUCHER10", "100000")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher");
        SecurityContextHolder.clearContext();
    }

    @Test
    void applyVoucherShouldRejectWhenVoucherEndDateIsInPast() {
        Fixture fixture = new Fixture();
        User user = activeUser();
        Voucher voucher = activeVoucher("VOUCHER10", Voucher.PERCENT_TYPE, "10");
        voucher.setEndDate(Instant.now().minus(1, ChronoUnit.DAYS));

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        assertThatThrownBy(() -> fixture.service.applyVoucher(applyVoucherRequest("VOUCHER10", "100000")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher");
        SecurityContextHolder.clearContext();
    }

    @Test
    void applyVoucherShouldRejectWhenVoucherUsageIsReached() {
        Fixture fixture = new Fixture();
        User user = activeUser();
        Voucher voucher = activeVoucher("VOUCHER10", Voucher.PERCENT_TYPE, "10");
        voucher.setMaxUsage(100);
        voucher.setUsedCount(100);

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        assertThatThrownBy(() -> fixture.service.applyVoucher(applyVoucherRequest("VOUCHER10", "100000")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher");
        SecurityContextHolder.clearContext();
    }

    @Test
    void applyVoucherShouldRejectWhenOrderAmountIsBelowMinimum() {
        Fixture fixture = new Fixture();
        User user = activeUser();
        Voucher voucher = activeVoucher("VOUCHER10", Voucher.PERCENT_TYPE, "10");
        voucher.setMinOrderAmount(new BigDecimal("200000"));

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        assertThatThrownBy(() -> fixture.service.applyVoucher(applyVoucherRequest("VOUCHER10", "100000")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher");
        SecurityContextHolder.clearContext();
    }

    @Test
    void applyVoucherShouldReturnZeroDiscountForPercentVoucherWithNullValue() {
        Fixture fixture = new Fixture();
        User user = activeUser();
        Voucher voucher = activeVoucher("VOUCHER10", Voucher.PERCENT_TYPE, null);

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        ResVoucherApplicationDTO result = fixture.service.applyVoucher(applyVoucherRequest("VOUCHER10", "100000"));

        assertThat(result.getVoucherCode()).isEqualTo("VOUCHER10");
        assertThat(result.getDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getFinalTotal()).isEqualByComparingTo("100000");
        assertThat(result.getMessage()).isEqualTo("Voucher applied successfully");
        SecurityContextHolder.clearContext();
    }

    @Test
    void applyVoucherShouldApplyPercentVoucherSuccessfully() {
        Fixture fixture = new Fixture();
        User user = activeUser();
        Voucher voucher = activeVoucher("VOUCHER10", Voucher.PERCENT_TYPE, "10");

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        ResVoucherApplicationDTO result = fixture.service.applyVoucher(applyVoucherRequest("VOUCHER10", "100000"));

        assertThat(result.getDiscountAmount()).isEqualByComparingTo("10000.00");
        assertThat(result.getFinalTotal()).isEqualByComparingTo("90000.00");
        SecurityContextHolder.clearContext();
    }

    @Test
    void applyVoucherShouldApplyFixedVoucherSuccessfully() {
        Fixture fixture = new Fixture();
        User user = activeUser();
        Voucher voucher = activeVoucher("VOUCHER10", Voucher.FIXED_TYPE, "20000");

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        ResVoucherApplicationDTO result = fixture.service.applyVoucher(applyVoucherRequest("VOUCHER10", "100000"));

        assertThat(result.getDiscountAmount()).isEqualByComparingTo("20000");
        assertThat(result.getFinalTotal()).isEqualByComparingTo("80000");
        SecurityContextHolder.clearContext();
    }

    @Test
    void applyVoucherShouldRejectWhenVoucherTypeIsInvalid() {
        Fixture fixture = new Fixture();
        User user = activeUser();
        Voucher voucher = activeVoucher("VOUCHER10", "UNKNOWN", "20000");

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        assertThatThrownBy(() -> fixture.service.applyVoucher(applyVoucherRequest("VOUCHER10", "100000")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher");
        SecurityContextHolder.clearContext();
    }

    @Test
    void confirmOrderShouldRequireLogin() {
        Fixture fixture = new Fixture();
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> fixture.service.confirmOrder(confirmOrderRequest(List.of(UUID.randomUUID()), "VOUCHER10")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void confirmOrderShouldRejectWhenCartDoesNotExist() {
        Fixture fixture = new Fixture();
        User user = activeUser();

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.cartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.confirmOrder(confirmOrderRequest(List.of(UUID.randomUUID()), null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cart not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
        SecurityContextHolder.clearContext();
    }

    @Test
    void confirmOrderShouldRejectWhenSelectedIdsAreNull() {
        Fixture fixture = new Fixture();
        User user = activeUser();
        Cart cart = cartFor(user);

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> fixture.service.confirmOrder(confirmOrderRequest(null, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must select at least one product")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        SecurityContextHolder.clearContext();
    }

    @Test
    void confirmOrderShouldRejectWhenSelectedProductsAreInvalid() {
        Fixture fixture = new Fixture();
        User user = activeUser();
        Cart cart = cartFor(user);
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(fixture.cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(fixture.cartItemRepository.findSelectedByCartIdWithProduct(cart.getId(), List.of(productId1, productId2)))
                .thenReturn(List.of(cartItem(cart, product(productId1, "P1", "100000"), 1)));

        assertThatThrownBy(() -> fixture.service.confirmOrder(confirmOrderRequest(List.of(productId1, productId2), null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Selected products are invalid");
        SecurityContextHolder.clearContext();
    }

    @Test
    void confirmOrderShouldRejectWhenShippingAddressDoesNotExist() {
        Fixture fixture = readyForConfirmOrder();
        ReqConfirmOrderDTO request = confirmOrderRequest(List.of(fixture.productId), null);

        when(fixture.shippingAddressRepository.findByIdAndUserIdAndDeletedAtIsNull(
                fixture.shippingAddressId, fixture.user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.confirmOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Shipping address not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
        fixture.clearAuth();
    }

    @Test
    void confirmOrderShouldRejectWhenPaymentMethodIsInvalid() {
        Fixture fixture = readyForConfirmOrder();
        ReqConfirmOrderDTO request = confirmOrderRequest(List.of(fixture.productId), null);
        stubShippingAddress(fixture);
        when(fixture.paymentMethodRepository.findById(fixture.paymentMethodId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.confirmOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid payment method")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void confirmOrderShouldRejectWhenPaymentMethodIsNotCash() {
        Fixture fixture = readyForConfirmOrder();
        ReqConfirmOrderDTO request = confirmOrderRequest(List.of(fixture.productId), null);
        stubShippingAddress(fixture);
        when(fixture.paymentMethodRepository.findById(fixture.paymentMethodId))
                .thenReturn(Optional.of(paymentMethod(fixture.paymentMethodId, "Credit Card", "CREDIT_CARD")));

        assertThatThrownBy(() -> fixture.service.confirmOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only cash payment is supported at the moment")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void confirmOrderShouldRejectWhenStockIsInsufficientDuringValidation() {
        Fixture fixture = readyForConfirmOrder();
        ReqConfirmOrderDTO request = confirmOrderRequest(List.of(fixture.productId), null);
        stubShippingAddress(fixture);
        stubCashPaymentMethod(fixture);
        when(fixture.inventoryRepository.findById(fixture.productId))
                .thenReturn(Optional.of(Inventory.builder()
                        .productId(fixture.productId)
                        .quantity(0)
                        .reservedQuantity(0)
                        .build()));

        assertThatThrownBy(() -> fixture.service.confirmOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Insufficient stock. Please check your cart again")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void confirmOrderShouldCreateOrderSuccessfullyWithoutVoucher() {
        Fixture fixture = readyForConfirmOrder();
        ReqConfirmOrderDTO request = confirmOrderRequest(List.of(fixture.productId), null);
        stubSuccessfulConfirmFlowWithoutVoucher(fixture);

        ResOrderDTO result = fixture.service.confirmOrder(request);

        assertThat(result.getOrderId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Order.PENDING_STATUS);
        assertThat(result.getTotalProductAmount()).isEqualByComparingTo("100000");
        assertThat(result.getShippingFee()).isEqualByComparingTo("30000");
        assertThat(result.getDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getTotalAmount()).isEqualByComparingTo("130000");
        assertThat(result.getPaymentMethodName()).isEqualTo("Cash on Delivery");
        assertThat(result.getPaymentStatus()).isEqualTo(Payment.PENDING_STATUS);
        assertThat(result.getMessage()).isEqualTo("Order created successfully");
        fixture.clearAuth();
    }

    @Test
    void confirmOrderShouldCreateOrderSuccessfullyWhenVoucherCodeIsBlank() {
        Fixture fixture = readyForConfirmOrder();
        ReqConfirmOrderDTO request = confirmOrderRequest(List.of(fixture.productId), "   ");
        stubSuccessfulConfirmFlowWithoutVoucher(fixture);

        ResOrderDTO result = fixture.service.confirmOrder(request);

        assertThat(result.getDiscountAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getTotalAmount()).isEqualByComparingTo("130000");
        fixture.clearAuth();
    }

    @Test
    void confirmOrderShouldRejectWhenVoucherIsInvalid() {
        Fixture fixture = readyForConfirmOrder();
        ReqConfirmOrderDTO request = confirmOrderRequest(List.of(fixture.productId), "VOUCHER10");
        stubShippingAddress(fixture);
        stubCashPaymentMethod(fixture);
        when(fixture.inventoryRepository.findById(fixture.productId)).thenReturn(Optional.of(fixture.inventory));
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.confirmOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher");
        fixture.clearAuth();
    }

    @Test
    void confirmOrderShouldCreateOrderSuccessfullyWithPercentVoucher() {
        Fixture fixture = readyForConfirmOrder();
        ReqConfirmOrderDTO request = confirmOrderRequest(List.of(fixture.productId), "VOUCHER10");
        Voucher voucher = activeVoucher("VOUCHER10", Voucher.PERCENT_TYPE, "10");
        voucher.setUsedCount(null);
        stubSuccessfulConfirmFlowWithVoucher(fixture, voucher);

        ResOrderDTO result = fixture.service.confirmOrder(request);

        assertThat(voucher.getUsedCount()).isEqualTo(1);
        assertThat(result.getDiscountAmount()).isEqualByComparingTo("10000.00");
        assertThat(result.getTotalAmount()).isEqualByComparingTo("120000.00");
        fixture.clearAuth();
    }

    @Test
    void confirmOrderShouldCreateOrderSuccessfullyWithFixedVoucher() {
        Fixture fixture = readyForConfirmOrder();
        ReqConfirmOrderDTO request = confirmOrderRequest(List.of(fixture.productId), "VOUCHER10");
        Voucher voucher = activeVoucher("VOUCHER10", Voucher.FIXED_TYPE, "20000");
        voucher.setUsedCount(5);
        stubSuccessfulConfirmFlowWithVoucher(fixture, voucher);

        ResOrderDTO result = fixture.service.confirmOrder(request);

        assertThat(voucher.getUsedCount()).isEqualTo(6);
        assertThat(result.getDiscountAmount()).isEqualByComparingTo("20000");
        assertThat(result.getTotalAmount()).isEqualByComparingTo("110000");
        fixture.clearAuth();
    }

    @Test
    void confirmOrderShouldRejectWhenInventoryIsMissingDuringDeduction() {
        Fixture fixture = readyForConfirmOrder();
        ReqConfirmOrderDTO request = confirmOrderRequest(List.of(fixture.productId), null);
        stubShippingAddress(fixture);
        stubCashPaymentMethod(fixture);
        when(fixture.inventoryRepository.findById(fixture.productId))
                .thenReturn(Optional.of(fixture.inventory))
                .thenReturn(Optional.empty());
        when(fixture.shippingFeeConfigService.getShippingFeeForProvince("79", "HCM"))
                .thenReturn(new BigDecimal("30000"));

        assertThatThrownBy(() -> fixture.service.confirmOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Insufficient stock. Please check your cart again");
        fixture.clearAuth();
    }

    @Test
    void confirmOrderShouldContinueWhenEmailSendingFails() {
        Fixture fixture = readyForConfirmOrder();
        ReqConfirmOrderDTO request = confirmOrderRequest(List.of(fixture.productId), "VOUCHER10");
        Voucher voucher = activeVoucher("VOUCHER10", Voucher.PERCENT_TYPE, "10");
        stubSuccessfulConfirmFlowWithVoucher(fixture, voucher);
        Mockito.doThrow(new RuntimeException("smtp down"))
                .when(fixture.emailService)
                .sendOrderConfirmation(any(), any(), any(), any(), any(), any(), any());

        ResOrderDTO result = fixture.service.confirmOrder(request);

        assertThat(result.getStatus()).isEqualTo(Order.PENDING_STATUS);
        assertThat(result.getTotalAmount()).isEqualByComparingTo("120000.00");
        fixture.clearAuth();
    }

    private static Fixture readyForConfirmOrder() {
        Fixture fixture = new Fixture();
        authenticate(fixture.user.getEmail());
        when(fixture.userRepository.findByEmail(fixture.user.getEmail())).thenReturn(Optional.of(fixture.user));
        when(fixture.cartRepository.findByUserId(fixture.user.getId())).thenReturn(Optional.of(fixture.cart));
        when(fixture.cartItemRepository.findSelectedByCartIdWithProduct(fixture.cart.getId(), List.of(fixture.productId)))
                .thenReturn(List.of(fixture.selectedItem));
        return fixture;
    }

    private static void stubShippingAddress(Fixture fixture) {
        when(fixture.shippingAddressRepository.findByIdAndUserIdAndDeletedAtIsNull(
                fixture.shippingAddressId, fixture.user.getId())).thenReturn(Optional.of(fixture.shippingAddress));
    }

    private static void stubCashPaymentMethod(Fixture fixture) {
        when(fixture.paymentMethodRepository.findById(fixture.paymentMethodId))
                .thenReturn(Optional.of(paymentMethod(fixture.paymentMethodId, "Cash on Delivery", PaymentMethod.CASH_TYPE)));
    }

    private static void stubSuccessfulConfirmFlowWithoutVoucher(Fixture fixture) {
        stubShippingAddress(fixture);
        stubCashPaymentMethod(fixture);
        when(fixture.inventoryRepository.findById(fixture.productId))
                .thenReturn(Optional.of(fixture.inventory))
                .thenReturn(Optional.of(fixture.inventory));
        when(fixture.inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fixture.shippingFeeConfigService.getShippingFeeForProvince("79", "HCM"))
                .thenReturn(new BigDecimal("30000"));
        when(fixture.orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(UUID.randomUUID());
            order.setStatus(Order.PENDING_STATUS);
            return order;
        });
        when(fixture.orderItemRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(fixture.paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(UUID.randomUUID());
            payment.setStatus(Payment.PENDING_STATUS);
            return payment;
        });
    }

    private static void stubSuccessfulConfirmFlowWithVoucher(Fixture fixture, Voucher voucher) {
        stubSuccessfulConfirmFlowWithoutVoucher(fixture);
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));
        when(fixture.voucherRepository.save(any(Voucher.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private static void authenticate(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null));
    }

    private static User activeUser() {
        return user("user@example.com", "ACTIVE");
    }

    private static User user(String email, String accountStatus) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .userFullName("Customer")
                .password("encoded")
                .accountStatus(accountStatus)
                .build();
    }

    private static Cart cartFor(User user) {
        return Cart.builder()
                .id(UUID.randomUUID())
                .user(user)
                .build();
    }

    private static Product product(UUID id, String name, String price) {
        return Product.builder()
                .id(id)
                .name(name)
                .price(new BigDecimal(price))
                .status(Product.ACTIVE_STATUS)
                .build();
    }

    private static CartItem cartItem(Cart cart, Product product, int quantity) {
        return CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .build();
    }

    private static ReqCheckoutSelectionDTO selectionRequest(List<UUID> productIds) {
        return ReqCheckoutSelectionDTO.builder()
                .selectedProductIds(productIds)
                .build();
    }

    private static ReqApplyVoucherDTO applyVoucherRequest(String voucherCode, String totalAmount) {
        return ReqApplyVoucherDTO.builder()
                .voucherCode(voucherCode)
                .totalOrderAmount(new BigDecimal(totalAmount))
                .build();
    }

    private static ReqConfirmOrderDTO confirmOrderRequest(List<UUID> productIds, String voucherCode) {
        return ReqConfirmOrderDTO.builder()
                .selectedProductIds(productIds)
                .shippingAddressId(Fixture.SHIPPING_ADDRESS_ID)
                .paymentMethodId(Fixture.PAYMENT_METHOD_ID)
                .voucherCode(voucherCode)
                .build();
    }

    private static PaymentMethod paymentMethod(UUID id, String name, String type) {
        return PaymentMethod.builder()
                .id(id)
                .name(name)
                .type(type)
                .build();
    }

    private static ShippingAddress shippingAddress(User user) {
        return ShippingAddress.builder()
                .id(Fixture.SHIPPING_ADDRESS_ID)
                .user(user)
                .province("HCM")
                .provinceCode("79")
                .ward("W1")
                .detail("D1")
                .build();
    }

    private static Voucher activeVoucher(String code, String type, String value) {
        return Voucher.builder()
                .id(UUID.randomUUID())
                .code(code)
                .type(type)
                .value(value == null ? null : new BigDecimal(value))
                .active(true)
                .startDate(Instant.now().minus(1, ChronoUnit.DAYS))
                .endDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .minOrderAmount(new BigDecimal("50000"))
                .maxUsage(100)
                .usedCount(0)
                .build();
    }

    private static class Fixture {
        private static final UUID PRODUCT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
        private static final UUID SHIPPING_ADDRESS_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
        private static final UUID PAYMENT_METHOD_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

        private final CartRepository cartRepository = Mockito.mock(CartRepository.class);
        private final CartItemRepository cartItemRepository = Mockito.mock(CartItemRepository.class);
        private final InventoryRepository inventoryRepository = Mockito.mock(InventoryRepository.class);
        private final OrderItemRepository orderItemRepository = Mockito.mock(OrderItemRepository.class);
        private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
        private final PaymentMethodRepository paymentMethodRepository = Mockito.mock(PaymentMethodRepository.class);
        private final PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
        private final ShippingAddressRepository shippingAddressRepository = Mockito.mock(ShippingAddressRepository.class);
        private final ShippingFeeConfigService shippingFeeConfigService = Mockito.mock(ShippingFeeConfigService.class);
        private final UserRepository userRepository = Mockito.mock(UserRepository.class);
        private final VoucherRepository voucherRepository = Mockito.mock(VoucherRepository.class);
        private final EmailService emailService = Mockito.mock(EmailService.class);
        private final CheckoutService service = new CheckoutService(
                cartRepository,
                cartItemRepository,
                inventoryRepository,
                orderItemRepository,
                orderRepository,
                paymentMethodRepository,
                paymentRepository,
                shippingAddressRepository,
                shippingFeeConfigService,
                userRepository,
                voucherRepository,
                emailService);

        private final User user = activeUser();
        private final UUID productId = PRODUCT_ID;
        private final UUID shippingAddressId = SHIPPING_ADDRESS_ID;
        private final UUID paymentMethodId = PAYMENT_METHOD_ID;
        private final Cart cart = cartFor(user);
        private final Product product = product(productId, "Product", "100000");
        private final CartItem selectedItem = cartItem(cart, product, 1);
        private final ShippingAddress shippingAddress = shippingAddress(user);
        private final Inventory inventory = Inventory.builder()
                .productId(productId)
                .quantity(10)
                .reservedQuantity(0)
                .build();

        private void clearAuth() {
            SecurityContextHolder.clearContext();
        }
    }
}
