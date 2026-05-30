## Information
Service Name: UserService
Method Name: resetPassword(ReqResetPasswordDTO request)
Mock class: 
   1. UserRepository
   2. PasswordEncoder

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Password Mismatch) | TC 2 (Token Not Found) | TC 3 (Token Null Expiry) | TC 4 (Token Expired) | TC 5 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because newPassword and confirmPassword do not match | Fails because token is not found in database | Fails because token expiration date is null | Fails because token expiration date is in the past | Resets password successfully |
| **Inputs** | | | | | |
| request.newPassword | "new_password" | "new_password" | "new_password" | "new_password" | "new_password" |
| request.confirmPassword | "wrong_password" | "new_password" | "new_password" | "new_password" | "new_password" |
| request.token | "any_token" | "invalid_token" | "valid_token" | "valid_token" | "valid_token" |
| **Mock / Context Setup** | | | | | |
| userRepository.findByResetPasswordToken(token) | N/A | returns empty | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "resetPasswordTokenExpiresAt": null } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "resetPasswordTokenExpiresAt": "2020-01-01T00:00:00Z" } | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "resetPasswordTokenExpiresAt": "2099-12-31T23:59:59Z" } |
| passwordEncoder.encode(newPassword) | N/A | N/A | N/A | N/A | returns "encoded_new_password" |
| userRepository.save(user) | N/A | N/A | N/A | N/A | verify called |
| **Expected Exception** | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A |
| **Expected Return** | N/A | N/A | N/A | N/A | { "message": "Password reset successfully", "token": null } |
| **Message** | "Password confirmation does not match" | "Invalid or expired reset link" | "Invalid or expired reset link" | "Invalid or expired reset link" | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- request.newPassword:
  - "new_password"
- request.confirmPassword:
  - "wrong_password"
  - "new_password"
- request.token:
  - "any_token"
  - "invalid_token"
  - "valid_token"

### MOCK/ CONTEXT SETUP
- userRepository.findByResetPasswordToken(token):
  - N/A
  - empty
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "resetPasswordTokenExpiresAt": null }
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "resetPasswordTokenExpiresAt": "2020-01-01T00:00:00Z" }
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "resetPasswordTokenExpiresAt": "2099-12-31T23:59:59Z" }
- passwordEncoder.encode(newPassword):
  - N/A
  - "encoded_new_password"
- userRepository.save(user):
  - N/A
  - verify called

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - N/A
- Expected Return:
  - N/A
  - { "message": "Password reset successfully", "token": null }
- Message:
  - "Password confirmation does not match"
  - "Invalid or expired reset link"
  - N/A
