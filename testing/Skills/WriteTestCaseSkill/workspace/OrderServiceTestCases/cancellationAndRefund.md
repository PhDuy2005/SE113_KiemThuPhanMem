---
name: unit-test-cancellation-and-refund-condition-coverage
description: Unit tests with 100% condition coverage for OrderService cancellation and refund logic.
---

## Infomation
Service Name: OrderService
Method Name: cancelOrderForStaff(UUID, ReqStaffCancelOrderDTO) and initiateRefund(UUID)
Mock class: 
   1. OrderRepository orderRepository
   2. PaymentRepository paymentRepository
   3. InventoryRepository inventoryRepository
   4. SecurityUtil (Static mock)

Mock Data:
   - orderId: "550e8400-e29b-41d4-a716-446655440001"
   - onlinePayment: Payment { status: "SUCCESS", paymentMethod: { type: "ONLINE" } }

## Testcase
### Testcase 1
Short Description: Test cancelOrderForStaff fails when cancelReason is missing.
Input: request = { cancelReason: null }.
Expected Output: BusinessException (400, "Cancel reason is required")
Actual Output: BusinessException (400, "Cancel reason is required")

### Testcase 2
Short Description: Test cancelOrderForStaff fails if order is already delivered or cancelled.
Input: Order status is "DELIVERED".
Expected Output: BusinessException (409, "Order has already been delivered or cancelled")
Actual Output: BusinessException (409, "Order has already been delivered or cancelled")

### Testcase 3
Short Description: Test cancelOrderForStaff sets refundStatus to PENDING_REFUND for online paid orders.
Input: Order is PENDING, has successful ONLINE payment.
Expected Output: order.refundStatus = "PENDING_REFUND".
Actual Output: order.refundStatus = "PENDING_REFUND".

### Testcase 4
Short Description: Test initiateRefund fails if order is not CANCELLED or payment not SUCCESS.
Input: Order status "PENDING", payment status "SUCCESS".
Expected Output: BusinessException (400, "Order is not eligible for refund")
Actual Output: BusinessException (400, "Order is not eligible for refund")

### Testcase 5
Short Description: Test initiateRefund succeeds and updates both payment and order.
Input: Order "CANCELLED", Payment "SUCCESS".
Expected Output: Payment status "REFUNDED", Order refundStatus "REFUNDED".
Actual Output: Payment status "REFUNDED", Order refundStatus "REFUNDED".

## Code of Test Case
```java
@Test
void cancelOrderForStaff_OnlinePaid_SetsPendingRefund() {
    // Arrange
    mockStaffAccess();
    UUID orderId = UUID.randomUUID();
    Order order = new Order();
    order.setId(orderId);
    order.setStatus("PENDING");
    order.setUser(new User());
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    PaymentMethod pm = new PaymentMethod(); pm.setType("ONLINE");
    Payment payment = new Payment();
    payment.setStatus("SUCCESS");
    payment.setPaymentMethod(pm);
    when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

    ReqStaffCancelOrderDTO req = new ReqStaffCancelOrderDTO();
    req.setCancelReason("Fraud detected");

    // Act
    orderService.cancelOrderForStaff(orderId, req);

    // Assert
    assertEquals("CANCELLED", order.getStatus());
    assertEquals("PENDING_REFUND", order.getRefundStatus());
}

@Test
void initiateRefund_IneligibleStatus_ThrowsBadRequest() {
    // Arrange
    mockStaffAccess();
    UUID orderId = UUID.randomUUID();
    Order order = new Order();
    order.setStatus("PENDING"); // Not CANCELLED
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    
    Payment payment = new Payment();
    payment.setStatus("SUCCESS");
    when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        orderService.initiateRefund(orderId));
    assertEquals("Order is not eligible for refund", exception.getMessage());
}

@Test
void initiateRefund_Valid_UpdatesToRefunded() {
    // Arrange
    mockStaffAccess();
    UUID orderId = UUID.randomUUID();
    Order order = new Order();
    order.setStatus("CANCELLED");
    order.setUser(new User());
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    
    Payment payment = new Payment();
    payment.setStatus("SUCCESS");
    when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

    // Act
    orderService.initiateRefund(orderId);

    // Assert
    assertEquals("REFUNDED", order.getRefundStatus());
    assertEquals("REFUNDED", payment.getStatus());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
