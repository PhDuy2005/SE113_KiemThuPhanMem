## Information
Service Name: ProductManagementService
Method Name: createProduct(String name, String description, BigDecimal price, UUID categoryId, Integer stock, String brand, List<MultipartFile> images)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. CategoryRepository
   4. ProductRepository
   5. ProductImageRepository
   6. InventoryRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Not Active) | TC 4 (Not Admin) | TC 5 (Missing Field) | TC 6 (Zero Price) | TC 7 (Negative Stock) | TC 8 (No Image) | TC 9 (Invalid Image) | TC 10 (Category Not Found) | TC 11 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user is not ACTIVE | Fails because user is not BUSINESS_ADMIN | Fails because name is null | Fails because price is 0 | Fails because stock is negative | Fails because images list is empty | Fails because image extension is txt | Fails because category does not exist | Creates product successfully |
| **Inputs** | | | | | | | | | | | |
| name | "Product" | "Product" | "Product" | "Product" | null | "Product" | "Product" | "Product" | "Product" | "Product" | "Product" |
| description | null | null | null | null | null | null | null | null | null | null | null |
| price | 100.0 | 100.0 | 100.0 | 100.0 | 100.0 | 0.0 | 100.0 | 100.0 | 100.0 | 100.0 | 100.0 |
| categoryId | "550e8400-e29b-41d4-a716-446655440000" | "550e8400-e29b-41d4-a716-446655440000" | "550e8400-e29b-41d4-a716-446655440000" | "550e8400-e29b-41d4-a716-446655440000" | "550e8400-e29b-41d4-a716-446655440000" | "550e8400-e29b-41d4-a716-446655440000" | "550e8400-e29b-41d4-a716-446655440000" | "550e8400-e29b-41d4-a716-446655440000" | "550e8400-e29b-41d4-a716-446655440000" | "550e8400-e29b-41d4-a716-446655440000" | "550e8400-e29b-41d4-a716-446655440000" |
| stock | 10 | 10 | 10 | 10 | 10 | 10 | -1 | 10 | 10 | 10 | 10 |
| brand | "Apple" | "Apple" | "Apple" | "Apple" | "Apple" | "Apple" | "Apple" | "Apple" | "Apple" | "Apple" | "Apple" |
| images | [{ "originalFilename": "test.jpg", "size": 1024, "isEmpty": false }] | [{ "originalFilename": "test.jpg", "size": 1024, "isEmpty": false }] | [{ "originalFilename": "test.jpg", "size": 1024, "isEmpty": false }] | [{ "originalFilename": "test.jpg", "size": 1024, "isEmpty": false }] | [{ "originalFilename": "test.jpg", "size": 1024, "isEmpty": false }] | [{ "originalFilename": "test.jpg", "size": 1024, "isEmpty": false }] | [{ "originalFilename": "test.jpg", "size": 1024, "isEmpty": false }] | [] | [{ "originalFilename": "test.txt", "size": 1024, "isEmpty": false }] | [{ "originalFilename": "test.jpg", "size": 1024, "isEmpty": false }] | [{ "originalFilename": "test.jpg", "size": 1024, "isEmpty": false }] |
| **Mock / Context Setup** | | | | | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" |
| userRepository.findByEmail(...) | N/A | returns empty | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "PENDING" } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } |
| categoryRepository.findById(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "name": "Electronics" } |
| productRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns { "id": "11111111-1111-1111-1111-111111111111", "name": "Product", "description": null, "price": 100.0, "status": "ACTIVE", "brand": "Apple", "categoryId": "550e8400-e29b-41d4-a716-446655440000" } |
| productImageRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | verify called |
| inventoryRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | verify called |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.NOT_FOUND) | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | { "id": "11111111-1111-1111-1111-111111111111", "name": "Product", "description": null, "price": 100.0, "status": "ACTIVE", "brand": "Apple", "categoryId": "550e8400-e29b-41d4-a716-446655440000", "stock": 10, "imageUrls": ["/storage/products/33333333-3333-3333-3333-333333333333.jpg"], "primaryImage": "/storage/products/33333333-3333-3333-3333-333333333333.jpg", "message": "Product created successfully" } |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "Only business admin can perform this action" | "Required field is missing" | "Price must be greater than 0" | "Stock must not be negative" | "At least one product image is required" | "Product image must be jpg, png or webp and no larger than 5MB" | "Category not found" | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- name:
  - "Product"
  - null
- description:
  - null
- price:
  - 100.0
  - 0.0
- categoryId:
  - "550e8400-e29b-41d4-a716-446655440000"
- stock:
  - 10
  - -1
- brand:
  - "Apple"
- images:
  - [{ "originalFilename": "test.jpg", "size": 1024, "isEmpty": false }]
  - []
  - [{ "originalFilename": "test.txt", "size": 1024, "isEmpty": false }]

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
- categoryRepository.findById(...):
  - N/A
  - empty
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "name": "Electronics" }
- productRepository.save(...):
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "name": "Product", "description": null, "price": 100.0, "status": "ACTIVE", "brand": "Apple", "categoryId": "550e8400-e29b-41d4-a716-446655440000" }
- productImageRepository.save(...):
  - N/A
  - verify called
- inventoryRepository.save(...):
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
  - { "id": "11111111-1111-1111-1111-111111111111", "name": "Product", "description": null, "price": 100.0, "status": "ACTIVE", "brand": "Apple", "categoryId": "550e8400-e29b-41d4-a716-446655440000", "stock": 10, "imageUrls": ["/storage/products/33333333-3333-3333-3333-333333333333.jpg"], "primaryImage": "/storage/products/33333333-3333-3333-3333-333333333333.jpg", "message": "Product created successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Only business admin can perform this action"
  - "Required field is missing"
  - "Price must be greater than 0"
  - "Stock must not be negative"
  - "At least one product image is required"
  - "Product image must be jpg, png or webp and no larger than 5MB"
  - "Category not found"
  - N/A
