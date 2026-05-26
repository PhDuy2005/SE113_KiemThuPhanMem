---
name: unit-test-handleGetAllRoles-condition-coverage
description: Unit tests with 100% condition coverage for RoleService.handleGetAllRoles method.
---

## Infomation
Service Name: RoleService
Method Name: handleGetAllRoles(Specification<Role> spec, Pageable pageable)
Mock class: 
   1. RoleRepository roleRepository

Mock Data:
   - roleList: [Role {id: 1, name: "ADMIN"}, Role {id: 2, name: "USER"}]
   - pageable: PageRequest.of(0, 10)
   - pageRoles: Page object containing roleList, totalElements=2, totalPages=1

## Testcase
### Testcase 1
Short Description: Test handleGetAllRoles successfully returns paginated roles and metadata.
Input: spec = someSpecification, pageable = PageRequest.of(0, 10), roleRepository.findAll returns pageRoles.
Expected Output: ResultPaginationDTO {meta: {page: 1, pageSize: 10, totalPages: 1, totalItems: 2}, result: roleList}
Actual Output: ResultPaginationDTO {meta: {page: 1, pageSize: 10, totalPages: 1, totalItems: 2}, result: roleList}

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
