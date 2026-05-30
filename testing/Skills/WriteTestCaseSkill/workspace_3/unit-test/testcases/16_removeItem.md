## Information
Service Name: CartService
Method Name: removeItem(UUID productId)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. CartRepository
   4. CartItemRepository
   5. InventoryRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Not Active) | TC 4 (Cart Not Found) | TC 5 (Cart Item Not Found) | TC 6 (Success Empty Inv) | TC 7 (Success Present Inv) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user is not ACTIVE | Fails because user does not have a cart | Fails because product is not in the cart | Removes item successfully when inventory is empty | Removes item successfully when inventory has stock |
| **Inputs** | | | | | | | |
| productId | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" |
| **Mock / Context Setup** | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" |
| userRepository.findByEmail(...) | N/A | returns empty | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "PENDING" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } |
| cartRepository.findByUserId(...) | N/A | N/A | N/A | returns empty | returns { "id": "33333333-3333-3333-3333-333333333333" } | returns { "id": "33333333-3333-3333-3333-333333333333" } | returns { "id": "33333333-3333-3333-3333-333333333333" } |
| cartItemRepository.findByCartIdAndProductId(...) | N/A | N/A | N/A | N/A | returns empty | returns { "quantity": 5 } | returns { "quantity": 5 } |
| cartItemRepository.delete(...) | N/A | N/A | N/A | N/A | N/A | verify called | verify called |
| cartItemRepository.getTotalItemsCount(...) | N/A | N/A | N/A | N/A | N/A | returns 0 | returns 0 |
| inventoryRepository.findById(...) | N/A | N/A | N/A | N/A | N/A | returns empty | returns { "availableQuantity": 15 } |
| cartItemRepository.findByCartIdWithProduct(...) | N/A | N/A | N/A | N/A | N/A | returns [] | returns [] |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.NOT_FOUND) | Throws BusinessException(HttpStatus.NOT_FOUND) | N/A | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | { "cartId": "33333333-3333-3333-3333-333333333333", "productId": "11111111-1111-1111-1111-111111111111", "quantity": 0, "cartBadgeCount": 0, "availableQuantity": 0, "totalCartPrice": 0.0, "message": "Cart item removed successfully" } | { "cartId": "33333333-3333-3333-3333-333333333333", "productId": "11111111-1111-1111-1111-111111111111", "quantity": 0, "cartBadgeCount": 0, "availableQuantity": 15, "totalCartPrice": 0.0, "message": "Cart item removed successfully" } |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "Cart not found" | "Cart item not found" | N/A | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- productId:
  - "11111111-1111-1111-1111-111111111111"

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
- cartItemRepository.delete(...):
  - N/A
  - verify called
- cartItemRepository.getTotalItemsCount(...):
  - N/A
  - 0
- inventoryRepository.findById(...):
  - N/A
  - empty
  - { "availableQuantity": 15 }
- cartItemRepository.findByCartIdWithProduct(...):
  - N/A
  - []

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.NOT_FOUND)
  - N/A
- Expected Return:
  - N/A
  - { "cartId": "33333333-3333-3333-3333-333333333333", "productId": "11111111-1111-1111-1111-111111111111", "quantity": 0, "cartBadgeCount": 0, "availableQuantity": 0, "totalCartPrice": 0.0, "message": "Cart item removed successfully" }
  - { "cartId": "33333333-3333-3333-3333-333333333333", "productId": "11111111-1111-1111-1111-111111111111", "quantity": 0, "cartBadgeCount": 0, "availableQuantity": 15, "totalCartPrice": 0.0, "message": "Cart item removed successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Cart not found"
  - "Cart item not found"
  - N/A
