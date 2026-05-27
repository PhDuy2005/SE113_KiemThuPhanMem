## Information
Service Name: UserService
Method Name: changeCurrentUserPassword(ReqChangePasswordDTO request)
Mock class: 
   1. UserRepository
   2. PasswordEncoder

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Mismatched Passwords) | TC 2 (Not Logged In) | TC 3 (Blocked User) | TC 4 (Wrong Current Password) | TC 5 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | New password and confirm password do not match | Try to change password when not logged in | Try to change password when user account is BLOCKED | Provided current password does not match DB password | Successfully change password |
| **Inputs** | | | | | |
| `request.currentPassword` | "OldPass123!" | "OldPass123!" | "OldPass123!" | "WrongPass123!" | "OldPass123!" |
| `request.newPassword` | "NewPass123!" | "NewPass123!" | "NewPass123!" | "NewPass123!" | "NewPass123!" |
| `request.confirmPassword` | "Mismatch123!" | "NewPass123!" | "NewPass123!" | "NewPass123!" | "NewPass123!" |
| **Mock / Context Setup** | | | | | |
| `SecurityContext` | null | empty (not logged in) | logged in as "test@example.com" | logged in as "test@example.com" | logged in as "test@example.com" |
| `userRepository.findByEmail` | null | null | returns User(accountStatus="BLOCKED") | returns User(accountStatus="ACTIVE", password="EncodedOldPassword") | returns User(accountStatus="ACTIVE", password="EncodedOldPassword") |
| `passwordEncoder.matches` | not called | not called | not called | returns false | returns true |
| `passwordEncoder.encode` | not called | not called | not called | not called | returns "EncodedNewPassword" |
| **Expected Output** | BusinessException(400, "Password confirmation does not match") | BusinessException(401, "You must login first") | BusinessException(403, "User account is not active") | BusinessException(400, "Current password is incorrect") | resAuthActionDTO.message = "Password changed successfully", User.password updated |
