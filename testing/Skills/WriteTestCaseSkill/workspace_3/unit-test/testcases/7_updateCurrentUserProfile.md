## Information
Service Name: UserService
Method Name: updateCurrentUserProfile(ReqUpdateProfileDTO request)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Logged In) | TC 2 (User Not Found) | TC 3 (User Not Active) | TC 4 (Success With Role) | TC 5 (Success Without Role) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because user is not authenticated | Fails because authenticated email is not in DB | Fails because user account status is not ACTIVE | Updates profile successfully for user with a role | Updates profile successfully for user without a role |
| **Inputs** | | | | | |
| request.fullName | "New Name" | "New Name" | "New Name" | "New Name" | "New Name" |
| request.phoneNumber | "0123456789" | "0123456789" | "0123456789" | "0123456789" | "0123456789" |
| **Mock / Context Setup** | | | | | |
| SecurityUtil.getCurrentUserLogin() | returns empty | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" |
| userRepository.findByEmail("user@example.com") | N/A | returns empty | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "PENDING" } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "accountStatus": "ACTIVE", "role": { "id": 1, "name": "CUSTOMER", "description": "Customer role", "active": true } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "accountStatus": "ACTIVE", "role": null } |
| userRepository.save(user) | N/A | N/A | N/A | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "userFullName": "New Name", "phoneNumber": "0123456789", "accountStatus": "ACTIVE", "role": { "id": 1, "name": "CUSTOMER", "description": "Customer role", "active": true } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "userFullName": "New Name", "phoneNumber": "0123456789", "accountStatus": "ACTIVE", "role": null } |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | N/A | N/A |
| **Expected Return** | N/A | N/A | N/A | { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "name": "New Name", "phoneNumber": "0123456789", "avatarUrl": null, "dateOfBirth": null, "accountStatus": "ACTIVE", "role": { "id": 1, "name": "CUSTOMER", "description": "Customer role", "active": true }, "createdAt": null, "updatedAt": null, "createdBy": null, "updatedBy": null } | { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "name": "New Name", "phoneNumber": "0123456789", "avatarUrl": null, "dateOfBirth": null, "accountStatus": "ACTIVE", "role": null, "createdAt": null, "updatedAt": null, "createdBy": null, "updatedBy": null } |
| **Message** | "You must login first" | "User session is invalid" | "User account is not active" | N/A | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- request.fullName:
  - "New Name"
- request.phoneNumber:
  - "0123456789"

### MOCK/ CONTEXT SETUP
- SecurityUtil.getCurrentUserLogin():
  - empty
  - "user@example.com"
- userRepository.findByEmail("user@example.com"):
  - N/A
  - empty
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "PENDING" }
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "accountStatus": "ACTIVE", "role": { "id": 1, "name": "CUSTOMER", "description": "Customer role", "active": true } }
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "accountStatus": "ACTIVE", "role": null }
- userRepository.save(user):
  - N/A
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "userFullName": "New Name", "phoneNumber": "0123456789", "accountStatus": "ACTIVE", "role": { "id": 1, "name": "CUSTOMER", "description": "Customer role", "active": true } }
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "userFullName": "New Name", "phoneNumber": "0123456789", "accountStatus": "ACTIVE", "role": null }

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - N/A
- Expected Return:
  - N/A
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "name": "New Name", "phoneNumber": "0123456789", "avatarUrl": null, "dateOfBirth": null, "accountStatus": "ACTIVE", "role": { "id": 1, "name": "CUSTOMER", "description": "Customer role", "active": true }, "createdAt": null, "updatedAt": null, "createdBy": null, "updatedBy": null }
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "name": "New Name", "phoneNumber": "0123456789", "avatarUrl": null, "dateOfBirth": null, "accountStatus": "ACTIVE", "role": null, "createdAt": null, "updatedAt": null, "createdBy": null, "updatedBy": null }
- Message:
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - N/A
