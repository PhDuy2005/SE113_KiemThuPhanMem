---
name: unit-test-product-state-management-condition-coverage
description: Unit tests with 100% condition coverage for ProductManagementService product state changes.
---

## Infomation
Service Name: ProductManagementService
Method Name: updatePrice(UUID, BigDecimal), updateStock(UUID, Integer), discontinueProduct(UUID)
Mock class: 
   1. ProductRepository productRepository
   2. InventoryRepository inventoryRepository
   3. SecurityUtil (Static mock)

Mock Data:
   - productId: "550e8400-e29b-41d4-a716-446655440001"
   - product: Product { id: productId, price: 100.00, status: "ACTIVE" }

## Testcase
### Testcase 1
Short Description: Test updatePrice fails if new price is same as current price.
Input: currentPrice = 100.00, newPrice = 100.00.
Expected Output: BusinessException (400, "New price must be different from current price")
Actual Output: BusinessException (400, "New price must be different from current price")

### Testcase 2
Short Description: Test updatePrice fails if new price is null or non-positive.
Input: newPrice = null, or newPrice = 0.
Expected Output: BusinessException (400, "Required field is missing" / "Price must be greater than 0")
Actual Output: BusinessException (400, "Required field is missing" / "Price must be greater than 0")

### Testcase 3
Short Description: Test updateStock fails if newStock is negative.
Input: newStock = -1.
Expected Output: BusinessException (400, "Stock must not be negative")
Actual Output: BusinessException (400, "Stock must not be negative")

### Testcase 4
Short Description: Test updateStock sets status to OUT_OF_STOCK if stock is 0.
Input: newStock = 0, currentStatus = "ACTIVE".
Expected Output: product.status = "OUT_OF_STOCK".
Actual Output: product.status = "OUT_OF_STOCK".

### Testcase 5
Short Description: Test updateStock sets status back to ACTIVE if stock > 0 and was OUT_OF_STOCK.
Input: newStock = 10, currentStatus = "OUT_OF_STOCK".
Expected Output: product.status = "ACTIVE".
Actual Output: product.status = "ACTIVE".

### Testcase 6
Short Description: Test discontinueProduct updates status to DISCONTINUED.
Input: productId = valid.
Expected Output: product.status = "DISCONTINUED".
Actual Output: product.status = "DISCONTINUED".

## Code of Test Case
```java
@Test
void updatePrice_SamePrice_ThrowsBadRequest() {
    mockAdminAccess();
    UUID productId = UUID.randomUUID();
    Product product = new Product();
    product.setPrice(new BigDecimal("100.00"));
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));

    BusinessException exception = assertThrows(BusinessException.class, () -> 
        productManagementService.updatePrice(productId, new BigDecimal("100.00")));
    assertEquals("New price must be different from current price", exception.getMessage());
}

@Test
void updateStock_ZeroStock_SetsOutOfStock() {
    mockAdminAccess();
    UUID productId = UUID.randomUUID();
    Product product = new Product();
    product.setStatus("ACTIVE");
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(inventoryRepository.findById(productId)).thenReturn(Optional.of(new Inventory()));
    when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    productManagementService.updateStock(productId, 0);
    assertEquals("OUT_OF_STOCK", product.getStatus());
}

@Test
void updateStock_BackToActive_SetsActive() {
    mockAdminAccess();
    UUID productId = UUID.randomUUID();
    Product product = new Product();
    product.setStatus("OUT_OF_STOCK");
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(inventoryRepository.findById(productId)).thenReturn(Optional.of(new Inventory()));
    when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    productManagementService.updateStock(productId, 5);
    assertEquals("ACTIVE", product.getStatus());
}

@Test
void discontinueProduct_Valid_SetsDiscontinued() {
    mockAdminAccess();
    UUID productId = UUID.randomUUID();
    Product product = new Product();
    product.setStatus("ACTIVE");
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    productManagementService.discontinueProduct(productId);
    assertEquals("DISCONTINUED", product.getStatus());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
