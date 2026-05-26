## Infomation
Service Name: UserService
Method Name: verifyRegistration(String)
Mock class: 
   1. UserRepository userRepository

Mock Data:
   - Token: "valid-token"
   - User: {email: "test@example.com", accountStatus: "PENDING", verificationToken: "valid-token"}

## Testcase
### Testcase 1
Short Description: Test verification fails when the provided token is not found in the database.
Input: token = "invalid-token"
Expected Output: BusinessException (400, "Invalid verification link")
Actual Output: BusinessException (400, "Invalid verification link")

### Testcase 2
Short Description: Test verification succeeds when the provided token is valid.
Input: token = "valid-token"
Expected Output: ResAuthActionDTO {message: "Registration verified successfully"}
Actual Output: ResAuthActionDTO {message: "Registration verified successfully"}
