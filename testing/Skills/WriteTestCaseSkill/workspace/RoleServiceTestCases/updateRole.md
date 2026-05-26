---
name: unit-test-updateRole-condition-coverage
description: Unit tests with 100% condition coverage for RoleService.updateRole method.
---

## Infomation
Service Name: RoleService
Method Name: updateRole(ReqUpdateRoleDTO request)
Mock class: 
   1. RoleRepository roleRepository

Mock Data:
   - existingRole: Role {id: 1, name: "ADMIN", description: "Administrator", active: true}
   - reqUpdateFull: {id: 1, name: "SUPER_ADMIN", description: "Super Administrator", active: false}
   - reqUpdateNameOnly: {id: 1, name: "ADMIN_MODIFIED"}
   - reqUpdateDescOnly: {id: 1, description: "New description"}
   - reqUpdateActiveOnly: {id: 1, active: false}

## Testcase
### Testcase 1
Short Description: Test updateRole fails when the role ID does not exist.
Input: request = {id: 999}, roleRepository.findById(999) returns Optional.empty().
Expected Output: IdInvalidException ("Role with id 999 does not exist")
Actual Output: IdInvalidException ("Role with id 999 does not exist")

### Testcase 2
Short Description: Test updateRole fails when the new name already exists in another role.
Input: request = {id: 1, name: "EXISTING_ROLE"}, roleRepository.findById(1) returns existingRole, roleRepository.existsByName("EXISTING_ROLE") returns true.
Expected Output: IllegalArgumentException ("Role with name 'EXISTING_ROLE' already exists")
Actual Output: IllegalArgumentException ("Role with name 'EXISTING_ROLE' already exists")

### Testcase 3
Short Description: Test updateRole succeeds with name update only.
Input: request = reqUpdateNameOnly, roleRepository.findById(1) returns existingRole, name is unique.
Expected Output: ResRoleDTO {id: 1, name: "ADMIN_MODIFIED", description: "Administrator", active: true}
Actual Output: ResRoleDTO {id: 1, name: "ADMIN_MODIFIED", description: "Administrator", active: true}

### Testcase 4
Short Description: Test updateRole succeeds with description update only.
Input: request = reqUpdateDescOnly, roleRepository.findById(1) returns existingRole.
Expected Output: ResRoleDTO {id: 1, name: "ADMIN", description: "New description", active: true}
Actual Output: ResRoleDTO {id: 1, name: "ADMIN", description: "New description", active: true}

### Testcase 5
Short Description: Test updateRole succeeds with active status update only.
Input: request = reqUpdateActiveOnly, roleRepository.findById(1) returns existingRole.
Expected Output: ResRoleDTO {id: 1, name: "ADMIN", description: "Administrator", active: false}
Actual Output: ResRoleDTO {id: 1, name: "ADMIN", description: "Administrator", active: false}

### Testcase 6
Short Description: Test updateRole succeeds with all fields updated.
Input: request = reqUpdateFull, roleRepository.findById(1) returns existingRole, name is unique.
Expected Output: ResRoleDTO {id: 1, name: "SUPER_ADMIN", description: "Super Administrator", active: false}
Actual Output: ResRoleDTO {id: 1, name: "SUPER_ADMIN", description: "Super Administrator", active: false}

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
