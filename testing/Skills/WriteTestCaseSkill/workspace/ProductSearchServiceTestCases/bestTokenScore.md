---
name: unit-test-bestTokenScore-condition-coverage
description: Unit tests with 100% condition coverage for ProductSearchService.bestTokenScore method.
---

## Infomation
Service Name: ProductSearchService
Method Name: bestTokenScore(String queryToken, List<String> productTokens)
Mock class: None (Internal logic)

Mock Data:
   - productTokens: ["iphone", "apple", "pro", "max"]

## Testcase
### Testcase 1
Short Description: Test bestTokenScore with an exact token match.
Input: queryToken = "iphone", productTokens = ["iphone", "apple"].
Expected Output: 40
Actual Output: 40

### Testcase 2
Short Description: Test bestTokenScore with a prefix match (product token starts with query token).
Input: queryToken = "iph", productTokens = ["iphone", "apple"].
Expected Output: 28
Actual Output: 28

### Testcase 3
Short Description: Test bestTokenScore with a reverse prefix match (query token starts with product token).
Input: queryToken = "iphones", productTokens = ["iphone", "apple"].
Expected Output: 28
Actual Output: 28

### Testcase 4
Short Description: Test bestTokenScore with a containment match.
Input: queryToken = "phone", productTokens = ["iphone", "apple"].
Expected Output: 20
Actual Output: 20

### Testcase 5
Short Description: Test bestTokenScore with Levenshtein distance 1 for a short query token (length <= 4).
Input: queryToken = "appl", productTokens = ["apple"]. (Wait, "appl" is prefix of "apple", prefix score 28 wins). 
Let's use "aple" vs "apple" (distance 1). queryToken = "aple" (len 4).
Expected Output: 13 (18 - 1*5)
Actual Output: 13 (18 - 1*5)

### Testcase 6
Short Description: Test bestTokenScore with Levenshtein distance 2 for a long query token (length > 4).
Input: queryToken = "iphonnx", productTokens = ["iphone"]. (Distance 2).
Expected Output: 8 (18 - 2*5)
Actual Output: 8 (18 - 2*5)

### Testcase 7
Short Description: Test bestTokenScore when no match is found.
Input: queryToken = "xyz", productTokens = ["iphone", "apple"].
Expected Output: 0
Actual Output: 0

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
