## Information
Service Name: UserService
Method Name: forgotPassword(ReqForgotPasswordDTO request)
Mock class: 
   1. UserRepository
   2. EmailService

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Null Email) | TC 2 (Email Not Found) | TC 3 (Success) |
| :--- | :--- | :--- | :--- |
| **Short Description** | Fails because email is null (testing normalizeEmail condition) | Fails because email does not exist in the database | Generates reset token and sends email successfully |
| **Inputs** | | | |
| request.email | null | "notfound@example.com" | "user@example.com" |
| **Mock / Context Setup** | | | |
| userRepository.findByEmail(...) | returns empty | returns empty | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com" } |
| userRepository.save(user) | N/A | N/A | verify called |
| emailService.sendPasswordReset(...) | N/A | N/A | verify called |
| **Expected Exception** | Throws BusinessException(HttpStatus.NOT_FOUND) | Throws BusinessException(HttpStatus.NOT_FOUND) | N/A |
| **Expected Return** | N/A | N/A | { "message": "Password reset email sent", "token": "mock_reset_token" } |
| **Message** | "Email does not exist" | "Email does not exist" | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- request.email:
  - null
  - "notfound@example.com"
  - "user@example.com"

### MOCK/ CONTEXT SETUP
- userRepository.findByEmail(...):
  - empty
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "email": "user@example.com" }
- userRepository.save(user):
  - N/A
  - verify called
- emailService.sendPasswordReset(...):
  - N/A
  - verify called

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.NOT_FOUND)
  - N/A
- Expected Return:
  - N/A
  - { "message": "Password reset email sent", "token": "mock_reset_token" }
- Message:
  - "Email does not exist"
  - N/A
