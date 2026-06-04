package com.uit.nhom7.KiemThuPhanMem.service.order_service;

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
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.springframework.http.HttpStatus;

import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResOrderDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Inventory;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Order;
import com.uit.nhom7.KiemThuPhanMem.domain.table.OrderItem;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Role;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

public class FUNC00027_cancelPendingOrder extends OrderServiceTestBase {

    private MockedStatic<SecurityUtil> mockedSecurityUtil;
    private Fixture fixture;

    private final UUID userId = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private final UUID orderId = UUID.fromString("11111111-1111-1111-1111-111111111111");
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

    private User getUser(String roleName) {
        Role role = roleName == null ? null : Role.builder().name(roleName).build();
        return User.builder()
                .id(userId)
                .accountStatus("ACTIVE")
                .role(role)
                .build();
    }

    private Order getOrder(String status) {
        return Order.builder()
                .id(orderId)
                .status(status)
                .user(getUser("CUSTOMER"))
                .totalProductAmount(BigDecimal.valueOf(100000))
                .shippingFee(BigDecimal.valueOf(30000))
                .discountAmount(BigDecimal.valueOf(10000))
                .totalAmount(BigDecimal.valueOf(120000))
                .build();
    }

    private void setupOrderItemMock() {
        Product product = Product.builder().id(productId).build();
        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(2)
                .build();
        when(fixture.orderItemRepository.findByOrderIdWithProduct(orderId)).thenReturn(List.of(item));
    }

    @Test
    @DisplayName("FUNC00027_UTCID01 - Fails because user is not authenticated")
    void TC1_NoLogin() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.orderService.cancelPendingOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("You must login first")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00027_UTCID02 - Fails because email is not in DB")
    void TC2_NoSession() {
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.orderService.cancelPendingOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User session is invalid")
                .extracting("status")
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("FUNC00027_UTCID03 - Fails because user accountStatus is null")
    void TC3_StatusNull() {
        User user = User.builder().id(userId).accountStatus(null).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.orderService.cancelPendingOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00027_UTCID04 - Fails because user is not ACTIVE")
    void TC4_NotActive() {
        User user = User.builder().id(userId).accountStatus("INACTIVE").build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> fixture.orderService.cancelPendingOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User account is not active")
                .extracting("status")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("FUNC00027_UTCID05 - Fails because role is null and findById is empty")
    void TC5_RoleNullOrderNotFound() {
        User user = getUser(null); // Role is null
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        // Since user is not CUSTOMER (role is null), it will call findById
        when(fixture.orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.orderService.cancelPendingOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Order not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00027_UTCID06 - Fails because role name is null and order status is APPROVED")
    void TC6_RoleNameNullNotPending() {
        User user = User.builder().id(userId).accountStatus("ACTIVE").role(Role.builder().name(null).build()).build();
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        // Since role name is null (not CUSTOMER), it calls findById
        Order order = getOrder("APPROVED");
        when(fixture.orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> fixture.orderService.cancelPendingOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Order cannot be cancelled because it has been processed")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00027_UTCID07 - Success (as STAFF, order PENDING, inventory is empty so new is created)")
    void TC7_SuccessStaffEmptyInv() {
        User user = getUser("STAFF");
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Order order = getOrder("PENDING");
        when(fixture.orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        setupOrderItemMock();
        
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.empty());
        
        Order savedOrder = getOrder("CANCELLED");
        when(fixture.orderRepository.save(order)).thenReturn(savedOrder);

        ResOrderDTO result = fixture.orderService.cancelPendingOrder(orderId);

        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getStatus()).isEqualTo("CANCELLED");
        assertThat(result.getTotalProductAmount().doubleValue()).isEqualTo(100000.0);
        assertThat(result.getShippingFee().doubleValue()).isEqualTo(30000.0);
        assertThat(result.getDiscountAmount().doubleValue()).isEqualTo(10000.0);
        assertThat(result.getTotalAmount().doubleValue()).isEqualTo(120000.0);
        assertThat(result.getMessage()).isEqualTo("Order cancelled successfully");

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(fixture.inventoryRepository).save(inventoryCaptor.capture());
        Inventory savedInv = inventoryCaptor.getValue();
        assertThat(savedInv.getProductId()).isEqualTo(productId);
        assertThat(savedInv.getQuantity()).isEqualTo(2); // 0 + 2
    }

    @Test
    @DisplayName("FUNC00027_UTCID08 - Fails because user is CUSTOMER and findByIdAndUserId is empty")
    void TC8_CustomerOrderNotFound() {
        User user = getUser("CUSTOMER");
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        when(fixture.orderRepository.findByIdAndUserId(orderId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fixture.orderService.cancelPendingOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Order not found")
                .extracting("status")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("FUNC00027_UTCID09 - Fails because user is CUSTOMER and order status is APPROVED")
    void TC9_CustomerNotPending() {
        User user = getUser("CUSTOMER");
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Order order = getOrder("APPROVED");
        when(fixture.orderRepository.findByIdAndUserId(orderId, userId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> fixture.orderService.cancelPendingOrder(orderId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Order cannot be cancelled because it has been processed")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("FUNC00027_UTCID10 - Success (as CUSTOMER, order PENDING, existing inventory quantity is null)")
    void TC10_CustomerSuccessNullQty() {
        User user = getUser("CUSTOMER");
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Order order = getOrder("PENDING");
        when(fixture.orderRepository.findByIdAndUserId(orderId, userId)).thenReturn(Optional.of(order));

        setupOrderItemMock();
        
        Inventory inventory = Inventory.builder().productId(productId).quantity(null).reservedQuantity(0).build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        
        Order savedOrder = getOrder("CANCELLED");
        when(fixture.orderRepository.save(order)).thenReturn(savedOrder);

        ResOrderDTO result = fixture.orderService.cancelPendingOrder(orderId);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("CANCELLED");

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(fixture.inventoryRepository).save(inventoryCaptor.capture());
        Inventory savedInv = inventoryCaptor.getValue();
        assertThat(savedInv.getQuantity()).isEqualTo(2); // null -> 0 + 2
    }

    @Test
    @DisplayName("FUNC00027_UTCID11 - Success (as CUSTOMER, order PENDING, existing inventory has quantity 10)")
    void TC11_CustomerSuccessNormalInv() {
        User user = getUser("CUSTOMER");
        mockedSecurityUtil.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(fixture.userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Order order = getOrder("PENDING");
        when(fixture.orderRepository.findByIdAndUserId(orderId, userId)).thenReturn(Optional.of(order));

        setupOrderItemMock();
        
        Inventory inventory = Inventory.builder().productId(productId).quantity(10).reservedQuantity(0).build();
        when(fixture.inventoryRepository.findById(productId)).thenReturn(Optional.of(inventory));
        
        Order savedOrder = getOrder("CANCELLED");
        when(fixture.orderRepository.save(order)).thenReturn(savedOrder);

        ResOrderDTO result = fixture.orderService.cancelPendingOrder(orderId);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("CANCELLED");

        ArgumentCaptor<Inventory> inventoryCaptor = ArgumentCaptor.forClass(Inventory.class);
        verify(fixture.inventoryRepository).save(inventoryCaptor.capture());
        Inventory savedInv = inventoryCaptor.getValue();
        assertThat(savedInv.getQuantity()).isEqualTo(12); // 10 + 2
    }
}
