---
name: unit-test-tokenize-condition-coverage
description: Unit tests with 100% condition coverage for ProductSearchService.tokenize method.
---

## Infomation
Service Name: ProductSearchService
Method Name: tokenize(String value)
Mock class: None

Mock Data:
   - value: "iphone 15 pro"

## Testcase
### Testcase 1
Short Description: Test tokenize with null input.
Input: value = null.
Expected Output: Empty List []
Actual Output: Empty List []

### Testcase 2
Short Description: Test tokenize with blank input.
Input: value = "   ".
Expected Output: Empty List []
Actual Output: Empty List []

### Testcase 3
Short Description: Test tokenize with normal input containing multiple spaces.
Input: value = "iphone   15   pro".
Expected Output: ["iphone", "15", "pro"]
Actual Output: ["iphone", "15", "pro"]

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
