## Information
Service Name: ProductManagementService
Method Name: updateStock(UUID productId, Integer newStock)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. ProductRepository
   4. InventoryRepository
   5. ProductImageRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Not Active) | TC 4 (Not Admin) | TC 5 (Missing Stock) | TC 6 (Negative Stock) | TC 7 (Product Not Found) | TC 8 (Success Empty Inv, Stock=0) | TC 9 (Success Null Reserved, Stock>0, OUT_OF_STOCK) | TC 10 (Success Valid Inv, Stock>0, ACTIVE) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user is not ACTIVE | Fails because user is not BUSINESS_ADMIN | Fails because newStock is null | Fails because newStock is negative | Fails because productId does not exist | Updates successfully: empty inventory created, status becomes OUT_OF_STOCK | Updates successfully: reserved null becomes 0, status becomes ACTIVE | Updates successfully: valid inventory updated, status remains ACTIVE |
| **Inputs** | | | | | | | | | | |
| productId | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" |
| newStock | 50 | 50 | 50 | 50 | null | -1 | 50 | 0 | 50 | 50 |
| **Mock / Context Setup** | | | | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" |
| userRepository.findByEmail(...) | N/A | returns empty | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "PENDING" } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } |
| productRepository.findById(productId) | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | returns { "id": "11111111-1111-1111-1111-111111111111", "status": "ACTIVE" } | returns { "id": "11111111-1111-1111-1111-111111111111", "status": "OUT_OF_STOCK" } | returns { "id": "11111111-1111-1111-1111-111111111111", "status": "ACTIVE" } |
| inventoryRepository.findById(productId) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | returns { "productId": "11111111-1111-1111-1111-111111111111", "quantity": 0, "reservedQuantity": null } | returns { "productId": "11111111-1111-1111-1111-111111111111", "quantity": 10, "reservedQuantity": 5 } |
| inventoryRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | verify called | verify called | verify called |
| productRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns { "id": "11111111-1111-1111-1111-111111111111", "status": "OUT_OF_STOCK" } | returns { "id": "11111111-1111-1111-1111-111111111111", "status": "ACTIVE" } | returns { "id": "11111111-1111-1111-1111-111111111111", "status": "ACTIVE" } |
| productImageRepository.findByProductId... | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns [] | returns [] | returns [] |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.NOT_FOUND) | N/A | N/A | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | N/A | N/A | { "id": "11111111-1111-1111-1111-111111111111", "name": null, "description": null, "price": null, "status": "OUT_OF_STOCK", "brand": null, "categoryId": null, "stock": null, "imageUrls": [], "primaryImage": null, "message": "Product stock updated successfully" } | { "id": "11111111-1111-1111-1111-111111111111", "name": null, "description": null, "price": null, "status": "ACTIVE", "brand": null, "categoryId": null, "stock": 50, "imageUrls": [], "primaryImage": null, "message": "Product stock updated successfully" } | { "id": "11111111-1111-1111-1111-111111111111", "name": null, "description": null, "price": null, "status": "ACTIVE", "brand": null, "categoryId": null, "stock": 50, "imageUrls": [], "primaryImage": null, "message": "Product stock updated successfully" } |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "Only business admin can perform this action" | "Required field is missing" | "Stock must not be negative" | "Product not found" | N/A | N/A | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- productId:
  - "11111111-1111-1111-1111-111111111111"
- newStock:
  - 50
  - null
  - -1
  - 0

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
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "OUT_OF_STOCK" }
- inventoryRepository.findById(productId):
  - N/A
  - empty
  - { "productId": "11111111-1111-1111-1111-111111111111", "quantity": 0, "reservedQuantity": null }
  - { "productId": "11111111-1111-1111-1111-111111111111", "quantity": 10, "reservedQuantity": 5 }
- inventoryRepository.save(...):
  - N/A
  - verify called
- productRepository.save(...):
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "OUT_OF_STOCK" }
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "ACTIVE" }
- productImageRepository.findByProductId...:
  - N/A
  - []

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - Throws BusinessException(HttpStatus.NOT_FOUND)
  - N/A
- Expected Return:
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "name": null, "description": null, "price": null, "status": "OUT_OF_STOCK", "brand": null, "categoryId": null, "stock": null, "imageUrls": [], "primaryImage": null, "message": "Product stock updated successfully" }
  - { "id": "11111111-1111-1111-1111-111111111111", "name": null, "description": null, "price": null, "status": "ACTIVE", "brand": null, "categoryId": null, "stock": 50, "imageUrls": [], "primaryImage": null, "message": "Product stock updated successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Only business admin can perform this action"
  - "Required field is missing"
  - "Stock must not be negative"
  - "Product not found"
  - N/A
