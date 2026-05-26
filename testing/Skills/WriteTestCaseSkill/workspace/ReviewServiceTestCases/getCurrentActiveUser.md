---
name: unit-test-getCurrentActiveUser-condition-coverage
description: Unit tests with 100% condition coverage for ReviewService.getCurrentActiveUser and role helpers.
---

## Infomation
Service Name: ReviewService
Method Name: getCurrentActiveUser() and getCurrentStaffOrBusinessAdmin()
Mock class: 
   1. UserRepository userRepository
   2. SecurityUtil (Static mock)

Mock Data:
   - activeUser: User { id: "550e8400-e29b-41d4-a716-446655440001", accountStatus: "ACTIVE", role: { name: "CUSTOMER" } }
   - staffUser: User { id: "550e8400-e29b-41d4-a716-446655440002", accountStatus: "ACTIVE", role: { name: "STAFF" } }

## Testcase
### Testcase 1
Short Description: Test getCurrentActiveUser fails when no user is logged in.
Input: SecurityUtil.getCurrentUserLogin() returns Optional.empty()
Expected Output: BusinessException (401, "You must login first")
Actual Output: BusinessException (401, "You must login first")

### Testcase 2
Short Description: Test getCurrentActiveUser fails when user account is not active.
Input: User with accountStatus = "BLOCKED".
Expected Output: BusinessException (403, "User account is not active")
Actual Output: BusinessException (403, "User account is not active")

### Testcase 3
Short Description: Test getCurrentStaffOrBusinessAdmin fails for customer role.
Input: Current user has role "CUSTOMER".
Expected Output: BusinessException (403, "Only staff or business admin can perform this action")
Actual Output: BusinessException (403, "Only staff or business admin can perform this action")

### Testcase 4
Short Description: Test getCurrentStaffOrBusinessAdmin succeeds for staff role.
Input: Current user has role "STAFF".
Expected Output: User object.
Actual Output: User object.

## Code of Test Case
```java
@Test
void getCurrentActiveUser_NoLogin_ThrowsUnauthorized() {
    try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
        securityUtilMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> 
            ReflectionTestUtils.invokeMethod(reviewService, "getCurrentActiveUser"));
        
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("You must login first", exception.getMessage());
    }
}

@Test
void getCurrentStaffOrBusinessAdmin_Customer_ThrowsForbidden() {
    try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
        securityUtilMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        User user = new User();
        user.setAccountStatus("ACTIVE");
        Role role = new Role(); role.setName("CUSTOMER");
        user.setRole(role);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class, () -> 
            ReflectionTestUtils.invokeMethod(reviewService, "getCurrentStaffOrBusinessAdmin"));
        
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Only staff or business admin can perform this action", exception.getMessage());
    }
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
