## Information
Service Name: OrderService
Method Name: updateShippingStatus(UUID orderId, ReqUpdateShippingStatusDTO request)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. OrderRepository
   4. NotificationRepository
   5. PaymentRepository
   6. OrderItemRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Status Null) | TC 4 (Not Active) | TC 5 (Role Null) | TC 6 (Role Name Null) | TC 7 (Role Customer) | TC 8 (Null Tracking) | TC 9 (Blank Tracking) | TC 10 (Order Not Found) | TC 11 (Order PENDING) | TC 12 (Success APPROVED) | TC 13 (Success SHIPPING) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user accountStatus is null | Fails because user is not ACTIVE | Fails because user role is null | Fails because user role name is null | Fails because user is CUSTOMER | Fails because trackingNumber is null | Fails because trackingNumber is blank | Fails because order not found | Fails because order status is PENDING | Success (updates from APPROVED, no payment info) | Success (updates from SHIPPING, has payment info) |
| **Inputs** | | | | | | | | | | | | | |
| orderId | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` |
| request.trackingNumber | `"TRACK123"` | `"TRACK123"` | `"TRACK123"` | `"TRACK123"` | `"TRACK123"` | `"TRACK123"` | `"TRACK123"` | `null` | `"   "` | `"TRACK123"` | `"TRACK123"` | `"TRACK123"` | `"TRACK123"` |
| **Mock / Context Setup** | | | | | | | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` | returns `"staff@example.com"` |
| userRepository.findByEmail(...) | N/A | returns empty | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": null }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "INACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": null }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": null } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }` |
| orderRepository.findById(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "PENDING", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice", "phoneNumber": "0901234567" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "shippingAddressSnapshot": "D1, HCM", "trackingNumber": null, "createdAt": "2026-05-29T10:00:00Z", "updatedAt": "2026-05-29T10:00:00Z" }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "APPROVED", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice", "phoneNumber": "0901234567" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "shippingAddressSnapshot": "D1, HCM", "trackingNumber": null, "createdAt": "2026-05-29T10:00:00Z", "updatedAt": "2026-05-29T10:00:00Z" }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "SHIPPING", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice", "phoneNumber": "0901234567" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "shippingAddressSnapshot": "D1, HCM", "trackingNumber": "OLDTRACK", "createdAt": "2026-05-29T10:00:00Z", "updatedAt": "2026-05-29T10:05:00Z" }` |
| orderRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "SHIPPING", "trackingNumber": "TRACK123", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice", "phoneNumber": "0901234567" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "shippingAddressSnapshot": "D1, HCM", "createdAt": "2026-05-29T10:00:00Z", "updatedAt": "2026-05-29T10:10:00Z" }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "status": "SHIPPING", "trackingNumber": "TRACK123", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice", "phoneNumber": "0901234567" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "shippingAddressSnapshot": "D1, HCM", "createdAt": "2026-05-29T10:00:00Z", "updatedAt": "2026-05-29T10:10:00Z" }` |
| notificationRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | executes void | executes void |
| paymentRepository.findByOrderId(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | returns `{ "id": "22222222-2222-2222-2222-222222222222", "status": "PENDING", "paymentMethod": { "name": "Cash on Delivery" } }` |
| orderItemRepository.findByOrderIdWithProduct(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns `[{ "product": { "id": "66666666-6666-6666-6666-666666666666", "name": "Laptop" }, "price": 100000, "quantity": 1 }]` | returns `[{ "product": { "id": "66666666-6666-6666-6666-666666666666", "name": "Laptop" }, "price": 100000, "quantity": 1 }]` |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.NOT_FOUND) | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | `{ "orderId": "11111111-1111-1111-1111-111111111111", "customerId": "55555555-5555-5555-5555-555555555555", "customerName": "Alice", "customerPhone": "0901234567", "status": "SHIPPING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "shippingAddressSnapshot": "D1, HCM", "trackingNumber": "TRACK123", "paymentMethod": null, "paymentStatus": null, "createdAt": "2026-05-29T10:00:00Z", "updatedAt": "2026-05-29T10:10:00Z", "items": [{ "productId": "66666666-6666-6666-6666-666666666666", "productName": "Laptop", "price": 100000, "quantity": 1, "lineTotal": 100000 }], "message": "Shipping status updated successfully" }` | `{ "orderId": "11111111-1111-1111-1111-111111111111", "customerId": "55555555-5555-5555-5555-555555555555", "customerName": "Alice", "customerPhone": "0901234567", "status": "SHIPPING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "shippingAddressSnapshot": "D1, HCM", "trackingNumber": "TRACK123", "paymentMethod": "Cash on Delivery", "paymentStatus": "PENDING", "createdAt": "2026-05-29T10:00:00Z", "updatedAt": "2026-05-29T10:10:00Z", "items": [{ "productId": "66666666-6666-6666-6666-666666666666", "productName": "Laptop", "price": 100000, "quantity": 1, "lineTotal": 100000 }], "message": "Shipping status updated successfully" }` |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "User account is not active" | "Only staff or business admin can perform this action" | "Only staff or business admin can perform this action" | "Only staff or business admin can perform this action" | "Tracking number is required" | "Tracking number is required" | "Order not found" | "Only approved orders can be moved to shipping" | N/A | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- orderId:
  - "11111111-1111-1111-1111-111111111111"
- request.trackingNumber:
  - "TRACK123"
  - null
  - "   "

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
- orderRepository.findById(...):
  - N/A
  - empty
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "PENDING", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice", "phoneNumber": "0901234567" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "shippingAddressSnapshot": "D1, HCM", "trackingNumber": null, "createdAt": "2026-05-29T10:00:00Z", "updatedAt": "2026-05-29T10:00:00Z" }
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "APPROVED", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice", "phoneNumber": "0901234567" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "shippingAddressSnapshot": "D1, HCM", "trackingNumber": null, "createdAt": "2026-05-29T10:00:00Z", "updatedAt": "2026-05-29T10:00:00Z" }
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "SHIPPING", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice", "phoneNumber": "0901234567" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "shippingAddressSnapshot": "D1, HCM", "trackingNumber": "OLDTRACK", "createdAt": "2026-05-29T10:00:00Z", "updatedAt": "2026-05-29T10:05:00Z" }
- orderRepository.save(...):
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "status": "SHIPPING", "trackingNumber": "TRACK123", "user": { "id": "55555555-5555-5555-5555-555555555555", "userFullName": "Alice", "phoneNumber": "0901234567" }, "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "shippingAddressSnapshot": "D1, HCM", "createdAt": "2026-05-29T10:00:00Z", "updatedAt": "2026-05-29T10:10:00Z" }
- notificationRepository.save(...):
  - N/A
  - executes void
- paymentRepository.findByOrderId(...):
  - N/A
  - empty
  - { "id": "22222222-2222-2222-2222-222222222222", "status": "PENDING", "paymentMethod": { "name": "Cash on Delivery" } }
- orderItemRepository.findByOrderIdWithProduct(...):
  - N/A
  - [{ "product": { "id": "66666666-6666-6666-6666-666666666666", "name": "Laptop" }, "price": 100000, "quantity": 1 }]

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - Throws BusinessException(HttpStatus.NOT_FOUND)
  - N/A
- Expected Return:
  - N/A
  - { "orderId": "11111111-1111-1111-1111-111111111111", "customerId": "55555555-5555-5555-5555-555555555555", "customerName": "Alice", "customerPhone": "0901234567", "status": "SHIPPING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "shippingAddressSnapshot": "D1, HCM", "trackingNumber": "TRACK123", "paymentMethod": null, "paymentStatus": null, "createdAt": "2026-05-29T10:00:00Z", "updatedAt": "2026-05-29T10:10:00Z", "items": [{ "productId": "66666666-6666-6666-6666-666666666666", "productName": "Laptop", "price": 100000, "quantity": 1, "lineTotal": 100000 }], "message": "Shipping status updated successfully" }
  - { "orderId": "11111111-1111-1111-1111-111111111111", "customerId": "55555555-5555-5555-5555-555555555555", "customerName": "Alice", "customerPhone": "0901234567", "status": "SHIPPING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000, "totalAmount": 120000, "shippingAddressSnapshot": "D1, HCM", "trackingNumber": "TRACK123", "paymentMethod": "Cash on Delivery", "paymentStatus": "PENDING", "createdAt": "2026-05-29T10:00:00Z", "updatedAt": "2026-05-29T10:10:00Z", "items": [{ "productId": "66666666-6666-6666-6666-666666666666", "productName": "Laptop", "price": 100000, "quantity": 1, "lineTotal": 100000 }], "message": "Shipping status updated successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Only staff or business admin can perform this action"
  - "Tracking number is required"
  - "Order not found"
  - "Only approved orders can be moved to shipping"
  - N/A
