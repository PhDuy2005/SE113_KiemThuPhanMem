## Information
Service Name: UserService
Method Name: verifyRegistration(String token)
Mock class: 
   1. UserRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Invalid Token) | TC 2 (Success) |
| :--- | :--- | :--- |
| **Short Description** | Fails because token is invalid or not found | Verifies account successfully and sets status to ACTIVE |
| **Inputs** | | |
| token | "invalid_token" | "valid_token" |
| **Mock / Context Setup** | | |
| userRepository.findByVerificationToken(token) | returns empty | returns { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "PENDING", "verificationToken": "valid_token" } |
| userRepository.save(user) | N/A | verify called |
| **Expected Exception** | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A |
| **Expected Return** | N/A | { "message": "Registration verified successfully", "token": null } |
| **Message** | "Invalid verification link" | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- token:
  - "invalid_token"
  - "valid_token"

### MOCK/ CONTEXT SETUP
- userRepository.findByVerificationToken(token):
  - empty
  - { "id": "550e8400-e29b-41d4-a716-446655440000", "accountStatus": "PENDING", "verificationToken": "valid_token" }
- userRepository.save(user):
  - N/A
  - verify called

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - N/A
- Expected Return:
  - N/A
  - { "message": "Registration verified successfully", "token": null }
- Message:
  - "Invalid verification link"
  - N/A
