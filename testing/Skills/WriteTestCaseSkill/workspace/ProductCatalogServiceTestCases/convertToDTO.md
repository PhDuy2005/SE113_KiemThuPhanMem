---
name: unit-test-convertToDTO-condition-coverage
description: Unit tests with 100% condition coverage for ProductCatalogService.convertToDTO method.
---

## Infomation
Service Name: ProductCatalogService
Method Name: convertToDTO(Product product)
Mock class: None (POJO mapping)

Mock Data:
   - product: Product {
       id: "22222222-2222-2222-2222-222222222222",
       name: "Test Product",
       description: "Test Description",
       price: 1500.00,
       status: "ACTIVE",
       brand: "Test Brand",
       categoryId: "33333333-3333-3333-3333-333333333333"
     }

## Testcase
### Testcase 1
Short Description: Test convertToDTO successfully maps all fields from Product entity to ResProductDTO.
Input: product = Product object with valid fields.
Expected Output: ResProductDTO {
       id: "22222222-2222-2222-2222-222222222222",
       name: "Test Product",
       description: "Test Description",
       price: 1500.00,
       status: "ACTIVE",
       brand: "Test Brand",
       categoryId: "33333333-3333-3333-3333-333333333333"
     }
Actual Output: ResProductDTO {
       id: "22222222-2222-2222-2222-222222222222",
       name: "Test Product",
       description: "Test Description",
       price: 1500.00,
       status: "ACTIVE",
       brand: "Test Brand",
       categoryId: "33333333-3333-3333-3333-333333333333"
     }

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
