## Infomation
Service Name: UserService
Method Name: resetFailedLoginAttempts(User)
Mock class: 
   1. UserRepository userRepository

Mock Data:
   - User user: {failedLoginAttempts: 3, lockedUntil: "2026-05-11T20:00:00Z"}

## Testcase
### Testcase 1
Short Description: Test throws NullPointerException when the user object is null.
Input: user = null
Expected Output: NullPointerException ("user must not be null")
Actual Output: NullPointerException ("user must not be null")

### Testcase 2
Short Description: Test returns early when attempts are already 0 and user is not locked.
Input: user = {failedLoginAttempts: 0, lockedUntil: null}
Expected Output: No interaction with userRepository
Actual Output: No interaction with userRepository

### Testcase 3
Short Description: Test resets attempts to 0 and clears lockedUntil when attempts were non-zero.
Input: user = {failedLoginAttempts: 3, lockedUntil: null}
Expected Output: user.failedLoginAttempts = 0, user.lockedUntil = null, userRepository.save(user) called
Actual Output: user.failedLoginAttempts = 0, user.lockedUntil = null, userRepository.save(user) called

### Testcase 4
Short Description: Test resets attempts and clears lockedUntil when user was locked but attempts were 0.
Input: user = {failedLoginAttempts: 0, lockedUntil: "2026-05-11T20:00:00Z"}
Expected Output: user.failedLoginAttempts = 0, user.lockedUntil = null, userRepository.save(user) called
Actual Output: user.failedLoginAttempts = 0, user.lockedUntil = null, userRepository.save(user) called
