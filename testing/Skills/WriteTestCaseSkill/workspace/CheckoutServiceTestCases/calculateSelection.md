---
name: unit-test-calculateSelection-condition-coverage
description: Unit tests with 100% condition coverage for CheckoutService.calculateSelection method.
---

## Infomation
Service Name: CheckoutService
Method Name: calculateSelection(ReqCheckoutSelectionDTO request)
Mock class: 
   1. CartRepository cartRepository
   2. CartItemRepository cartItemRepository
   3. UserRepository userRepository
   4. SecurityUtil (Static mock)

Mock Data:
   - currentUser: User { id: "550e8400-e29b-41d4-a716-446655440000", email: "user@example.com", accountStatus: "ACTIVE" }
   - cart: Cart { id: "550e8400-e29b-41d4-a716-446655441111", userId: "550e8400-e29b-41d4-a716-446655440000" }
   - product1Id: "550e8400-e29b-41d4-a716-446655442222"
   - item1: CartItem { product: Product { id: product1Id, price: 100.0 }, quantity: 2 }

## Testcase
### Testcase 1
Short Description: Test calculateSelection fails when cart is not found.
Input: Authorized user, cartRepository.findByUserId(userId) returns Optional.empty()
Expected Output: BusinessException (404, "Cart not found")
Actual Output: BusinessException (404, "Cart not found")

### Testcase 2
Short Description: Test calculateSelection fails when selectedProductIds is null.
Input: request = { selectedProductIds: null }, valid cart.
Expected Output: BusinessException (400, "You must select at least one product")
Actual Output: BusinessException (400, "You must select at least one product")

### Testcase 3
Short Description: Test calculateSelection fails when selectedProductIds is empty.
Input: request = { selectedProductIds: [] }, valid cart.
Expected Output: BusinessException (400, "You must select at least one product")
Actual Output: BusinessException (400, "You must select at least one product")

### Testcase 4
Short Description: Test calculateSelection fails when some selected products are not in cart.
Input: request = { selectedProductIds: [id1, id2] }, cartItemRepository returns only 1 item.
Expected Output: BusinessException (400, "Selected products are invalid")
Actual Output: BusinessException (400, "Selected products are invalid")

### Testcase 5
Short Description: Test calculateSelection succeeds with valid selection.
Input: request = { selectedProductIds: [product1Id] }, item1 in cart with price 100.0 and quantity 2.
Expected Output: ResCheckoutSelectionDTO with tempTotalPrice = 200.0.
Actual Output: ResCheckoutSelectionDTO with tempTotalPrice = 200.0.

## Code of Test Case
```java
@Test
void calculateSelection_CartNotFound_ThrowsNotFound() {
    // Arrange
    User user = mockActiveUser();
    ReqCheckoutSelectionDTO req = new ReqCheckoutSelectionDTO();
    req.setSelectedProductIds(List.of(UUID.randomUUID()));
    when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        checkoutService.calculateSelection(req));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
}

@Test
void calculateSelection_EmptySelection_ThrowsBadRequest() {
    // Arrange
    User user = mockActiveUser();
    Cart cart = new Cart();
    cart.setId(UUID.randomUUID());
    when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
    
    ReqCheckoutSelectionDTO req = new ReqCheckoutSelectionDTO();
    req.setSelectedProductIds(List.of());

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        checkoutService.calculateSelection(req));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("You must select at least one product", exception.getMessage());
}

@Test
void calculateSelection_InvalidProducts_ThrowsBadRequest() {
    // Arrange
    User user = mockActiveUser();
    Cart cart = new Cart();
    cart.setId(UUID.randomUUID());
    when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    ReqCheckoutSelectionDTO req = new ReqCheckoutSelectionDTO();
    req.setSelectedProductIds(List.of(id1, id2));

    when(cartItemRepository.findSelectedByCartIdWithProduct(cart.getId(), req.getSelectedProductIds()))
        .thenReturn(List.of(new CartItem())); // Only 1 found

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        checkoutService.calculateSelection(req));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Selected products are invalid", exception.getMessage());
}

@Test
void calculateSelection_Valid_ReturnsTotal() {
    // Arrange
    User user = mockActiveUser();
    Cart cart = new Cart();
    cart.setId(UUID.randomUUID());
    when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

    UUID pid = UUID.randomUUID();
    ReqCheckoutSelectionDTO req = new ReqCheckoutSelectionDTO();
    req.setSelectedProductIds(List.of(pid));

    Product product = new Product();
    product.setPrice(new BigDecimal("100.00"));
    CartItem item = new CartItem();
    item.setProduct(product);
    item.setQuantity(2);

    when(cartItemRepository.findSelectedByCartIdWithProduct(cart.getId(), req.getSelectedProductIds()))
        .thenReturn(List.of(item));

    // Act
    ResCheckoutSelectionDTO result = checkoutService.calculateSelection(req);

    // Assert
    assertEquals(new BigDecimal("200.00"), result.getTempTotalPrice());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
