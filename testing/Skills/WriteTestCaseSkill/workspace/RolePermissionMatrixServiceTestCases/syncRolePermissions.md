---
name: unit-test-syncRolePermissions-condition-coverage
description: Unit tests with 100% condition coverage for RolePermissionMatrixService.syncRolePermissions method.
---

## Infomation
Service Name: RolePermissionMatrixService
Method Name: syncRolePermissions(ReqSyncRolePermissionsDTO request)
Mock class: 
   1. RoleRepository roleRepository
   2. PermissionRepository permissionRepository
   3. UserRepository userRepository
   4. AuditLogService auditLogService
   5. SecurityUtil (Static mock)

Mock Data:
   - role1: Role { id: 1L, name: "STAFF", permissions: [] }
   - permission1: Permission { id: 101L, name: "CREATE_PRODUCT" }

## Testcase
### Testcase 1
Short Description: Test syncRolePermissions fails when request is null.
Input: request = null.
Expected Output: BusinessException (400, "Invalid role or permission configuration")
Actual Output: BusinessException (400, "Invalid role or permission configuration")

### Testcase 2
Short Description: Test syncRolePermissions fails when rolePermissions list is null.
Input: request.rolePermissions = null.
Expected Output: BusinessException (400, "Invalid role or permission configuration")
Actual Output: BusinessException (400, "Invalid role or permission configuration")

### Testcase 3
Short Description: Test syncRolePermissions fails for duplicate role IDs in the settings list.
Input: rolePermissions = [{ roleId: 1L }, { roleId: 1L }].
Expected Output: BusinessException (400, "Invalid role or permission configuration")
Actual Output: BusinessException (400, "Invalid role or permission configuration")

### Testcase 4
Short Description: Test syncRolePermissions fails for non-existent role ID.
Input: roleId = 999L (not in database).
Expected Output: BusinessException (400, "Invalid role or permission configuration")
Actual Output: BusinessException (400, "Invalid role or permission configuration")

### Testcase 5
Short Description: Test syncRolePermissions fails when one or more permission IDs are invalid.
Input: permissionIds = [101L, 999L] (101 exists, 999 does not).
Expected Output: BusinessException (400, "Invalid role or permission configuration")
Actual Output: BusinessException (400, "Invalid role or permission configuration")

### Testcase 6
Short Description: Test syncRolePermissions succeeds with valid role and permissions.
Input: roleId = 1L, permissionIds = [101L].
Expected Output: Role permissions updated, Audit log recorded, and matrix returned.
Actual Output: Role permissions updated, Audit log recorded, and matrix returned.

## Code of Test Case
```java
@Test
void syncRolePermissions_DuplicateRoleId_ThrowsBadRequest() {
    // Arrange
    mockAdminAccess();
    ReqSyncRolePermissionsDTO req = new ReqSyncRolePermissionsDTO();
    ReqSyncRolePermissionsDTO.RolePermissionSetting s1 = new ReqSyncRolePermissionsDTO.RolePermissionSetting();
    s1.setRoleId(1L);
    ReqSyncRolePermissionsDTO.RolePermissionSetting s2 = new ReqSyncRolePermissionsDTO.RolePermissionSetting();
    s2.setRoleId(1L);
    req.setRolePermissions(List.of(s1, s2));

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        rolePermissionMatrixService.syncRolePermissions(req));
    assertEquals("Invalid role or permission configuration", exception.getMessage());
}

@Test
void syncRolePermissions_Valid_Succeeds() {
    // Arrange
    mockAdminAccess();
    ReqSyncRolePermissionsDTO req = new ReqSyncRolePermissionsDTO();
    ReqSyncRolePermissionsDTO.RolePermissionSetting setting = new ReqSyncRolePermissionsDTO.RolePermissionSetting();
    setting.setRoleId(1L);
    setting.setPermissionIds(Set.of(101L));
    req.setRolePermissions(List.of(setting));

    Role role = new Role(); role.setId(1L);
    Permission p = new Permission(); p.setId(101L);
    
    when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
    when(permissionRepository.findAllById(anySet())).thenReturn(List.of(p));
    when(roleRepository.findAll()).thenReturn(List.of(role));
    when(permissionRepository.findAll()).thenReturn(List.of(p));

    // Act
    ResRolePermissionMatrixDTO result = rolePermissionMatrixService.syncRolePermissions(req);

    // Assert
    assertEquals("Role permissions updated successfully", result.getMessage());
    verify(roleRepository).save(role);
    verify(auditLogService).record(any(), any(), any(), any(), any(), any());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
