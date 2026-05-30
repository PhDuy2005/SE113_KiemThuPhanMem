## Information
Service Name: OrderService
Method Name: approveOrder(UUID orderId)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. OrderRepository
   4. NotificationRepository
   5. PaymentRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Status Null) | TC 4 (Not Active) | TC 5 (Role Null) | TC 6 (Role Name Null) | TC 7 (Role Customer) | TC 8 (Order Not Found) | TC 9 (Order Not Pending) | TC 10 (Success No Payment) | TC 11 (Success No PaymentMethod) | TC 12 (Success PM Normal) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user accountStatus is null | Fails because user is not ACTIVE | Fails because user role is null | Fails because user role name is null | Fails because user is CUSTOMER | Fails because order not found | Fails because order status is not PENDING | Success (order approved, payment is null) | Success (order approved, paymentMethod is null) | Success (order approved, paymentMethod is Cash on Delivery) |
| **Inputs** | | | | | | | | | | | | |
| orderId | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` |
| **Mock / Context Setup** | | | | | | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` |
| userRepository.findByEmail(...) | N/A | returns empty | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": null }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "INACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": null }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": null } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }` |
| orderRepository.findById(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "APPROVED", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "PENDING", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "PENDING", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "PENDING", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }` |
| orderRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "APPROVED", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "APPROVED", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "APPROVED", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }` |
| notificationRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | executes void | executes void | executes void |
| paymentRepository.findByOrderId(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | returns `{ "id": "22222222-2222-2222-2222-222222222222", "status": "PENDING", "paymentMethod": null }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "status": "PENDING", "paymentMethod": { "name": "Cash on Delivery" } }` |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.NOT_FOUND) | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A | N/A | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | `{ "orderId": "11111111-1111-1111-1111-111111111111", "customerId": "55555555-5555-5555-5555-555555555555", "customerName": "Alice", "status": "APPROVED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "paymentId": null, "paymentMethodName": null, "paymentStatus": null, "message": "Order approved successfully" }` | `{ "orderId": "11111111-1111-1111-1111-111111111111", "customerId": "55555555-5555-5555-5555-555555555555", "customerName": "Alice", "status": "APPROVED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "paymentId": "22222222-2222-2222-2222-222222222222", "paymentMethodName": null, "paymentStatus": "PENDING", "message": "Order approved successfully" }` | `{ "orderId": "11111111-1111-1111-1111-111111111111", "customerId": "55555555-5555-5555-5555-555555555555", "customerName": "Alice", "status": "APPROVED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "paymentId": "22222222-2222-2222-2222-222222222222", "paymentMethodName": "Cash on Delivery", "paymentStatus": "PENDING", "message": "Order approved successfully" }` |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "User account is not active" | "Only staff or business admin can perform this action" | "Only staff or business admin can perform this action" | "Only staff or business admin can perform this action" | "Order not found" | "Only pending orders can be approved" | N/A | N/A | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- orderId:
  - "11111111-1111-1111-1111-111111111111"

### MOCK/ CONTEXT SETUP
- SecurityUtil.getCurrentUserLogin():
  - empty
  - "staff@example.com"
- userRepository.findByEmail(...):
  - N/A
  - empty
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": null }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "INACTIVE" }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": null }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": null } }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }
- orderRepository.findById(...):
  - N/A
  - empty
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "APPROVED", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "PENDING", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }
- orderRepository.save(...):
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "APPROVED", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000 }
- notificationRepository.save(...):
  - N/A
  - executes void
- paymentRepository.findByOrderId(...):
  - N/A
  - empty
  - { "id": "22222222-2222-2222-2222-222222222222", "status": "PENDING", "paymentMethod": null }
  - { "id": "22222222-2222-2222-2222-222222222222", "status": "PENDING", "paymentMethod": { "name": "Cash on Delivery" } }

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.NOT_FOUND)
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - N/A
- Expected Return:
  - N/A
  - { "orderId": "11111111-1111-1111-1111-111111111111", "customerId": "55555555-5555-5555-5555-555555555555", "customerName": "Alice", "status": "APPROVED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "paymentId": null, "paymentMethodName": null, "paymentStatus": null, "message": "Order approved successfully" }
  - { "orderId": "11111111-1111-1111-1111-111111111111", "customerId": "55555555-5555-5555-5555-555555555555", "customerName": "Alice", "status": "APPROVED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "paymentId": "22222222-2222-2222-2222-222222222222", "paymentMethodName": null, "paymentStatus": "PENDING", "message": "Order approved successfully" }
  - { "orderId": "11111111-1111-1111-1111-111111111111", "customerId": "55555555-5555-5555-5555-555555555555", "customerName": "Alice", "status": "APPROVED", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "paymentId": "22222222-2222-2222-2222-222222222222", "paymentMethodName": "Cash on Delivery", "paymentStatus": "PENDING", "message": "Order approved successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Only staff or business admin can perform this action"
  - "Order not found"
  - "Only pending orders can be approved"
  - N/A
