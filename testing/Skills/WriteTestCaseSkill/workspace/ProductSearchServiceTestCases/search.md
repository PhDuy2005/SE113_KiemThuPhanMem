---
name: unit-test-search-condition-coverage
description: Unit tests with 100% condition coverage for ProductSearchService.search method.
---

## Infomation
Service Name: ProductSearchService
Method Name: search(String keyword, Integer limit)
Mock class: 
   1. ProductRepository productRepository

Mock Data:
   - product1: Product {id: "1", name: "iPhone 15 Pro", brand: "Apple", status: "ACTIVE", normalizedName: "iphone 15 pro"}
   - product2: Product {id: "2", name: "Samsung Galaxy S24", brand: "Samsung", status: "ACTIVE", normalizedName: "samsung galaxy s24"}
   - product3: Product {id: "3", name: "MacBook Air", brand: "Apple", status: "ACTIVE", normalizedName: null}

## Testcase
### Testcase 1
Short Description: Test search with a blank or null keyword.
Input: keyword = "  ", limit = 10.
Expected Output: Empty List
Actual Output: Empty List

### Testcase 2
Short Description: Test search with null, zero, and negative limits (should use DEFAULT_LIMIT=20).
Input: keyword = "iphone", limit = null (or 0 or -5).
Expected Output: List with up to 20 products.
Actual Output: List with up to 20 products.

### Testcase 3
Short Description: Test search with a limit exceeding MAX_LIMIT (should use MAX_LIMIT=50).
Input: keyword = "iphone", limit = 100.
Expected Output: List with up to 50 products.
Actual Output: List with up to 50 products.

### Testcase 4
Short Description: Test search with exact keyword match in searchable text.
Input: keyword = "iPhone 15", productRepository returns [product1].
Expected Output: product1 (score >= 100)
Actual Output: product1 (score >= 100)

### Testcase 5
Short Description: Test search with token equality match.
Input: keyword = "Apple", productRepository returns [product1]. "apple" equals "apple" token.
Expected Output: product1 (score > 0)
Actual Output: product1 (score > 0)

### Testcase 6
Short Description: Test search with token prefix match (product token starts with query token).
Input: keyword = "iph", productRepository returns [product1]. "iphone" starts with "iph".
Expected Output: product1 (score > 0)
Actual Output: product1 (score > 0)

### Testcase 7
Short Description: Test search with query token prefix match (query token starts with product token).
Input: keyword = "apples", productRepository returns [product1]. "apples" starts with "apple".
Expected Output: product1 (score > 0)
Actual Output: product1 (score > 0)

### Testcase 8
Short Description: Test search with token contains match.
Input: keyword = "phone", productRepository returns [product1]. "iphone" contains "phone".
Expected Output: product1 (score > 0)
Actual Output: product1 (score > 0)

### Testcase 9
Short Description: Test search with Levenshtein distance match (distance 1, length <= 4).
Input: keyword = "ippo", productRepository returns [product1]. "ippo" vs "apple" or "iphone". (e.g. "iphne" vs "iphone" distance 1). Let's use keyword "iphne".
Expected Output: product1 (score > 0)
Actual Output: product1 (score > 0)

### Testcase 10
Short Description: Test search with Levenshtein distance match (distance 2, length > 4).
Input: keyword = "iphonxx", productRepository returns [product1]. "iphonxx" vs "iphone" distance 2.
Expected Output: product1 (score > 0)
Actual Output: product1 (score > 0)

### Testcase 11
Short Description: Test search fails when matched tokens ratio is too low (less than 60%).
Input: keyword = "iphone samsung macbook", productRepository returns [product1]. Only "iphone" matches. (1/3 = 33% < 60%).
Expected Output: Empty List
Actual Output: Empty List

### Testcase 12
Short Description: Test search when no tokens match at all.
Input: keyword = "xyzabc", productRepository returns [product1].
Expected Output: Empty List
Actual Output: Empty List

### Testcase 13
Short Description: Test search with product having null normalized name (should compute it from name).
Input: keyword = "macbook", productRepository returns [product3] (normalizedName=null, name="MacBook Air").
Expected Output: product3 (score > 0)
Actual Output: product3 (score > 0)

### Testcase 14
Short Description: Test sorting of search results (by score descending, then name ascending).
Input: keyword = "apple", productRepository returns [product1, product3]. Both match "apple".
Expected Output: product1 (if higher score) or product3 (if higher score or alpha order).
Actual Output: product1, product3 sorted correctly.

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
