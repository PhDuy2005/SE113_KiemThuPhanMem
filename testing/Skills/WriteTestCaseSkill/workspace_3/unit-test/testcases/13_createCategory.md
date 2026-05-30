## Information
Service Name: CategoryService
Method Name: createCategory(ReqCreateCategoryDTO request)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. CategoryRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Not Active) | TC 4 (Not Admin) | TC 5 (Request Null) | TC 6 (Name Null) | TC 7 (Name Blank) | TC 8 (Name Exists) | TC 9 (Success All Fields) | TC 10 (Success Null/Blank Fields) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user is not ACTIVE | Fails because user is not BUSINESS_ADMIN | Fails because request object is null | Fails because categoryName is null | Fails because categoryName is blank | Fails because categoryName already exists | Creates category successfully with all valid fields | Creates category successfully while handling null and blank optional fields |
| **Inputs** | | | | | | | | | | |
| request | { "categoryName": "Electronics" } | { "categoryName": "Electronics" } | { "categoryName": "Electronics" } | { "categoryName": "Electronics" } | null | { "categoryName": null } | { "categoryName": "   " } | { "categoryName": "Electronics" } | { "categoryName": "Electronics", "parentId": "22222222-2222-2222-2222-222222222222", "categoryImage": "image.jpg", "categoryDescription": "Desc" } | { "categoryName": "Toys", "parentId": null, "categoryImage": null, "categoryDescription": "   " } |
| **Mock / Context Setup** | | | | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" |
| userRepository.findByEmail(...) | N/A | returns empty | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "PENDING" } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } |
| categoryRepository.existsByNameIgnoreCase(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns true | returns false | returns false |
| categoryRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns { "id": "11111111-1111-1111-1111-111111111111", "name": "Electronics", "parentId": "22222222-2222-2222-2222-222222222222", "imageUrl": "image.jpg", "description": "Desc" } | returns { "id": "11111111-1111-1111-1111-111111111111", "name": "Toys", "parentId": null, "imageUrl": null, "description": null } |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.CONFLICT) | N/A | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | { "id": "11111111-1111-1111-1111-111111111111", "categoryName": "Electronics", "parentId": "22222222-2222-2222-2222-222222222222", "categoryImage": "image.jpg", "categoryDescription": "Desc", "createdAt": null, "updatedAt": null, "message": "Category created successfully" } | { "id": "11111111-1111-1111-1111-111111111111", "categoryName": "Toys", "parentId": null, "categoryImage": null, "categoryDescription": null, "createdAt": null, "updatedAt": null, "message": "Category created successfully" } |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "Only business admin can perform this action" | "Category name is required" | "Category name is required" | "Category name is required" | "Category name already exists" | N/A | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- request:
  - { "categoryName": "Electronics" }
  - null
  - { "categoryName": null }
  - { "categoryName": "   " }
  - { "categoryName": "Electronics", "parentId": "22222222-2222-2222-2222-222222222222", "categoryImage": "image.jpg", "categoryDescription": "Desc" }
  - { "categoryName": "Toys", "parentId": null, "categoryImage": null, "categoryDescription": "   " }

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
- categoryRepository.existsByNameIgnoreCase(...):
  - N/A
  - true
  - false
- categoryRepository.save(...):
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "name": "Electronics", "parentId": "22222222-2222-2222-2222-222222222222", "imageUrl": "image.jpg", "description": "Desc" }
  - { "id": "11111111-1111-1111-1111-111111111111", "name": "Toys", "parentId": null, "imageUrl": null, "description": null }

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - Throws BusinessException(HttpStatus.CONFLICT)
  - N/A
- Expected Return:
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "categoryName": "Electronics", "parentId": "22222222-2222-2222-2222-222222222222", "categoryImage": "image.jpg", "categoryDescription": "Desc", "createdAt": null, "updatedAt": null, "message": "Category created successfully" }
  - { "id": "11111111-1111-1111-1111-111111111111", "categoryName": "Toys", "parentId": null, "categoryImage": null, "categoryDescription": null, "createdAt": null, "updatedAt": null, "message": "Category created successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Only business admin can perform this action"
  - "Category name is required"
  - "Category name already exists"
  - N/A
