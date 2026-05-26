---
name: unit-test-updateStock-condition-coverage
description: Unit tests with 100% condition coverage for ProductManagementService.updateStock method.
---

## Infomation
Service Name: ProductManagementService
Method Name: updateStock(UUID productId, Integer newStock)
Mock class: 
   1. ProductRepository productRepository
   2. InventoryRepository inventoryRepository
   3. ProductImageRepository productImageRepository
   4. SecurityUtil (Static mock)

Mock Data:
   - productId: "550e8400-e29b-41d4-a716-446655440001"
   - currentStock: 10
   - newStock: 0

## Testcase
### Testcase 1
Short Description: Test updateStock fails when newStock is null.
Input: newStock = null
Expected Output: BusinessException (400, "Required field is missing")
Actual Output: BusinessException (400, "Required field is missing")

### Testcase 2
Short Description: Test updateStock fails when newStock is negative.
Input: newStock = -1
Expected Output: BusinessException (400, "Stock must not be negative")
Actual Output: BusinessException (400, "Stock must not be negative")

### Testcase 3
Short Description: Test updateStock fails when product is not found.
Input: productRepository.findById returns empty.
Expected Output: BusinessException (404, "Product not found")
Actual Output: BusinessException (404, "Product not found")

### Testcase 4
Short Description: Test updateStock creates new inventory if not exists and sets stock.
Input: inventoryRepository.findById returns empty.
Expected Output: Saves new Inventory with quantity = newStock and reservedQuantity = 0.
Actual Output: Saves new Inventory with quantity = newStock and reservedQuantity = 0.

### Testcase 5
Short Description: Test updateStock sets status to OUT_OF_STOCK if stock is 0.
Input: newStock = 0, currentStatus = "ACTIVE".
Expected Output: product.status = "OUT_OF_STOCK".
Actual Output: product.status = "OUT_OF_STOCK".

### Testcase 6
Short Description: Test updateStock sets status to ACTIVE if stock > 0 and was OUT_OF_STOCK.
Input: newStock = 5, currentStatus = "OUT_OF_STOCK".
Expected Output: product.status = "ACTIVE".
Actual Output: product.status = "ACTIVE".

## Code of Test Case
```java
@Test
void updateStock_ZeroStock_SetsOutOfStock() {
    // Arrange
    mockAdminAccess();
    UUID productId = UUID.randomUUID();
    Product product = new Product();
    product.setStatus("ACTIVE");
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(inventoryRepository.findById(productId)).thenReturn(Optional.of(new Inventory()));
    when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    when(productImageRepository.findByProductIdOrderByPrimaryImageDescCreatedAtAsc(productId)).thenReturn(List.of());

    // Act
    ResProductDTO result = productManagementService.updateStock(productId, 0);

    // Assert
    assertEquals("OUT_OF_STOCK", result.getStatus());
}

@Test
void updateStock_NewInventory_SetsReservedZero() {
    // Arrange
    mockAdminAccess();
    UUID productId = UUID.randomUUID();
    Product product = new Product();
    product.setStatus("ACTIVE");
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(inventoryRepository.findById(productId)).thenReturn(Optional.empty()); // No inventory yet
    when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    when(inventoryRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    // Act
    productManagementService.updateStock(productId, 10);

    // Assert
    verify(inventoryRepository).save(argThat(inv -> inv.getReservedQuantity() == 0 && inv.getQuantity() == 10));
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
