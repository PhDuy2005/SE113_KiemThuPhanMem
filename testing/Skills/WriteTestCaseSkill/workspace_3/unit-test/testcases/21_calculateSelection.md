## Information
Service Name: CheckoutService
Method Name: calculateSelection(ReqCheckoutSelectionDTO request)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. CartRepository
   4. CartItemRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Not Active) | TC 4 (Cart Not Found) | TC 5 (Selection Empty) | TC 6 (Invalid Products) | TC 7 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user is not ACTIVE | Fails because user does not have a cart | Fails because selectedProductIds is empty or null | Fails because some product IDs are not in the cart | Calculates temp total successfully |
| **Inputs** | | | | | | | |
| request | { "selectedProductIds": ["11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222"] } | { "selectedProductIds": ["11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222"] } | { "selectedProductIds": ["11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222"] } | { "selectedProductIds": ["11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222"] } | { "selectedProductIds": [] } | { "selectedProductIds": ["11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222"] } | { "selectedProductIds": ["11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222"] } |
| **Mock / Context Setup** | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" |
| userRepository.findByEmail(...) | N/A | returns empty | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "PENDING" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" } |
| cartRepository.findByUserId(...) | N/A | N/A | N/A | returns empty | returns { "id": "33333333-3333-3333-3333-333333333333" } | returns { "id": "33333333-3333-3333-3333-333333333333" } | returns { "id": "33333333-3333-3333-3333-333333333333" } |
| cartItemRepository.findSelectedByCartIdWithProduct(...) | N/A | N/A | N/A | N/A | N/A | returns [ { "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100.0 }, "quantity": 2 } ] | returns [ { "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100.0 }, "quantity": 2 }, { "product": { "id": "22222222-2222-2222-2222-222222222222", "price": 50.0 }, "quantity": 1 } ] |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.NOT_FOUND) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | N/A | { "selectedProductIds": ["11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222"], "tempTotalPrice": 250.0, "shippingFee": 0.0, "discountAmount": 0.0, "totalPrice": 250.0, "checkoutUrl": "/checkout", "message": null } |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "Cart not found" | "You must select at least one product" | "Selected products are invalid" | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- request:
  - { "selectedProductIds": ["11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222"] }
  - { "selectedProductIds": [] }

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
- cartItemRepository.findSelectedByCartIdWithProduct(...):
  - N/A
  - returns [ { "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100.0 }, "quantity": 2 } ]
  - returns [ { "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100.0 }, "quantity": 2 }, { "product": { "id": "22222222-2222-2222-2222-222222222222", "price": 50.0 }, "quantity": 1 } ]

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.NOT_FOUND)
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - N/A
- Expected Return:
  - N/A
  - { "selectedProductIds": ["11111111-1111-1111-1111-111111111111", "22222222-2222-2222-2222-222222222222"], "tempTotalPrice": 250.0, "shippingFee": 0.0, "discountAmount": 0.0, "totalPrice": 250.0, "checkoutUrl": "/checkout", "message": null }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Cart not found"
  - "You must select at least one product"
  - "Selected products are invalid"
  - N/A
