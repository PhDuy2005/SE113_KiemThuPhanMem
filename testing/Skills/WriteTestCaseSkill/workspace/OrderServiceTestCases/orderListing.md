---
name: unit-test-order-listing-condition-coverage
description: Unit tests with 100% condition coverage for OrderService listing methods.
---

## Infomation
Service Name: OrderService
Method Name: getOrderHistory(UUID, int, int) and getPendingOrdersForStaff(UUID, int, int)
Mock class: 
   1. OrderRepository orderRepository
   2. UserRepository userRepository
   3. SecurityUtil (Static mock)

Mock Data:
   - customerId: "550e8400-e29b-41d4-a716-446655440001"
   - ordersPage: Page<Order> containing [order1]

## Testcase
### Testcase 1
Short Description: Test getOrderHistory for customer (enforces their own ID).
Input: Current user is customer "id1", requests history for "id2".
Expected Output: Calls repository with "id1".
Actual Output: Calls repository with "id1".

### Testcase 2
Short Description: Test getOrderHistory for admin (can request any ID).
Input: Current user is admin, requests history for "id2".
Expected Output: Calls repository with "id2".
Actual Output: Calls repository with "id2".

### Testcase 3
Short Description: Test getOrderHistory fails when effective userId is null.
Input: Current user is admin, requests history for null userId.
Expected Output: BusinessException (400, "User id is required")
Actual Output: BusinessException (400, "User id is required")

### Testcase 4
Short Description: Test getPendingOrdersForStaff with null userId (finds all pending).
Input: userId = null.
Expected Output: Calls orderRepository.findByStatusIgnoreCase("PENDING", ...).
Actual Output: Calls orderRepository.findByStatusIgnoreCase("PENDING", ...).

### Testcase 5
Short Description: Test getPendingOrdersForStaff with specific userId.
Input: userId = "id1".
Expected Output: Calls orderRepository.findByStatusIgnoreCaseAndUserId("PENDING", "id1", ...).
Actual Output: Calls orderRepository.findByStatusIgnoreCaseAndUserId("PENDING", "id1", ...).

## Code of Test Case
```java
@Test
void getOrderHistory_Customer_EnforcesOwnId() {
    // Arrange
    User customer = mockUser("CUSTOMER", UUID.randomUUID());
    mockSecurity(customer);
    
    UUID otherId = UUID.randomUUID();
    when(orderRepository.findByUserId(eq(customer.getId()), any())).thenReturn(Page.empty());

    // Act
    orderService.getOrderHistory(otherId, 1, 10);

    // Assert
    verify(orderRepository).findByUserId(eq(customer.getId()), any());
}

@Test
void getOrderHistory_Admin_UsesRequestedId() {
    // Arrange
    User admin = mockUser("BUSINESS_ADMIN", UUID.randomUUID());
    mockSecurity(admin);
    
    UUID targetId = UUID.randomUUID();
    when(orderRepository.findByUserId(eq(targetId), any())).thenReturn(Page.empty());

    // Act
    orderService.getOrderHistory(targetId, 1, 10);

    // Assert
    verify(orderRepository).findByUserId(eq(targetId), any());
}

@Test
void getPendingOrdersForStaff_NullUser_FindsAll() {
    // Arrange
    User staff = mockUser("STAFF", UUID.randomUUID());
    mockSecurity(staff);
    when(orderRepository.findByStatusIgnoreCase(eq("PENDING"), any())).thenReturn(Page.empty());

    // Act
    orderService.getPendingOrdersForStaff(null, 1, 10);

    // Assert
    verify(orderRepository).findByStatusIgnoreCase(eq("PENDING"), any());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
