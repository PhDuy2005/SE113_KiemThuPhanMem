---
name: unit-test-order-access-and-address-condition-coverage
description: Unit tests with 100% condition coverage for OrderService access control and address changes.
---

## Infomation
Service Name: OrderService
Method Name: getAccessibleOrder(UUID), changeShippingAddress(UUID, ReqChangeOrderAddressDTO)
Mock class: 
   1. OrderRepository orderRepository
   2. ShippingAddressRepository shippingAddressRepository
   3. PaymentRepository paymentRepository
   4. SecurityUtil (Static mock)

Mock Data:
   - orderId: "550e8400-e29b-41d4-a716-446655440001"
   - customer: User { id: "CUST1", role: { name: "CUSTOMER" } }
   - staff: User { id: "STAFF1", role: { name: "STAFF" } }

## Testcase
### Testcase 1
Short Description: Test getAccessibleOrder for customer (checks userId ownership).
Input: Customer requests an order that belongs to another user.
Expected Output: BusinessException (404, "Order not found")
Actual Output: BusinessException (404, "Order not found")

### Testcase 2
Short Description: Test getAccessibleOrder for staff (checks any order existence).
Input: Staff requests an order that belongs to anyone.
Expected Output: Returns the order if it exists.
Actual Output: Returns the order if it exists.

### Testcase 3
Short Description: Test changeShippingAddress fails if order status is SHIPPING/DELIVERED/CANCELLED.
Input: Order status "SHIPPING".
Expected Output: BusinessException (400, "Cannot change address because order has been shipped")
Actual Output: BusinessException (400, "Cannot change address because order has been shipped")

### Testcase 4
Short Description: Test changeShippingAddress fails if new address not found for user.
Input: addressRepository returns empty.
Expected Output: BusinessException (404, "Shipping address not found")
Actual Output: BusinessException (404, "Shipping address not found")

### Testcase 5
Short Description: Test changeShippingAddress updates totalAmount and payment amount.
Input: New address has higher shipping fee.
Expected Output: order.totalAmount updated, payment.amount updated.
Actual Output: order.totalAmount updated, payment.amount updated.

## Code of Test Case
```java
@Test
void getAccessibleOrder_CustomerUnauthorized_ThrowsNotFound() {
    // Arrange
    User customer = mockUser("CUSTOMER", UUID.randomUUID());
    mockSecurity(customer);
    UUID orderId = UUID.randomUUID();
    when(orderRepository.findByIdAndUserId(orderId, customer.getId())).thenReturn(Optional.empty());

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(orderService, "getAccessibleOrder", orderId));
    assertEquals("Order not found", exception.getMessage());
}

@Test
void changeShippingAddress_Shipped_ThrowsBadRequest() {
    // Arrange
    User customer = mockUser("CUSTOMER", UUID.randomUUID());
    mockSecurity(customer);
    UUID orderId = UUID.randomUUID();
    Order order = new Order();
    order.setStatus("SHIPPING");
    order.setUser(customer);
    when(orderRepository.findByIdAndUserId(orderId, customer.getId())).thenReturn(Optional.of(order));

    ReqChangeOrderAddressDTO req = new ReqChangeOrderAddressDTO();
    req.setNewAddressId(1L);

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        orderService.changeShippingAddress(orderId, req));
    assertEquals("Cannot change address because order has been shipped", exception.getMessage());
}

@Test
void changeShippingAddress_Valid_UpdatesTotals() {
    // Arrange
    User customer = mockUser("CUSTOMER", UUID.randomUUID());
    mockSecurity(customer);
    UUID orderId = UUID.randomUUID();
    Order order = new Order();
    order.setId(orderId);
    order.setStatus("PENDING");
    order.setUser(customer);
    order.setTotalProductAmount(new BigDecimal("100.00"));
    order.setDiscountAmount(BigDecimal.ZERO);
    when(orderRepository.findByIdAndUserId(orderId, customer.getId())).thenReturn(Optional.of(order));
    when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    ShippingAddress addr = new ShippingAddress();
    addr.setId(1L); addr.setProvinceCode("79"); addr.setProvince("HCM");
    addr.setUser(customer);
    when(shippingAddressRepository.findByIdAndUserIdAndDeletedAtIsNull(1L, customer.getId()))
        .thenReturn(Optional.of(addr));
    
    when(shippingFeeConfigService.getShippingFeeForProvince("79", "HCM")).thenReturn(new BigDecimal("30.00"));

    Payment payment = new Payment();
    when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

    ReqChangeOrderAddressDTO req = new ReqChangeOrderAddressDTO();
    req.setNewAddressId(1L);

    // Act
    orderService.changeShippingAddress(orderId, req);

    // Assert
    assertEquals(new BigDecimal("130.00"), order.getTotalAmount());
    assertEquals(new BigDecimal("130.00"), payment.getAmount());
    verify(paymentRepository).save(payment);
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
