---
name: unit-test-normalizeForSearch-condition-coverage
description: Unit tests with 100% condition coverage for ProductSearchService.normalizeForSearch method.
---

## Infomation
Service Name: ProductSearchService
Method Name: normalizeForSearch(String value)
Mock class: None

Mock Data:
   - value1: "iPhone 15!"
   - value2: "Điện thoại"

## Testcase
### Testcase 1
Short Description: Test normalizeForSearch with null input.
Input: value = null.
Expected Output: "" (Empty string)
Actual Output: "" (Empty string)

### Testcase 2
Short Description: Test normalizeForSearch with uppercase and special characters.
Input: value = "iPhone 15!".
Expected Output: "iphone 15"
Actual Output: "iphone 15"

### Testcase 3
Short Description: Test normalizeForSearch with Vietnamese characters (accents and 'đ').
Input: value = "Điện thoại".
Expected Output: "dien thoai"
Actual Output: "dien thoai"

### Testcase 4
Short Description: Test normalizeForSearch with extra spaces.
Input: value = "  iphone   15  ".
Expected Output: "iphone 15"
Actual Output: "iphone 15"

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
