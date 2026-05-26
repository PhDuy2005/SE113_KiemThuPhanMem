---
name: unit-test-hasCategoryFilter-condition-coverage
description: Unit tests with 100% condition coverage for ProductCatalogService.hasCategoryFilter method.
---

## Infomation
Service Name: ProductCatalogService
Method Name: hasCategoryFilter(List<UUID> categoryIds)
Mock class: None

Mock Data:
   - categoryId: "11111111-1111-1111-1111-111111111111"

## Testcase
### Testcase 1
Short Description: Test hasCategoryFilter when categoryIds list is null.
Input: categoryIds = null.
Expected Output: false
Actual Output: false

### Testcase 2
Short Description: Test hasCategoryFilter when categoryIds list is empty.
Input: categoryIds = [].
Expected Output: false
Actual Output: false

### Testcase 3
Short Description: Test hasCategoryFilter when categoryIds list contains at least one UUID.
Input: categoryIds = ["11111111-1111-1111-1111-111111111111"].
Expected Output: true
Actual Output: true

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
