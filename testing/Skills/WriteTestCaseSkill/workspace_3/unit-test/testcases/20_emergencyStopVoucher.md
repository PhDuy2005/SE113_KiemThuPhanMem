## Information
Service Name: VoucherManagementService
Method Name: emergencyStopVoucher(UUID voucherId)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. VoucherRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Not Active) | TC 4 (Not Admin) | TC 5 (Voucher Not Found) | TC 6 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because user is not ACTIVE | Fails because user is not BUSINESS_ADMIN | Fails because voucherId does not exist | Stops voucher successfully (active=false, status=STOPPED) |
| **Inputs** | | | | | | |
| voucherId | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" | "11111111-1111-1111-1111-111111111111" |
| **Mock / Context Setup** | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" | returns "admin@example.com" |
| userRepository.findByEmail(...) | N/A | returns empty | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "PENDING" } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "CUSTOMER" } } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } | returns { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } } |
| voucherRepository.findById(...) | N/A | N/A | N/A | N/A | returns empty | returns { "id": "11111111-1111-1111-1111-111111111111", "code": "SUMMER", "type": "PERCENT", "value": 10, "maxUsage": 100, "usedCount": 5, "minOrderAmount": 0, "startDate": "today", "endDate": "next_month", "active": true, "status": "ACTIVE" } |
| voucherRepository.save(...) | N/A | N/A | N/A | N/A | N/A | returns { "id": "11111111-1111-1111-1111-111111111111", "code": "SUMMER", "type": "PERCENT", "value": 10, "maxUsage": 100, "usedCount": 5, "minOrderAmount": 0, "startDate": "today", "endDate": "next_month", "active": false, "status": "STOPPED" } |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.NOT_FOUND) | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | { "id": "11111111-1111-1111-1111-111111111111", "voucherCode": "SUMMER", "discountType": "PERCENT", "discountValue": 10, "quantity": 100, "usedCount": 5, "minOrderAmount": 0, "startDate": "today", "endDate": "next_month", "active": false, "status": "STOPPED", "message": "Voucher stopped successfully" } |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "Only business admin can perform this action" | "Voucher not found" | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- voucherId:
  - "11111111-1111-1111-1111-111111111111"

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
- voucherRepository.findById(...):
  - N/A
  - empty
  - { "id": "11111111-1111-1111-1111-111111111111", "code": "SUMMER", "type": "PERCENT", "value": 10, "maxUsage": 100, "usedCount": 5, "minOrderAmount": 0, "startDate": "today", "endDate": "next_month", "active": true, "status": "ACTIVE" }
- voucherRepository.save(...):
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "code": "SUMMER", "type": "PERCENT", "value": 10, "maxUsage": 100, "usedCount": 5, "minOrderAmount": 0, "startDate": "today", "endDate": "next_month", "active": false, "status": "STOPPED" }

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.NOT_FOUND)
  - N/A
- Expected Return:
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "voucherCode": "SUMMER", "discountType": "PERCENT", "discountValue": 10, "quantity": 100, "usedCount": 5, "minOrderAmount": 0, "startDate": "today", "endDate": "next_month", "active": false, "status": "STOPPED", "message": "Voucher stopped successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Only business admin can perform this action"
  - "Voucher not found"
  - N/A
