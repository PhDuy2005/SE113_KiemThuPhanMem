## Information
Service Name: ProductManagementService
Method Name: discontinueProduct(UUID productId)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. ProductRepository
   4. InventoryRepository
   5. ProductImageRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Not Active) | TC 4 (Not Admin) | TC 5 (Product Not Found) | TC 6 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user is not ACTIVE | Fails because user is not BUSINESS_ADMIN | Fails because productId does not exist | Discontinues product successfully |
| **Inputs** | | | | | | |
| productId | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" |
| **Mock / Context Setup** | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" |
| userRepository.findByEmail(...) | N/A | returns empty | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "PENDING" } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } |
| productRepository.findById(productId) | N/A | N/A | N/A | N/A | returns empty | returns { "id": "11111111-1111-1111-1111-111111111111", "status": "ACTIVE" } |
| productRepository.save(...) | N/A | N/A | N/A | N/A | N/A | returns { "id": "11111111-1111-1111-1111-111111111111", "status": "DISCONTINUED" } |
| inventoryRepository.findById(productId) | N/A | N/A | N/A | N/A | N/A | returns { "quantity": 10 } |
| productImageRepository.findByProductId... | N/A | N/A | N/A | N/A | N/A | returns [{ "imageUrl": "/img.jpg" }] |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.NOT_FOUND) | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | { "id": "11111111-1111-1111-1111-111111111111", "name": null, "description": null, "price": null, "status": "DISCONTINUED", "brand": null, "categoryId": null, "stock": 10, "imageUrls": ["/img.jpg"], "primaryImage": "/img.jpg", "message": "Product discontinued successfully" } |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "Only business admin can perform this action" | "Product not found" | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- productId:
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
- productRepository.findById(productId):
  - N/A
  - empty
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "ACTIVE" }
- productRepository.save(...):
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "DISCONTINUED" }
- inventoryRepository.findById(productId):
  - N/A
  - { "quantity": 10 }
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
  - { "id": "11111111-1111-1111-1111-111111111111", "name": null, "description": null, "price": null, "status": "DISCONTINUED", "brand": null, "categoryId": null, "stock": 10, "imageUrls": ["/img.jpg"], "primaryImage": "/img.jpg", "message": "Product discontinued successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Only business admin can perform this action"
  - "Product not found"
  - N/A
