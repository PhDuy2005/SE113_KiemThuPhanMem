## Information
Service Name: StaffManagementService
Method Name: lockStaff(UUID staffId)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Admin Status Null) | TC 4 (Admin INACTIVE) | TC 5 (Admin Role Null) | TC 6 (Admin Role Name Null) | TC 7 (Admin is STAFF) | TC 8 (Staff Not Found) | TC 9 (Target Role Null) | TC 10 (Target Role Name Null) | TC 11 (Target Role CUSTOMER) | TC 12 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because admin accountStatus is null | Fails because admin is not ACTIVE | Fails because admin role is null | Fails because admin role name is null | Fails because admin is not BUSINESS_ADMIN | Fails because target staff not found | Fails because target staff role is null | Fails because target staff role name is null | Fails because target staff is CUSTOMER | Success (target is STAFF, locked successfully) |
| **Inputs** | | | | | | | | | | | | |
| staffId | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` | `"11111111-1111-1111-1111-111111111111"` |
| **Mock / Context Setup** | | | | | | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` |
| userRepository.findByEmail(...) | N/A | returns empty | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": null }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "INACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": null }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": null } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` |
| userRepository.findById(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | returns `{ "id": "11111111-1111-1111-1111-111111111111", "email": "staff@example.com", "userFullName": "Nguyen Van Staff", "accountStatus": "ACTIVE", "role": null }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "email": "staff@example.com", "userFullName": "Nguyen Van Staff", "accountStatus": "ACTIVE", "role": { "id": "22222222-2222-2222-2222-222222222222", "name": null } }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "email": "staff@example.com", "userFullName": "Nguyen Van Staff", "accountStatus": "ACTIVE", "role": { "id": "22222222-2222-2222-2222-222222222222", "name": "CUSTOMER" } }` | returns `{ "id": "11111111-1111-1111-1111-111111111111", "email": "staff@example.com", "userFullName": "Nguyen Van Staff", "accountStatus": "ACTIVE", "role": { "id": "22222222-2222-2222-2222-222222222222", "name": "STAFF", "description": "Staff Role", "active": true } }` |
| userRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns `{ "id": "11111111-1111-1111-1111-111111111111", "email": "staff@example.com", "userFullName": "Nguyen Van Staff", "accountStatus": "LOCKED", "role": { "id": "22222222-2222-2222-2222-222222222222", "name": "STAFF", "description": "Staff Role", "active": true } }` |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.NOT_FOUND) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | `{ "id": "11111111-1111-1111-1111-111111111111", "email": "staff@example.com", "name": "Nguyen Van Staff", "accountStatus": "LOCKED", "role": { "id": "22222222-2222-2222-2222-222222222222", "name": "STAFF", "description": "Staff Role", "active": true }, "message": "Staff account locked successfully" }` |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "User account is not active" | "Only business admin can perform this action" | "Only business admin can perform this action" | "Only business admin can perform this action" | "Staff account not found" | "Only staff accounts can be locked by this action" | "Only staff accounts can be locked by this action" | "Only staff accounts can be locked by this action" | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- staffId:
  - "11111111-1111-1111-1111-111111111111"

### MOCK/ CONTEXT SETUP
- SecurityUtil.getCurrentUserLogin():
  - empty
  - "admin@example.com"
- userRepository.findByEmail(...):
  - N/A
  - empty
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": null }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "INACTIVE" }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": null }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": null } }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }
  - { "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }
- userRepository.findById(...):
  - N/A
  - returns empty
  - { "id": "11111111-1111-1111-1111-111111111111", "email": "staff@example.com", "userFullName": "Nguyen Van Staff", "accountStatus": "ACTIVE", "role": null }
  - { "id": "11111111-1111-1111-1111-111111111111", "email": "staff@example.com", "userFullName": "Nguyen Van Staff", "accountStatus": "ACTIVE", "role": { "id": "22222222-2222-2222-2222-222222222222", "name": null } }
  - { "id": "11111111-1111-1111-1111-111111111111", "email": "staff@example.com", "userFullName": "Nguyen Van Staff", "accountStatus": "ACTIVE", "role": { "id": "22222222-2222-2222-2222-222222222222", "name": "CUSTOMER" } }
  - { "id": "11111111-1111-1111-1111-111111111111", "email": "staff@example.com", "userFullName": "Nguyen Van Staff", "accountStatus": "ACTIVE", "role": { "id": "22222222-2222-2222-2222-222222222222", "name": "STAFF", "description": "Staff Role", "active": true } }
- userRepository.save(...):
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "email": "staff@example.com", "userFullName": "Nguyen Van Staff", "accountStatus": "LOCKED", "role": { "id": "22222222-2222-2222-2222-222222222222", "name": "STAFF", "description": "Staff Role", "active": true } }

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.NOT_FOUND)
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - N/A
- Expected Return:
  - N/A
  - { "id": "11111111-1111-1111-1111-111111111111", "email": "staff@example.com", "name": "Nguyen Van Staff", "accountStatus": "LOCKED", "role": { "id": "22222222-2222-2222-2222-222222222222", "name": "STAFF", "description": "Staff Role", "active": true }, "message": "Staff account locked successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Only business admin can perform this action"
  - "Staff account not found"
  - "Only staff accounts can be locked by this action"
  - N/A
