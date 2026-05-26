## Infomation
Service Name: UserService
Method Name: forgotPassword(ReqForgotPasswordDTO)
Mock class: 
   1. UserRepository userRepository
   2. EmailService emailService

Mock Data:
   - ReqForgotPasswordDTO request: {email: "test@example.com"}
   - User user: {email: "test@example.com"}

## Testcase
### Testcase 1
Short Description: Test forgot password fails when the email does not exist in the database.
Input: ReqForgotPasswordDTO {email: "nonexistent@example.com"}
Expected Output: BusinessException (404, "Email does not exist")
Actual Output: BusinessException (404, "Email does not exist")

### Testcase 2
Short Description: Test forgot password succeeds when the email exists.
Input: ReqForgotPasswordDTO {email: "test@example.com"}
Expected Output: ResAuthActionDTO {message: "Password reset email sent", token: "550e8400-e29b-41d4-a716-446655440000"}
Actual Output: ResAuthActionDTO {message: "Password reset email sent", token: "550e8400-e29b-41d4-a716-446655440000"}
