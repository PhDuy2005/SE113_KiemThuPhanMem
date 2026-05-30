## Information
Service Name: CheckoutService
Method Name: confirmOrder(ReqConfirmOrderDTO request)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. CartRepository
   4. CartItemRepository
   5. ShippingAddressRepository
   6. PaymentMethodRepository
   7. InventoryRepository
   8. VoucherRepository
   9. ShippingFeeConfigService
   10. OrderRepository
   11. OrderItemRepository
   12. PaymentRepository
   13. EmailService

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Status Null) | TC 4 (Not Active) | TC 5 (No Cart) | TC 6 (Null IDs) | TC 7 (Empty IDs) | TC 8 (Invalid IDs) | TC 9 (No Shipping) | TC 10 (Invalid PM) | TC 11 (Not Cash) | TC 12 (No Stock) | TC 13 (No Voucher) | TC 14 (Blank Voucher) | TC 15 (Invalid Voucher) | TC 16 (Success PERCENT) | TC 17 (Success FIXED) | TC 18 (No Stock deduct) | TC 19 (Null Stock deduct) | TC 20 (Email Throws) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user accountStatus is null | Fails because user is not ACTIVE | Fails because cart is not found | Fails because selectedProductIds is null | Fails because selectedProductIds is empty | Fails because selected products mismatch with DB | Fails because shipping address not found | Fails because payment method not found | Fails because payment method is not CASH | Fails because stock is insufficient in validateStock | Success without voucher (voucherCode is null) | Success without voucher (voucherCode is blank) | Fails because voucher is invalid/not found | Success with PERCENT voucher (usedCount was null) | Success with FIXED voucher (usedCount was not null) | Fails in deductStock because inventory is empty | Success with inventory quantity being null | Success but emailService throws RuntimeException |
| **Inputs** | | | | | | | | | | | | | | | | | | | | |
| request.selectedProductIds | `["11111111-1111-1111-1111-111111111111"]` | `["11111111-1111-1111-1111-111111111111"]` | `["11111111-1111-1111-1111-111111111111"]` | `["11111111-1111-1111-1111-111111111111"]` | `["11111111-1111-1111-1111-111111111111"]` | `null` | `[]` | `["11111111-1111-1111-1111-111111111111", "99999999-9999-9999-9999-999999999999"]` | `["11111111-1111-1111-1111-111111111111"]` | `["11111111-1111-1111-1111-111111111111"]` | `["11111111-1111-1111-1111-111111111111"]` | `["11111111-1111-1111-1111-111111111111"]` | `["11111111-1111-1111-1111-111111111111"]` | `["11111111-1111-1111-1111-111111111111"]` | `["11111111-1111-1111-1111-111111111111"]` | `["11111111-1111-1111-1111-111111111111"]` | `["11111111-1111-1111-1111-111111111111"]` | `["11111111-1111-1111-1111-111111111111"]` | `["11111111-1111-1111-1111-111111111111"]` | `["11111111-1111-1111-1111-111111111111"]` |
| request.shippingAddressId | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` | `"22222222-2222-2222-2222-222222222222"` |
| request.paymentMethodId | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` | `"33333333-3333-3333-3333-333333333333"` |
| request.voucherCode | `"VOUCHER10"` | `"VOUCHER10"` | `"VOUCHER10"` | `"VOUCHER10"` | `"VOUCHER10"` | `"VOUCHER10"` | `"VOUCHER10"` | `"VOUCHER10"` | `"VOUCHER10"` | `"VOUCHER10"` | `"VOUCHER10"` | `"VOUCHER10"` | `null` | `"   "` | `"VOUCHER10"` | `"VOUCHER10"` | `"VOUCHER10"` | `"VOUCHER10"` | `"VOUCHER10"` | `"VOUCHER10"` |
| **Mock / Context Setup** | | | | | | | | | | | | | | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` | returns `"user@example.com"` |
| userRepository.findByEmail(...) | N/A | returns empty | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": null }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "INACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }` |
| cartRepository.findByUserId(...) | N/A | N/A | N/A | N/A | returns empty | returns `{ "id": "55555555-5555-5555-5555-555555555555" }` | returns `{ "id": "55555555-5555-5555-5555-555555555555" }` | returns `{ "id": "55555555-5555-5555-5555-555555555555" }` | returns `{ "id": "55555555-5555-5555-5555-555555555555" }` | returns `{ "id": "55555555-5555-5555-5555-555555555555" }` | returns `{ "id": "55555555-5555-5555-5555-555555555555" }` | returns `{ "id": "55555555-5555-5555-5555-555555555555" }` | returns `{ "id": "55555555-5555-5555-5555-555555555555" }` | returns `{ "id": "55555555-5555-5555-5555-555555555555" }` | returns `{ "id": "55555555-5555-5555-5555-555555555555" }` | returns `{ "id": "55555555-5555-5555-5555-555555555555" }` | returns `{ "id": "55555555-5555-5555-5555-555555555555" }` | returns `{ "id": "55555555-5555-5555-5555-555555555555" }` | returns `{ "id": "55555555-5555-5555-5555-555555555555" }` | returns `{ "id": "55555555-5555-5555-5555-555555555555" }` |
| cartItemRepository.findSelectedByCartIdWithProduct(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns `[{ "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100000 }, "quantity": 1 }]` | returns `[{ "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100000 }, "quantity": 1 }]` | returns `[{ "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100000 }, "quantity": 1 }]` | returns `[{ "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100000 }, "quantity": 1 }]` | returns `[{ "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100000 }, "quantity": 1 }]` | returns `[{ "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100000 }, "quantity": 1 }]` | returns `[{ "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100000 }, "quantity": 1 }]` | returns `[{ "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100000 }, "quantity": 1 }]` | returns `[{ "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100000 }, "quantity": 1 }]` | returns `[{ "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100000 }, "quantity": 1 }]` | returns `[{ "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100000 }, "quantity": 0 }]` | returns `[{ "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100000 }, "quantity": 1 }]` | returns `[{ "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100000 }, "quantity": 1 }]` |
| shippingAddressRepository.findById...(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | returns `{ "id": "22222222-2222-2222-2222-222222222222", "provinceCode": "79", "province": "HCM", "ward": "W1", "detail": "D1" }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "provinceCode": "79", "province": "HCM", "ward": "W1", "detail": "D1" }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "provinceCode": "79", "province": "HCM", "ward": "W1", "detail": "D1" }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "provinceCode": "79", "province": "HCM", "ward": "W1", "detail": "D1" }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "provinceCode": "79", "province": "HCM", "ward": "W1", "detail": "D1" }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "provinceCode": "79", "province": "HCM", "ward": "W1", "detail": "D1" }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "provinceCode": "79", "province": "HCM", "ward": "W1", "detail": "D1" }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "provinceCode": "79", "province": "HCM", "ward": "W1", "detail": "D1" }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "provinceCode": "79", "province": "HCM", "ward": "W1", "detail": "D1" }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "provinceCode": "79", "province": "HCM", "ward": "W1", "detail": "D1" }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "provinceCode": "79", "province": "HCM", "ward": "W1", "detail": "D1" }` |
| paymentMethodRepository.findById(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | returns `{ "id": "33333333-3333-3333-3333-333333333333", "type": "CREDIT_CARD", "name": "Credit Card" }` | returns `{ "id": "33333333-3333-3333-3333-333333333333", "type": "CASH", "name": "Cash on Delivery" }` | returns `{ "id": "33333333-3333-3333-3333-333333333333", "type": "CASH", "name": "Cash on Delivery" }` | returns `{ "id": "33333333-3333-3333-3333-333333333333", "type": "CASH", "name": "Cash on Delivery" }` | returns `{ "id": "33333333-3333-3333-3333-333333333333", "type": "CASH", "name": "Cash on Delivery" }` | returns `{ "id": "33333333-3333-3333-3333-333333333333", "type": "CASH", "name": "Cash on Delivery" }` | returns `{ "id": "33333333-3333-3333-3333-333333333333", "type": "CASH", "name": "Cash on Delivery" }` | returns `{ "id": "33333333-3333-3333-3333-333333333333", "type": "CASH", "name": "Cash on Delivery" }` | returns `{ "id": "33333333-3333-3333-3333-333333333333", "type": "CASH", "name": "Cash on Delivery" }` | returns `{ "id": "33333333-3333-3333-3333-333333333333", "type": "CASH", "name": "Cash on Delivery" }` |
| inventoryRepository.findById(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns `{ "id": "11111111-1111-1111-1111-111111111111", "availableQuantity": 0, "quantity": 0 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "availableQuantity": 10, "quantity": 10 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "availableQuantity": 10, "quantity": 10 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "availableQuantity": 10, "quantity": 10 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "availableQuantity": 10, "quantity": 10 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "availableQuantity": 10, "quantity": 10 }` | returns empty | returns `{ "id": "11111111-1111-1111-1111-111111111111", "availableQuantity": 10, "quantity": null }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "availableQuantity": 10, "quantity": 10 }` |
| voucherRepository.findByCodeIgnoreCase(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | returns `{ "id": "88888888-8888-8888-8888-888888888888", "code": "VOUCHER10", "type": "PERCENT", "value": 10, "active": true, "usedCount": null }` | returns `{ "id": "88888888-8888-8888-8888-888888888888", "code": "VOUCHER10", "type": "FIXED", "value": 20000, "active": true, "usedCount": 5 }` | N/A | N/A | returns `{ "id": "88888888-8888-8888-8888-888888888888", "code": "VOUCHER10", "type": "PERCENT", "value": 10, "active": true, "usedCount": null }` |
| shippingFeeConfigService.getShippingFee...(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns `30000` | returns `30000` | N/A | returns `30000` | returns `30000` | N/A | returns `30000` | returns `30000` |
| voucherRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns `{ "id": "88888888-8888-8888-8888-888888888888", "usedCount": 1 }` | returns `{ "id": "88888888-8888-8888-8888-888888888888", "usedCount": 6 }` | N/A | N/A | returns `{ "id": "88888888-8888-8888-8888-888888888888", "usedCount": 1 }` |
| inventoryRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns `{ "id": "11111111-1111-1111-1111-111111111111", "quantity": 9 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "quantity": 9 }` | N/A | returns `{ "id": "11111111-1111-1111-1111-111111111111", "quantity": 9 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "quantity": 9 }` | N/A | returns `{ "id": "11111111-1111-1111-1111-111111111111", "quantity": -1 }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "quantity": 9 }` |
| orderRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns `{ "id": "66666666-6666-6666-6666-666666666666", "status": "PENDING" }` | returns `{ "id": "66666666-6666-6666-6666-666666666666", "status": "PENDING" }` | N/A | returns `{ "id": "66666666-6666-6666-6666-666666666666", "status": "PENDING" }` | returns `{ "id": "66666666-6666-6666-6666-666666666666", "status": "PENDING" }` | N/A | returns `{ "id": "66666666-6666-6666-6666-666666666666", "status": "PENDING" }` | returns `{ "id": "66666666-6666-6666-6666-666666666666", "status": "PENDING" }` |
| orderItemRepository.saveAll(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns list | returns list | N/A | returns list | returns list | N/A | returns list | returns list |
| paymentRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns `{ "id": "77777777-7777-7777-7777-777777777777", "status": "PENDING" }` | returns `{ "id": "77777777-7777-7777-7777-777777777777", "status": "PENDING" }` | N/A | returns `{ "id": "77777777-7777-7777-7777-777777777777", "status": "PENDING" }` | returns `{ "id": "77777777-7777-7777-7777-777777777777", "status": "PENDING" }` | N/A | returns `{ "id": "77777777-7777-7777-7777-777777777777", "status": "PENDING" }` | returns `{ "id": "77777777-7777-7777-7777-777777777777", "status": "PENDING" }` |
| cartItemRepository.deleteByCartId...(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | executes void | executes void | N/A | executes void | executes void | N/A | executes void | executes void |
| emailService.sendOrderConfirmation(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | executes void | executes void | N/A | executes void | executes void | N/A | executes void | throws RuntimeException |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.NOT_FOUND) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.NOT_FOUND) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A | N/A | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A | N/A | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | `{ "orderId": "66666666-6666-6666-6666-666666666666", "status": "PENDING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 0, "totalAmount": 130000, "paymentId": "77777777-7777-7777-7777-777777777777", "paymentMethodName": "Cash on Delivery", "paymentStatus": "PENDING", "message": "Order created successfully" }` | `{ "orderId": "66666666-6666-6666-6666-666666666666", "status": "PENDING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 0, "totalAmount": 130000, "paymentId": "77777777-7777-7777-7777-777777777777", "paymentMethodName": "Cash on Delivery", "paymentStatus": "PENDING", "message": "Order created successfully" }` | N/A | `{ "orderId": "66666666-6666-6666-6666-666666666666", "status": "PENDING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000.00, "totalAmount": 120000.00, "paymentId": "77777777-7777-7777-7777-777777777777", "paymentMethodName": "Cash on Delivery", "paymentStatus": "PENDING", "message": "Order created successfully" }` | `{ "orderId": "66666666-6666-6666-6666-666666666666", "status": "PENDING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 20000, "totalAmount": 110000, "paymentId": "77777777-7777-7777-7777-777777777777", "paymentMethodName": "Cash on Delivery", "paymentStatus": "PENDING", "message": "Order created successfully" }` | N/A | `{ "orderId": "66666666-6666-6666-6666-666666666666", "status": "PENDING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 0, "totalAmount": 130000, "paymentId": "77777777-7777-7777-7777-777777777777", "paymentMethodName": "Cash on Delivery", "paymentStatus": "PENDING", "message": "Order created successfully" }` | `{ "orderId": "66666666-6666-6666-6666-666666666666", "status": "PENDING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000.00, "totalAmount": 120000.00, "paymentId": "77777777-7777-7777-7777-777777777777", "paymentMethodName": "Cash on Delivery", "paymentStatus": "PENDING", "message": "Order created successfully" }` |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "User account is not active" | "Cart not found" | "You must select at least one product" | "You must select at least one product" | "Selected products are invalid" | "Shipping address not found" | "Invalid payment method" | "Only cash payment is supported at the moment" | "Insufficient stock. Please check your cart again" | N/A | N/A | "Invalid or expired voucher" | N/A | N/A | "Insufficient stock. Please check your cart again" | N/A | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- request.selectedProductIds:
  - ["11111111-1111-1111-1111-111111111111"]
  - null
  - []
  - ["11111111-1111-1111-1111-111111111111", "99999999-9999-9999-9999-999999999999"]
- request.shippingAddressId:
  - "22222222-2222-2222-2222-222222222222"
- request.paymentMethodId:
  - "33333333-3333-3333-3333-333333333333"
- request.voucherCode:
  - "VOUCHER10"
  - null
  - "   "

### MOCK/ CONTEXT SETUP
- SecurityUtil.getCurrentUserLogin():
  - empty
  - "user@example.com"
- userRepository.findByEmail(...):
  - N/A
  - empty
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": null }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "INACTIVE" }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE" }
- cartRepository.findByUserId(...):
  - N/A
  - empty
  - { "id": "55555555-5555-5555-5555-555555555555" }
- cartItemRepository.findSelectedByCartIdWithProduct(...):
  - N/A
  - [{ "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100000 }, "quantity": 1 }]
  - [{ "product": { "id": "11111111-1111-1111-1111-111111111111", "price": 100000 }, "quantity": 0 }]
- shippingAddressRepository.findById...(...):
  - N/A
  - empty
  - { "id": "22222222-2222-2222-2222-222222222222", "provinceCode": "79", "province": "HCM", "ward": "W1", "detail": "D1" }
- paymentMethodRepository.findById(...):
  - N/A
  - empty
  - { "id": "33333333-3333-3333-3333-333333333333", "type": "CREDIT_CARD", "name": "Credit Card" }
  - { "id": "33333333-3333-3333-3333-333333333333", "type": "CASH", "name": "Cash on Delivery" }
- inventoryRepository.findById(...):
  - N/A
  - empty
  - { "id": "11111111-1111-1111-1111-111111111111", "availableQuantity": 0, "quantity": 0 }
  - { "id": "11111111-1111-1111-1111-111111111111", "availableQuantity": 10, "quantity": 10 }
  - { "id": "11111111-1111-1111-1111-111111111111", "availableQuantity": 10, "quantity": null }
- voucherRepository.findByCodeIgnoreCase(...):
  - N/A
  - empty
  - { "id": "88888888-8888-8888-8888-888888888888", "code": "VOUCHER10", "type": "PERCENT", "value": 10, "active": true, "usedCount": null }
  - { "id": "88888888-8888-8888-8888-888888888888", "code": "VOUCHER10", "type": "FIXED", "value": 20000, "active": true, "usedCount": 5 }
- shippingFeeConfigService.getShippingFee...(...):
  - N/A
  - 30000
- voucherRepository.save(...):
  - N/A
  - { "id": "88888888-8888-8888-8888-888888888888", "usedCount": 1 }
  - { "id": "88888888-8888-8888-8888-888888888888", "usedCount": 6 }
- inventoryRepository.save(...):
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "quantity": 9 }
  - { "id": "11111111-1111-1111-1111-111111111111", "quantity": -1 }
- orderRepository.save(...):
  - N/A
  - { "id": "66666666-6666-6666-6666-666666666666", "status": "PENDING" }
- orderItemRepository.saveAll(...):
  - N/A
  - list
- paymentRepository.save(...):
  - N/A
  - { "id": "77777777-7777-7777-7777-777777777777", "status": "PENDING" }
- cartItemRepository.deleteByCartId...(...):
  - N/A
  - executes void
- emailService.sendOrderConfirmation(...):
  - N/A
  - executes void
  - throws RuntimeException

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.NOT_FOUND)
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - N/A
- Expected Return:
  - N/A
  - { "orderId": "66666666-6666-6666-6666-666666666666", "status": "PENDING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 0, "totalAmount": 130000, "paymentId": "77777777-7777-7777-7777-777777777777", "paymentMethodName": "Cash on Delivery", "paymentStatus": "PENDING", "message": "Order created successfully" }
  - { "orderId": "66666666-6666-6666-6666-666666666666", "status": "PENDING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 10000.00, "totalAmount": 120000.00, "paymentId": "77777777-7777-7777-7777-777777777777", "paymentMethodName": "Cash on Delivery", "paymentStatus": "PENDING", "message": "Order created successfully" }
  - { "orderId": "66666666-6666-6666-6666-666666666666", "status": "PENDING", "totalProductAmount": 100000, "shippingFee": 30000, "discountAmount": 20000, "totalAmount": 110000, "paymentId": "77777777-7777-7777-7777-777777777777", "paymentMethodName": "Cash on Delivery", "paymentStatus": "PENDING", "message": "Order created successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Cart not found"
  - "You must select at least one product"
  - "Selected products are invalid"
  - "Shipping address not found"
  - "Invalid payment method"
  - "Only cash payment is supported at the moment"
  - "Insufficient stock. Please check your cart again"
  - "Invalid or expired voucher"
  - N/A
