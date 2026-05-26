---
name: unit-test-role-permission-sync-condition-coverage
description: Unit tests with 100% condition coverage for RolePermissionMatrixService sync and matrix logic.
---

## Infomation
Service Name: RolePermissionMatrixService
Method Name: syncRolePermissions(ReqSyncRolePermissionsDTO request) and getMatrix()
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
Short Description: Test syncRolePermissions fails when request or rolePermissions is null.
Input: request = null, or request.rolePermissions = null.
Expected Output: BusinessException (400, "Invalid role or permission configuration")
Actual Output: BusinessException (400, "Invalid role or permission configuration")

### Testcase 2
Short Description: Test syncRolePermissions fails for duplicate role IDs in request.
Input: rolePermissions = [{ roleId: 1L }, { roleId: 1L }].
Expected Output: BusinessException (400, "Invalid role or permission configuration")
Actual Output: BusinessException (400, "Invalid role or permission configuration")

### Testcase 3
Short Description: Test syncRolePermissions fails for non-existent role ID.
Input: roleId = 999L (not in DB).
Expected Output: BusinessException (400, "Invalid role or permission configuration")
Actual Output: BusinessException (400, "Invalid role or permission configuration")

### Testcase 4
Short Description: Test syncRolePermissions fails when one or more permission IDs are invalid.
Input: permissionIds = [101L, 999L] (101 exists, 999 does not).
Expected Output: BusinessException (400, "Invalid role or permission configuration")
Actual Output: BusinessException (400, "Invalid role or permission configuration")

### Testcase 5
Short Description: Test syncRolePermissions succeeds with valid configuration.
Input: Valid roleId and list of permissionIds.
Expected Output: Saves role with new permissions and records audit log.
Actual Output: Saves role with new permissions and records audit log.

### Testcase 6
Short Description: Test getMatrix handles roles with null permissions set.
Input: Role with permissions = null.
Expected Output: ResRolePermissionMatrixDTO with empty permissionIds set for that role.
Actual Output: ResRolePermissionMatrixDTO with empty permissionIds set for that role.

## Code of Test Case
```java
@Test
void syncRolePermissions_DuplicateRoleId_ThrowsBadRequest() {
    mockAdminAccess();
    ReqSyncRolePermissionsDTO req = new ReqSyncRolePermissionsDTO();
    ReqSyncRolePermissionsDTO.RolePermissionSetting s1 = new ReqSyncRolePermissionsDTO.RolePermissionSetting();
    s1.setRoleId(1L);
    ReqSyncRolePermissionsDTO.RolePermissionSetting s2 = new ReqSyncRolePermissionsDTO.RolePermissionSetting();
    s2.setRoleId(1L); // Duplicate
    req.setRolePermissions(List.of(s1, s2));

    BusinessException exception = assertThrows(BusinessException.class, () -> 
        rolePermissionMatrixService.syncRolePermissions(req));
    assertEquals("Invalid role or permission configuration", exception.getMessage());
}

@Test
void syncRolePermissions_InvalidPermissionId_ThrowsBadRequest() {
    mockAdminAccess();
    ReqSyncRolePermissionsDTO req = new ReqSyncRolePermissionsDTO();
    ReqSyncRolePermissionsDTO.RolePermissionSetting s1 = new ReqSyncRolePermissionsDTO.RolePermissionSetting();
    s1.setRoleId(1L);
    s1.setPermissionIds(Set.of(101L, 999L));
    req.setRolePermissions(List.of(s1));

    when(roleRepository.findById(1L)).thenReturn(Optional.of(new Role()));
    when(permissionRepository.findAllById(anySet())).thenReturn(List.of(new Permission())); // Only 1 found, expected 2

    assertThrows(BusinessException.class, () -> rolePermissionMatrixService.syncRolePermissions(req));
}

@Test
void getMatrix_NullPermissions_ReturnsEmptySet() {
    mockAdminAccess();
    Role role = new Role(); role.setId(1L); role.setPermissions(null);
    when(roleRepository.findAll()).thenReturn(List.of(role));
    when(permissionRepository.findAll()).thenReturn(List.of());

    ResRolePermissionMatrixDTO matrix = rolePermissionMatrixService.getMatrix();
    
    assertEquals(1, matrix.getRolePermissions().size());
    assertTrue(matrix.getRolePermissions().get(0).getPermissionIds().isEmpty());
}

@Test
void syncRolePermissions_Valid_Succeeds() {
    mockAdminAccess();
    ReqSyncRolePermissionsDTO req = new ReqSyncRolePermissionsDTO();
    ReqSyncRolePermissionsDTO.RolePermissionSetting s1 = new ReqSyncRolePermissionsDTO.RolePermissionSetting();
    s1.setRoleId(1L);
    s1.setPermissionIds(Set.of(101L));
    req.setRolePermissions(List.of(s1));

    Role role = new Role(); role.setId(1L);
    when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
    when(permissionRepository.findAllById(anySet())).thenReturn(List.of(new Permission()));
    when(roleRepository.findAll()).thenReturn(List.of(role));

    ResRolePermissionMatrixDTO result = rolePermissionMatrixService.syncRolePermissions(req);
    
    assertEquals("Role permissions updated successfully", result.getMessage());
    verify(roleRepository).save(role);
    verify(auditLogService).record(any(), any(), any(), any(), any(), any());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
