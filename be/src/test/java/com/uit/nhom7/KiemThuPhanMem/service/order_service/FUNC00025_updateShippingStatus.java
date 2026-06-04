package com.uit.nhom7.KiemThuPhanMem.service.order_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateShippingStatusDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderDetailDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Notification;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Order;
import com.uit.nhom7.KiemThuPhanMem.domain.table.OrderItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Payment;
import com.uit.nhom7.KiemThuPhanMem.domain.table.PaymentMethod;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00025_updateShippingStatus extends OrderServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    private final UUID staffId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final UUID customerId = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private final UUID orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID paymentId = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private final UUID productId = UUID.fromString("66666666-6666-6666-6666-666666666666");

    @BeforeEach
    void setUp() {
        fixture = new Fixture();
        mockedSecurityUtil = mockStatic(SecurityUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtil.close();
    }

    private User getCustomer() {
        return User.builder()
                .id(customerId)
                .userFullName("Alice")
                .phoneNumber("0901234567")
                .build();
    }

    private Order getOrder(String status, String trackingNumber) {
        return Order.builder()
                .id(orderId)
                .status(status)
                .user(getCustomer())
                .totalProductAmount(BigDecimal.valueOf(100000))
                .shippingFee(BigDecimal.valueOf(30000))
                .discountAmount(BigDecimal.valueOf(10000))
                .totalAmount(BigDecimal.valueOf(120000))
                .shippingAddressSnapshot("D1, HCM")
                .trackingNumber(trackingNumber)
                .createdAt(Instant.parse("2026-05-29T10:00:00Z"))
                .updatedAt(Instant.parse("2026-05-29T10:00:00Z"))
                .build();
    }

    private void setupOrderItemMock() {
        Product product = Product.builder().id(productId).name("Laptop").build();
        OrderItem item = OrderItem.builder()
                .product(product)
                .price(BigDecimal.valueOf(100000))
                .quantity(1)
                .build();
        when(fixture.orderItemRepository.findByOrderIdWithProduct(orderId)).thenReturn(List.of(item));
    }

    @Test
    @DisplayName("FUNC00025_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        ReqUpdateShippingStatusDTO req = new ReqUpdateShippingStatusDTO();
        req.setTrackingNumber("TRACK123");

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.orderService.updateShippingStatus(orderId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00025_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        ReqUpdateShippingStatusDTO req = new ReqUpdateShippingStatusDTO();
        req.setTrackingNumber("TRACK123");

        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.orderService.updateShippingStatus(orderId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00025_UTCID03 - Fails because user accountStatus is null")
    void TC3_StatusNull() {
        ReqUpdateShippingStatusDTO req = new ReqUpdateShippingStatusDTO();
        req.setTrackingNumber("TRACK123");

        User staff = User.builder().id(staffId).accountStatus(null).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> fixture.orderService.updateShippingStatus(orderId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00025_UTCID04 - Fails because user is not ACTIVE")
    void TC4_NotActive() {
        ReqUpdateShippingStatusDTO req = new ReqUpdateShippingStatusDTO();
        req.setTrackingNumber("TRACK123");

        User staff = User.builder().id(staffId).accountStatus("INACTIVE").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> fixture.orderService.updateShippingStatus(orderId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00025_UTCID05 - Fails because user role is null")
    void TC5_RoleNull() {
        ReqUpdateShippingStatusDTO req = new ReqUpdateShippingStatusDTO();
        req.setTrackingNumber("TRACK123");

        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(null).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> fixture.orderService.updateShippingStatus(orderId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only staff or business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00025_UTCID06 - Fails because user role name is null")
    void TC6_RoleNameNull() {
        ReqUpdateShippingStatusDTO req = new ReqUpdateShippingStatusDTO();
        req.setTrackingNumber("TRACK123");

        Role role = Role.builder().name(null).build();
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(role).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> fixture.orderService.updateShippingStatus(orderId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only staff or business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00025_UTCID07 - Fails because user is CUSTOMER")
    void TC7_RoleCustomer() {
        ReqUpdateShippingStatusDTO req = new ReqUpdateShippingStatusDTO();
        req.setTrackingNumber("TRACK123");

        Role role = Role.builder().name("CUSTOMER").build();
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(role).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> fixture.orderService.updateShippingStatus(orderId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only staff or business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00025_UTCID08 - Fails because trackingNumber is null")
    void TC8_NullTracking() {
        ReqUpdateShippingStatusDTO req = new ReqUpdateShippingStatusDTO();
        req.setTrackingNumber(null);

        Role role = Role.builder().name("STAFF").build();
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(role).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> fixture.orderService.updateShippingStatus(orderId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Tracking number is required")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00025_UTCID09 - Fails because trackingNumber is blank")
    void TC9_BlankTracking() {
        ReqUpdateShippingStatusDTO req = new ReqUpdateShippingStatusDTO();
        req.setTrackingNumber("   ");

        Role role = Role.builder().name("STAFF").build();
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(role).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> fixture.orderService.updateShippingStatus(orderId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Tracking number is required")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00025_UTCID10 - Fails because order not found")
    void TC10_OrderNotFound() {
        ReqUpdateShippingStatusDTO req = new ReqUpdateShippingStatusDTO();
        req.setTrackingNumber("TRACK123");

        Role role = Role.builder().name("STAFF").build();
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(role).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        when(fixture.orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.orderService.updateShippingStatus(orderId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Order not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00025_UTCID11 - Fails because order status is PENDING")
    void TC11_OrderPending() {
        ReqUpdateShippingStatusDTO req = new ReqUpdateShippingStatusDTO();
        req.setTrackingNumber("TRACK123");

        Role role = Role.builder().name("STAFF").build();
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(role).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        Order order = getOrder("PENDING", null);
        when(fixture.orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> fixture.orderService.updateShippingStatus(orderId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only approved orders can be moved to shipping")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00025_UTCID12 - Success (updates from APPROVED, no payment info)")
    void TC12_SuccessAPPROVED() {
        ReqUpdateShippingStatusDTO req = new ReqUpdateShippingStatusDTO();
        req.setTrackingNumber("TRACK123");

        Role role = Role.builder().name("STAFF").build();
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(role).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        Order order = getOrder("APPROVED", null);
        when(fixture.orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order savedOrder = getOrder("SHIPPING", "TRACK123");
        savedOrder.setUpdatedAt(Instant.parse("2026-05-29T10:10:00Z"));
        when(fixture.orderRepository.save(order)).thenReturn(savedOrder);
        
        when(fixture.paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        setupOrderItemMock();

        ResOrderDetailDTO result = fixture.orderService.updateShippingStatus(orderId, req);

        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getStatus()).isEqualTo("SHIPPING");
        assertThat(result.getTrackingNumber()).isEqualTo("TRACK123");
        assertThat(result.getPaymentMethod()).isNull();
        assertThat(result.getPaymentStatus()).isNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getLineTotal().doubleValue()).isEqualTo(100000.0);
        assertThat(result.getMessage()).isEqualTo("Shipping status updated successfully");

        verify(fixture.notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("FUNC00025_UTCID13 - Success (updates from SHIPPING, has payment info)")
    void TC13_SuccessSHIPPING() {
        ReqUpdateShippingStatusDTO req = new ReqUpdateShippingStatusDTO();
        req.setTrackingNumber("TRACK123");

        Role role = Role.builder().name("STAFF").build();
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(role).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        Order order = getOrder("SHIPPING", "OLDTRACK");
        order.setUpdatedAt(Instant.parse("2026-05-29T10:05:00Z"));
        when(fixture.orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order savedOrder = getOrder("SHIPPING", "TRACK123");
        savedOrder.setUpdatedAt(Instant.parse("2026-05-29T10:10:00Z"));
        when(fixture.orderRepository.save(order)).thenReturn(savedOrder);
        
        PaymentMethod pm = PaymentMethod.builder().name("Cash on Delivery").build();
        Payment payment = Payment.builder().id(paymentId).status("PENDING").paymentMethod(pm).build();
        when(fixture.paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));
        
        setupOrderItemMock();

        ResOrderDetailDTO result = fixture.orderService.updateShippingStatus(orderId, req);

        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getStatus()).isEqualTo("SHIPPING");
        assertThat(result.getTrackingNumber()).isEqualTo("TRACK123");
        assertThat(result.getPaymentMethod()).isEqualTo("Cash on Delivery");
        assertThat(result.getPaymentStatus()).isEqualTo("PENDING");
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getMessage()).isEqualTo("Shipping status updated successfully");

        verify(fixture.notificationRepository).save(any(Notification.class));
    }
}
