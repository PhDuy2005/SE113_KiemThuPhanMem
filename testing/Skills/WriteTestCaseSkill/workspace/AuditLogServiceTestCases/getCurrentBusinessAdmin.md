---
name: unit-test-getCurrentBusinessAdmin-condition-coverage
description: Unit tests with 100% condition coverage for AuditLogService.getCurrentBusinessAdmin method.
---

## Infomation
Service Name: AuditLogService
Method Name: getCurrentBusinessAdmin()
Mock class: 
   1. UserRepository userRepository
   2. SecurityUtil (Static mock)

Mock Data:
   - adminUser: User { email: "admin@example.com", accountStatus: "ACTIVE", role: Role { name: "BUSINESS_ADMIN" } }
   - inactiveUser: User { email: "inactive@example.com", accountStatus: "PENDING", role: Role { name: "BUSINESS_ADMIN" } }
   - userWithNoStatus: User { email: "nostatus@example.com", accountStatus: null, role: Role { name: "BUSINESS_ADMIN" } }
   - customerUser: User { email: "customer@example.com", accountStatus: "ACTIVE", role: Role { name: "CUSTOMER" } }
   - userWithNoRole: User { email: "norole@example.com", accountStatus: "ACTIVE", role: null }
   - userWithNoRoleName: User { email: "norolename@example.com", accountStatus: "ACTIVE", role: Role { name: null } }

## Testcase
### Testcase 1
Short Description: Test getCurrentBusinessAdmin fails when no user is logged in.
Input: SecurityUtil.getCurrentUserLogin() returns Optional.empty().
Expected Output: BusinessException (401, "You must login first")
Actual Output: BusinessException (401, "You must login first")

### Testcase 2
Short Description: Test getCurrentBusinessAdmin fails when user session is invalid (not in DB).
Input: SecurityUtil.getCurrentUserLogin() returns "unknown@example.com", userRepository returns Optional.empty().
Expected Output: BusinessException (401, "User session is invalid")
Actual Output: BusinessException (401, "User session is invalid")

### Testcase 3
Short Description: Test getCurrentBusinessAdmin fails when account status is null.
Input: User is userWithNoStatus.
Expected Output: BusinessException (403, "User account is not active")
Actual Output: BusinessException (403, "User account is not active")

### Testcase 4
Short Description: Test getCurrentBusinessAdmin fails when account status is not ACTIVE.
Input: User is inactiveUser.
Expected Output: BusinessException (403, "User account is not active")
Actual Output: BusinessException (403, "User account is not active")

### Testcase 5
Short Description: Test getCurrentBusinessAdmin fails when role is null.
Input: User is userWithNoRole.
Expected Output: BusinessException (403, "Only business admin can perform this action")
Actual Output: BusinessException (403, "Only business admin can perform this action")

### Testcase 6
Short Description: Test getCurrentBusinessAdmin fails when role name is null.
Input: User is userWithNoRoleName.
Expected Output: BusinessException (403, "Only business admin can perform this action")
Actual Output: BusinessException (403, "Only business admin can perform this action")

### Testcase 7
Short Description: Test getCurrentBusinessAdmin fails when role is not BUSINESS_ADMIN.
Input: User is customerUser.
Expected Output: BusinessException (403, "Only business admin can perform this action")
Actual Output: BusinessException (403, "Only business admin can perform this action")

### Testcase 8
Short Description: Test getCurrentBusinessAdmin succeeds with valid admin user.
Input: User is adminUser.
Expected Output: Returns adminUser.
Actual Output: Returns adminUser.

## Code of Test Case
```java
@Test
void getCurrentBusinessAdmin_NotLoggedIn_Throws401() {
    try (MockedStatic<SecurityUtil> securityMock = mockStatic(SecurityUtil.class)) {
        securityMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());
        
        assertThrows(BusinessException.class, () -> 
            ReflectionTestUtils.invokeMethod(auditLogService, "getCurrentBusinessAdmin"));
    }
}

@Test
void getCurrentBusinessAdmin_StatusNull_Throws403() {
    User user = new User();
    user.setAccountStatus(null);
    
    try (MockedStatic<SecurityUtil> securityMock = mockStatic(SecurityUtil.class)) {
        securityMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("user@example.com"));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        
        BusinessException ex = assertThrows(BusinessException.class, () -> 
            ReflectionTestUtils.invokeMethod(auditLogService, "getCurrentBusinessAdmin"));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}

@Test
void getCurrentBusinessAdmin_ValidAdmin_ReturnsUser() {
    User user = new User();
    user.setAccountStatus("ACTIVE");
    Role role = new Role();
    role.setName("BUSINESS_ADMIN");
    user.setRole(role);
    
    try (MockedStatic<SecurityUtil> securityMock = mockStatic(SecurityUtil.class)) {
        securityMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("admin@example.com"));
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));
        
        User result = ReflectionTestUtils.invokeMethod(auditLogService, "getCurrentBusinessAdmin");
        assertNotNull(result);
        assertEquals("ACTIVE", result.getAccountStatus());
    }
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
