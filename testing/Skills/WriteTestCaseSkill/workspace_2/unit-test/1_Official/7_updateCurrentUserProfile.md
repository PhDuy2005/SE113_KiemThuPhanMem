## Information
Service Name: UserService
Method Name: updateCurrentUserProfile(ReqUpdateProfileDTO request)
Mock class: 
   1. UserRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Logged In) | TC 2 (Blocked User) | TC 3 (Success) |
| :--- | :--- | :--- | :--- |
| **Short Description** | Try to update profile when not logged in | Try to update profile when user account is not ACTIVE | Successfully update user full name and phone number |
| **Inputs** | | | |
| `request.fullName` | "Thuan Duong" | "Thuan Duong" | "Thuan Duong" |
| `request.phoneNumber` | "0987654321" | "0987654321" | "0987654321" |
| **Mock / Context Setup** | | | |
| `SecurityContext` | empty (not logged in) | logged in as "test@example.com" | logged in as "test@example.com" |
| `userRepository.findByEmail` | null | returns User(accountStatus="BLOCKED") | returns User(accountStatus="ACTIVE", userFullName="Old Name", phoneNumber="0123") |
| **Expected Output** | BusinessException(401, "You must login first") | BusinessException(403, "User account is not active") | resUserDTO.name = "Thuan Duong", phoneNumber = "0987654321" |
