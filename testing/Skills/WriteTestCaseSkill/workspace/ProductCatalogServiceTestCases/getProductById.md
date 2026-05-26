---
name: unit-test-getProductById-condition-coverage
description: Unit tests with 100% condition coverage for ProductCatalogService.getProductById method.
---

## Infomation
Service Name: ProductCatalogService
Method Name: getProductById(UUID productId)
Mock class: 
   1. ProductRepository productRepository

Mock Data:
   - productId: "22222222-2222-2222-2222-222222222222"
   - product: Product {id: "22222222-2222-2222-2222-222222222222", name: "Active Product", status: "ACTIVE"}

## Testcase
### Testcase 1
Short Description: Test getProductById when the product exists and is active.
Input: productId = "22222222-2222-2222-2222-222222222222", productRepository.findByIdAndStatusIgnoreCase returns Optional.of(product).
Expected Output: ResProductDTO object
Actual Output: ResProductDTO object

### Testcase 2
Short Description: Test getProductById when the product does not exist or is inactive.
Input: productId = "22222222-2222-2222-2222-222222222222", productRepository.findByIdAndStatusIgnoreCase returns Optional.empty().
Expected Output: BusinessException (404, "Product not found")
Actual Output: BusinessException (404, "Product not found")

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
