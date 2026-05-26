---
name: unit-test-getCurrentActiveUser-condition-coverage
description: Unit tests with 100% condition coverage for PaymentService.getCurrentActiveUser method.
---

## Infomation
Service Name: PaymentService
Method Name: getCurrentActiveUser()
Mock class: 
   1. UserRepository userRepository
   2. SecurityUtil (Static mock)

Mock Data:
   - activeUser: User { id: "550e8400-e29b-41d4-a716-446655440000", email: "user@example.com", accountStatus: "ACTIVE" }

## Testcase
### Testcase 1
Short Description: Test getCurrentActiveUser fails when no user is logged in.
Input: SecurityUtil.getCurrentUserLogin() returns Optional.empty()
Expected Output: BusinessException (401, "You must login first")
Actual Output: BusinessException (401, "You must login first")

### Testcase 2
Short Description: Test getCurrentActiveUser fails when logged in email does not exist.
Input: SecurityUtil.getCurrentUserLogin() returns "unknown@example.com", userRepository.findByEmail("unknown@example.com") returns Optional.empty()
Expected Output: BusinessException (401, "User session is invalid")
Actual Output: BusinessException (401, "User session is invalid")

### Testcase 3
Short Description: Test getCurrentActiveUser fails when user account status is null.
Input: User with accountStatus = null.
Expected Output: BusinessException (403, "User account is not active")
Actual Output: BusinessException (403, "User account is not active")

### Testcase 4
Short Description: Test getCurrentActiveUser fails when user account status is not "ACTIVE".
Input: User with accountStatus = "LOCKED".
Expected Output: BusinessException (403, "User account is not active")
Actual Output: BusinessException (403, "User account is not active")

### Testcase 5
Short Description: Test getCurrentActiveUser succeeds with valid active user.
Input: Valid ACTIVE user.
Expected Output: User object.
Actual Output: User object.

## Code of Test Case
```java
@Test
void getCurrentActiveUser_NoLogin_ThrowsUnauthorized() {
    try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
        securityUtilMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> 
            ReflectionTestUtils.invokeMethod(paymentService, "getCurrentActiveUser"));
        
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("You must login first", exception.getMessage());
    }
}

@Test
void getCurrentActiveUser_Valid_ReturnsUser() {
    try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
        securityUtilMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        User user = new User();
        user.setAccountStatus("ACTIVE");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        User result = ReflectionTestUtils.invokeMethod(paymentService, "getCurrentActiveUser");
        
        assertNotNull(result);
        assertEquals("ACTIVE", result.getAccountStatus());
    }
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
