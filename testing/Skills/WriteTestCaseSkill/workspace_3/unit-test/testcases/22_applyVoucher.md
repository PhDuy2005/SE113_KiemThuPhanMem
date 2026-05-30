## Information
Service Name: CheckoutService
Method Name: applyVoucher(ReqApplyVoucherDTO request)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. VoucherRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Status Null) | TC 4 (Not Active) | TC 5 (Voucher Not Found) | TC 6 (Not Active) | TC 7 (Start > Now) | TC 8 (End < Now) | TC 9 (Usage Reached) | TC 10 (Total < Min) | TC 11 (PERCENT Null) | TC 12 (PERCENT Normal) | TC 13 (FIXED Null) | TC 14 (FIXED Normal) | TC 15 (Invalid Type) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user accountStatus is null | Fails because user is not ACTIVE | Fails because voucher is not found | Fails because voucher is not active | Fails because start date is in the future | Fails because end date is in the past | Fails because max usage is reached | Fails because total < minOrderAmount | Success (0% discount due to null value) | Success (10% discount) | Success (0 fixed discount due to null) | Success (20000 fixed discount) | Fails because voucher type is invalid |
| **Inputs** | | | | | | | | | | | | | | | |
| request | `{ "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }` | `{ "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }` | `{ "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }` | `{ "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }` | `{ "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }` | `{ "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }` | `{ "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }` | `{ "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }` | `{ "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }` | `{ "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }` | `{ "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }` | `{ "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }` | `{ "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }` | `{ "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }` | `{ "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }` |
| **Mock / Context Setup** | | | | | | | | | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" |
| userRepository.findByEmail(...) | N/A | returns empty | returns `{ "id": "11111111-1111-1111-1111-111111111111", "accountStatus": null }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "accountStatus": "INACTIVE" }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "accountStatus": "ACTIVE" }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "accountStatus": "ACTIVE" }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "accountStatus": "ACTIVE" }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "accountStatus": "ACTIVE" }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "accountStatus": "ACTIVE" }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "accountStatus": "ACTIVE" }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "accountStatus": "ACTIVE" }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "accountStatus": "ACTIVE" }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "accountStatus": "ACTIVE" }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "accountStatus": "ACTIVE" }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "accountStatus": "ACTIVE" }` |
| voucherRepository.findByCodeIgnoreCase(...) | N/A | N/A | N/A | N/A | returns empty | returns `{ "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": false }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "startDate": "3000-01-01T00:00:00Z" }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "endDate": "2000-01-01T00:00:00Z" }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "maxUsage": 100, "usedCount": 100 }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "minOrderAmount": 200000 }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "type": "PERCENT", "value": null }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "startDate": "2000-01-01T00:00:00Z", "endDate": "3000-01-01T00:00:00Z", "maxUsage": 100, "usedCount": null, "minOrderAmount": 50000, "type": "PERCENT", "value": 10 }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "startDate": "2000-01-01T00:00:00Z", "endDate": "3000-01-01T00:00:00Z", "maxUsage": 100, "usedCount": null, "minOrderAmount": 50000, "type": "FIXED", "value": null }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "startDate": "2000-01-01T00:00:00Z", "endDate": "3000-01-01T00:00:00Z", "maxUsage": 100, "usedCount": 50, "minOrderAmount": 50000, "type": "FIXED", "value": 20000 }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "startDate": "2000-01-01T00:00:00Z", "endDate": "3000-01-01T00:00:00Z", "maxUsage": 100, "usedCount": 50, "minOrderAmount": 50000, "type": "UNKNOWN", "value": 20000 }` |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A | N/A | N/A | N/A | Throws BusinessException(HttpStatus.BAD_REQUEST) |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | `{ "voucherCode": "VOUCHER10", "discountAmount": 0, "finalTotal": 100000, "message": "Voucher applied successfully" }` | `{ "voucherCode": "VOUCHER10", "discountAmount": 10000.00, "finalTotal": 90000.00, "message": "Voucher applied successfully" }` | `{ "voucherCode": "VOUCHER10", "discountAmount": 0, "finalTotal": 100000, "message": "Voucher applied successfully" }` | `{ "voucherCode": "VOUCHER10", "discountAmount": 20000, "finalTotal": 80000, "message": "Voucher applied successfully" }` | N/A |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "User account is not active" | "Invalid or expired voucher" | "Invalid or expired voucher" | "Invalid or expired voucher" | "Invalid or expired voucher" | "Invalid or expired voucher" | "Invalid or expired voucher" | N/A | N/A | N/A | N/A | "Invalid or expired voucher" |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- request:
  - { "voucherCode": "VOUCHER10", "totalOrderAmount": 100000 }

### MOCK/ CONTEXT SETUP
- SecurityUtil.getCurrentUserLogin():
  - empty
  - "user@example.com"
- userRepository.findByEmail(...):
  - N/A
  - empty
  - { "id": "11111111-1111-1111-1111-111111111111", "accountStatus": null }
  - { "id": "11111111-1111-1111-1111-111111111111", "accountStatus": "INACTIVE" }
  - { "id": "11111111-1111-1111-1111-111111111111", "accountStatus": "ACTIVE" }
- voucherRepository.findByCodeIgnoreCase(...):
  - N/A
  - empty
  - { "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": false }
  - { "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "startDate": "3000-01-01T00:00:00Z" }
  - { "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "endDate": "2000-01-01T00:00:00Z" }
  - { "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "maxUsage": 100, "usedCount": 100 }
  - { "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "minOrderAmount": 200000 }
  - { "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "type": "PERCENT", "value": null }
  - { "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "startDate": "2000-01-01T00:00:00Z", "endDate": "3000-01-01T00:00:00Z", "maxUsage": 100, "usedCount": null, "minOrderAmount": 50000, "type": "PERCENT", "value": 10 }
  - { "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "startDate": "2000-01-01T00:00:00Z", "endDate": "3000-01-01T00:00:00Z", "maxUsage": 100, "usedCount": null, "minOrderAmount": 50000, "type": "FIXED", "value": null }
  - { "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "startDate": "2000-01-01T00:00:00Z", "endDate": "3000-01-01T00:00:00Z", "maxUsage": 100, "usedCount": 50, "minOrderAmount": 50000, "type": "FIXED", "value": 20000 }
  - { "id": "22222222-2222-2222-2222-222222222222", "code": "VOUCHER10", "active": true, "startDate": "2000-01-01T00:00:00Z", "endDate": "3000-01-01T00:00:00Z", "maxUsage": 100, "usedCount": 50, "minOrderAmount": 50000, "type": "UNKNOWN", "value": 20000 }

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - N/A
- Expected Return:
  - N/A
  - { "voucherCode": "VOUCHER10", "discountAmount": 0, "finalTotal": 100000, "message": "Voucher applied successfully" }
  - { "voucherCode": "VOUCHER10", "discountAmount": 10000.00, "finalTotal": 90000.00, "message": "Voucher applied successfully" }
  - { "voucherCode": "VOUCHER10", "discountAmount": 20000, "finalTotal": 80000, "message": "Voucher applied successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Invalid or expired voucher"
  - N/A
