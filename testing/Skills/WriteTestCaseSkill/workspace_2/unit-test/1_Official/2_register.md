## Information
Service Name: UserService
Method Name: register(ReqRegisterDTO request)
Mock class: 
   1. UserRepository
   2. RoleRepository
   3. EmailService

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Mismatched Passwords) | TC 2 (Email Already Exists) | TC 3 (Success Registration) |
| :--- | :--- | :--- | :--- |
| **Short Description** | Try to register with password and confirmPassword that do not match | Try to register with an email that is already used by another account | Successfully register a new account and send verification email |
| **Inputs** | | | |
| `request.email` | "newuser@example.com" | "existing@example.com" | "newuser@example.com" |
| `request.password` | "Password123!" | "Password123!" | "Password123!" |
| `request.confirmPassword` | "Password321!" | "Password123!" | "Password123!" |
| **Mock / Context Setup** | | | |
| `SecurityContext` | null | null | null |
| `userRepository.existsByEmail` | null | returns true | returns false |
| `RoleRepository.findByName("CUSTOMER")`| null | null | returns Role(id=1, name="CUSTOMER") |
| `EmailService.sendRegistrationVerification`| not called | not called | called successfully |
| **Expected Output** | BusinessException(400, "Password confirmation does not match") | BusinessException(400, "Account already exists") | resAuthActionDTO.message = "Registration created. Please verify your email.", token is generated and not null |
