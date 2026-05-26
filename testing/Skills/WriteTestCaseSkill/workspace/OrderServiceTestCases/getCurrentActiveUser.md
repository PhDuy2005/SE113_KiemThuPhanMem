---
name: unit-test-getCurrentActiveUser-condition-coverage
description: Unit tests with 100% condition coverage for OrderService.getCurrentActiveUser and related helpers.
---

## Infomation
Service Name: OrderService
Method Name: getCurrentActiveUser(), isCustomer(User), getCurrentStaffOrBusinessAdmin()
Mock class: 
   1. UserRepository userRepository
   2. SecurityUtil (Static mock)

Mock Data:
   - customerUser: User { id: "550e8400-e29b-41d4-a716-446655440001", accountStatus: "ACTIVE", role: { name: "CUSTOMER" } }
   - staffUser: User { id: "550e8400-e29b-41d4-a716-446655440002", accountStatus: "ACTIVE", role: { name: "STAFF" } }
   - adminUser: User { id: "550e8400-e29b-41d4-a716-446655440003", accountStatus: "ACTIVE", role: { name: "BUSINESS_ADMIN" } }

## Testcase
### Testcase 1
Short Description: Test getCurrentActiveUser fails when no user is logged in.
Input: SecurityUtil.getCurrentUserLogin() returns Optional.empty()
Expected Output: BusinessException (401, "You must login first")
Actual Output: BusinessException (401, "You must login first")

### Testcase 2
Short Description: Test getCurrentActiveUser fails when user account is not active.
Input: User with accountStatus = "INACTIVE".
Expected Output: BusinessException (403, "User account is not active")
Actual Output: BusinessException (403, "User account is not active")

### Testcase 3
Short Description: Test isCustomer returns true for customer role.
Input: User with role name "CUSTOMER".
Expected Output: true
Actual Output: true

### Testcase 4
Short Description: Test isCustomer returns false for null role or other role.
Input: User with role = null or role name "STAFF".
Expected Output: false
Actual Output: false

### Testcase 5
Short Description: Test getCurrentStaffOrBusinessAdmin fails for customer.
Input: Logged in user is a CUSTOMER.
Expected Output: BusinessException (403, "Only staff or business admin can perform this action")
Actual Output: BusinessException (403, "Only staff or business admin can perform this action")

### Testcase 6
Short Description: Test getCurrentStaffOrBusinessAdmin succeeds for staff or admin.
Input: Logged in user is STAFF or BUSINESS_ADMIN.
Expected Output: User object.
Actual Output: User object.

## Code of Test Case
```java
@Test
void getCurrentActiveUser_NoLogin_ThrowsUnauthorized() {
    try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
        securityUtilMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> 
            ReflectionTestUtils.invokeMethod(orderService, "getCurrentActiveUser"));
        
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("You must login first", exception.getMessage());
    }
}

@Test
void isCustomer_Conditions() {
    User user = new User();
    assertFalse((Boolean) ReflectionTestUtils.invokeMethod(orderService, "isCustomer", user));
    
    Role role = new Role();
    user.setRole(role);
    assertFalse((Boolean) ReflectionTestUtils.invokeMethod(orderService, "isCustomer", user));
    
    role.setName("CUSTOMER");
    assertTrue((Boolean) ReflectionTestUtils.invokeMethod(orderService, "isCustomer", user));
    
    role.setName("STAFF");
    assertFalse((Boolean) ReflectionTestUtils.invokeMethod(orderService, "isCustomer", user));
}

@Test
void getCurrentStaffOrBusinessAdmin_ForbiddenForCustomer() {
    try (MockedStatic<SecurityUtil> securityUtilMock = mockStatic(SecurityUtil.class)) {
        securityUtilMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("cust@example.com"));
        User user = new User();
        user.setAccountStatus("ACTIVE");
        Role role = new Role();
        role.setName("CUSTOMER");
        user.setRole(role);
        when(userRepository.findByEmail("cust@example.com")).thenReturn(Optional.of(user));

        BusinessException exception = assertThrows(BusinessException.class, () -> 
            ReflectionTestUtils.invokeMethod(orderService, "getCurrentStaffOrBusinessAdmin"));
        
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Only staff or business admin can perform this action", exception.getMessage());
    }
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
