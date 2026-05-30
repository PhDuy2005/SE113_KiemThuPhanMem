## Information
Service Name: ProductManagementService
Method Name: updateProduct(UUID productId, ReqUpdateProductGeneralDTO request)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. ProductRepository
   4. CategoryRepository
   5. InventoryRepository
   6. ProductImageRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Not Active) | TC 4 (Not Admin) | TC 5 (Product Not Found) | TC 6 (Category Not Found) | TC 7 (Update All Fields Valid) | TC 8 (Update Blank Strings) | TC 9 (Update Null Fields) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user is not ACTIVE | Fails because user is not BUSINESS_ADMIN | Fails because productId does not exist | Fails because categoryId does not exist | Updates all provided valid fields successfully | Updates with blank strings to trigger cleanNullableText | Updates with all null fields, making no changes |
| **Inputs** | | | | | | | | | |
| productId | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "invalid-1111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" |
| request.name | "New Name" | "New Name" | "New Name" | "New Name" | "New Name" | "New Name" | "New Name" | "   " | null |
| request.description | "New Desc" | "New Desc" | "New Desc" | "New Desc" | "New Desc" | "New Desc" | "New Desc" | "   " | null |
| request.categoryId | "22222222-2222-2222-2222-222222222222" | "22222222-2222-2222-2222-222222222222" | "22222222-2222-2222-2222-222222222222" | "22222222-2222-2222-2222-222222222222" | "22222222-2222-2222-2222-222222222222" | "invalid-2222" | "22222222-2222-2222-2222-222222222222" | null | null |
| request.brand | "New Brand" | "New Brand" | "New Brand" | "New Brand" | "New Brand" | "New Brand" | "New Brand" | "   " | null |
| request.status | "ACTIVE" | "ACTIVE" | "ACTIVE" | "ACTIVE" | "ACTIVE" | "ACTIVE" | "ACTIVE" | "   " | null |
| **Mock / Context Setup** | | | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" |
| userRepository.findByEmail(...) | N/A | returns empty | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "PENDING" } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } |
| productRepository.findById(productId) | N/A | N/A | N/A | N/A | returns empty | returns { "id": "11111111-1111-1111-1111-111111111111", "name": "Old", "description": "Old", "categoryId": "22222222-2222-2222-2222-222222222222", "brand": "Old", "status": "ACTIVE" } | returns { "id": "11111111-1111-1111-1111-111111111111", "name": "Old", "description": "Old", "categoryId": "22222222-2222-2222-2222-222222222222", "brand": "Old", "status": "ACTIVE" } | returns { "id": "11111111-1111-1111-1111-111111111111", "name": "Old", "description": "Old", "categoryId": "22222222-2222-2222-2222-222222222222", "brand": "Old", "status": "ACTIVE" } | returns { "id": "11111111-1111-1111-1111-111111111111", "name": "Old", "description": "Old", "categoryId": "22222222-2222-2222-2222-222222222222", "brand": "Old", "status": "ACTIVE" } |
| categoryRepository.findById(categoryId) | N/A | N/A | N/A | N/A | N/A | returns empty | returns { "id": "22222222-2222-2222-2222-222222222222", "name": "New Category" } | N/A | N/A |
| productRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | returns { "id": "11111111-1111-1111-1111-111111111111", "name": "New Name", "description": "New Desc", "categoryId": "22222222-2222-2222-2222-222222222222", "brand": "New Brand", "status": "ACTIVE" } | returns { "id": "11111111-1111-1111-1111-111111111111", "name": "Old", "description": null, "categoryId": "22222222-2222-2222-2222-222222222222", "brand": null, "status": "ACTIVE" } | returns { "id": "11111111-1111-1111-1111-111111111111", "name": "Old", "description": "Old", "categoryId": "22222222-2222-2222-2222-222222222222", "brand": "Old", "status": "ACTIVE" } |
| inventoryRepository.findById(productId) | N/A | N/A | N/A | N/A | N/A | N/A | returns { "quantity": 15 } | returns { "quantity": 15 } | returns { "quantity": 15 } |
| productImageRepository.findByProductId... | N/A | N/A | N/A | N/A | N/A | N/A | returns [{ "imageUrl": "/img.jpg" }] | returns [{ "imageUrl": "/img.jpg" }] | returns [{ "imageUrl": "/img.jpg" }] |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.NOT_FOUND) | Throws BusinessException(HttpStatus.NOT_FOUND) | N/A | N/A | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | N/A | { "id": "11111111-1111-1111-1111-111111111111", "name": "New Name", "description": "New Desc", "price": null, "status": "ACTIVE", "brand": "New Brand", "categoryId": "22222222-2222-2222-2222-222222222222", "stock": 15, "imageUrls": ["/img.jpg"], "primaryImage": "/img.jpg", "message": "Product details updated successfully" } | { "id": "11111111-1111-1111-1111-111111111111", "name": "Old", "description": null, "price": null, "status": "ACTIVE", "brand": null, "categoryId": "22222222-2222-2222-2222-222222222222", "stock": 15, "imageUrls": ["/img.jpg"], "primaryImage": "/img.jpg", "message": "Product details updated successfully" } | { "id": "11111111-1111-1111-1111-111111111111", "name": "Old", "description": "Old", "price": null, "status": "ACTIVE", "brand": "Old", "categoryId": "22222222-2222-2222-2222-222222222222", "stock": 15, "imageUrls": ["/img.jpg"], "primaryImage": "/img.jpg", "message": "Product details updated successfully" } |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "Only business admin can perform this action" | "Product not found" | "Category not found" | N/A | N/A | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- productId:
  - "11111111-1111-1111-1111-111111111111"
  - "invalid-1111"
- request.name:
  - "New Name"
  - "   "
  - null
- request.description:
  - "New Desc"
  - "   "
  - null
- request.categoryId:
  - "22222222-2222-2222-2222-222222222222"
  - "invalid-2222"
  - null
- request.brand:
  - "New Brand"
  - "   "
  - null
- request.status:
  - "ACTIVE"
  - "   "
  - null

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
- productRepository.findById(productId):
  - N/A
  - empty
  - { "id": "11111111-1111-1111-1111-111111111111", "name": "Old", "description": "Old", "categoryId": "22222222-2222-2222-2222-222222222222", "brand": "Old", "status": "ACTIVE" }
- categoryRepository.findById(categoryId):
  - N/A
  - empty
  - { "id": "22222222-2222-2222-2222-222222222222", "name": "New Category" }
- productRepository.save(...):
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "name": "New Name", "description": "New Desc", "categoryId": "22222222-2222-2222-2222-222222222222", "brand": "New Brand", "status": "ACTIVE" }
  - { "id": "11111111-1111-1111-1111-111111111111", "name": "Old", "description": null, "categoryId": "22222222-2222-2222-2222-222222222222", "brand": null, "status": "ACTIVE" }
  - { "id": "11111111-1111-1111-1111-111111111111", "name": "Old", "description": "Old", "categoryId": "22222222-2222-2222-2222-222222222222", "brand": "Old", "status": "ACTIVE" }
- inventoryRepository.findById(productId):
  - N/A
  - { "quantity": 15 }
- productImageRepository.findByProductId...:
  - N/A
  - [{ "imageUrl": "/img.jpg" }]

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.NOT_FOUND)
  - N/A
- Expected Return:
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "name": "New Name", "description": "New Desc", "price": null, "status": "ACTIVE", "brand": "New Brand", "categoryId": "22222222-2222-2222-2222-222222222222", "stock": 15, "imageUrls": ["/img.jpg"], "primaryImage": "/img.jpg", "message": "Product details updated successfully" }
  - { "id": "11111111-1111-1111-1111-111111111111", "name": "Old", "description": null, "price": null, "status": "ACTIVE", "brand": null, "categoryId": "22222222-2222-2222-2222-222222222222", "stock": 15, "imageUrls": ["/img.jpg"], "primaryImage": "/img.jpg", "message": "Product details updated successfully" }
  - { "id": "11111111-1111-1111-1111-111111111111", "name": "Old", "description": "Old", "price": null, "status": "ACTIVE", "brand": "Old", "categoryId": "22222222-2222-2222-2222-222222222222", "stock": 15, "imageUrls": ["/img.jpg"], "primaryImage": "/img.jpg", "message": "Product details updated successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Only business admin can perform this action"
  - "Product not found"
  - "Category not found"
  - N/A
