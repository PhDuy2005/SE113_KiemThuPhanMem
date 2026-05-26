---
name: unit-test-convertToDTO-condition-coverage
description: Unit tests with 100% condition coverage for RoleService.convertToDTO method.
---

## Infomation
Service Name: RoleService
Method Name: convertToDTO(Role role)
Mock class: None

Mock Data:
   - roleWithPermissions: Role {id: 1, name: "ADMIN", permissions: [Permission{id: 10, name: "READ"}]}
   - roleNoPermissions: Role {id: 2, name: "USER", permissions: []}
   - roleNullPermissions: Role {id: 3, name: "GUEST", permissions: null}

## Testcase
### Testcase 1
Short Description: Test convertToDTO when role is null.
Input: role = null.
Expected Output: null
Actual Output: null

### Testcase 2
Short Description: Test convertToDTO when role exists but permissions list is null.
Input: role = roleNullPermissions.
Expected Output: ResRoleDTO {id: 3, name: "GUEST", permissions: []}
Actual Output: ResRoleDTO {id: 3, name: "GUEST", permissions: []}

### Testcase 3
Short Description: Test convertToDTO when role exists but permissions list is empty.
Input: role = roleNoPermissions.
Expected Output: ResRoleDTO {id: 2, name: "USER", permissions: []}
Actual Output: ResRoleDTO {id: 2, name: "USER", permissions: []}

### Testcase 4
Short Description: Test convertToDTO when role exists and has permissions.
Input: role = roleWithPermissions.
Expected Output: ResRoleDTO {id: 1, name: "ADMIN", permissions: [ResPermissionDTO{id: 10, name: "READ"}]}
Actual Output: ResRoleDTO {id: 1, name: "ADMIN", permissions: [ResPermissionDTO{id: 10, name: "READ"}]}

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
