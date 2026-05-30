## Information
Service Name: CartService
Method Name: addItem(ReqAddCartItemDTO request)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. ProductRepository
   4. InventoryRepository
   5. CartRepository
   6. CartItemRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Not Active) | TC 4 (Product Not Found) | TC 5 (Insufficient Stock) | TC 6 (Success Empty DBs) | TC 7 (Success Present DBs) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user is not ACTIVE | Fails because active product does not exist | Fails because requested quantity exceeds stock | Adds item successfully when cart and cartItem do not exist | Adds item successfully when cart and cartItem already exist |
| **Inputs** | | | | | | | |
| request.productId | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" |
| request.quantity | 10 | 10 | 10 | 10 | 10 | 0 | 10 |
| **Mock / Context Setup** | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" |
| userRepository.findByEmail(...) | N/A | returns empty | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "PENDING" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } |
| productRepository.findByIdAndStatusIgnoreCase(...) | N/A | N/A | N/A | returns empty | returns { "id": "11111111-1111-1111-1111-111111111111" } | returns { "id": "11111111-1111-1111-1111-111111111111" } | returns { "id": "11111111-1111-1111-1111-111111111111" } |
| inventoryRepository.findById(...) | N/A | N/A | N/A | N/A | returns { "availableQuantity": 5 } | returns empty | returns { "availableQuantity": 20 } |
| cartRepository.findByUserId(...) | N/A | N/A | N/A | N/A | returns { "id": "33333333-3333-3333-3333-333333333333" } | returns empty | returns { "id": "33333333-3333-3333-3333-333333333333" } |
| cartRepository.save(...) | N/A | N/A | N/A | N/A | N/A | returns { "id": "33333333-3333-3333-3333-333333333333" } | N/A |
| cartItemRepository.findByCartIdAndProductId(...) | N/A | N/A | N/A | N/A | returns { "quantity": 0 } | returns empty | returns { "quantity": 5 } |
| cartItemRepository.save(...) | N/A | N/A | N/A | N/A | N/A | returns { "quantity": 0 } | returns { "quantity": 15 } |
| cartItemRepository.getTotalItemsCount(...) | N/A | N/A | N/A | N/A | N/A | returns 0 | returns 1 |
| cartItemRepository.findByCartIdWithProduct(...) | N/A | N/A | N/A | N/A | N/A | returns [] | returns [{ "product": { "price": 100.0 }, "quantity": 15 }] |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.NOT_FOUND) | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | { "cartId": "33333333-3333-3333-3333-333333333333", "productId": "11111111-1111-1111-1111-111111111111", "quantity": 0, "cartBadgeCount": 0, "availableQuantity": 0, "totalCartPrice": 0.0, "message": "Product added to cart successfully" } | { "cartId": "33333333-3333-3333-3333-333333333333", "productId": "11111111-1111-1111-1111-111111111111", "quantity": 15, "cartBadgeCount": 1, "availableQuantity": 20, "totalCartPrice": 1500.0, "message": "Product added to cart successfully" } |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "Product not found" | "Insufficient stock. Available quantity: 5" | N/A | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- request.productId:
  - "11111111-1111-1111-1111-111111111111"
- request.quantity:
  - 10
  - 0

### MOCK/ CONTEXT SETUP
- SecurityUtil.getCurrentUserLogin():
  - empty
  - "user@example.com"
- userRepository.findByEmail(...):
  - N/A
  - empty
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "PENDING" }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }
- productRepository.findByIdAndStatusIgnoreCase(...):
  - N/A
  - empty
  - { "id": "11111111-1111-1111-1111-111111111111" }
- inventoryRepository.findById(...):
  - N/A
  - empty
  - { "availableQuantity": 5 }
  - { "availableQuantity": 20 }
- cartRepository.findByUserId(...):
  - N/A
  - empty
  - { "id": "33333333-3333-3333-3333-333333333333" }
- cartRepository.save(...):
  - N/A
  - { "id": "33333333-3333-3333-3333-333333333333" }
- cartItemRepository.findByCartIdAndProductId(...):
  - N/A
  - empty
  - { "quantity": 0 }
  - { "quantity": 5 }
- cartItemRepository.save(...):
  - N/A
  - { "quantity": 0 }
  - { "quantity": 15 }
- cartItemRepository.getTotalItemsCount(...):
  - N/A
  - 0
  - 1
- cartItemRepository.findByCartIdWithProduct(...):
  - N/A
  - []
  - [{ "product": { "price": 100.0 }, "quantity": 15 }]

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.NOT_FOUND)
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - N/A
- Expected Return:
  - N/A
  - { "cartId": "33333333-3333-3333-3333-333333333333", "productId": "11111111-1111-1111-1111-111111111111", "quantity": 0, "cartBadgeCount": 0, "availableQuantity": 0, "totalCartPrice": 0.0, "message": "Product added to cart successfully" }
  - { "cartId": "33333333-3333-3333-3333-333333333333", "productId": "11111111-1111-1111-1111-111111111111", "quantity": 15, "cartBadgeCount": 1, "availableQuantity": 20, "totalCartPrice": 1500.0, "message": "Product added to cart successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Product not found"
  - "Insufficient stock. Available quantity: 5"
  - N/A
