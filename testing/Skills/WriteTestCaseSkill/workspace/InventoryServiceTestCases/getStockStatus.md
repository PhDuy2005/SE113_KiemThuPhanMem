---
name: unit-test-getStockStatus-condition-coverage
description: Unit tests with 100% condition coverage for InventoryService.getStockStatus method.
---

## Infomation
Service Name: InventoryService
Method Name: getStockStatus(UUID productId)
Mock class: 
   1. InventoryRepository inventoryRepository
   2. ProductRepository productRepository

Mock Data:
   - productId: "22222222-2222-2222-2222-222222222222"
   - product: Product {id: "22222222-2222-2222-2222-222222222222", status: "ACTIVE"}
   - inventoryInStock: Inventory {productId: "22222222-2222-2222-2222-222222222222", availableQuantity: 10}
   - inventoryOutOfStock: Inventory {productId: "22222222-2222-2222-2222-222222222222", availableQuantity: 0}

## Testcase
### Testcase 1
Short Description: Test getStockStatus fails when the product is not found or is inactive.
Input: productId = "22222222-2222-2222-2222-222222222222", productRepository.findByIdAndStatusIgnoreCase returns Optional.empty().
Expected Output: BusinessException (404, "Product not found")
Actual Output: BusinessException (404, "Product not found")

### Testcase 2
Short Description: Test getStockStatus when the product exists but no inventory record is found (defaults to 0 available quantity).
Input: productId = "22222222-2222-2222-2222-222222222222", productRepository returns product, inventoryRepository.findById returns Optional.empty().
Expected Output: ResInventoryStockDTO {productId: "22222222-2222-2222-2222-222222222222", availableQuantity: 0, stockStatus: "OUT_OF_STOCK"}
Actual Output: ResInventoryStockDTO {productId: "22222222-2222-2222-2222-222222222222", availableQuantity: 0, stockStatus: "OUT_OF_STOCK"}

### Testcase 3
Short Description: Test getStockStatus when the product exists and has an inventory record with 0 available quantity.
Input: productId = "22222222-2222-2222-2222-222222222222", productRepository returns product, inventoryRepository.findById returns Optional.of(inventoryOutOfStock).
Expected Output: ResInventoryStockDTO {productId: "22222222-2222-2222-2222-222222222222", availableQuantity: 0, stockStatus: "OUT_OF_STOCK"}
Actual Output: ResInventoryStockDTO {productId: "22222222-2222-2222-2222-222222222222", availableQuantity: 0, stockStatus: "OUT_OF_STOCK"}

### Testcase 4
Short Description: Test getStockStatus when the product exists and has an inventory record with positive available quantity.
Input: productId = "22222222-2222-2222-2222-222222222222", productRepository returns product, inventoryRepository.findById returns Optional.of(inventoryInStock).
Expected Output: ResInventoryStockDTO {productId: "22222222-2222-2222-2222-222222222222", availableQuantity: 10, stockStatus: "IN_STOCK"}
Actual Output: ResInventoryStockDTO {productId: "22222222-2222-2222-2222-222222222222", availableQuantity: 10, stockStatus: "IN_STOCK"}

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
