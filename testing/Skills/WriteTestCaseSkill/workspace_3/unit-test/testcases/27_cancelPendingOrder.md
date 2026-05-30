## Information
Service Name: OrderService
Method Name: cancelPendingOrder(UUID orderId)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. OrderRepository
   4. OrderItemRepository
   5. InventoryRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Status Null) | TC 4 (Not Active) | TC 5 (Role Null, Order Not Found) | TC 6 (Role Name Null, Not PENDING) | TC 7 (Role STAFF, Success Empty Inv) | TC 8 (Role CUSTOMER, Order Not Found) | TC 9 (Role CUSTOMER, Not PENDING) | TC 10 (Role CUSTOMER, Success Null Qty) | TC 11 (Role CUSTOMER, Success Normal Inv) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user accountStatus is null | Fails because user is not ACTIVE | Fails because role is null and findById is empty | Fails because role name is null and order status is APPROVED | Success (as STAFF, order PENDING, inventory is empty so new is created) | Fails because user is CUSTOMER and findByIdAndUserId is empty | Fails because user is CUSTOMER and order status is APPROVED | Success (as CUSTOMER, order PENDING, existing inventory quantity is null) | Success (as CUSTOMER, order PENDING, existing inventory has quantity 10) |
| **Inputs** | | | | | | | | | | | |
| orderId | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` |
| **Mock / Context Setup** | | | | | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` |
| userRepository.findByEmail(...) | N/A | returns empty | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": null }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "INACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": null }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": null } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } }` |
| orderRepository.findById(...) | N/A | N/A | N/A | N/A | returns empty | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "APPROVED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "PENDING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }` | N/A | N/A | N/A | N/A |
| orderRepository.findByIdAndUserId(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "APPROVED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "PENDING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "PENDING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }` |
| orderItemRepository.findByOrderIdWithProduct(...) | N/A | N/A | N/A | N/A | N/A | N/A | returns `[{ "product": { "id": "66666666-6666-6666-6666-666666666666" }, "quantity": 2 }]` | N/A | N/A | returns `[{ "product": { "id": "66666666-6666-6666-6666-666666666666" }, "quantity": 2 }]` | returns `[{ "product": { "id": "66666666-6666-6666-6666-666666666666" }, "quantity": 2 }]` |
| inventoryRepository.findById(...) | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | N/A | N/A | returns `{ "productId": "66666666-6666-6666-6666-666666666666", "quantity": null, "reservedQuantity": 0 }` | returns `{ "productId": "66666666-6666-6666-6666-666666666666", "quantity": 10, "reservedQuantity": 0 }` |
| inventoryRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | executes void | N/A | N/A | executes void | executes void |
| orderRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "CANCELLED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }` | N/A | N/A | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "CANCELLED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "CANCELLED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }` |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.NOT_FOUND) | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A | Throws BusinessException(HttpStatus.NOT_FOUND) | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | N/A | `{ "orderId": "11111111-1111-1111-1111-111111111111", "status": "CANCELLED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "message": "Order cancelled successfully" }` | N/A | N/A | `{ "orderId": "11111111-1111-1111-1111-111111111111", "status": "CANCELLED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "message": "Order cancelled successfully" }` | `{ "orderId": "11111111-1111-1111-1111-111111111111", "status": "CANCELLED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "message": "Order cancelled successfully" }` |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "User account is not active" | "Order not found" | "Order cannot be cancelled because it has been processed" | N/A | "Order not found" | "Order cannot be cancelled because it has been processed" | N/A | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- orderId:
  - "11111111-1111-1111-1111-111111111111"

### MOCK/ CONTEXT SETUP
- SecurityUtil.getCurrentUserLogin():
  - empty
  - "user@example.com"
- userRepository.findByEmail(...):
  - N/A
  - empty
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": null }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "INACTIVE" }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": null }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": null } }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } }
- orderRepository.findById(...):
  - N/A
  - empty
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "APPROVED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "PENDING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }
- orderRepository.findByIdAndUserId(...):
  - N/A
  - empty
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "APPROVED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "PENDING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }
- orderItemRepository.findByOrderIdWithProduct(...):
  - N/A
  - [{ "product": { "id": "66666666-6666-6666-6666-666666666666" }, "quantity": 2 }]
- inventoryRepository.findById(...):
  - N/A
  - empty
  - { "productId": "66666666-6666-6666-6666-666666666666", "quantity": null, "reservedQuantity": 0 }
  - { "productId": "66666666-6666-6666-6666-666666666666", "quantity": 10, "reservedQuantity": 0 }
- inventoryRepository.save(...):
  - N/A
  - executes void
- orderRepository.save(...):
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "CANCELLED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.NOT_FOUND)
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - N/A
- Expected Return:
  - N/A
  - { "orderId": "11111111-1111-1111-1111-111111111111", "status": "CANCELLED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "message": "Order cancelled successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Order not found"
  - "Order cannot be cancelled because it has been processed"
  - N/A
