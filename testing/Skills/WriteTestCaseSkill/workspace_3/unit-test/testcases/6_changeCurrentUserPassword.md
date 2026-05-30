## Information
Service Name: UserService
Method Name: changeCurrentUserPassword(ReqChangePasswordDTO request)
Mock class: 
   1. SecurityUtil (Static Mock)
   2. UserRepository
   3. PasswordEncoder

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Password Mismatch) | TC 2 (Not Logged In) | TC 3 (User Not Found) | TC 4 (User Not Active) | TC 5 (Wrong Current Password) | TC 6 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because new password and confirm password do not match | Fails because user is not authenticated | Fails because authenticated email is not in DB | Fails because user account status is not ACTIVE | Fails because current password does not match | Changes password successfully |
| **Inputs** | | | | | | |
| request.currentPassword | "any_pass" | "old_pass" | "old_pass" | "old_pass" | "wrong_old_pass" | "old_pass" |
| request.newPassword | "new_pass" | "new_pass" | "new_pass" | "new_pass" | "new_pass" | "new_pass" |
| request.confirmPassword | "wrong_pass" | "new_pass" | "new_pass" | "new_pass" | "new_pass" | "new_pass" |
| **Mock / Context Setup** | | | | | | |
| SecurityUtil.getCurrentUserLogin() | N/A | returns empty | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" | returns "user@example.com" |
| userRepository.findByEmail("user@example.com") | N/A | N/A | returns empty | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "PENDING" } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "password": "encoded_old_password" } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "password": "encoded_old_password" } |
| passwordEncoder.matches(...) | N/A | N/A | N/A | N/A | returns false | returns true |
| passwordEncoder.encode("new_pass") | N/A | N/A | N/A | N/A | N/A | returns "encoded_new_password" |
| userRepository.save(user) | N/A | N/A | N/A | N/A | N/A | verify called |
| **Expected Exception** | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | N/A | { "message": "Password changed successfully", "token": null } |
| **Message** | "Password confirmation does not match" | "You must login first" | "User session is invalid" | "User account is not active" | "Current password is incorrect" | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- request.currentPassword:
  - "any_pass"
  - "old_pass"
  - "wrong_old_pass"
- request.newPassword:
  - "new_pass"
- request.confirmPassword:
  - "wrong_pass"
  - "new_pass"

### MOCK/ CONTEXT SETUP
- SecurityUtil.getCurrentUserLogin():
  - N/A
  - empty
  - "user@example.com"
- userRepository.findByEmail("user@example.com"):
  - N/A
  - empty
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "PENDING" }
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "ACTIVE", "password": "encoded_old_password" }
- passwordEncoder.matches(...):
  - N/A
  - false
  - true
- passwordEncoder.encode("new_pass"):
  - N/A
  - "encoded_new_password"
- userRepository.save(user):
  - N/A
  - verify called

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - Throws BusinessException(HttpStatus.UNAUTHORIZED)
  - Throws BusinessException(HttpStatus.FORBIDDEN)
  - N/A
- Expected Return:
  - N/A
  - { "message": "Password changed successfully", "token": null }
- Message:
  - "Password confirmation does not match"
  - "You must login first"
  - "User session is invalid"
  - "User account is not active"
  - "Current password is incorrect"
  - N/A
