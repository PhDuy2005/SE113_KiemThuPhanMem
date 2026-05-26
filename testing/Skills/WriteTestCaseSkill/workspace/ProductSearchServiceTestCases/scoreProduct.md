---
name: unit-test-scoreProduct-condition-coverage
description: Unit tests with 100% condition coverage for ProductSearchService.scoreProduct method.
---

## Infomation
Service Name: ProductSearchService
Method Name: scoreProduct(Product product, String normalizedKeyword, List<String> queryTokens)
Mock class: None (Internal logic)

Mock Data:
   - product1: Product {name: "iPhone 15 Pro", brand: "Apple", normalizedName: "iphone 15 pro"}
   - product2: Product {name: "Samsung Galaxy", brand: "Samsung", normalizedName: null}
   - queryTokensSingle: ["iphone"]
   - queryTokensMulti: ["iphone", "15", "pro"]
   - queryTokensNoMatch: ["xyz"]

## Testcase
### Testcase 1
Short Description: Test scoreProduct when product has no normalized name (computed from name).
Input: product = product2, normalizedKeyword = "samsung", queryTokens = ["samsung"].
Expected Output: score > 0
Actual Output: score > 0

### Testcase 2
Short Description: Test scoreProduct when product details result in no searchable tokens.
Input: product = Product {name: " ", brand: " "}, normalizedKeyword = "test", queryTokens = ["test"].
Expected Output: 0
Actual Output: 0

### Testcase 3
Short Description: Test scoreProduct when searchable text contains the exact normalized keyword.
Input: product = product1, normalizedKeyword = "iphone 15", queryTokens = ["iphone", "15"].
Expected Output: score >= 100 (exact match bonus)
Actual Output: score >= 100 (exact match bonus)

### Testcase 4
Short Description: Test scoreProduct when no query tokens match the product tokens.
Input: product = product1, normalizedKeyword = "samsung", queryTokens = ["samsung"].
Expected Output: 0
Actual Output: 0

### Testcase 5
Short Description: Test scoreProduct when match ratio is too low for a multi-token query (e.g. 1/3 matches < 60%).
Input: product = product1, normalizedKeyword = "iphone samsung macbook", queryTokens = ["iphone", "samsung", "macbook"].
Expected Output: 0
Actual Output: 0

### Testcase 6
Short Description: Test scoreProduct when match ratio is sufficient for a multi-token query (e.g. 2/3 matches >= 60%).
Input: product = product1, normalizedKeyword = "iphone 15 ultra", queryTokens = ["iphone", "15", "ultra"].
Expected Output: score > 0
Actual Output: score > 0

### Testcase 7
Short Description: Test scoreProduct with a single token query that matches.
Input: product = product1, normalizedKeyword = "iphone", queryTokens = ["iphone"].
Expected Output: score > 0
Actual Output: score > 0

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
