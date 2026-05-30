## Information
Service Name: CartService
Method Name: updateItemQuantity(UUID productId, ReqUpdateCartItemDTO request)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. CartRepository
   4. CartItemRepository
   5. InventoryRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Not Active) | TC 4 (Cart Not Found) | TC 5 (Item Not Found) | TC 6 (Fail, Auto-Update) | TC 7 (Fail, Auto-Delete) | TC 8 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user is not ACTIVE | Fails because user does not have a cart | Fails because product is not in the cart | Fails due to insufficient stock > 0, auto-updates to max available | Fails due to insufficient stock (0 available), auto-deletes item | Updates cart item quantity successfully |
| **Inputs** | | | | | | | | |
| productId | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" |
| request.newQuantity | 10 | 10 | 10 | 10 | 10 | 10 | 10 | 10 |
| **Mock / Context Setup** | | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" |
| userRepository.findByEmail(...) | N/A | returns empty | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "PENDING" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } |
| cartRepository.findByUserId(...) | N/A | N/A | N/A | returns empty | returns { "id": "33333333-3333-3333-3333-333333333333" } | returns { "id": "33333333-3333-3333-3333-333333333333" } | returns { "id": "33333333-3333-3333-3333-333333333333" } | returns { "id": "33333333-3333-3333-3333-333333333333" } |
| cartItemRepository.findByCartIdAndProductId(...) | N/A | N/A | N/A | N/A | returns empty | returns { "quantity": 5 } | returns { "quantity": 5 } | returns { "quantity": 5 } |
| inventoryRepository.findById(...) | N/A | N/A | N/A | N/A | N/A | returns { "availableQuantity": 5 } | returns empty | returns { "availableQuantity": 20 } |
| cartItemRepository.save(...) | N/A | N/A | N/A | N/A | N/A | verify called | N/A | returns { "quantity": 10 } |
| cartItemRepository.delete(...) | N/A | N/A | N/A | N/A | N/A | N/A | verify called | N/A |
| cartItemRepository.getTotalItemsCount(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns 1 |
| cartItemRepository.findByCartIdWithProduct(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns [{ "product": { "price": 100.0 }, "quantity": 10 }] |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.NOT_FOUND) | Throws BusinessException(HttpStatus.NOT_FOUND) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | N/A | N/A | { "cartId": "33333333-3333-3333-3333-333333333333", "productId": "11111111-1111-1111-1111-111111111111", "quantity": 10, "cartBadgeCount": 1, "availableQuantity": 20, "totalCartPrice": 1000.0, "message": "Cart item quantity updated successfully" } |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "Cart not found" | "Cart item not found" | "Insufficient stock. Available quantity: 5" | "Insufficient stock. Available quantity: 0" | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- productId:
  - "11111111-1111-1111-1111-111111111111"
- request.newQuantity:
  - 10

### MOCK/ CONTEXT SETUP
- SecurityUtil.getCurrentUserLogin():
  - empty
  - "user@example.com"
- userRepository.findByEmail(...):
  - N/A
  - empty
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "PENDING" }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }
- cartRepository.findByUserId(...):
  - N/A
  - empty
  - { "id": "33333333-3333-3333-3333-333333333333" }
- cartItemRepository.findByCartIdAndProductId(...):
  - N/A
  - empty
  - { "quantity": 5 }
- inventoryRepository.findById(...):
  - N/A
  - empty
  - { "availableQuantity": 5 }
  - { "availableQuantity": 20 }
- cartItemRepository.save(...):
  - N/A
  - verify called
  - { "quantity": 10 }
- cartItemRepository.delete(...):
  - N/A
  - verify called
- cartItemRepository.getTotalItemsCount(...):
  - N/A
  - 1
- cartItemRepository.findByCartIdWithProduct(...):
  - N/A
  - [{ "product": { "price": 100.0 }, "quantity": 10 }]

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.NOT_FOUND)
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - N/A
- Expected Return:
  - N/A
  - { "cartId": "33333333-3333-3333-3333-333333333333", "productId": "11111111-1111-1111-1111-111111111111", "quantity": 10, "cartBadgeCount": 1, "availableQuantity": 20, "totalCartPrice": 1000.0, "message": "Cart item quantity updated successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Cart not found"
  - "Cart item not found"
  - "Insufficient stock. Available quantity: 5"
  - "Insufficient stock. Available quantity: 0"
  - N/A
