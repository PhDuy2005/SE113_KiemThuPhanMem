---
name: unit-test-discontinueProduct-condition-coverage
description: Unit tests with 100% condition coverage for ProductManagementService.discontinueProduct method.
---

## Infomation
Service Name: ProductManagementService
Method Name: discontinueProduct(UUID productId)
Mock class: 
   1. ProductRepository productRepository
   2. InventoryRepository inventoryRepository
   3. ProductImageRepository productImageRepository
   4. SecurityUtil (Static mock)

Mock Data:
   - productId: "550e8400-e29b-41d4-a716-446655440001"
   - product: Product { id: productId, status: "ACTIVE" }

## Testcase
### Testcase 1
Short Description: Test discontinueProduct fails when product is not found.
Input: productRepository.findById returns empty.
Expected Output: BusinessException (404, "Product not found")
Actual Output: BusinessException (404, "Product not found")

### Testcase 2
Short Description: Test discontinueProduct succeeds and sets status to DISCONTINUED.
Input: Valid productId.
Expected Output: product.status = "DISCONTINUED", message = "Product discontinued successfully".
Actual Output: product.status = "DISCONTINUED", message = "Product discontinued successfully".

## Code of Test Case
```java
@Test
void discontinueProduct_Valid_SetsDiscontinued() {
    // Arrange
    mockAdminAccess();
    UUID productId = UUID.randomUUID();
    Product product = new Product();
    product.setId(productId);
    product.setStatus("ACTIVE");
    when(productRepository.findById(productId)).thenReturn(Optional.of(product));
    when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    when(inventoryRepository.findById(productId)).thenReturn(Optional.empty());
    when(productImageRepository.findByProductIdOrderByPrimaryImageDescCreatedAtAsc(productId)).thenReturn(List.of());

    // Act
    ResProductDTO result = productManagementService.discontinueProduct(productId);

    // Assert
    assertEquals("DISCONTINUED", result.getStatus());
    assertEquals("Product discontinued successfully", result.getMessage());
}

@Test
void discontinueProduct_NotFound_ThrowsNotFound() {
    // Arrange
    mockAdminAccess();
    UUID productId = UUID.randomUUID();
    when(productRepository.findById(productId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(BusinessException.class, () -> 
        productManagementService.discontinueProduct(productId));
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
