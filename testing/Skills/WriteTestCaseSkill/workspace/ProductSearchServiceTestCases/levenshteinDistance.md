---
name: unit-test-levenshteinDistance-condition-coverage
description: Unit tests with 100% condition coverage for ProductSearchService.levenshteinDistance method.
---

## Infomation
Service Name: ProductSearchService
Method Name: levenshteinDistance(String first, String second)
Mock class: None

Mock Data:
   - first: "iphone"
   - second: "iphne"

## Testcase
### Testcase 1
Short Description: Test levenshteinDistance with identical strings.
Input: first = "iphone", second = "iphone".
Expected Output: 0
Actual Output: 0

### Testcase 2
Short Description: Test levenshteinDistance with one empty string.
Input: first = "iphone", second = "".
Expected Output: 6
Actual Output: 6

### Testcase 3
Short Description: Test levenshteinDistance with strings requiring one deletion.
Input: first = "iphone", second = "iphne".
Expected Output: 1
Actual Output: 1

### Testcase 4
Short Description: Test levenshteinDistance with strings requiring one insertion.
Input: first = "iphne", second = "iphone".
Expected Output: 1
Actual Output: 1

### Testcase 5
Short Description: Test levenshteinDistance with strings requiring one substitution.
Input: first = "iphone", second = "iphona".
Expected Output: 1
Actual Output: 1

### Testcase 6
Short Description: Test levenshteinDistance with completely different strings.
Input: first = "abc", second = "def".
Expected Output: 3
Actual Output: 3

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
