---
name: unit-test-lockStaff-condition-coverage
description: Unit tests with 100% condition coverage for StaffManagementService.lockStaff method.
---

## Infomation
Service Name: StaffManagementService
Method Name: lockStaff(UUID staffId)
Mock class: 
   1. UserRepository userRepository
   2. SecurityUtil (Static mock)

Mock Data:
   - staffId: "550e8400-e29b-41d4-a716-446655440001"
   - staffUser: User { id: staffId, role: { name: "STAFF" } }

## Testcase
### Testcase 1
Short Description: Test lockStaff fails when staff account not found.
Input: userRepository.findById returns empty.
Expected Output: BusinessException (404, "Staff account not found")
Actual Output: BusinessException (404, "Staff account not found")

### Testcase 2
Short Description: Test lockStaff fails for non-staff accounts.
Input: User has role "CUSTOMER".
Expected Output: BusinessException (400, "Only staff accounts can be locked by this action")
Actual Output: BusinessException (400, "Only staff accounts can be locked by this action")

### Testcase 3
Short Description: Test lockStaff succeeds and clears session tokens.
Input: Valid staffId.
Expected Output: accountStatus = "LOCKED", refreshToken = null, success message returned.
Actual Output: accountStatus = "LOCKED", refreshToken = null, success message returned.

## Code of Test Case
```java
@Test
void lockStaff_NotFound_ThrowsNotFound() {
    mockAdminAccess();
    UUID staffId = UUID.randomUUID();
    when(userRepository.findById(staffId)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> staffManagementService.lockStaff(staffId));
}

@Test
void lockStaff_NonStaffRole_ThrowsBadRequest() {
    mockAdminAccess();
    UUID staffId = UUID.randomUUID();
    User user = new User();
    Role role = new Role(); role.setName("CUSTOMER");
    user.setRole(role);
    when(userRepository.findById(staffId)).thenReturn(Optional.of(user));

    BusinessException exception = assertThrows(BusinessException.class, () -> 
        staffManagementService.lockStaff(staffId));
    assertEquals("Only staff accounts can be locked by this action", exception.getMessage());
}

@Test
void lockStaff_Valid_Succeeds() {
    mockAdminAccess();
    UUID staffId = UUID.randomUUID();
    User staff = new User();
    staff.setId(staffId);
    staff.setRefreshToken("some-token");
    Role role = new Role(); role.setName("STAFF");
    staff.setRole(role);

    when(userRepository.findById(staffId)).thenReturn(Optional.of(staff));
    when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    ResUserDTO result = staffManagementService.lockStaff(staffId);

    assertEquals("LOCKED", result.getAccountStatus());
    assertNull(staff.getRefreshToken());
    assertEquals("Staff account locked successfully", result.getMessage());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
