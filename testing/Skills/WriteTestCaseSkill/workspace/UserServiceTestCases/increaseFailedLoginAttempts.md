## Infomation
Service Name: UserService
Method Name: increaseFailedLoginAttempts(User)
Mock class: 
   1. UserRepository userRepository

Mock Data:
   - User user: {failedLoginAttempts: 0}
   - MAX_FAILED_LOGIN_ATTEMPTS: 5

## Testcase
### Testcase 1
Short Description: Test throws NullPointerException when the user object is null.
Input: user = null
Expected Output: NullPointerException ("user must not be null")
Actual Output: NullPointerException ("user must not be null")

### Testcase 2
Short Description: Test increases attempts and handles null current failedLoginAttempts (defaults to 0).
Input: user = {failedLoginAttempts: null}
Expected Output: returns 1, user.lockedUntil is null
Actual Output: returns 1, user.lockedUntil is null

### Testcase 3
Short Description: Test increases attempts and does not lock when total is less than 5.
Input: user = {failedLoginAttempts: 3}
Expected Output: returns 4, user.lockedUntil is null
Actual Output: returns 4, user.lockedUntil is null

### Testcase 4
Short Description: Test increases attempts and locks the user when total reaches 5.
Input: user = {failedLoginAttempts: 4}
Expected Output: returns 5, user.lockedUntil is set to a future timestamp
Actual Output: returns 5, user.lockedUntil is set to a future timestamp
