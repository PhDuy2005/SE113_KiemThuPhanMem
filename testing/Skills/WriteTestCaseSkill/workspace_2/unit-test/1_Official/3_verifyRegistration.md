## Information
Service Name: UserService
Method Name: verifyRegistration(String token)
Mock class: 
   1. UserRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Invalid Token) | TC 2 (Success Verification) |
| :--- | :--- | :--- |
| **Short Description** | Try to verify using a non-existent or expired token | Successfully verify account with a valid token |
| **Inputs** | | |
| `token` | "invalid-token-12345" | "valid-token-67890" |
| **Mock / Context Setup** | | |
| `SecurityContext` | null | null |
| `userRepository.findByVerificationToken` | returns empty | returns User(accountStatus="PENDING", verificationToken="valid-token-67890") |
| **Expected Output** | BusinessException(400, "Invalid verification link") | resAuthActionDTO.message = "Registration verified successfully", user accountStatus becomes "ACTIVE", verificationToken is cleared |
