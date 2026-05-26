---
name: unit-test-getProducts-condition-coverage
description: Unit tests with 100% condition coverage for ProductCatalogService.getProducts method.
---

## Infomation
Service Name: ProductCatalogService
Method Name: getProducts(List<UUID> categoryIds, String sortPrice)
Mock class: 
   1. ProductRepository productRepository

Mock Data:
   - categoryIds: ["11111111-1111-1111-1111-111111111111"]
   - product1: Product {id: "22222222-2222-2222-2222-222222222222", name: "Product 1"}

## Testcase
### Testcase 1
Short Description: Test getProducts when sortPrice is null and categoryIds is null.
Input: categoryIds = null, sortPrice = null. productRepository.findByStatusIgnoreCase called with Sort.unsorted().
Expected Output: List of ResProductDTO
Actual Output: List of ResProductDTO

### Testcase 2
Short Description: Test getProducts when sortPrice is blank and categoryIds is empty.
Input: categoryIds = [], sortPrice = "  ". productRepository.findByStatusIgnoreCase called with Sort.unsorted().
Expected Output: List of ResProductDTO
Actual Output: List of ResProductDTO

### Testcase 3
Short Description: Test getProducts when sortPrice is "asc" and categoryIds is provided.
Input: categoryIds = ["11111111-1111-1111-1111-111111111111"], sortPrice = "asc". productRepository.findByStatusIgnoreCaseAndCategoryIdIn called with Sort.by(ASC, "price").
Expected Output: List of ResProductDTO
Actual Output: List of ResProductDTO

### Testcase 4
Short Description: Test getProducts when sortPrice is "DESC" and categoryIds is provided.
Input: categoryIds = ["11111111-1111-1111-1111-111111111111"], sortPrice = "DESC". productRepository.findByStatusIgnoreCaseAndCategoryIdIn called with Sort.by(DESC, "price").
Expected Output: List of ResProductDTO
Actual Output: List of ResProductDTO

### Testcase 5
Short Description: Test getProducts fails when sortPrice is invalid.
Input: categoryIds = null, sortPrice = "invalid_sort".
Expected Output: BusinessException (400, "sortPrice must be ASC or DESC")
Actual Output: BusinessException (400, "sortPrice must be ASC or DESC")

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
