---
name: unit-test-normalizeLimit-condition-coverage
description: Unit tests with 100% condition coverage for ProductSearchService.normalizeLimit method.
---

## Infomation
Service Name: ProductSearchService
Method Name: normalizeLimit(Integer limit)
Mock class: None

Mock Data:
   - DEFAULT_LIMIT: 20
   - MAX_LIMIT: 50

## Testcase
### Testcase 1
Short Description: Test normalizeLimit with null input.
Input: limit = null.
Expected Output: 20
Actual Output: 20

### Testcase 2
Short Description: Test normalizeLimit with zero or negative input.
Input: limit = 0 (or -5).
Expected Output: 20
Actual Output: 20

### Testcase 3
Short Description: Test normalizeLimit with valid input within range.
Input: limit = 10.
Expected Output: 10
Actual Output: 10

### Testcase 4
Short Description: Test normalizeLimit with input exceeding MAX_LIMIT.
Input: limit = 100.
Expected Output: 50
Actual Output: 50

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
