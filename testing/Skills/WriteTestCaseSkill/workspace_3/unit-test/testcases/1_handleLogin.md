## Information
Service Name: UserService
Method Name: handleLogin(ReqLoginDTO loginDTO)
Mock class: 
   1. UserRepository
   2. PasswordEncoder
   3. SecurityUtil

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (User Not Found) | TC 2 (Account Locked) | TC 3 (Wrong Password) | TC 4 (Wrong Password triggers Lock) | TC 5 (Inactive Account) | TC 6 (Success With Role) | TC 7 (Success No Role) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Login fails because email does not exist | Login fails because account is temporarily locked | Login fails because of incorrect password (failed attempts < 5) | Login fails because incorrect password reaches 5th attempt | Login fails because account status is not ACTIVE | Login successfully, user has role | Login successfully, user has no role |
| **Inputs** | | | | | | | |
| loginDTO.email | "user@example.com" | "user@example.com" | "user@example.com" | "user@example.com" | "user@example.com" | "user@example.com" | "user@example.com" |
| loginDTO.password | "wrong_password" | "correct_password" | "wrong_password" | "wrong_password" | "correct_password" | "correct_password" | "correct_password" |
| **Mock / Context Setup** | | | | | | | |
| userRepository.findByEmail(email) | returns empty | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "password": "encoded_password", "accountStatus": "ACTIVE", "failedLoginAttempts": 5, "lockedUntil": "2099-12-31T23:59:59Z" } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "password": "encoded_password", "accountStatus": "ACTIVE", "failedLoginAttempts": 0, "lockedUntil": null, "role": { "id": 1, "name": "CUSTOMER" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "password": "encoded_password", "accountStatus": "ACTIVE", "failedLoginAttempts": 4, "lockedUntil": null } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "password": "encoded_password", "accountStatus": "PENDING", "failedLoginAttempts": 0, "lockedUntil": null } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "password": "encoded_password", "accountStatus": "ACTIVE", "failedLoginAttempts": 0, "lockedUntil": null, "role": { "id": 1, "name": "CUSTOMER" } } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "password": "encoded_password", "accountStatus": "ACTIVE", "failedLoginAttempts": 0, "lockedUntil": null, "role": null } |
| passwordEncoder.matches(...) | N/A | N/A | returns false | returns false | returns true | returns true | returns true |
| securityUtil.createAccessToken(...) | N/A | N/A | N/A | N/A | N/A | returns "mock_access_token" | returns "mock_access_token" |
| securityUtil.createRefreshToken(...) | N/A | N/A | N/A | N/A | N/A | returns "mock_refresh_token" | returns "mock_refresh_token" |
| **Expected Exception** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | N/A | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | { "resLoginDTO": { "user": { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "name": null, "phoneNumber": null, "avatarUrl": null, "dateOfBirth": null }, "role": { "id": 1, "name": "CUSTOMER" }, "accessToken": "mock_access_token" }, "refreshToken": "mock_refresh_token" } | { "resLoginDTO": { "user": { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "name": null, "phoneNumber": null, "avatarUrl": null, "dateOfBirth": null }, "role": null, "accessToken": "mock_access_token" }, "refreshToken": "mock_refresh_token" } |
| **Message** | "Email or password incorrect" | "Account is temporarily locked" | "Email or password incorrect" | "Account locked due to too many failed attempts" | "Account is not active" | N/A | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- loginDTO.email:
  - "user@example.com"
- loginDTO.password:
  - "wrong_password"
  - "correct_password"

### MOCK/ CONTEXT SETUP
- userRepository.findByEmail(email):
  - empty
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "password": "encoded_password", "accountStatus": "ACTIVE", "failedLoginAttempts": 5, "lockedUntil": "2099-12-31T23:59:59Z" }
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "password": "encoded_password", "accountStatus": "ACTIVE", "failedLoginAttempts": 0, "lockedUntil": null, "role": { "id": 1, "name": "CUSTOMER" } }
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "password": "encoded_password", "accountStatus": "ACTIVE", "failedLoginAttempts": 4, "lockedUntil": null }
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "password": "encoded_password", "accountStatus": "PENDING", "failedLoginAttempts": 0, "lockedUntil": null }
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "password": "encoded_password", "accountStatus": "ACTIVE", "failedLoginAttempts": 0, "lockedUntil": null, "role": null }
- passwordEncoder.matches(...):
  - N/A
  - false
  - true
- securityUtil.createAccessToken(...):
  - N/A
  - "mock_access_token"
- securityUtil.createRefreshToken(...):
  - N/A
  - "mock_refresh_token"

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - N/A
- Expected Return:
  - N/A
  - { "resLoginDTO": { "user": { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "name": null, "phoneNumber": null, "avatarUrl": null, "dateOfBirth": null }, "role": { "id": 1, "name": "CUSTOMER" }, "accessToken": "mock_access_token" }, "refreshToken": "mock_refresh_token" }
  - { "resLoginDTO": { "user": { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com", "name": null, "phoneNumber": null, "avatarUrl": null, "dateOfBirth": null }, "role": null, "accessToken": "mock_access_token" }, "refreshToken": "mock_refresh_token" }
- Message:
  - "Email or password incorrect"
  - "Account is temporarily locked"
  - "Account locked due to too many failed attempts"
  - "Account is not active"
  - N/A
