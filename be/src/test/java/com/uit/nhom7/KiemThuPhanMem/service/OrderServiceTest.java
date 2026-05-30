package com.uit.nhom7.KiemThuPhanMem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqUpdateShippingStatusDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderDetailDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Notification;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Order;
import com.uit.nhom7.KiemThuPhanMem.domain.table.OrderItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Payment;
import com.uit.nhom7.KiemThuPhanMem.domain.table.PaymentMethod;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.InventoryRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.NotificationRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderItemRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.PaymentRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ShippingAddressRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

class OrderServiceTest {
    @Test
    void approveOrderShouldRequireLogin() {
        Fixture fixture = new Fixture();
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> fixture.service.approveOrder(fixture.orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void approveOrderShouldRejectWhenUserSessionIsInvalid() {
        Fixture fixture = new Fixture();
        authenticate("missing@example.com");
        when(fixture.userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.approveOrder(fixture.orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        fixture.clearAuth();
    }

    @Test
    void approveOrderShouldRejectWhenAccountStatusIsNull() {
        Fixture fixture = new Fixture();
        User user = userWithRole("staff@example.com", null, "STAFF");

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.service.approveOrder(fixture.orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
        fixture.clearAuth();
    }

    @Test
    void approveOrderShouldRejectWhenUserIsCustomer() {
        Fixture fixture = new Fixture();
        User user = userWithRole("customer@example.com", "ACTIVE", "CUSTOMER");

        authenticate(user.getEmail());
        when(fixture.userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.service.approveOrder(fixture.orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only staff or business admin can perform this action")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
        fixture.clearAuth();
    }

    @Test
    void approveOrderShouldRejectWhenOrderDoesNotExist() {
        Fixture fixture = new Fixture();
        stubStaff(fixture);
        when(fixture.orderRepository.findById(fixture.orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.approveOrder(fixture.orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Order not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
        fixture.clearAuth();
    }

    @Test
    void approveOrderShouldRejectWhenOrderIsNotPending() {
        Fixture fixture = new Fixture();
        stubStaff(fixture);
        fixture.order.setStatus(Order.APPROVED_STATUS);
        when(fixture.orderRepository.findById(fixture.orderId)).thenReturn(Optional.of(fixture.order));

        assertThatThrownBy(() -> fixture.service.approveOrder(fixture.orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only pending orders can be approved")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void approveOrderShouldApproveOrderSuccessfully() {
        Fixture fixture = new Fixture();
        stubStaff(fixture);
        when(fixture.orderRepository.findById(fixture.orderId)).thenReturn(Optional.of(fixture.order));
        when(fixture.orderRepository.save(fixture.order)).thenAnswer(invocation -> invocation.getArgument(0));
        when(fixture.paymentRepository.findByOrderId(fixture.orderId)).thenReturn(Optional.of(fixture.payment));

        ResOrderDTO result = fixture.service.approveOrder(fixture.orderId);

        assertThat(result.getOrderId()).isEqualTo(fixture.orderId);
        assertThat(result.getStatus()).isEqualTo(Order.APPROVED_STATUS);
        assertThat(result.getPaymentMethodName()).isEqualTo("Cash on Delivery");
        assertThat(result.getPaymentStatus()).isEqualTo(Payment.PENDING_STATUS);
        assertThat(result.getMessage()).isEqualTo("Order approved successfully");
        verify(fixture.notificationRepository).save(any(Notification.class));
        fixture.clearAuth();
    }

    @Test
    void updateShippingStatusShouldRejectWhenTrackingNumberIsBlank() {
        Fixture fixture = new Fixture();
        stubStaff(fixture);

        assertThatThrownBy(() -> fixture.service.updateShippingStatus(fixture.orderId, requestWithTracking("   ")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Tracking number is required")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void updateShippingStatusShouldRejectWhenOrderDoesNotExist() {
        Fixture fixture = new Fixture();
        stubStaff(fixture);
        when(fixture.orderRepository.findById(fixture.orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.updateShippingStatus(fixture.orderId, requestWithTracking("TRACK123")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Order not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
        fixture.clearAuth();
    }

    @Test
    void updateShippingStatusShouldRejectWhenOrderIsPending() {
        Fixture fixture = new Fixture();
        stubStaff(fixture);
        when(fixture.orderRepository.findById(fixture.orderId)).thenReturn(Optional.of(fixture.order));

        assertThatThrownBy(() -> fixture.service.updateShippingStatus(fixture.orderId, requestWithTracking("TRACK123")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only approved orders can be moved to shipping")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void updateShippingStatusShouldUpdateApprovedOrderSuccessfully() {
        Fixture fixture = new Fixture();
        stubStaff(fixture);
        fixture.order.setStatus(Order.APPROVED_STATUS);
        when(fixture.orderRepository.findById(fixture.orderId)).thenReturn(Optional.of(fixture.order));
        when(fixture.orderRepository.save(fixture.order)).thenAnswer(invocation -> invocation.getArgument(0));
        when(fixture.paymentRepository.findByOrderId(fixture.orderId)).thenReturn(Optional.empty());
        when(fixture.orderItemRepository.findByOrderIdWithProduct(fixture.orderId)).thenReturn(List.of(fixture.orderItem));

        ResOrderDetailDTO result = fixture.service.updateShippingStatus(fixture.orderId, requestWithTracking("TRACK123"));

        assertThat(result.getStatus()).isEqualTo(Order.SHIPPING_STATUS);
        assertThat(result.getTrackingNumber()).isEqualTo("TRACK123");
        assertThat(result.getPaymentMethod()).isNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getMessage()).isEqualTo("Shipping status updated successfully");
        fixture.clearAuth();
    }

    @Test
    void updateShippingStatusShouldUpdateShippingOrderSuccessfully() {
        Fixture fixture = new Fixture();
        stubStaff(fixture);
        fixture.order.setStatus(Order.SHIPPING_STATUS);
        fixture.order.setTrackingNumber("OLDTRACK");
        when(fixture.orderRepository.findById(fixture.orderId)).thenReturn(Optional.of(fixture.order));
        when(fixture.orderRepository.save(fixture.order)).thenAnswer(invocation -> invocation.getArgument(0));
        when(fixture.paymentRepository.findByOrderId(fixture.orderId)).thenReturn(Optional.of(fixture.payment));
        when(fixture.orderItemRepository.findByOrderIdWithProduct(fixture.orderId)).thenReturn(List.of(fixture.orderItem));

        ResOrderDetailDTO result = fixture.service.updateShippingStatus(fixture.orderId, requestWithTracking("TRACK123"));

        assertThat(result.getStatus()).isEqualTo(Order.SHIPPING_STATUS);
        assertThat(result.getTrackingNumber()).isEqualTo("TRACK123");
        assertThat(result.getPaymentMethod()).isEqualTo("Cash on Delivery");
        assertThat(result.getPaymentStatus()).isEqualTo(Payment.PENDING_STATUS);
        fixture.clearAuth();
    }

    @Test
    void markOrderDeliveredShouldRejectWhenOrderDoesNotExist() {
        Fixture fixture = new Fixture();
        stubStaff(fixture);
        when(fixture.orderRepository.findById(fixture.orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.markOrderDelivered(fixture.orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Order not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
        fixture.clearAuth();
    }

    @Test
    void markOrderDeliveredShouldRejectWhenOrderIsNotShipping() {
        Fixture fixture = new Fixture();
        stubStaff(fixture);
        fixture.order.setStatus(Order.APPROVED_STATUS);
        when(fixture.orderRepository.findById(fixture.orderId)).thenReturn(Optional.of(fixture.order));

        assertThatThrownBy(() -> fixture.service.markOrderDelivered(fixture.orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only shipping orders can be marked as delivered")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void markOrderDeliveredShouldSucceedWithoutPayment() {
        Fixture fixture = new Fixture();
        stubStaff(fixture);
        fixture.order.setStatus(Order.SHIPPING_STATUS);
        fixture.order.setTrackingNumber("TRACK123");
        when(fixture.orderRepository.findById(fixture.orderId)).thenReturn(Optional.of(fixture.order));
        when(fixture.orderRepository.save(fixture.order)).thenAnswer(invocation -> invocation.getArgument(0));
        when(fixture.paymentRepository.findByOrderId(fixture.orderId)).thenReturn(Optional.empty());
        when(fixture.orderItemRepository.findByOrderIdWithProduct(fixture.orderId)).thenReturn(List.of(fixture.orderItem));

        ResOrderDetailDTO result = fixture.service.markOrderDelivered(fixture.orderId);

        assertThat(result.getStatus()).isEqualTo(Order.DELIVERED_STATUS);
        assertThat(result.getPaymentStatus()).isNull();
        assertThat(result.getMessage()).isEqualTo("Order delivered successfully");
        assertThat(fixture.order.getCompletedAt()).isNotNull();
        fixture.clearAuth();
    }

    @Test
    void markOrderDeliveredShouldUpdatePaymentStatusWhenPaymentExists() {
        Fixture fixture = new Fixture();
        stubStaff(fixture);
        fixture.order.setStatus(Order.SHIPPING_STATUS);
        fixture.order.setTrackingNumber("TRACK123");
        when(fixture.orderRepository.findById(fixture.orderId)).thenReturn(Optional.of(fixture.order));
        when(fixture.orderRepository.save(fixture.order)).thenAnswer(invocation -> invocation.getArgument(0));
        when(fixture.paymentRepository.findByOrderId(fixture.orderId)).thenReturn(Optional.of(fixture.payment));
        when(fixture.paymentRepository.save(fixture.payment)).thenAnswer(invocation -> invocation.getArgument(0));
        when(fixture.orderItemRepository.findByOrderIdWithProduct(fixture.orderId)).thenReturn(List.of(fixture.orderItem));

        ResOrderDetailDTO result = fixture.service.markOrderDelivered(fixture.orderId);

        assertThat(fixture.payment.getStatus()).isEqualTo(Payment.SUCCESS_STATUS);
        assertThat(result.getPaymentStatus()).isEqualTo(Payment.SUCCESS_STATUS);
        assertThat(result.getPaymentMethod()).isEqualTo("Cash on Delivery");
        fixture.clearAuth();
    }

    @Test
    void cancelPendingOrderShouldRequireLogin() {
        Fixture fixture = new Fixture();
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> fixture.service.cancelPendingOrder(fixture.orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void cancelPendingOrderShouldRejectWhenCustomerOrderDoesNotExist() {
        Fixture fixture = new Fixture();
        stubCustomer(fixture);
        when(fixture.orderRepository.findByIdAndUserId(fixture.orderId, fixture.customer.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.service.cancelPendingOrder(fixture.orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Order not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
        fixture.clearAuth();
    }

    @Test
    void cancelPendingOrderShouldRejectWhenOrderIsNotPending() {
        Fixture fixture = new Fixture();
        stubCustomer(fixture);
        fixture.order.setStatus(Order.APPROVED_STATUS);
        when(fixture.orderRepository.findByIdAndUserId(fixture.orderId, fixture.customer.getId()))
                .thenReturn(Optional.of(fixture.order));

        assertThatThrownBy(() -> fixture.service.cancelPendingOrder(fixture.orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Order cannot be cancelled because it has been processed")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
        fixture.clearAuth();
    }

    @Test
    void cancelPendingOrderShouldRestoreInventoryFromNullQuantityAndCancelOrder() {
        Fixture fixture = new Fixture();
        stubCustomer(fixture);
        when(fixture.orderRepository.findByIdAndUserId(fixture.orderId, fixture.customer.getId()))
                .thenReturn(Optional.of(fixture.order));
        when(fixture.orderItemRepository.findByOrderIdWithProduct(fixture.orderId)).thenReturn(List.of(fixture.orderItem));
        fixture.inventory.setQuantity(null);
        when(fixture.inventoryRepository.findById(fixture.productId)).thenReturn(Optional.of(fixture.inventory));
        when(fixture.inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fixture.orderRepository.save(fixture.order)).thenAnswer(invocation -> invocation.getArgument(0));

        ResOrderDTO result = fixture.service.cancelPendingOrder(fixture.orderId);

        assertThat(fixture.inventory.getQuantity()).isEqualTo(2);
        assertThat(result.getStatus()).isEqualTo(Order.CANCELLED_STATUS);
        assertThat(result.getMessage()).isEqualTo("Order cancelled successfully");
        fixture.clearAuth();
    }

    @Test
    void cancelPendingOrderShouldRestoreInventoryAndCancelOrderSuccessfully() {
        Fixture fixture = new Fixture();
        stubCustomer(fixture);
        when(fixture.orderRepository.findByIdAndUserId(fixture.orderId, fixture.customer.getId()))
                .thenReturn(Optional.of(fixture.order));
        when(fixture.orderItemRepository.findByOrderIdWithProduct(fixture.orderId)).thenReturn(List.of(fixture.orderItem));
        when(fixture.inventoryRepository.findById(fixture.productId)).thenReturn(Optional.of(fixture.inventory));
        when(fixture.inventoryRepository.save(any(Inventory.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fixture.orderRepository.save(fixture.order)).thenAnswer(invocation -> invocation.getArgument(0));

        ResOrderDTO result = fixture.service.cancelPendingOrder(fixture.orderId);

        assertThat(fixture.inventory.getQuantity()).isEqualTo(12);
        assertThat(result.getOrderId()).isEqualTo(fixture.orderId);
        assertThat(result.getStatus()).isEqualTo(Order.CANCELLED_STATUS);
        assertThat(result.getTotalAmount()).isEqualByComparingTo("120000");
        fixture.clearAuth();
    }

    private static void stubStaff(Fixture fixture) {
        authenticate(fixture.staff.getEmail());
        when(fixture.userRepository.findByEmail(fixture.staff.getEmail())).thenReturn(Optional.of(fixture.staff));
    }

    private static void stubCustomer(Fixture fixture) {
        authenticate(fixture.customer.getEmail());
        when(fixture.userRepository.findByEmail(fixture.customer.getEmail())).thenReturn(Optional.of(fixture.customer));
    }

    private static ReqUpdateShippingStatusDTO requestWithTracking(String tracking) {
        ReqUpdateShippingStatusDTO request = new ReqUpdateShippingStatusDTO();
        request.setTrackingNumber(tracking);
        return request;
    }

    private static void authenticate(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null));
    }

    private static User userWithRole(String email, String accountStatus, String roleName) {
        return User.builder()
                .id(UUID.randomUUID())
                .email(email)
                .userFullName("Alice")
                .phoneNumber("0901234567")
                .password("encoded")
                .accountStatus(accountStatus)
                .role(roleName == null ? null : Role.builder().name(roleName).build())
                .build();
    }

    private static class Fixture {
        private final InventoryRepository inventoryRepository = Mockito.mock(InventoryRepository.class);
        private final NotificationRepository notificationRepository = Mockito.mock(NotificationRepository.class);
        private final OrderItemRepository orderItemRepository = Mockito.mock(OrderItemRepository.class);
        private final OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
        private final PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
        private final ShippingAddressRepository shippingAddressRepository = Mockito.mock(ShippingAddressRepository.class);
        private final ShippingFeeConfigService shippingFeeConfigService = Mockito.mock(ShippingFeeConfigService.class);
        private final UserRepository userRepository = Mockito.mock(UserRepository.class);
        private final EmailService emailService = Mockito.mock(EmailService.class);
        private final OrderService service = new OrderService(
                inventoryRepository,
                notificationRepository,
                orderItemRepository,
                orderRepository,
                paymentRepository,
                shippingAddressRepository,
                shippingFeeConfigService,
                userRepository,
                emailService);

        private final UUID orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        private final UUID productId = UUID.fromString("66666666-6666-6666-6666-666666666666");
        private final User orderOwner = userWithRole("owner@example.com", "ACTIVE", "CUSTOMER");
        private final User staff = userWithRole("staff@example.com", "ACTIVE", "STAFF");
        private final User customer = userWithRole("user@example.com", "ACTIVE", "CUSTOMER");
        private final Product product = Product.builder()
                .id(productId)
                .name("Laptop")
                .price(new BigDecimal("100000"))
                .status(Product.ACTIVE_STATUS)
                .build();
        private final Order order = Order.builder()
                .id(orderId)
                .user(orderOwner)
                .status(Order.PENDING_STATUS)
                .totalProductAmount(new BigDecimal("100000"))
                .shippingFee(new BigDecimal("30000"))
                .discountAmount(new BigDecimal("10000"))
                .totalAmount(new BigDecimal("120000"))
                .shippingAddressSnapshot("D1, HCM")
                .build();
        private final OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .price(new BigDecimal("100000"))
                .quantity(2)
                .build();
        private final Payment payment = Payment.builder()
                .id(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                .order(order)
                .paymentMethod(PaymentMethod.builder()
                        .name("Cash on Delivery")
                        .type(PaymentMethod.CASH_TYPE)
                        .build())
                .status(Payment.PENDING_STATUS)
                .build();
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
