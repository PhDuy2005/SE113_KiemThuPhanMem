---
name: unit-test-getCurrentActiveUser-condition-coverage
description: Unit tests with 100% condition coverage for CartService.getCurrentActiveUser method.
---

## Infomation
Service Name: CartService
Method Name: getCurrentActiveUser()
Mock class: 
   1. UserRepository userRepository
   2. SecurityUtil (Security context mock)

Mock Data:
   - email: "user@example.com"
   - userActive: User {email: "user@example.com", accountStatus: "ACTIVE"}
   - userInactive: User {email: "user@example.com", accountStatus: "INACTIVE"}
   - userStatusNull: User {email: "user@example.com", accountStatus: null}

## Testcase
### Testcase 1
Short Description: Test getCurrentActiveUser when no user is logged in (SecurityUtil returns empty).
Input: SecurityUtil.getCurrentUserLogin() returns Optional.empty()
Expected Output: BusinessException (401, "You must login first")
Actual Output: BusinessException (401, "You must login first")

### Testcase 2
Short Description: Test getCurrentActiveUser when the email from security context is not found in the database.
Input: SecurityUtil.getCurrentUserLogin() returns Optional.of("user@example.com"), userRepository.findByEmail("user@example.com") returns Optional.empty()
Expected Output: BusinessException (401, "User session is invalid")
Actual Output: BusinessException (401, "User session is invalid")

### Testcase 3
Short Description: Test getCurrentActiveUser when the user's account status is null.
Input: userRepository.findByEmail returns userStatusNull (accountStatus = null)
Expected Output: BusinessException (403, "User account is not active")
Actual Output: BusinessException (403, "User account is not active")

### Testcase 4
Short Description: Test getCurrentActiveUser when the user's account status is not "ACTIVE" (e.g., "INACTIVE").
Input: userRepository.findByEmail returns userInactive (accountStatus = "INACTIVE")
Expected Output: BusinessException (403, "User account is not active")
Actual Output: BusinessException (403, "User account is not active")

### Testcase 5
Short Description: Test getCurrentActiveUser when the user is logged in and the account is "ACTIVE".
Input: userRepository.findByEmail returns userActive (accountStatus = "ACTIVE")
Expected Output: User object (userActive)
Actual Output: User object (userActive)

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
