## Information
Service Name: UserService
Method Name: forgotPassword(ReqForgotPasswordDTO request)
Mock class: 
   1. UserRepository
   2. EmailService

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Email Not Found) | TC 2 (Success) |
| :--- | :--- | :--- |
| **Short Description** | Try to request forgot password for an email that does not exist | Successfully generate reset token and send email |
| **Inputs** | | |
| `request.email` | "nonexistent@example.com" | "validuser@example.com" |
| **Mock / Context Setup** | | |
| `SecurityContext` | null | null |
| `userRepository.findByEmail` | returns empty | returns User(email="validuser@example.com") |
| `EmailService.sendPasswordReset` | not called | called successfully |
| **Expected Output** | BusinessException(404, "Email does not exist") | resAuthActionDTO.message = "Password reset email sent", resetToken is generated, user gets resetPasswordTokenExpiresAt set in future |
