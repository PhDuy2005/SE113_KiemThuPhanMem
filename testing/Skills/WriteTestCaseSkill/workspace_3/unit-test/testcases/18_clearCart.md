## Information
Service Name: CartService
Method Name: clearCart()
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. CartRepository
   4. CartItemRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Not Active) | TC 4 (Cart Not Found) | TC 5 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user is not ACTIVE | Fails because user does not have a cart | Clears the cart successfully |
| **Inputs** | | | | | |
| (No parameters) | N/A | N/A | N/A | N/A | N/A |
| **Mock / Context Setup** | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" |
| userRepository.findByEmail(...) | N/A | returns empty | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "PENDING" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } |
| cartRepository.findByUserId(...) | N/A | N/A | N/A | returns empty | returns { "id": "33333333-3333-3333-3333-333333333333" } |
| cartItemRepository.deleteByCartId(...) | N/A | N/A | N/A | N/A | verify called |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.NOT_FOUND) | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A (void) |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "Cart not found" | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- (No parameters):
  - N/A

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
- cartItemRepository.deleteByCartId(...):
  - N/A
  - verify called

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.NOT_FOUND)
  - N/A
- Expected Return:
  - N/A
  - N/A (void)
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Cart not found"
  - N/A
