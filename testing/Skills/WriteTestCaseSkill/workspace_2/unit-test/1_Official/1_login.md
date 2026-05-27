## Information
Controller/Service Name: UserService
Method Name: handleLogin(ReqLoginDTO loginDTO)
Mock class: 
   1. UserRepository
   2. PasswordEncoder
   3. SecurityUtil

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Email Not Found) | TC 2 (Temporarily Locked) | TC 3 (Wrong Password - Warning) | TC 4 (Wrong Password - Gets Locked) | TC 5 (Inactive Account) | TC 6 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Try to login with an email that does not exist in DB | Try to login when account is temporarily locked due to too many failed attempts | Try to login with correct email but wrong password (attempts < 5) | Try to login with wrong password and hit the maximum failed attempt limit (5) | Try to login with correct credentials but account is not ACTIVE | Successfully login with correct credentials |
| **Inputs** | | | | | | |
| `loginDTO.email` | "nonexistent@example.com" | "user@example.com" | "user@example.com" | "user@example.com" | "user@example.com" | "user@example.com" |
| `loginDTO.password` | "Pass123!" | "Pass123!" | "WrongPass" | "WrongPass" | "Pass123!" | "Pass123!" |
| **Mock / Context Setup** | | | | | | |
| `SecurityContext` | null | null | null | null | null | null |
| `userRepository.findByEmail` | returns Optional.empty() | returns Optional.of(User(lockedUntil=future)) | returns Optional.of(User(failedAttempts=2)) | returns Optional.of(User(failedAttempts=4)) | returns Optional.of(User(accountStatus="PENDING")) | returns Optional.of(User(accountStatus="ACTIVE")) |
| `passwordEncoder.matches` | not called | not called | returns false | returns false | returns true | returns true |
| `userRepository.save` | not called | not called | called (update attempts to 3) | called (update attempts to 5, lockedUntil=future) | not called | called (reset attempts, update refresh token) |
| `securityUtil.createAccessToken` | not called | not called | not called | not called | not called | returns "eyJhbGciOi..." |
| `securityUtil.createRefreshToken`| not called | not called | not called | not called | not called | returns "eyJhbGciOi..." |
| **Expected Output** | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.UNAUTHORIZED) | Throws BusinessException(HttpStatus.FORBIDDEN) | Throws BusinessException(HttpStatus.FORBIDDEN) | Returns AuthResult with resLoginDTO and refreshToken |
