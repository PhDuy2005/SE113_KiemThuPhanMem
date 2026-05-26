---
name: unit-test-initializeOnlinePayment-condition-coverage
description: Unit tests with 100% condition coverage for PaymentService functional logic.
---

## Infomation
Service Name: PaymentService
Method Name: initializeOnlinePayment(ReqOnlinePaymentDTO request) and isCustomer(User user)
Mock class: 
   1. OrderRepository orderRepository
   2. PaymentMethodRepository paymentMethodRepository
   3. UserRepository userRepository
   4. SecurityUtil (Static mock)

Mock Data:
   - customerUser: User { id: "CUST1", role: { name: "CUSTOMER" } }
   - staffUser: User { id: "STAFF1", role: { name: "STAFF" } }
   - onlineMethod: PaymentMethod { type: "ONLINE" }
   - cashMethod: PaymentMethod { type: "CASH" }

## Testcase
### Testcase 1
Short Description: Test isCustomer returns true for CUSTOMER role.
Input: User with role name "CUSTOMER".
Expected Output: true
Actual Output: true

### Testcase 2
Short Description: Test isCustomer returns false for null role or other name.
Input: User with role = null or role name "STAFF".
Expected Output: false
Actual Output: false

### Testcase 3
Short Description: Test initializeOnlinePayment for customer fails when order not found or unauthorized.
Input: Current user is customer, orderRepository.findByIdAndUserId returns empty.
Expected Output: BusinessException (404, "Order not found")
Actual Output: BusinessException (404, "Order not found")

### Testcase 4
Short Description: Test initializeOnlinePayment for staff fails when order not found.
Input: Current user is staff, orderRepository.findById returns empty.
Expected Output: BusinessException (404, "Order not found")
Actual Output: BusinessException (404, "Order not found")

### Testcase 5
Short Description: Test initializeOnlinePayment fails for invalid payment method id.
Input: paymentMethodRepository returns empty.
Expected Output: BusinessException (400, "Invalid payment method")
Actual Output: BusinessException (400, "Invalid payment method")

### Testcase 6
Short Description: Test initializeOnlinePayment fails for non-online payment method.
Input: PaymentMethod with type "CASH".
Expected Output: BusinessException (400, "Payment method is not online payment")
Actual Output: BusinessException (400, "Payment method is not online payment")

### Testcase 7
Short Description: Test initializeOnlinePayment throws NOT_IMPLEMENTED for valid input.
Input: Valid order and online payment method.
Expected Output: BusinessException (501, "Online payment is not supported yet...")
Actual Output: BusinessException (501, "Online payment is not supported yet...")

## Code of Test Case
```java
@Test
void isCustomer_Conditions() {
    User user = new User();
    assertFalse((Boolean) ReflectionTestUtils.invokeMethod(paymentService, "isCustomer", user));
    
    Role role = new Role();
    user.setRole(role);
    assertFalse((Boolean) ReflectionTestUtils.invokeMethod(paymentService, "isCustomer", user));
    
    role.setName("CUSTOMER");
    assertTrue((Boolean) ReflectionTestUtils.invokeMethod(paymentService, "isCustomer", user));
}

@Test
void initializeOnlinePayment_CustomerOrderNotFound_ThrowsNotFound() {
    // Arrange
    User user = mockUser("CUSTOMER", UUID.randomUUID());
    mockSecurity(user);
    ReqOnlinePaymentDTO req = new ReqOnlinePaymentDTO();
    req.setOrderId(UUID.randomUUID());
    when(orderRepository.findByIdAndUserId(any(), any())).thenReturn(Optional.empty());

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        paymentService.initializeOnlinePayment(req));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
}

@Test
void initializeOnlinePayment_InvalidMethodType_ThrowsBadRequest() {
    // Arrange
    User user = mockUser("STAFF", UUID.randomUUID());
    mockSecurity(user);
    ReqOnlinePaymentDTO req = new ReqOnlinePaymentDTO();
    req.setOrderId(UUID.randomUUID());
    req.setPaymentMethodId(1L);

    when(orderRepository.findById(any())).thenReturn(Optional.of(new Order()));
    
    PaymentMethod pm = new PaymentMethod();
    pm.setType("CASH");
    when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(pm));

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        paymentService.initializeOnlinePayment(req));
    assertEquals("Payment method is not online payment", exception.getMessage());
}

@Test
void initializeOnlinePayment_Valid_ThrowsNotImplemented() {
    // Arrange
    User user = mockUser("CUSTOMER", UUID.randomUUID());
    mockSecurity(user);
    ReqOnlinePaymentDTO req = new ReqOnlinePaymentDTO();
    req.setOrderId(UUID.randomUUID());
    req.setPaymentMethodId(1L);

    when(orderRepository.findByIdAndUserId(any(), any())).thenReturn(Optional.of(new Order()));
    
    PaymentMethod pm = new PaymentMethod();
    pm.setType("ONLINE");
    when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(pm));

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        paymentService.initializeOnlinePayment(req));
    assertEquals(HttpStatus.NOT_IMPLEMENTED, exception.getStatusCode());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
