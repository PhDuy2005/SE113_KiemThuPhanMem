---
name: unit-test-deleteRole-condition-coverage
description: Unit tests with 100% condition coverage for RoleService.deleteRole method.
---

## Infomation
Service Name: RoleService
Method Name: deleteRole(Long id)
Mock class: 
   1. RoleRepository roleRepository

Mock Data:
   - role: Role {id: 1, name: "ADMIN"}

## Testcase
### Testcase 1
Short Description: Test deleteRole when the role exists.
Input: id = 1, roleRepository.findById(1) returns Optional.of(role).
Expected Output: Void (method completes normally, deleteById called)
Actual Output: Void (method completes normally, deleteById called)

### Testcase 2
Short Description: Test deleteRole fails when the role ID does not exist.
Input: id = 999, roleRepository.findById(999) returns Optional.empty().
Expected Output: IdInvalidException ("Role with id 999 does not exist")
Actual Output: IdInvalidException ("Role with id 999 does not exist")

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
