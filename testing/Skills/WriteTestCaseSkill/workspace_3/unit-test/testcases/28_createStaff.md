## Information
Service Name: StaffManagementService
Method Name: createStaff(ReqCreateStaffDTO request)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. RoleRepository
   4. PasswordEncoder
   5. EmailService

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (No Login) | TC 2 (No Session) | TC 3 (Admin Status Null) | TC 4 (Admin INACTIVE) | TC 5 (Admin Role Null) | TC 6 (Admin Role Name Null) | TC 7 (Admin is STAFF) | TC 8 (Request Null) | TC 9 (Email Null) | TC 10 (Email Blank) | TC 11 (FullName Null) | TC 12 (FullName Blank) | TC 13 (RoleId Null) | TC 14 (Invalid Email) | TC 15 (Email Exists) | TC 16 (Role Not Found) | TC 17 (Target Role Name Null) | TC 18 (Target Role CUSTOMER) | TC 19 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because email is not in DB | Fails because admin accountStatus is null | Fails because admin is not ACTIVE | Fails because admin role is null | Fails because admin role name is null | Fails because user is not BUSINESS_ADMIN | Fails because request object is null | Fails because request.email is null | Fails because request.email is blank | Fails because request.fullName is null | Fails because request.fullName is blank | Fails because request.roleId is null | Fails because email format is invalid | Fails because email already exists | Fails because assigned role is not found | Fails because assigned role name is null | Fails because assigned role is not STAFF | Success (creates staff, saves to DB, sends email) |
| **Inputs** | | | | | | | | | | | | | | | | | | | |
| request | `{ "email": "valid@uit.edu.vn", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }` | `{ "email": "valid@uit.edu.vn", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }` | `{ "email": "valid@uit.edu.vn", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }` | `{ "email": "valid@uit.edu.vn", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }` | `{ "email": "valid@uit.edu.vn", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }` | `{ "email": "valid@uit.edu.vn", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }` | `{ "email": "valid@uit.edu.vn", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }` | `null` | `{ "email": null, "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }` | `{ "email": "   ", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }` | `{ "email": "valid@uit.edu.vn", "fullName": null, "roleId": "22222222-2222-2222-2222-222222222222" }` | `{ "email": "valid@uit.edu.vn", "fullName": "   ", "roleId": "22222222-2222-2222-2222-222222222222" }` | `{ "email": "valid@uit.edu.vn", "fullName": "Valid", "roleId": null }` | `{ "email": "invalid-email", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }` | `{ "email": "valid@uit.edu.vn", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }` | `{ "email": "valid@uit.edu.vn", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }` | `{ "email": "valid@uit.edu.vn", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }` | `{ "email": "valid@uit.edu.vn", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }` | `{ "email": "valid@uit.edu.vn", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }` |
| **Mock / Context Setup** | | | | | | | | | | | | | | | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` | returns `"admin@example.com"` |
| userRepository.findByEmail(...) | N/A | returns empty | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": null }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "INACTIVE" }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": null }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": null } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "STAFF" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` | returns `{ "id": "44444444-4444-4444-4444-444444444444", "accountStatus": "ACTIVE", "role": { "name": "BUSINESS_ADMIN" } }` |
| userRepository.existsByEmail(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns true | returns false | returns false | returns false | returns false |
| roleRepository.findById(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns empty | returns `{ "id": "22222222-2222-2222-2222-222222222222", "name": null }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "name": "CUSTOMER" }` | returns `{ "id": "22222222-2222-2222-2222-222222222222", "name": "STAFF", "description": "Staff Role", "active": true }` |
| passwordEncoder.encode(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns `"encoded_password"` |
| userRepository.save(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | returns `{ "id": "55555555-5555-5555-5555-555555555555", "email": "valid@uit.edu.vn", "userFullName": "Valid", "accountStatus": "ACTIVE", "role": { "id": "22222222-2222-2222-2222-222222222222", "name": "STAFF", "description": "Staff Role", "active": true } }` |
| emailService.sendStaffLoginDetails(...) | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | executes void |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.CONFLICT) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | `{ "id": "55555555-5555-5555-5555-555555555555", "email": "valid@uit.edu.vn", "name": "Valid", "accountStatus": "ACTIVE", "role": { "id": "22222222-2222-2222-2222-222222222222", "name": "STAFF", "description": "Staff Role", "active": true }, "message": "Staff account created successfully" }` |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | "User account is not active" | "Only business admin can perform this action" | "Only business admin can perform this action" | "Only business admin can perform this action" | "Required field is missing" | "Required field is missing" | "Required field is missing" | "Required field is missing" | "Required field is missing" | "Required field is missing" | "Email format is invalid" | "Account already exists" | "Role not found" | "Only STAFF role can be assigned to staff accounts" | "Only STAFF role can be assigned to staff accounts" | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- request:
  - { "email": "valid@uit.edu.vn", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }
  - null
  - { "email": null, "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }
  - { "email": "   ", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }
  - { "email": "valid@uit.edu.vn", "fullName": null, "roleId": "22222222-2222-2222-2222-222222222222" }
  - { "email": "valid@uit.edu.vn", "fullName": "   ", "roleId": "22222222-2222-2222-2222-222222222222" }
  - { "email": "valid@uit.edu.vn", "fullName": "Valid", "roleId": null }
  - { "email": "invalid-email", "fullName": "Valid", "roleId": "22222222-2222-2222-2222-222222222222" }

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
- userRepository.existsByEmail(...):
  - N/A
  - returns true
  - returns false
- roleRepository.findById(...):
  - N/A
  - returns empty
  - { "id": "22222222-2222-2222-2222-222222222222", "name": null }
  - { "id": "22222222-2222-2222-2222-222222222222", "name": "CUSTOMER" }
  - { "id": "22222222-2222-2222-2222-222222222222", "name": "STAFF", "description": "Staff Role", "active": true }
- passwordEncoder.encode(...):
  - N/A
  - returns "encoded_password"
- userRepository.save(...):
  - N/A
  - { "id": "55555555-5555-5555-5555-555555555555", "email": "valid@uit.edu.vn", "userFullName": "Valid", "accountStatus": "ACTIVE", "role": { "id": "22222222-2222-2222-2222-222222222222", "name": "STAFF", "description": "Staff Role", "active": true } }
- emailService.sendStaffLoginDetails(...):
  - N/A
  - executes void

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - Throws BusinessException(HttpStatus.CONFLICT)
  - N/A
- Expected Return:
  - N/A
  - { "id": "55555555-5555-5555-5555-555555555555", "email": "valid@uit.edu.vn", "name": "Valid", "accountStatus": "ACTIVE", "role": { "id": "22222222-2222-2222-2222-222222222222", "name": "STAFF", "description": "Staff Role", "active": true }, "message": "Staff account created successfully" }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Only business admin can perform this action"
  - "Required field is missing"
  - "Email format is invalid"
  - "Account already exists"
  - "Role not found"
  - "Only STAFF role can be assigned to staff accounts"
  - N/A
