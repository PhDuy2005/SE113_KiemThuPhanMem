## Information
Service Name: UserService
Method Name: resetPassword(ReqResetPasswordDTO request)
Mock class: 
   1. UserRepository
   2. PasswordEncoder

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Mismatched Passwords) | TC 2 (Invalid Token) | TC 3 (Expired Token) | TC 4 (Success) |
| :--- | :--- | :--- | :--- | :--- |
| **Short Description** | New password and confirm password do not match | Token is invalid or does not exist in DB | Token exists but has expired | Successfully reset password with a valid token |
| **Inputs** | | | | |
| `request.token` | "valid-token-123" | "invalid-token" | "expired-token" | "valid-token-123" |
| `request.newPassword` | "NewPass123!" | "NewPass123!" | "NewPass123!" | "NewPass123!" |
| `request.confirmPassword` | "WrongPass123!" | "NewPass123!" | "NewPass123!" | "NewPass123!" |
| **Mock / Context Setup** | | | | |
| `SecurityContext` | null | null | null | null |
| `userRepository.findByResetPasswordToken` | null | returns empty | returns User(resetPasswordToken="expired-token", resetPasswordTokenExpiresAt=Instant.now().minusSeconds(3600)) | returns User(resetPasswordToken="valid-token-123", resetPasswordTokenExpiresAt=Instant.now().plusSeconds(3600), failedLoginAttempts=3, lockedUntil=Instant.now().plusSeconds(600)) |
| `passwordEncoder.encode` | not called | not called | not called | returns "EncodedNewPassword" |
| **Expected Output** | BusinessException(400, "Password confirmation does not match") | BusinessException(400, "Invalid or expired reset link") | BusinessException(400, "Invalid or expired reset link") | resAuthActionDTO.message = "Password reset successfully", User object resets token to null, clears failedLoginAttempts to 0, unlocks account (lockedUntil=null), and updates password |
