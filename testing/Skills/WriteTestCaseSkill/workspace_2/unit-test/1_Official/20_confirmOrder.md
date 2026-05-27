## Information
Service Name: CheckoutService
Method Name: confirmOrder(ReqConfirmOrderDTO request)
Mock class: 
   1. UserRepository
   2. CartRepository
   3. CartItemRepository
   4. ShippingAddressRepository
   5. PaymentMethodRepository
   6. InventoryRepository
   7. VoucherRepository
   8. OrderRepository
   9. OrderItemRepository
   10. PaymentRepository
   11. ShippingFeeConfigService
   12. EmailService

## Testcase Specification Matrix (Part 1 - Authentication & Validations)

| Row / Testcase Column | TC 1 (Not Logged In) | TC 2 (Address Not Found) | TC 3 (Non-Cash Payment) | TC 4 (Insufficient Stock) |
| :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Confirm order when user not logged in | Confirm order when shipping address is invalid or not owned | Confirm order selecting a payment method that is VNPAY | Confirm order when requested product quantity exceeds available stock |
| **Inputs** | | | | |
| `request.selectedProductIds`| ["88888888-8888-8888-8888-888888888888"] | ["88888888-8888-8888-8888-888888888888"] | ["88888888-8888-8888-8888-888888888888"] | ["88888888-8888-8888-8888-888888888888"] |
| `request.shippingAddressId` | "44444444-4444-4444-4444-444444444444" | "55555555-5555-5555-5555-555555555555" | "44444444-4444-4444-4444-444444444444" | "44444444-4444-4444-4444-444444444444" |
| `request.paymentMethodId` | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "22222222-2222-2222-2222-222222222222" | "11111111-1111-1111-1111-111111111111" |
| `request.voucherCode` | null | null | null | null |
| **Mock / Context Setup** | | | | |
| `SecurityContext` | empty (not logged in) | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" |
| `currentUser.accountStatus`| null | "ACTIVE" | "ACTIVE" | "ACTIVE" |
| `CartRepository.findByUserId` | null | returns Cart(id="1111...") | returns Cart(id="1111...") | returns Cart(id="1111...") |
| `CartItemRepository.findSelectedByCartIdWithProduct` | null | returns [CartItem(quantity=2, product=Product(price=100000))] | returns [CartItem(quantity=2, product=Product(price=100000))] | returns [CartItem(quantity=2, product=Product(price=100000))] |
| `ShippingAddressRepository.findByIdAndUserIdAndDeletedAtIsNull` | null | returns empty | returns ShippingAddress(id="4444...", province="TP. Hồ Chí Minh") | returns ShippingAddress(id="4444...", province="TP. Hồ Chí Minh") |
| `PaymentMethodRepository.findById`| null | null | returns PaymentMethod(id="2222...", type="VNPAY") | returns PaymentMethod(id="1111...", type="CASH") |
| `InventoryRepository.findById`| null | null | null | returns Inventory(productId="88888888...", quantity=1, reservedQuantity=0) |
| `VoucherRepository.findByCodeIgnoreCase`| null | null | null | null |
| **Expected Output** | BusinessException(401, "You must login first") | BusinessException(404, "Shipping address not found") | BusinessException(400, "Only cash payment is supported at the moment") | BusinessException(400, "Insufficient stock. Please check your cart again") |

<!-- slide -->

## Testcase Specification Matrix (Part 2 - Voucher Validation & Success Cases)

| Row / Testcase Column | TC 5 (Invalid Voucher Code) | TC 6 (Success Without Voucher) | TC 7 (Success With Voucher) |
| :--- | :--- | :--- | :--- |
| **Short Description** | Confirm order with invalid or expired voucher code | Place cash-on-delivery order successfully without discount | Place cash-on-delivery order successfully with voucher discount applied |
| **Inputs** | | | |
| `request.selectedProductIds`| ["88888888-8888-8888-8888-888888888888"] | ["88888888-8888-8888-8888-888888888888"] | ["88888888-8888-8888-8888-888888888888"] |
| `request.shippingAddressId` | "44444444-4444-4444-4444-444444444444" | "44444444-4444-4444-4444-444444444444" | "44444444-4444-4444-4444-444444444444" |
| `request.paymentMethodId` | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" |
| `request.voucherCode` | "INVALID99" | null | "SALE10" |
| **Mock / Context Setup** | | | |
| `SecurityContext` | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" |
| `currentUser.accountStatus`| "ACTIVE" | "ACTIVE" | "ACTIVE" |
| `CartRepository.findByUserId` | returns Cart(id="1111...") | returns Cart(id="1111...") | returns Cart(id="1111...") |
| `CartItemRepository.findSelectedByCartIdWithProduct` | returns [CartItem(quantity=2, product=Product(price=100000))] | returns [CartItem(quantity=2, product=Product(price=100000))] | returns [CartItem(quantity=2, product=Product(price=100000))] |
| `ShippingAddressRepository.findByIdAndUserIdAndDeletedAtIsNull` | returns ShippingAddress(id="4444...", province="TP. Hồ Chí Minh") | returns ShippingAddress(id="4444...", province="TP. Hồ Chí Minh") | returns ShippingAddress(id="4444...", province="TP. Hồ Chí Minh") |
| `PaymentMethodRepository.findById`| returns PaymentMethod(id="1111...", type="CASH") | returns PaymentMethod(id="1111...", type="CASH") | returns PaymentMethod(id="1111...", type="CASH") |
| `InventoryRepository.findById`| returns Inventory(quantity=10) | returns Inventory(quantity=10) | returns Inventory(quantity=10) |
| `VoucherRepository.findByCodeIgnoreCase`| returns empty | null | returns Voucher(code="SALE10", type="PERCENT", value=10, active=true, minOrderAmount=100000) |
| `ShippingFeeConfigService.getShippingFeeForProvince`| null | returns BigDecimal(30000) | returns BigDecimal(30000) |
| **Expected Output** | BusinessException(400, "Invalid or expired voucher") | resOrderDTO.totalProductAmount = BigDecimal(200000), shippingFee = BigDecimal(30000), discountAmount = 0, totalAmount = BigDecimal(230000), status = "PENDING" | resOrderDTO.totalProductAmount = BigDecimal(200000), shippingFee = BigDecimal(30000), discountAmount = BigDecimal(20000), totalAmount = BigDecimal(210000), status = "PENDING" |
