---
name: unit-test-buildMatrix-condition-coverage
description: Unit tests with 100% condition coverage for RolePermissionMatrixService.buildMatrix method.
---

## Infomation
Service Name: RolePermissionMatrixService
Method Name: buildMatrix(String message)
Mock class: 
   1. RoleRepository roleRepository
   2. PermissionRepository permissionRepository

Mock Data:
   - role1: Role { id: 1L, name: "ADMIN", permissions: null }
   - role2: Role { id: 2L, name: "STAFF", permissions: [Permission { id: 101L }] }

## Testcase
### Testcase 1
Short Description: Test buildMatrix handles roles with null permissions.
Input: Role with permissions = null.
Expected Output: matrix.rolePermissions entry has an empty permissionIds set.
Actual Output: matrix.rolePermissions entry has an empty permissionIds set.

### Testcase 2
Short Description: Test buildMatrix converts entities to DTOs correctly.
Input: Roles and permissions in database.
Expected Output: ResRolePermissionMatrixDTO with populated roles, permissions, and selections.
Actual Output: ResRolePermissionMatrixDTO with populated roles, permissions, and selections.

## Code of Test Case
```java
@Test
void buildMatrix_NullPermissions_ReturnsEmptySet() {
    // Arrange
    Role role = new Role(); 
    role.setId(1L); 
    role.setPermissions(null); // Condition: null permissions
    
    when(roleRepository.findAll()).thenReturn(List.of(role));
    when(permissionRepository.findAll()).thenReturn(List.of());

    // Act
    ResRolePermissionMatrixDTO result = ReflectionTestUtils.invokeMethod(rolePermissionMatrixService, "buildMatrix", "Success");

    // Assert
    assertEquals(1, result.getRolePermissions().size());
    assertTrue(result.getRolePermissions().get(0).getPermissionIds().isEmpty());
    assertEquals("Success", result.getMessage());
}

@Test
void buildMatrix_WithPermissions_ReturnsPopulatedSet() {
    // Arrange
    Permission p = new Permission(); p.setId(101L); p.setName("READ");
    Role role = new Role(); 
    role.setId(2L); 
    role.setName("STAFF");
    role.setPermissions(Set.of(p));
    
    when(roleRepository.findAll()).thenReturn(List.of(role));
    when(permissionRepository.findAll()).thenReturn(List.of(p));

    // Act
    ResRolePermissionMatrixDTO result = ReflectionTestUtils.invokeMethod(rolePermissionMatrixService, "buildMatrix", null);

    // Assert
    assertEquals(1, result.getRolePermissions().size());
    assertTrue(result.getRolePermissions().get(0).getPermissionIds().contains(101L));
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
