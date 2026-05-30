## Information
Service Name: CategoryService
Method Name: deleteCategory(UUID categoryId, UUID replacementCategoryId)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. CategoryRepository
   4. ProductRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Not Active) | TC 4 (Not Admin) | TC 5 (Replacement Null) | TC 6 (Same IDs) | TC 7 (Target Not Found) | TC 8 (Replacement Not Found) | TC 9 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user is not ACTIVE | Fails because user is not BUSINESS_ADMIN | Fails because replacementCategoryId is null | Fails because target and replacement IDs are identical | Fails because target categoryId does not exist | Fails because replacementCategoryId does not exist | Deletes category successfully and updates products |
| **Inputs** | | | | | | | | | |
| categoryId | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" |
| replacementCategoryId | "22222222-2222-2222-2222-222222222222" | "22222222-2222-2222-2222-222222222222" | "22222222-2222-2222-2222-222222222222" | "22222222-2222-2222-2222-222222222222" | null | "11111111-1111-1111-1111-111111111111" | "22222222-2222-2222-2222-222222222222" | "22222222-2222-2222-2222-222222222222" | "22222222-2222-2222-2222-222222222222" |
| **Mock / Context Setup** | | | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" |
| userRepository.findByEmail(...) | N/A | returns empty | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "PENDING" } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } |
| categoryRepository.findById(categoryId) | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | returns { "id": "11111111-1111-1111-1111-111111111111", "name": "Target" } | returns { "id": "11111111-1111-1111-1111-111111111111", "name": "Target" } |
| categoryRepository.findById(replacementCategoryId) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | returns { "id": "22222222-2222-2222-2222-222222222222", "name": "Replacement" } |
| productRepository.updateCategory(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | verify called |
| categoryRepository.delete(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | verify called |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.NOT_FOUND) | Throws BusinessException(HttpStatus.NOT_FOUND) | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | { "id": "11111111-1111-1111-1111-111111111111", "categoryName": "Target", "parentId": null, "categoryImage": null, "categoryDescription": null, "createdAt": null, "updatedAt": null, "message": "Category deleted successfully" } |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "Only business admin can perform this action" | "Replacement category is required" | "Replacement category must be different from target category" | "Category not found" | "Replacement category not found" | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- categoryId:
  - "11111111-1111-1111-1111-111111111111"
- replacementCategoryId:
  - "22222222-2222-2222-2222-222222222222"
  - null
  - "11111111-1111-1111-1111-111111111111"

### MOCK/ CONTEXT SETUP
- SecurityUtil.getCurrentUserLogin():
  - empty
  - "admin@example.com"
- userRepository.findByEmail(...):
  - N/A
  - empty
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "PENDING" }
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } }
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }
- categoryRepository.findById(categoryId):
  - N/A
  - empty
  - { "id": "11111111-1111-1111-1111-111111111111", "name": "Target" }
- categoryRepository.findById(replacementCategoryId):
  - N/A
  - empty
  - { "id": "22222222-2222-2222-2222-222222222222", "name": "Replacement" }
- productRepository.updateCategory(...):
  - N/A
  - verify called
- categoryRepository.delete(...):
  - N/A
  - verify called

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - Throws BusinessException(HttpStatus.NOT_FOUND)
  - N/A
- Expected Return:
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "categoryName": "Target", "parentId": null, "categoryImage": null, "categoryDescription": null, "createdAt": null, "updatedAt": null, "message": "Category deleted successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Only business admin can perform this action"
  - "Replacement category is required"
  - "Replacement category must be different from target category"
  - "Category not found"
  - "Replacement category not found"
  - N/A
