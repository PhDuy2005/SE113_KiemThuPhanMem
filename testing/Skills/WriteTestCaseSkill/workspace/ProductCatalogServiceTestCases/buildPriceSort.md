---
name: unit-test-buildPriceSort-condition-coverage
description: Unit tests with 100% condition coverage for ProductCatalogService.buildPriceSort method.
---

## Infomation
Service Name: ProductCatalogService
Method Name: buildPriceSort(String sortPrice)
Mock class: None

Mock Data:
   - sortPriceNull: null
   - sortPriceBlank: "   "
   - sortPriceAsc: "asc"
   - sortPriceDesc: "DESC"
   - sortPriceInvalid: "random"

## Testcase
### Testcase 1
Short Description: Test buildPriceSort when sortPrice is null.
Input: sortPrice = null.
Expected Output: Sort.unsorted()
Actual Output: Sort.unsorted()

### Testcase 2
Short Description: Test buildPriceSort when sortPrice is a blank string.
Input: sortPrice = "   ".
Expected Output: Sort.unsorted()
Actual Output: Sort.unsorted()

### Testcase 3
Short Description: Test buildPriceSort when sortPrice is "asc" (case-insensitive).
Input: sortPrice = "asc".
Expected Output: Sort.by(Sort.Direction.ASC, "price")
Actual Output: Sort.by(Sort.Direction.ASC, "price")

### Testcase 4
Short Description: Test buildPriceSort when sortPrice is "DESC" (case-insensitive).
Input: sortPrice = "DESC".
Expected Output: Sort.by(Sort.Direction.DESC, "price")
Actual Output: Sort.by(Sort.Direction.DESC, "price")

### Testcase 5
Short Description: Test buildPriceSort when sortPrice is an invalid string.
Input: sortPrice = "random".
Expected Output: BusinessException (400, "sortPrice must be ASC or DESC")
Actual Output: BusinessException (400, "sortPrice must be ASC or DESC")

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
