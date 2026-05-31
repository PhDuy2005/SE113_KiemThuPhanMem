package com.uit.nhom7.KiemThuPhanMem.service.checkout_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqConfirmOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Cart;
import com.uit.nhom7.KiemThuPhanMem.domain.table.CartItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Order;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Payment;
import com.uit.nhom7.KiemThuPhanMem.domain.table.PaymentMethod;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.ShippingAddress;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Voucher;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00023_confirmOrder extends CheckoutServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    private final UUID userId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final UUID cartId = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private final UUID productId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID productId2 = UUID.fromString("99999999-9999-9999-9999-999999999999");
    private final UUID shippingAddressId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID paymentMethodId = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private final UUID orderId = UUID.fromString("66666666-6666-6666-6666-666666666666");
    private final UUID paymentId = UUID.fromString("77777777-7777-7777-7777-777777777777");
    private final UUID voucherId = UUID.fromString("88888888-8888-8888-8888-888888888888");

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
                .userFullName("Test User")
                .accountStatus("ACTIVE")
                .build();
    }
    
    private ReqConfirmOrderDTO createValidRequest() {
        ReqConfirmOrderDTO req = new ReqConfirmOrderDTO();
        req.setSelectedProductIds(List.of(productId));
        req.setShippingAddressId(shippingAddressId);
        req.setPaymentMethodId(paymentMethodId);
        req.setVoucherCode("VOUCHER10");
        return req;
    }

    private void setupValidBasicMocks(ReqConfirmOrderDTO req) {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        
        Cart cart = Cart.builder().id(cartId).build();
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        
        Product product = Product.builder().id(productId).price(BigDecimal.valueOf(100000)).name("Product 1").build();
        CartItem cartItem = CartItem.builder().product(product).quantity(1).build();
        when(fixture.cartItemRepository.findSelectedByCartIdWithProduct(cartId, req.getSelectedProductIds()))
                .thenReturn(List.of(cartItem));
                
        ShippingAddress address = ShippingAddress.builder()
                .id(shippingAddressId)
                .provinceCode("79")
                .province("HCM")
                .ward("W1")
                .detail("D1")
                .build();
        when(fixture.shippingAddressRepository.findByIdAndUserIdAndDeletedAtIsNull(shippingAddressId, userId))
                .thenReturn(Optional.of(address));
                
        PaymentMethod paymentMethod = PaymentMethod.builder()
                .id(paymentMethodId)
                .type("CASH")
                .name("Cash on Delivery")
                .build();
        when(fixture.paymentMethodRepository.findById(paymentMethodId))
                .thenReturn(Optional.of(paymentMethod));
    }

    private void setupSuccessFinalMocks() {
        when(fixture.shippingFeeConfigService.getShippingFeeForProvince(eq("79"), eq("HCM")))
                .thenReturn(BigDecimal.valueOf(30000));
                
        Order order = Order.builder().id(orderId).status("PENDING").build();
        when(fixture.orderRepository.save(any(Order.class))).thenReturn(order);
        
        Payment payment = Payment.builder().id(paymentId).status("PENDING").build();
        when(fixture.paymentRepository.save(any(Payment.class))).thenReturn(payment);
    }

    @Test
    @DisplayName("FUNC00023_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        ReqConfirmOrderDTO req = createValidRequest();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.checkoutService.confirmOrder(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00023_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        ReqConfirmOrderDTO req = createValidRequest();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.checkoutService.confirmOrder(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00023_UTCID03 - Fails because user accountStatus is null")
    void TC3_StatusNull() {
        ReqConfirmOrderDTO req = createValidRequest();
        User user = User.builder().id(userId).accountStatus(null).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.checkoutService.confirmOrder(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00023_UTCID04 - Fails because user is not ACTIVE")
    void TC4_NotActive() {
        ReqConfirmOrderDTO req = createValidRequest();
        User user = User.builder().id(userId).accountStatus("INACTIVE").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.checkoutService.confirmOrder(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00023_UTCID05 - Fails because cart is not found")
    void TC5_NoCart() {
        ReqConfirmOrderDTO req = createValidRequest();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.checkoutService.confirmOrder(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cart not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00023_UTCID06 - Fails because selectedProductIds is null")
    void TC6_NullIDs() {
        ReqConfirmOrderDTO req = createValidRequest();
        req.setSelectedProductIds(null);
        
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        Cart cart = Cart.builder().id(cartId).build();
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> fixture.checkoutService.confirmOrder(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must select at least one product")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00023_UTCID07 - Fails because selectedProductIds is empty")
    void TC7_EmptyIDs() {
        ReqConfirmOrderDTO req = createValidRequest();
        req.setSelectedProductIds(List.of());
        
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        Cart cart = Cart.builder().id(cartId).build();
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> fixture.checkoutService.confirmOrder(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must select at least one product")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00023_UTCID08 - Fails because selected products mismatch with DB")
    void TC8_InvalidIDs() {
        ReqConfirmOrderDTO req = createValidRequest();
        req.setSelectedProductIds(List.of(productId, productId2));
        
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(getActiveUser()));
        Cart cart = Cart.builder().id(cartId).build();
        when(fixture.cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        
        Product product = Product.builder().id(productId).price(BigDecimal.valueOf(100000)).build();
        CartItem cartItem = CartItem.builder().product(product).quantity(1).build();
        // Return 1 item but requested 2
        when(fixture.cartItemRepository.findSelectedByCartIdWithProduct(cartId, req.getSelectedProductIds()))
                .thenReturn(List.of(cartItem));

        assertThatThrownBy(() -> fixture.checkoutService.confirmOrder(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Selected products are invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00023_UTCID09 - Fails because shipping address not found")
    void TC9_NoShipping() {
        ReqConfirmOrderDTO req = createValidRequest();
        setupValidBasicMocks(req);
        
        when(fixture.shippingAddressRepository.findByIdAndUserIdAndDeletedAtIsNull(shippingAddressId, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.checkoutService.confirmOrder(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Shipping address not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00023_UTCID10 - Fails because payment method not found")
    void TC10_InvalidPM() {
        ReqConfirmOrderDTO req = createValidRequest();
        setupValidBasicMocks(req);
        
        when(fixture.paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.checkoutService.confirmOrder(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid payment method")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00023_UTCID11 - Fails because payment method is not CASH")
    void TC11_NotCash() {
        ReqConfirmOrderDTO req = createValidRequest();
        setupValidBasicMocks(req);
        
        PaymentMethod paymentMethod = PaymentMethod.builder()
                .id(paymentMethodId).type("CREDIT_CARD").name("Credit Card").build();
        when(fixture.paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.of(paymentMethod));

        assertThatThrownBy(() -> fixture.checkoutService.confirmOrder(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only cash payment is supported at the moment")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00023_UTCID12 - Fails because stock is insufficient in validateStock")
    void TC12_NoStock() {
        ReqConfirmOrderDTO req = createValidRequest();
        setupValidBasicMocks(req);
        
        Inventory inventory = Inventory.builder().productId(productId).quantity(0).reservedQuantity(0).build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory)); // availableQuantity = 0

        assertThatThrownBy(() -> fixture.checkoutService.confirmOrder(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Insufficient stock. Please check your cart again")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00023_UTCID13 - Success without voucher (voucherCode is null)")
    void TC13_NoVoucher() {
        ReqConfirmOrderDTO req = createValidRequest();
        req.setVoucherCode(null);
        setupValidBasicMocks(req);
        
        Inventory inventory = Inventory.builder().productId(productId).quantity(10).reservedQuantity(0).build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        
        setupSuccessFinalMocks();

        ResOrderDTO res = fixture.checkoutService.confirmOrder(req);

        assertThat(res).isNotNull();
        assertThat(res.getOrderId()).isEqualTo(orderId);
        assertThat(res.getStatus()).isEqualTo("PENDING");
        assertThat(res.getTotalProductAmount().doubleValue()).isEqualTo(100000.0);
        assertThat(res.getShippingFee().doubleValue()).isEqualTo(30000.0);
        assertThat(res.getDiscountAmount().doubleValue()).isEqualTo(0.0);
        assertThat(res.getTotalAmount().doubleValue()).isEqualTo(130000.0); // 100k + 30k
        assertThat(res.getPaymentId()).isEqualTo(paymentId);
        assertThat(res.getPaymentMethodName()).isEqualTo("Cash on Delivery");
        assertThat(res.getPaymentStatus()).isEqualTo("PENDING");
        
        verify(fixture.cartItemRepository).deleteByCartIdAndProductIdIn(cartId, req.getSelectedProductIds());
        verify(fixture.emailService).sendOrderConfirmation(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("FUNC00023_UTCID14 - Success without voucher (voucherCode is blank)")
    void TC14_BlankVoucher() {
        ReqConfirmOrderDTO req = createValidRequest();
        req.setVoucherCode("   ");
        setupValidBasicMocks(req);
        
        Inventory inventory = Inventory.builder().productId(productId).quantity(10).reservedQuantity(0).build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        
        setupSuccessFinalMocks();

        ResOrderDTO res = fixture.checkoutService.confirmOrder(req);

        assertThat(res.getTotalAmount().doubleValue()).isEqualTo(130000.0);
    }

    @Test
    @DisplayName("FUNC00023_UTCID15 - Fails because voucher is invalid/not found")
    void TC15_InvalidVoucher() {
        ReqConfirmOrderDTO req = createValidRequest();
        setupValidBasicMocks(req);
        
        Inventory inventory = Inventory.builder().productId(productId).quantity(10).reservedQuantity(0).build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.checkoutService.confirmOrder(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Invalid or expired voucher")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00023_UTCID16 - Success with PERCENT voucher (usedCount was null)")
    void TC16_SuccessPERCENT() {
        ReqConfirmOrderDTO req = createValidRequest();
        setupValidBasicMocks(req);
        
        Inventory inventory = Inventory.builder().productId(productId).quantity(10).reservedQuantity(0).build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        
        Voucher voucher = Voucher.builder()
                .id(voucherId).code("VOUCHER10").type(Voucher.PERCENT_TYPE).value(BigDecimal.valueOf(10))
                .active(true).usedCount(null).build();
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));
        
        setupSuccessFinalMocks();

        ResOrderDTO res = fixture.checkoutService.confirmOrder(req);

        assertThat(res.getDiscountAmount().doubleValue()).isEqualTo(10000.0); // 10% of 100k
        assertThat(res.getTotalAmount().doubleValue()).isEqualTo(120000.0); // 100k + 30k - 10k
        assertThat(voucher.getUsedCount()).isEqualTo(1); // null -> 1
        verify(fixture.voucherRepository).save(voucher);
    }

    @Test
    @DisplayName("FUNC00023_UTCID17 - Success with FIXED voucher (usedCount was not null)")
    void TC17_SuccessFIXED() {
        ReqConfirmOrderDTO req = createValidRequest();
        setupValidBasicMocks(req);
        
        Inventory inventory = Inventory.builder().productId(productId).quantity(10).reservedQuantity(0).build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        
        Voucher voucher = Voucher.builder()
                .id(voucherId).code("VOUCHER10").type(Voucher.FIXED_TYPE).value(BigDecimal.valueOf(20000))
                .active(true).usedCount(5).build();
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));
        
        setupSuccessFinalMocks();

        ResOrderDTO res = fixture.checkoutService.confirmOrder(req);

        assertThat(res.getDiscountAmount().doubleValue()).isEqualTo(20000.0); // 20k fixed
        assertThat(res.getTotalAmount().doubleValue()).isEqualTo(110000.0); // 100k + 30k - 20k
        assertThat(voucher.getUsedCount()).isEqualTo(6); // 5 -> 6
        verify(fixture.voucherRepository).save(voucher);
    }

    @Test
    @DisplayName("FUNC00023_UTCID18 - Fails in deductStock because inventory is empty")
    void TC18_EmptyInvDeductStock() {
        ReqConfirmOrderDTO req = createValidRequest();
        req.setVoucherCode("VOUCHER10");
        setupValidBasicMocks(req);
        setupSuccessFinalMocks();
        
        // Mock validateStock to pass
        Inventory validateInventory = Inventory.builder().productId(productId).quantity(10).reservedQuantity(0).build();
        when(fixture.inventoryRepository.findById(productId))
                .thenReturn(Optional.of(validateInventory)) // 1st call for validateStock passes
                .thenReturn(Optional.empty()); // 2nd call for deductStock fails
        
        Voucher voucher = Voucher.builder()
                .id(voucherId).code("VOUCHER10").type(Voucher.PERCENT_TYPE).value(BigDecimal.valueOf(10))
                .active(true).usedCount(null).build();
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));

        assertThatThrownBy(() -> fixture.checkoutService.confirmOrder(req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Insufficient stock. Please check your cart again")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00023_UTCID19 - Success with inventory quantity being null")
    void TC19_NullStockDeduct() {
        ReqConfirmOrderDTO req = createValidRequest();
        req.setVoucherCode("VOUCHER10");
        setupValidBasicMocks(req);
        setupSuccessFinalMocks();
        
        // Setup voucher mock
        Voucher voucher = Voucher.builder()
                .id(voucherId).code("VOUCHER10").type(Voucher.PERCENT_TYPE).value(BigDecimal.valueOf(10))
                .active(true).usedCount(null).build();
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));
        
        // Mock validateStock to pass (1st call) and deductStock with null quantity (2nd call)
        Inventory validInventory = Inventory.builder().productId(productId).quantity(10).reservedQuantity(0).build();
        Inventory nullQtyInventory = Inventory.builder().productId(productId).quantity(null).reservedQuantity(0).build();
        when(fixture.inventoryRepository.findById(productId))
                .thenReturn(Optional.of(validInventory))
                .thenReturn(Optional.of(nullQtyInventory));

        ResOrderDTO res = fixture.checkoutService.confirmOrder(req);

        assertThat(res).isNotNull();
        // Since original quantity was null (treated as 0), deduct 1 (cart item qty is 1) => 0 - 1 = -1
        verify(fixture.inventoryRepository).save(org.mockito.ArgumentMatchers.argThat(inv -> inv.getQuantity() == -1));
    }

    @Test
    @DisplayName("FUNC00023_UTCID20 - Success but emailService throws RuntimeException")
    void TC20_EmailThrows() {
        ReqConfirmOrderDTO req = createValidRequest();
        setupValidBasicMocks(req);
        
        Inventory inventory = Inventory.builder().productId(productId).quantity(10).reservedQuantity(0).build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        
        Voucher voucher = Voucher.builder()
                .id(voucherId).code("VOUCHER10").type(Voucher.PERCENT_TYPE).value(BigDecimal.valueOf(10))
                .active(true).usedCount(null).build();
        when(fixture.voucherRepository.findByCodeIgnoreCase("VOUCHER10")).thenReturn(Optional.of(voucher));
        
        setupSuccessFinalMocks();
        
        doThrow(new RuntimeException("Mail server down"))
                .when(fixture.emailService)
                .sendOrderConfirmation(any(), any(), any(), any(), any(), any(), any());

        // Should not throw exception, just catch and log
        ResOrderDTO res = fixture.checkoutService.confirmOrder(req);

        assertThat(res).isNotNull();
        assertThat(res.getDiscountAmount().doubleValue()).isEqualTo(10000.0); 
    }
}
