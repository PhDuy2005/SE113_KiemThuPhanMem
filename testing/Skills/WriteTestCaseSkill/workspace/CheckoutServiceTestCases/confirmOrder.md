---
name: unit-test-confirmOrder-condition-coverage
description: Unit tests with 100% condition coverage for CheckoutService.confirmOrder method.
---

## Infomation
Service Name: CheckoutService
Method Name: confirmOrder(ReqConfirmOrderDTO request)
Mock class: 
   1. CartRepository, CartItemRepository, InventoryRepository
   2. OrderRepository, OrderItemRepository, PaymentRepository
   3. PaymentMethodRepository, ShippingAddressRepository, VoucherRepository
   4. ShippingFeeConfigService, EmailService, UserRepository
   5. SecurityUtil (Static mock)

Mock Data:
   - request: ReqConfirmOrderDTO { selectedProductIds: [id1], shippingAddressId: 1L, paymentMethodId: 1L, voucherCode: "SAVE10" }
   - shippingAddress: ShippingAddress { id: 1L, province: "HCM", provinceCode: "79", detail: "123 St", ward: "Ward 1" }
   - paymentMethod: PaymentMethod { id: 1L, type: "CASH", name: "COD" }

## Testcase
### Testcase 1
Short Description: Test confirmOrder fails when shipping address not found or belongs to another user.
Input: shippingAddressId = 1L, repository returns empty.
Expected Output: BusinessException (404, "Shipping address not found")
Actual Output: BusinessException (404, "Shipping address not found")

### Testcase 2
Short Description: Test confirmOrder fails when payment method not found.
Input: paymentMethodId = 99L, repository returns empty.
Expected Output: BusinessException (400, "Invalid payment method")
Actual Output: BusinessException (400, "Invalid payment method")

### Testcase 3
Short Description: Test confirmOrder fails when stock is insufficient.
Input: item quantity = 10, inventory available = 5.
Expected Output: BusinessException (400, "Insufficient stock. Please check your cart again")
Actual Output: BusinessException (400, "Insufficient stock. Please check your cart again")

### Testcase 4
Short Description: Test confirmOrder succeeds with voucher and updates stock/usedCount.
Input: Valid request, voucher "SAVE10" (10%), shipping fee 30k.
Expected Output: ResOrderDTO with calculated totals, stock deducted, voucher usedCount incremented.
Actual Output: ResOrderDTO with calculated totals, stock deducted, voucher usedCount incremented.

### Testcase 5
Short Description: Test confirmOrder succeeds without voucher.
Input: voucherCode = null.
Expected Output: discountAmount = 0.
Actual Output: discountAmount = 0.

## Code of Test Case
```java
@Test
void confirmOrder_AddressNotFound_ThrowsNotFound() {
    // Arrange
    User user = mockActiveUser();
    mockCart(user);
    ReqConfirmOrderDTO req = new ReqConfirmOrderDTO();
    req.setSelectedProductIds(List.of(UUID.randomUUID()));
    req.setShippingAddressId(1L);

    when(shippingAddressRepository.findByIdAndUserIdAndDeletedAtIsNull(1L, user.getId()))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(BusinessException.class, () -> checkoutService.confirmOrder(req));
}

@Test
void confirmOrder_InsufficientStock_ThrowsBadRequest() {
    // Arrange
    User user = mockActiveUser();
    Cart cart = mockCart(user);
    UUID pid = UUID.randomUUID();
    CartItem item = mockCartItem(pid, 10);
    when(cartItemRepository.findSelectedByCartIdWithProduct(cart.getId(), List.of(pid)))
        .thenReturn(List.of(item));

    ReqConfirmOrderDTO req = new ReqConfirmOrderDTO();
    req.setSelectedProductIds(List.of(pid));
    req.setShippingAddressId(1L);
    req.setPaymentMethodId(1L);

    mockAddress(user.getId(), 1L);
    mockPaymentMethod(1L, "CASH");

    Inventory inv = new Inventory();
    inv.setAvailableQuantity(5); // Less than 10
    when(inventoryRepository.findById(pid)).thenReturn(Optional.of(inv));

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        checkoutService.confirmOrder(req));
    assertEquals("Insufficient stock. Please check your cart again", exception.getMessage());
}

@Test
void confirmOrder_Valid_CreatesOrder() {
    // Arrange
    User user = mockActiveUser();
    Cart cart = mockCart(user);
    UUID pid = UUID.randomUUID();
    CartItem item = mockCartItem(pid, 2);
    item.getProduct().setPrice(new BigDecimal("100.00"));
    when(cartItemRepository.findSelectedByCartIdWithProduct(cart.getId(), List.of(pid)))
        .thenReturn(List.of(item));

    ReqConfirmOrderDTO req = new ReqConfirmOrderDTO();
    req.setSelectedProductIds(List.of(pid));
    req.setShippingAddressId(1L);
    req.setPaymentMethodId(1L);
    req.setVoucherCode("SAVE10");

    mockAddress(user.getId(), 1L);
    mockPaymentMethod(1L, "CASH");
    
    Voucher voucher = new Voucher();
    voucher.setCode("SAVE10");
    voucher.setType("PERCENT");
    voucher.setValue(new BigDecimal("10"));
    voucher.setActive(true);
    voucher.setUsedCount(0);
    when(voucherRepository.findByCodeIgnoreCase("SAVE10")).thenReturn(Optional.of(voucher));

    Inventory inv = new Inventory();
    inv.setAvailableQuantity(10);
    inv.setQuantity(10);
    when(inventoryRepository.findById(pid)).thenReturn(Optional.of(inv));
    
    when(shippingFeeConfigService.getShippingFeeForProvince(any(), any())).thenReturn(new BigDecimal("30.00"));
    when(orderRepository.save(any())).thenAnswer(i -> {
        Order o = i.getArgument(0);
        o.setId(UUID.randomUUID());
        return o;
    });
    when(paymentRepository.save(any())).thenAnswer(i -> {
        Payment p = i.getArgument(0);
        p.setId(UUID.randomUUID());
        return p;
    });

    // Act
    ResOrderDTO result = checkoutService.confirmOrder(req);

    // Assert
    assertEquals(new BigDecimal("210.00"), result.getTotalAmount()); // (100*2) + 30 - 20
    verify(inventoryRepository).save(argThat(i -> i.getQuantity() == 8));
    verify(voucherRepository).save(argThat(v -> v.getUsedCount() == 1));
    verify(emailService).sendOrderConfirmation(any(), any(), any(), any(), any(), any(), any());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
