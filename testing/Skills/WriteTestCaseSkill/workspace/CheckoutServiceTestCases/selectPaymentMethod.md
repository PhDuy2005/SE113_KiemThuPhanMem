---
name: unit-test-selectPaymentMethod-condition-coverage
description: Unit tests with 100% condition coverage for CheckoutService.selectPaymentMethod method.
---

## Infomation
Service Name: CheckoutService
Method Name: selectPaymentMethod(ReqSelectPaymentMethodDTO request)
Mock class: 
   1. PaymentMethodRepository paymentMethodRepository
   2. UserRepository userRepository
   3. SecurityUtil (Static mock)

Mock Data:
   - cashMethod: PaymentMethod { id: 1L, name: "Cash on Delivery", type: "CASH" }
   - onlineMethod: PaymentMethod { id: 2L, name: "Credit Card", type: "ONLINE" }

## Testcase
### Testcase 1
Short Description: Test selectPaymentMethod fails when payment method id is invalid.
Input: paymentMethodId = 999L, repository returns empty.
Expected Output: BusinessException (400, "Invalid payment method")
Actual Output: BusinessException (400, "Invalid payment method")

### Testcase 2
Short Description: Test selectPaymentMethod fails when payment method is not CASH.
Input: paymentMethodId = 2L, type = "ONLINE".
Expected Output: BusinessException (400, "Only cash payment is supported at the moment")
Actual Output: BusinessException (400, "Only cash payment is supported at the moment")

### Testcase 3
Short Description: Test selectPaymentMethod succeeds with CASH method.
Input: paymentMethodId = 1L, type = "CASH".
Expected Output: ResPaymentMethodSelectionDTO with name "Cash on Delivery".
Actual Output: ResPaymentMethodSelectionDTO with name "Cash on Delivery".

## Code of Test Case
```java
@Test
void selectPaymentMethod_InvalidId_ThrowsBadRequest() {
    // Arrange
    mockActiveUser();
    ReqSelectPaymentMethodDTO req = new ReqSelectPaymentMethodDTO();
    req.setPaymentMethodId(999L);
    when(paymentMethodRepository.findById(999L)).thenReturn(Optional.empty());

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        checkoutService.selectPaymentMethod(req));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
}

@Test
void selectPaymentMethod_NotCash_ThrowsBadRequest() {
    // Arrange
    mockActiveUser();
    PaymentMethod method = new PaymentMethod();
    method.setType("ONLINE");
    when(paymentMethodRepository.findById(2L)).thenReturn(Optional.of(method));

    ReqSelectPaymentMethodDTO req = new ReqSelectPaymentMethodDTO();
    req.setPaymentMethodId(2L);

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        checkoutService.selectPaymentMethod(req));
    assertEquals("Only cash payment is supported at the moment", exception.getMessage());
}

@Test
void selectPaymentMethod_ValidCash_ReturnsSelection() {
    // Arrange
    mockActiveUser();
    PaymentMethod method = new PaymentMethod();
    method.setId(1L);
    method.setName("Cash on Delivery");
    method.setType("CASH");
    when(paymentMethodRepository.findById(1L)).thenReturn(Optional.of(method));

    ReqSelectPaymentMethodDTO req = new ReqSelectPaymentMethodDTO();
    req.setPaymentMethodId(1L);

    // Act
    ResPaymentMethodSelectionDTO result = checkoutService.selectPaymentMethod(req);

    // Assert
    assertEquals("Cash on Delivery", result.getName());
    assertEquals("CASH", result.getType());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
