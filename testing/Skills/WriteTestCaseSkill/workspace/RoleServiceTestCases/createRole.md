---
name: unit-test-createRole-condition-coverage
description: Unit tests with 100% condition coverage for RoleService.createRole method.
---

## Infomation
Service Name: RoleService
Method Name: createRole(ReqCreateRoleDTO request)
Mock class: 
   1. RoleRepository roleRepository

Mock Data:
   - reqCreateRoleDTO: {name: "MANAGER", description: "Manager role"}
   - savedRole: Role {id: 4, name: "MANAGER", description: "Manager role", active: true}

## Testcase
### Testcase 1
Short Description: Test createRole fails when a role with the same name already exists.
Input: request = {name: "MANAGER"}, roleRepository.existsByName("MANAGER") returns true.
Expected Output: IllegalArgumentException ("Role with name 'MANAGER' already exists")
Actual Output: IllegalArgumentException ("Role with name 'MANAGER' already exists")

### Testcase 2
Short Description: Test createRole succeeds when the role name is unique.
Input: request = {name: "MANAGER", description: "Manager role"}, roleRepository.existsByName("MANAGER") returns false.
Expected Output: ResRoleDTO {id: 4, name: "MANAGER", description: "Manager role", active: true}
Actual Output: ResRoleDTO {id: 4, name: "MANAGER", description: "Manager role", active: true}

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
