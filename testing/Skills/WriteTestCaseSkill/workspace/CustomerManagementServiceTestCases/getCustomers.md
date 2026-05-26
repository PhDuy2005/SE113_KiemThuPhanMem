---
name: unit-test-getCustomers-condition-coverage
description: Unit tests with 100% condition coverage for CustomerManagementService.getCustomers method.
---

## Infomation
Service Name: CustomerManagementService
Method Name: getCustomers(int pageNumber, int pageSize)
Mock class: 
   1. UserRepository userRepository
   2. SecurityUtil (Static mock)

Mock Data:
   - customer1: User { id: "550e8400-e29b-41d4-a716-446655441111", email: "cust1@example.com", role: { name: "CUSTOMER" } }
   - customersPage: Page<User> containing [customer1]
   - emptyPage: Page<User> containing []

## Testcase
### Testcase 1
Short Description: Test getCustomers with pageSize <= 0 (defaults to 10).
Input: pageNumber = 1, pageSize = 0.
Expected Output: ResultPaginationDTO with pageSize 10.
Actual Output: ResultPaginationDTO with pageSize 10.

### Testcase 2
Short Description: Test getCustomers with pageNumber = 0 (defaults to 0 for repository).
Input: pageNumber = 0, pageSize = 10.
Expected Output: ResultPaginationDTO with meta.page = 1.
Actual Output: ResultPaginationDTO with meta.page = 1.

### Testcase 3
Short Description: Test getCustomers returns empty results with message.
Input: userRepository returns emptyPage.
Expected Output: ResultPaginationDTO with message "There are no customers".
Actual Output: ResultPaginationDTO with message "There are no customers".

### Testcase 4
Short Description: Test getCustomers returns list of customers.
Input: userRepository returns customersPage.
Expected Output: ResultPaginationDTO with 1 result, message = null.
Actual Output: ResultPaginationDTO with 1 result, message = null.

## Code of Test Case
```java
@Test
void getCustomers_PageSizeZero_UsesDefault() {
    // Arrange
    mockAdminAccess();
    Page<User> page = new PageImpl<>(List.of(new User()));
    when(userRepository.findByRoleNameIgnoreCase(eq("CUSTOMER"), any(Pageable.class))).thenReturn(page);

    // Act
    ResultPaginationDTO result = customerManagementService.getCustomers(1, 0);

    // Assert
    assertEquals(10, result.getMeta().getPageSize());
    verify(userRepository).findByRoleNameIgnoreCase(eq("CUSTOMER"), argThat(p -> p.getPageSize() == 10));
}

@Test
void getCustomers_Empty_ReturnsMessage() {
    // Arrange
    mockAdminAccess();
    when(userRepository.findByRoleNameIgnoreCase(eq("CUSTOMER"), any(Pageable.class))).thenReturn(Page.empty());

    // Act
    ResultPaginationDTO result = customerManagementService.getCustomers(1, 10);

    // Assert
    assertEquals("There are no customers", result.getMessage());
    assertTrue(result.getResult().isEmpty());
}

@Test
void getCustomers_Valid_ReturnsResults() {
    // Arrange
    mockAdminAccess();
    User customer = new User();
    customer.setEmail("cust1@example.com");
    Page<User> page = new PageImpl<>(List.of(customer));
    when(userRepository.findByRoleNameIgnoreCase(eq("CUSTOMER"), any(Pageable.class))).thenReturn(page);

    // Act
    ResultPaginationDTO result = customerManagementService.getCustomers(1, 10);

    // Assert
    assertNull(result.getMessage());
    assertEquals(1, result.getResult().size());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
