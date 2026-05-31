package com.uit.nhom7.KiemThuPhanMem.service.order_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Notification;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Order;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Payment;
import com.uit.nhom7.KiemThuPhanMem.domain.table.PaymentMethod;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00024_approveOrder extends OrderServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    private final UUID staffId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final UUID customerId = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private final UUID orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final UUID paymentId = UUID.fromString("22222222-2222-2222-2222-222222222222");

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
                .build();
    }

    private Order getPendingOrder() {
        return Order.builder()
                .id(orderId)
                .status("PENDING")
                .user(getCustomer())
                .totalProductAmount(BigDecimal.valueOf(100000))
                .shippingFee(BigDecimal.valueOf(30000))
                .discountAmount(BigDecimal.valueOf(10000))
                .totalAmount(BigDecimal.valueOf(120000))
                .build();
    }

    @Test
    @DisplayName("FUNC00024_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.orderService.approveOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00024_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.orderService.approveOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00024_UTCID03 - Fails because user accountStatus is null")
    void TC3_StatusNull() {
        User staff = User.builder().id(staffId).accountStatus(null).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> fixture.orderService.approveOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00024_UTCID04 - Fails because user is not ACTIVE")
    void TC4_NotActive() {
        User staff = User.builder().id(staffId).accountStatus("INACTIVE").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> fixture.orderService.approveOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00024_UTCID05 - Fails because user role is null")
    void TC5_RoleNull() {
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(null).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> fixture.orderService.approveOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only staff or business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00024_UTCID06 - Fails because user role name is null")
    void TC6_RoleNameNull() {
        Role role = Role.builder().name(null).build();
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(role).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> fixture.orderService.approveOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only staff or business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00024_UTCID07 - Fails because user is CUSTOMER")
    void TC7_RoleCustomer() {
        Role role = Role.builder().name("CUSTOMER").build();
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(role).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> fixture.orderService.approveOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only staff or business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00024_UTCID08 - Fails because order not found")
    void TC8_OrderNotFound() {
        Role role = Role.builder().name("STAFF").build();
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(role).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        when(fixture.orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.orderService.approveOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Order not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00024_UTCID09 - Fails because order status is not PENDING")
    void TC9_OrderNotPending() {
        Role role = Role.builder().name("STAFF").build();
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(role).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        Order order = getPendingOrder();
        order.setStatus("APPROVED");
        when(fixture.orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> fixture.orderService.approveOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only pending orders can be approved")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00024_UTCID10 - Success (order approved, payment is null)")
    void TC10_SuccessNoPayment() {
        Role role = Role.builder().name("STAFF").build();
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(role).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        Order order = getPendingOrder();
        when(fixture.orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order savedOrder = getPendingOrder();
        savedOrder.setStatus("APPROVED");
        when(fixture.orderRepository.save(order)).thenReturn(savedOrder);
        when(fixture.paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        ResOrderDTO result = fixture.orderService.approveOrder(orderId);

        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getCustomerId()).isEqualTo(customerId);
        assertThat(result.getCustomerName()).isEqualTo("Alice");
        assertThat(result.getStatus()).isEqualTo("APPROVED");
        assertThat(result.getTotalProductAmount().doubleValue()).isEqualTo(100000.0);
        assertThat(result.getShippingFee().doubleValue()).isEqualTo(30000.0);
        assertThat(result.getDiscountAmount().doubleValue()).isEqualTo(10000.0);
        assertThat(result.getTotalAmount().doubleValue()).isEqualTo(120000.0);
        assertThat(result.getPaymentId()).isNull();
        assertThat(result.getPaymentMethodName()).isNull();
        assertThat(result.getPaymentStatus()).isNull();
        assertThat(result.getMessage()).isEqualTo("Order approved successfully");

        verify(fixture.notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("FUNC00024_UTCID11 - Success (order approved, paymentMethod is null)")
    void TC11_SuccessNoPaymentMethod() {
        Role role = Role.builder().name("BUSINESS_ADMIN").build();
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(role).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        Order order = getPendingOrder();
        when(fixture.orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order savedOrder = getPendingOrder();
        savedOrder.setStatus("APPROVED");
        when(fixture.orderRepository.save(order)).thenReturn(savedOrder);

        Payment payment = Payment.builder().id(paymentId).status("PENDING").paymentMethod(null).build();
        when(fixture.paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

        ResOrderDTO result = fixture.orderService.approveOrder(orderId);

        assertThat(result).isNotNull();
        assertThat(result.getPaymentId()).isEqualTo(paymentId);
        assertThat(result.getPaymentMethodName()).isNull();
        assertThat(result.getPaymentStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("FUNC00024_UTCID12 - Success (order approved, paymentMethod is Cash on Delivery)")
    void TC12_SuccessPMNormal() {
        Role role = Role.builder().name("STAFF").build();
        User staff = User.builder().id(staffId).accountStatus("ACTIVE").role(role).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("staff@example.com"));
        when(fixture.userRepository.findByEmail("staff@example.com")).thenReturn(Optional.of(staff));

        Order order = getPendingOrder();
        when(fixture.orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order savedOrder = getPendingOrder();
        savedOrder.setStatus("APPROVED");
        when(fixture.orderRepository.save(order)).thenReturn(savedOrder);

        PaymentMethod pm = PaymentMethod.builder().name("Cash on Delivery").build();
        Payment payment = Payment.builder().id(paymentId).status("PENDING").paymentMethod(pm).build();
        when(fixture.paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

        ResOrderDTO result = fixture.orderService.approveOrder(orderId);

        assertThat(result).isNotNull();
        assertThat(result.getPaymentId()).isEqualTo(paymentId);
        assertThat(result.getPaymentMethodName()).isEqualTo("Cash on Delivery");
        assertThat(result.getPaymentStatus()).isEqualTo("PENDING");
    }
}
