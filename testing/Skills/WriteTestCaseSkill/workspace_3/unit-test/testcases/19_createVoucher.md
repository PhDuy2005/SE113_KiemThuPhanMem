## Information
Service Name: VoucherManagementService
Method Name: createVoucher(ReqCreateVoucherDTO request)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. VoucherRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Not Active) | TC 4 (Not Admin) | TC 5 (Null Req) | TC 6 (Invalid Date) | TC 7 (Invalid Val) | TC 8 (Invalid Type) | TC 9 (Code Exists) | TC 10 (Success Active) | TC 11 (Success Sched) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user is not ACTIVE | Fails because user is not BUSINESS_ADMIN | Fails because request is null | Fails because endDate <= startDate | Fails because discountValue is 0 | Fails because discountType is invalid | Fails because voucher code already exists | Creates voucher successfully with ACTIVE status (starts today) | Creates voucher successfully with SCHEDULED status (starts tomorrow) |
| **Inputs** | | | | | | | | | | | |
| request | { "voucherCode": "SUMMER" } | { "voucherCode": "SUMMER" } | { "voucherCode": "SUMMER" } | { "voucherCode": "SUMMER" } | null | { "voucherCode": "SUMMER", "discountType": "PERCENT", "discountValue": 10, "quantity": 100, "startDate": "tomorrow", "endDate": "today" } | { "voucherCode": "SUMMER", "discountType": "PERCENT", "discountValue": 0, "quantity": 100, "startDate": "today", "endDate": "tomorrow" } | { "voucherCode": "SUMMER", "discountType": "OTHER", "discountValue": 10, "quantity": 100, "startDate": "today", "endDate": "tomorrow" } | { "voucherCode": "SUMMER", "discountType": "PERCENT", "discountValue": 10, "quantity": 100, "startDate": "today", "endDate": "tomorrow" } | { "voucherCode": "SUMMER", "discountType": "PERCENT", "discountValue": 10, "quantity": 100, "startDate": "today", "endDate": "tomorrow" } | { "voucherCode": "SUMMER", "discountType": "PERCENT", "discountValue": 10, "quantity": 100, "startDate": "tomorrow", "endDate": "next_week" } |
| **Mock / Context Setup** | | | | | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" |
| userRepository.findByEmail(...) | N/A | returns empty | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "PENDING" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } |
| voucherRepository.existsByCodeIgnoreCase(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns true | returns false | returns false |
| voucherRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns { "id": "11111111-1111-1111-1111-111111111111", "code": "SUMMER", "type": "PERCENT", "value": 10, "maxUsage": 100, "usedCount": 0, "active": true, "status": "ACTIVE" } | returns { "id": "11111111-1111-1111-1111-111111111111", "code": "SUMMER", "type": "PERCENT", "value": 10, "maxUsage": 100, "usedCount": 0, "active": false, "status": "SCHEDULED" } |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.CONFLICT) | N/A | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | { "id": "11111111-1111-1111-1111-111111111111", "voucherCode": "SUMMER", "discountType": "PERCENT", "discountValue": 10, "quantity": 100, "usedCount": 0, "active": true, "status": "ACTIVE", "message": "Voucher created successfully" } | { "id": "11111111-1111-1111-1111-111111111111", "voucherCode": "SUMMER", "discountType": "PERCENT", "discountValue": 10, "quantity": 100, "usedCount": 0, "active": false, "status": "SCHEDULED", "message": "Voucher created successfully" } |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "Only business admin can perform this action" | "Required field is missing" | "End date must be after start date" | "Discount value and quantity must be greater than 0" | "discountType must be FIXED or PERCENT" | "Voucher code already exists" | N/A | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- request:
  - { "voucherCode": "SUMMER" }
  - null
  - { "voucherCode": "SUMMER", "discountType": "PERCENT", "discountValue": 10, "quantity": 100, "startDate": "tomorrow", "endDate": "today" }
  - { "voucherCode": "SUMMER", "discountType": "PERCENT", "discountValue": 0, "quantity": 100, "startDate": "today", "endDate": "tomorrow" }
  - { "voucherCode": "SUMMER", "discountType": "OTHER", "discountValue": 10, "quantity": 100, "startDate": "today", "endDate": "tomorrow" }
  - { "voucherCode": "SUMMER", "discountType": "PERCENT", "discountValue": 10, "quantity": 100, "startDate": "today", "endDate": "tomorrow" }
  - { "voucherCode": "SUMMER", "discountType": "PERCENT", "discountValue": 10, "quantity": 100, "startDate": "tomorrow", "endDate": "next_week" }

### MOCK/ CONTEXT SETUP
- SecurityUtil.getCurrentUserLogin():
  - empty
  - "admin@example.com"
- userRepository.findByEmail(...):
  - N/A
  - empty
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "PENDING" }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }
- voucherRepository.existsByCodeIgnoreCase(...):
  - N/A
  - true
  - false
- voucherRepository.save(...):
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "code": "SUMMER", "type": "PERCENT", "value": 10, "maxUsage": 100, "usedCount": 0, "active": true, "status": "ACTIVE" }
  - { "id": "11111111-1111-1111-1111-111111111111", "code": "SUMMER", "type": "PERCENT", "value": 10, "maxUsage": 100, "usedCount": 0, "active": false, "status": "SCHEDULED" }

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - Throws BusinessException(HttpStatus.CONFLICT)
  - N/A
- Expected Return:
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "voucherCode": "SUMMER", "discountType": "PERCENT", "discountValue": 10, "quantity": 100, "usedCount": 0, "active": true, "status": "ACTIVE", "message": "Voucher created successfully" }
  - { "id": "11111111-1111-1111-1111-111111111111", "voucherCode": "SUMMER", "discountType": "PERCENT", "discountValue": 10, "quantity": 100, "usedCount": 0, "active": false, "status": "SCHEDULED", "message": "Voucher created successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Only business admin can perform this action"
  - "Required field is missing"
  - "End date must be after start date"
  - "Discount value and quantity must be greater than 0"
  - "discountType must be FIXED or PERCENT"
  - "Voucher code already exists"
  - N/A
