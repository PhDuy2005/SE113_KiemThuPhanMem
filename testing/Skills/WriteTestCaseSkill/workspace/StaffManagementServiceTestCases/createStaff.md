---
name: unit-test-createStaff-condition-coverage
description: Unit tests with 100% condition coverage for StaffManagementService.createStaff method.
---

## Infomation
Service Name: StaffManagementService
Method Name: createStaff(ReqCreateStaffDTO request)
Mock class: 
   1. UserRepository userRepository
   2. RoleRepository roleRepository
   3. EmailService emailService
   4. PasswordEncoder passwordEncoder
   5. SecurityUtil (Static mock)

Mock Data:
   - staffEmail: "newstaff@example.com"
   - staffName: "New Staff"
   - roleId: 2L (STAFF role)

## Testcase
### Testcase 1
Short Description: Test validateCreateStaffRequest fails for missing fields.
Input: email = null, fullName = "John", roleId = 1L.
Expected Output: BusinessException (400, "Required field is missing")
Actual Output: BusinessException (400, "Required field is missing")

### Testcase 2
Short Description: Test validateCreateStaffRequest fails for invalid email format.
Input: email = "invalid-email".
Expected Output: BusinessException (400, "Email format is invalid")
Actual Output: BusinessException (400, "Email format is invalid")

### Testcase 3
Short Description: Test createStaff fails if email already exists.
Input: userRepository.existsByEmail("newstaff@example.com") returns true.
Expected Output: BusinessException (409, "Account already exists")
Actual Output: BusinessException (409, "Account already exists")

### Testcase 4
Short Description: Test createStaff fails if role is not found.
Input: roleRepository.findById returns empty.
Expected Output: BusinessException (400, "Role not found")
Actual Output: BusinessException (400, "Role not found")

### Testcase 5
Short Description: Test createStaff fails if role is not STAFF.
Input: role name "BUSINESS_ADMIN" instead of "STAFF".
Expected Output: BusinessException (400, "Only STAFF role can be assigned to staff accounts")
Actual Output: BusinessException (400, "Only STAFF role can be assigned to staff accounts")

### Testcase 6
Short Description: Test createStaff succeeds with valid input.
Input: Valid email, name, and STAFF role.
Expected Output: ResUserDTO with success message, password generated and email sent.
Actual Output: ResUserDTO with success message, password generated and email sent.

## Code of Test Case
```java
@Test
void createStaff_InvalidEmail_ThrowsBadRequest() {
    mockAdminAccess();
    ReqCreateStaffDTO req = new ReqCreateStaffDTO();
    req.setEmail("invalid-email");
    req.setFullName("Name");
    req.setRoleId(1L);

    BusinessException exception = assertThrows(BusinessException.class, () -> 
        staffManagementService.createStaff(req));
    assertEquals("Email format is invalid", exception.getMessage());
}

@Test
void createStaff_EmailExists_ThrowsConflict() {
    mockAdminAccess();
    ReqCreateStaffDTO req = new ReqCreateStaffDTO();
    req.setEmail("existing@ex.com");
    req.setFullName("Name");
    req.setRoleId(1L);

    when(userRepository.existsByEmail("existing@ex.com")).thenReturn(true);

    assertThrows(BusinessException.class, () -> staffManagementService.createStaff(req));
}

@Test
void createStaff_InvalidRole_ThrowsBadRequest() {
    mockAdminAccess();
    ReqCreateStaffDTO req = new ReqCreateStaffDTO();
    req.setEmail("new@ex.com");
    req.setFullName("Name");
    req.setRoleId(1L);

    Role role = new Role(); role.setName("BUSINESS_ADMIN"); // Not STAFF
    when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

    BusinessException exception = assertThrows(BusinessException.class, () -> 
        staffManagementService.createStaff(req));
    assertEquals("Only STAFF role can be assigned to staff accounts", exception.getMessage());
}

@Test
void createStaff_Valid_Succeeds() {
    mockAdminAccess();
    ReqCreateStaffDTO req = new ReqCreateStaffDTO();
    req.setEmail("staff@ex.com");
    req.setFullName("Staff Name");
    req.setRoleId(1L);

    Role role = new Role(); role.setName("STAFF");
    when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
    when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    ResUserDTO result = staffManagementService.createStaff(req);
    
    assertEquals("Staff account created successfully", result.getMessage());
    verify(emailService).sendStaffLoginDetails(eq("staff@ex.com"), eq("Staff Name"), anyString());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
