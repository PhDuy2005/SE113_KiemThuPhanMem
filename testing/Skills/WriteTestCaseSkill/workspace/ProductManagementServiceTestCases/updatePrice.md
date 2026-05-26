---
name: unit-test-updatePrice-condition-coverage
description: Unit tests with 100% condition coverage for ProductManagementService.updatePrice method.
---

## Infomation
Service Name: ProductManagementService
Method Name: updatePrice(UUID productId, BigDecimal newPrice)
Mock class: 
   1. ProductRepository productRepository
   2. InventoryRepository inventoryRepository
   3. ProductImageRepository productImageRepository
   4. SecurityUtil (Static mock)

Mock Data:
   - productId: "550e8400-e29b-41d4-a716-446655440001"
   - currentPrice: 100.00
   - newPrice: 150.00

## Testcase
### Testcase 1
Short Description: Test updatePrice fails when newPrice is null.
Input: newPrice = null
Expected Output: BusinessException (400, "Required field is missing")
Actual Output: BusinessException (400, "Required field is missing")

### Testcase 2
Short Description: Test updatePrice fails when newPrice is less than or equal to 0.
Input: newPrice = 0, or newPrice = -1.
Expected Output: BusinessException (400, "Price must be greater than 0")
Actual Output: BusinessException (400, "Price must be greater than 0")

### Testcase 3
Short Description: Test updatePrice fails when product is not found.
Input: productRepository.findById returns empty.
Expected Output: BusinessException (404, "Product not found")
Actual Output: BusinessException (404, "Product not found")

### Testcase 4
Short Description: Test updatePrice fails when new price is identical to current price.
Input: currentPrice = 100.00, newPrice = 100.00.
Expected Output: BusinessException (400, "New price must be different from current price")
Actual Output: BusinessException (400, "New price must be different from current price")

### Testcase 5
Short Description: Test updatePrice succeeds with valid different price.
Input: currentPrice = 100.00, newPrice = 150.00.
Expected Output: ResProductDTO with new price and success message.
Actual Output: ResProductDTO with new price and success message.

## Code of Test Case
```java
@Test
void updatePrice_SamePrice_ThrowsBadRequest() {
    // Arrange
    mockAdminAccess();
    UUID productId = UUID.randomUUID();
    Product product = new Product();
    product.setPrice(new BigDecimal("100.00"));
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        productManagementService.updatePrice(productId, new BigDecimal("100.00")));
    assertEquals("New price must be different from current price", exception.getMessage());
}

@Test
void updatePrice_Valid_Succeeds() {
    // Arrange
    mockAdminAccess();
    UUID productId = UUID.randomUUID();
    Product product = new Product();
    product.setId(productId);
    product.setPrice(new BigDecimal("100.00"));
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    when(inventoryRepository.findById(productId)).thenReturn(Optional.empty());
    when(productImageRepository.findByProductIdOrderByPrimaryImageDescCreatedAtAsc(productId)).thenReturn(List.of());

    // Act
    ResProductDTO result = productManagementService.updatePrice(productId, new BigDecimal("150.00"));

    // Assert
    assertEquals(new BigDecimal("150.00"), result.getPrice());
    assertEquals("Product price updated successfully", result.getMessage());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
