---
name: unit-test-order-status-management-condition-coverage
description: Unit tests with 100% condition coverage for OrderService status transitions.
---

## Infomation
Service Name: OrderService
Method Name: approveOrder(UUID), updateShippingStatus(UUID, ReqUpdateShippingStatusDTO), markOrderDelivered(UUID)
Mock class: 
   1. OrderRepository orderRepository
   2. NotificationRepository notificationRepository
   3. PaymentRepository paymentRepository
   4. SecurityUtil (Static mock)

Mock Data:
   - orderId: "550e8400-e29b-41d4-a716-446655440001"
   - pendingOrder: Order { id: orderId, status: "PENDING" }
   - approvedOrder: Order { id: orderId, status: "APPROVED" }
   - shippingOrder: Order { id: orderId, status: "SHIPPING" }

## Testcase
### Testcase 1
Short Description: Test approveOrder fails for non-pending order.
Input: Order status is "APPROVED".
Expected Output: BusinessException (400, "Only pending orders can be approved")
Actual Output: BusinessException (400, "Only pending orders can be approved")

### Testcase 2
Short Description: Test updateShippingStatus fails when tracking number is blank.
Input: request = { trackingNumber: "   " }.
Expected Output: BusinessException (400, "Tracking number is required")
Actual Output: BusinessException (400, "Tracking number is required")

### Testcase 3
Short Description: Test updateShippingStatus fails for ineligible status.
Input: Order status is "PENDING" (must be APPROVED or SHIPPING).
Expected Output: BusinessException (400, "Only approved orders can be moved to shipping")
Actual Output: BusinessException (400, "Only approved orders can be moved to shipping")

### Testcase 4
Short Description: Test markOrderDelivered fails for non-shipping order.
Input: Order status is "APPROVED".
Expected Output: BusinessException (400, "Only shipping orders can be marked as delivered")
Actual Output: BusinessException (400, "Only shipping orders can be marked as delivered")

### Testcase 5
Short Description: Test markOrderDelivered succeeds and updates payment to SUCCESS.
Input: Order status "SHIPPING", payment exists.
Expected Output: Order status "DELIVERED", Payment status "SUCCESS".
Actual Output: Order status "DELIVERED", Payment status "SUCCESS".

## Code of Test Case
```java
@Test
void approveOrder_NotPending_ThrowsBadRequest() {
    // Arrange
    mockStaffAccess();
    Order order = new Order();
    order.setStatus("APPROVED");
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        orderService.approveOrder(UUID.randomUUID()));
    assertEquals("Only pending orders can be approved", exception.getMessage());
}

@Test
void updateShippingStatus_InvalidStatus_ThrowsBadRequest() {
    // Arrange
    mockStaffAccess();
    Order order = new Order();
    order.setStatus("PENDING");
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));
    
    ReqUpdateShippingStatusDTO req = new ReqUpdateShippingStatusDTO();
    req.setTrackingNumber("TRK123");

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        orderService.updateShippingStatus(UUID.randomUUID(), req));
    assertEquals("Only approved orders can be moved to shipping", exception.getMessage());
}

@Test
void markOrderDelivered_Valid_UpdatesPayment() {
    // Arrange
    mockStaffAccess();
    UUID orderId = UUID.randomUUID();
    Order order = new Order();
    order.setId(orderId);
    order.setStatus("SHIPPING");
    order.setUser(new User());
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    Payment payment = new Payment();
    payment.setStatus("PENDING");
    when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

    // Act
    orderService.markOrderDelivered(orderId);

    // Assert
    assertEquals("DELIVERED", order.getStatus());
    assertEquals("SUCCESS", payment.getStatus());
    verify(paymentRepository).save(payment);
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
