---
name: unit-test-order-search-condition-coverage
description: Unit tests with 100% condition coverage for OrderService.searchOrdersForStaff method.
---

## Infomation
Service Name: OrderService
Method Name: searchOrdersForStaff(String, int, int)
Mock class: 
   1. OrderRepository orderRepository
   2. SecurityUtil (Static mock)

Mock Data:
   - validUuid: "550e8400-e29b-41d4-a716-446655440001"
   - phoneNumber: "0901234567"

## Testcase
### Testcase 1
Short Description: Test searchOrdersForStaff with valid UUID (search by ID).
Input: searchKeyword = "550e8400-e29b-41d4-a716-446655440001".
Expected Output: Calls orderRepository.findById(UUID).
Actual Output: Calls orderRepository.findById(UUID).

### Testcase 2
Short Description: Test searchOrdersForStaff with non-UUID string (search by phone number).
Input: searchKeyword = "0901234567".
Expected Output: Calls orderRepository.findByUserPhoneNumberContaining("0901234567", ...).
Actual Output: Calls orderRepository.findByUserPhoneNumberContaining("0901234567", ...).

### Testcase 3
Short Description: Test searchOrdersForStaff returns "No matching orders found" for empty results.
Input: Search yields no results.
Expected Output: ResultPaginationDTO with message "No matching orders found".
Actual Output: ResultPaginationDTO with message "No matching orders found".

## Code of Test Case
```java
@Test
void searchOrdersForStaff_WithUuid_FindsById() {
    // Arrange
    mockStaffAccess();
    UUID orderId = UUID.randomUUID();
    Order order = new Order();
    order.setId(orderId);
    order.setUser(new User());
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    // Act
    ResultPaginationDTO result = orderService.searchOrdersForStaff(orderId.toString(), 1, 10);

    // Assert
    verify(orderRepository).findById(orderId);
    assertEquals(1, result.getResult().size());
}

@Test
void searchOrdersForStaff_WithPhone_FindsByPhone() {
    // Arrange
    mockStaffAccess();
    String phone = "0901234567";
    when(orderRepository.findByUserPhoneNumberContaining(eq(phone), any())).thenReturn(Page.empty());

    // Act
    ResultPaginationDTO result = orderService.searchOrdersForStaff(phone, 1, 10);

    // Assert
    verify(orderRepository).findByUserPhoneNumberContaining(eq(phone), any());
    assertEquals("No matching orders found", result.getMessage());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
