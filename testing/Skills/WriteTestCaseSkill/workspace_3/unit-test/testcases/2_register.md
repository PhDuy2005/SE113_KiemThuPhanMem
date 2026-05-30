## Information
Service Name: UserService
Method Name: register(ReqRegisterDTO request)
Mock class: 
   1. UserRepository
   2. RoleRepository
   3. PasswordEncoder
   4. EmailService

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Password Mismatch) | TC 2 (Null Email) | TC 3 (Email Exists) | TC 4 (Success) |
| :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Fails because password and confirmPassword do not match | Continues with null email to test normalizeEmail condition | Fails because email already exists in the system | Registers new user successfully |
| **Inputs** | | | | |
| request.email | "new@example.com" | null | "exist@example.com" | "new@example.com" |
| request.password | "password123" | "password123" | "password123" | "password123" |
| request.confirmPassword | "wrong_password" | "password123" | "password123" | "password123" |
| **Mock / Context Setup** | | | | |
| userRepository.existsByEmail(...) | N/A | returns false | returns true | returns false |
| roleRepository.findByName("CUSTOMER") | N/A | returns { "id": 1, "name": "CUSTOMER" } | N/A | returns { "id": 1, "name": "CUSTOMER" } |
| passwordEncoder.encode(password) | N/A | returns "encoded_password" | N/A | returns "encoded_password" |
| userRepository.save(user) | N/A | verify called | N/A | verify called |
| emailService.sendRegistrationVerification(...) | N/A | verify called | N/A | verify called |
| **Expected Exception** | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A | Throws BusinessException(HttpStatus.BAD_REQUEST) | N/A |
| **Expected Return** | N/A | { "message": "Registration created. Please verify your email.", "token": "mock_verification_token" } | N/A | { "message": "Registration created. Please verify your email.", "token": "mock_verification_token" } |
| **Message** | "Password confirmation does not match" | N/A | "Account already exists" | N/A |

## Data Variations Summary (Tập dữ liệu)

### INPUT METHOD
- request.email:
  - "new@example.com"
  - null
  - "exist@example.com"
- request.password:
  - "password123"
- request.confirmPassword:
  - "wrong_password"
  - "password123"

### MOCK/ CONTEXT SETUP
- userRepository.existsByEmail(...):
  - N/A
  - false
  - true
- roleRepository.findByName("CUSTOMER"):
  - N/A
  - { "id": 1, "name": "CUSTOMER" }
- passwordEncoder.encode(password):
  - N/A
  - "encoded_password"
- userRepository.save(user):
  - N/A
  - verify called
- emailService.sendRegistrationVerification(...):
  - N/A
  - verify called

### EXPECTED OUTPUT & MESSAGE
- Expected Exception:
  - Throws BusinessException(HttpStatus.BAD_REQUEST)
  - N/A
- Expected Return:
  - N/A
  - { "message": "Registration created. Please verify your email.", "token": "mock_verification_token" }
- Message:
  - "Password confirmation does not match"
  - N/A
  - "Account already exists"
