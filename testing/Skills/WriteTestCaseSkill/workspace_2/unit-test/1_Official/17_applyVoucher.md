## Information
Service Name: CheckoutService
Method Name: applyVoucher(ReqApplyVoucherDTO request)
Mock class: 
   1. UserRepository
   2. VoucherRepository

## Testcase Specification Matrix (Part 1 - Authentication & Validity Checks)

| Row / Testcase Column | TC 1 (Not Logged In) | TC 2 (Blocked User) | TC 3 (Voucher Not Found) | TC 4 (Inactive Voucher) | TC 5 (Not Started Voucher) | TC 6 (Expired Voucher) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Apply voucher when not logged in | Apply when user is blocked | Apply code that does not exist in DB | Apply inactive voucher | Apply voucher whose start date is in the future | Apply voucher whose end date is in the past |
| **Inputs** | | | | | | |
| `request.voucherCode`| "SALE10" | "SALE10" | "INVALID99" | "SALE10" | "SALE10" | "SALE10" |
| `request.totalOrderAmount`| BigDecimal(150000) | BigDecimal(150000) | BigDecimal(150000) | BigDecimal(150000) | BigDecimal(150000) | BigDecimal(150000) |
| **Mock / Context Setup** | | | | | | |
| `SecurityContext` | empty (not logged in) | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" |
| `currentUser.accountStatus`| null | "BLOCKED" | "ACTIVE" | "ACTIVE" | "ACTIVE" | "ACTIVE" |
| `VoucherRepository.findByCodeIgnoreCase`| null | null | returns empty | returns Voucher(code="SALE10", active=false) | returns Voucher(code="SALE10", active=true, startDate=Instant.now().plusSeconds(7200)) | returns Voucher(code="SALE10", active=true, endDate=Instant.now().minusSeconds(7200)) |
| **Expected Output** | BusinessException(401, "You must login first") | BusinessException(403, "User account is not active") | BusinessException(400, "Invalid or expired voucher") | BusinessException(400, "Invalid or expired voucher") | BusinessException(400, "Invalid or expired voucher") | BusinessException(400, "Invalid or expired voucher") |

<!-- slide -->

## Testcase Specification Matrix (Part 2 - Usage, Min Amount & Success Cases)

| Row / Testcase Column | TC 7 (Usage Limit Exceeded) | TC 8 (Below Min Order Amount) | TC 9 (Percent Success) | TC 10 (Fixed Success) |
| :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Apply voucher that reached its maximum usage limit | Apply when order amount is less than minOrderAmount | Successfully apply percentage voucher | Successfully apply fixed-amount voucher |
| **Inputs** | | | | |
| `request.voucherCode`| "SALE10" | "SALE10" | "SALE10" | "SALE50K" |
| `request.totalOrderAmount`| BigDecimal(150000) | BigDecimal(50000) | BigDecimal(150000) | BigDecimal(250000) |
| **Mock / Context Setup** | | | | |
| `SecurityContext` | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" |
| `currentUser.accountStatus`| "ACTIVE" | "ACTIVE" | "ACTIVE" | "ACTIVE" |
| `VoucherRepository.findByCodeIgnoreCase`| returns Voucher(code="SALE10", active=true, maxUsage=10, usedCount=10) | returns Voucher(code="SALE10", active=true, minOrderAmount=BigDecimal(100000)) | returns Voucher(code="SALE10", type="PERCENT", value=BigDecimal(10), active=true, minOrderAmount=BigDecimal(100000)) | returns Voucher(code="SALE50K", type="FIXED", value=BigDecimal(50000), active=true, minOrderAmount=BigDecimal(200000)) |
| **Expected Output** | BusinessException(400, "Invalid or expired voucher") | BusinessException(400, "Invalid or expired voucher") | resVoucherApplicationDTO.voucherCode = "SALE10", discountAmount = BigDecimal(15000), finalTotal = BigDecimal(135000) | resVoucherApplicationDTO.voucherCode = "SALE50K", discountAmount = BigDecimal(50000), finalTotal = BigDecimal(200000) |
