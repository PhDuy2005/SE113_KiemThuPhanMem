## Infomation
Service Name: UserService
Method Name: register(ReqRegisterDTO)
Mock class: 
   1. UserRepository userRepository
   2. RoleRepository roleRepository
   3. PasswordEncoder passwordEncoder
   4. EmailService emailService

Mock Data:
   - ReqRegisterDTO request: {email: "test@example.com", password: "password123", confirmPassword: "password123"}
   - Role customerRole: {id: 1, name: "CUSTOMER"}

## Testcase
### Testcase 1
Short Description: Test registration fails when password and confirm password do not match.
Input: ReqRegisterDTO {email: "test@example.com", password: "password123", confirmPassword: "wrongPassword"}
Expected Output: BusinessException (400, "Password confirmation does not match")
Actual Output: BusinessException (400, "Password confirmation does not match")

### Testcase 2
Short Description: Test registration fails when the email already exists in the system.
Input: ReqRegisterDTO {email: "existed@test.com", password: "password123", confirmPassword: "password123"}
Expected Output: BusinessException (400, "Account already exists")
Actual Output: BusinessException (400, "Account already exists")

### Testcase 3
Short Description: Test registration succeeds when inputs are valid and email is unique.
Input: ReqRegisterDTO {email: "new@test.com", password: "password123", confirmPassword: "password123"}
Expected Output: ResAuthActionDTO {message: "Registration created. Please verify your email.", token: "550e8400-e29b-41d4-a716-446655440000"}
Actual Output: ResAuthActionDTO {message: "Registration created. Please verify your email.", token: "550e8400-e29b-41d4-a716-446655440000"}
