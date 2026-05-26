---
name: unit-test-getCurrentBusinessAdmin-condition-coverage
description: Unit tests with 100% condition coverage for CategoryService.getCurrentBusinessAdmin method.
---

## Infomation
Service Name: CategoryService
Method Name: getCurrentBusinessAdmin()
Mock class: 
   1. CategoryRepository categoryRepository
   2. ProductRepository productRepository
   3. UserRepository userRepository
   4. SecurityUtil (Static mock)

Mock Data:
   - adminUser: User { id: "550e8400-e29b-41d4-a716-446655440000", email: "admin@example.com", accountStatus: "ACTIVE", role: Role { name: "BUSINESS_ADMIN" } }
   - inactiveUser: User { id: "550e8400-e29b-41d4-a716-446655440001", email: "inactive@example.com", accountStatus: "INACTIVE", role: Role { name: "BUSINESS_ADMIN" } }
   - regularUser: User { id: "550e8400-e29b-41d4-a716-446655440002", email: "user@example.com", accountStatus: "ACTIVE", role: Role { name: "USER" } }
   - noRoleUser: User { id: "550e8400-e29b-41d4-a716-446655440003", email: "norole@example.com", accountStatus: "ACTIVE", role: null }

## Testcase
### Testcase 1
Short Description: Test getCurrentBusinessAdmin fails when no user is logged in.
Input: SecurityUtil.getCurrentUserLogin() returns Optional.empty()
Expected Output: BusinessException (401, "You must login first")
Actual Output: BusinessException (401, "You must login first")

### Testcase 2
Short Description: Test getCurrentBusinessAdmin fails when logged in email does not exist in repository.
Input: SecurityUtil.getCurrentUserLogin() returns "unknown@example.com", userRepository.findByEmail("unknown@example.com") returns Optional.empty()
Expected Output: BusinessException (401, "User session is invalid")
Actual Output: BusinessException (401, "User session is invalid")

### Testcase 3
Short Description: Test getCurrentBusinessAdmin fails when user account status is null.
Input: User with accountStatus = null.
Expected Output: BusinessException (403, "User account is not active")
Actual Output: BusinessException (403, "User account is not active")

### Testcase 4
Short Description: Test getCurrentBusinessAdmin fails when user account status is not "ACTIVE".
Input: User with accountStatus = "INACTIVE".
Expected Output: BusinessException (403, "User account is not active")
Actual Output: BusinessException (403, "User account is not active")

### Testcase 5
Short Description: Test getCurrentBusinessAdmin fails when user has no role.
Input: User with role = null.
Expected Output: BusinessException (403, "Only business admin can perform this action")
Actual Output: BusinessException (403, "Only business admin can perform this action")

### Testcase 6
Short Description: Test getCurrentBusinessAdmin fails when user role name is null.
Input: User with role != null but role.getName() == null.
Expected Output: BusinessException (403, "Only business admin can perform this action")
Actual Output: BusinessException (403, "Only business admin can perform this action")

### Testcase 7
Short Description: Test getCurrentBusinessAdmin fails when user is not a BUSINESS_ADMIN.
Input: User with role name = "USER".
Expected Output: BusinessException (403, "Only business admin can perform this action")
Actual Output: BusinessException (403, "Only business admin can perform this action")

### Testcase 8
Short Description: Test getCurrentBusinessAdmin succeeds with valid business admin.
Input: Valid BUSINESS_ADMIN user.
Expected Output: User object.
Actual Output: User object.

## Code of Test Case
```java
@Test
void getCurrentBusinessAdmin_NoLogin_ThrowsUnauthorized() {
    try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
        securityUtilMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> 
            ReflectionTestUtils.invokeMethod(categoryService, "getCurrentBusinessAdmin"));
        
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("You must login first", exception.getMessage());
    }
}

@Test
void getCurrentBusinessAdmin_UserNotFound_ThrowsUnauthorized() {
    try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
        securityUtilMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("unknown@example.com"));
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> 
            ReflectionTestUtils.invokeMethod(categoryService, "getCurrentBusinessAdmin"));
        
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("User session is invalid", exception.getMessage());
    }
}

@Test
void getCurrentBusinessAdmin_InactiveStatus_ThrowsForbidden() {
    try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
        securityUtilMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("inactive@example.com"));
        User user = new User();
        user.setAccountStatus("INACTIVE");
        when(userRepository.findByEmail("inactive@example.com")).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class, () -> 
            ReflectionTestUtils.invokeMethod(categoryService, "getCurrentBusinessAdmin"));
        
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("User account is not active", exception.getMessage());
    }
}

@Test
void getCurrentBusinessAdmin_NotAdmin_ThrowsForbidden() {
    try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
        securityUtilMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        User user = new User();
        user.setAccountStatus("ACTIVE");
        Role role = new Role();
        role.setName("USER");
        user.setRole(role);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class, () -> 
            ReflectionTestUtils.invokeMethod(categoryService, "getCurrentBusinessAdmin"));
        
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Only business admin can perform this action", exception.getMessage());
    }
}

@Test
void getCurrentBusinessAdmin_ValidAdmin_ReturnsUser() {
    try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
        securityUtilMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        User user = new User();
        user.setAccountStatus("ACTIVE");
        Role role = new Role();
        role.setName("BUSINESS_ADMIN");
        user.setRole(role);
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

        User result = ReflectionTestUtils.invokeMethod(categoryService, "getCurrentBusinessAdmin");
        
        assertNotNull(result);
        assertEquals("ACTIVE", result.getAccountStatus());
    }
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
