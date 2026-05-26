---
name: unit-test-blockFraudCustomer-condition-coverage
description: Unit tests with 100% condition coverage for CustomerManagementService.blockFraudCustomer method.
---

## Infomation
Service Name: CustomerManagementService
Method Name: blockFraudCustomer(UUID customerId)
Mock class: 
   1. UserRepository userRepository
   2. OrderRepository orderRepository
   3. OrderItemRepository orderItemRepository
   4. InventoryRepository inventoryRepository
   5. SecurityUtil (Static mock)

Mock Data:
   - customerId: "550e8400-e29b-41d4-a716-446655441111"
   - customer: User { id: customerId, role: { name: "CUSTOMER" } }
   - adminUser: User { role: { name: "BUSINESS_ADMIN" } }
   - order: Order { id: "550e8400-e29b-41d4-a716-446655442222", status: "PENDING" }

## Testcase
### Testcase 1
Short Description: Test blockFraudCustomer fails when customer account not found.
Input: customerId, userRepository.findById(customerId) returns Optional.empty()
Expected Output: BusinessException (404, "Customer account not found")
Actual Output: BusinessException (404, "Customer account not found")

### Testcase 2
Short Description: Test blockFraudCustomer fails when user is not a customer.
Input: User with role "BUSINESS_ADMIN".
Expected Output: BusinessException (400, "Only customer accounts can be blocked by this action")
Actual Output: BusinessException (400, "Only customer accounts can be blocked by this action")

### Testcase 3
Short Description: Test blockFraudCustomer succeeds and cancels pending orders.
Input: Valid customer, has 1 pending order.
Expected Output: ResUserDTO with message "Customer account blocked and pending orders cancelled successfully", order status updated to "CANCELLED".
Actual Output: ResUserDTO with message "Customer account blocked and pending orders cancelled successfully", order status updated to "CANCELLED".

### Testcase 4
Short Description: Test restoreInventory creates new inventory entry if not exists.
Input: OrderItem for product with no inventory record.
Expected Output: Inventory saved with quantity = item.getQuantity() and reservedQuantity = 0.
Actual Output: Inventory saved with quantity = item.getQuantity() and reservedQuantity = 0.

### Testcase 5
Short Description: Test restoreInventory handles null quantity and reservedQuantity in existing record.
Input: Inventory record with quantity = null, reservedQuantity = null.
Expected Output: Inventory saved with quantity = 0 + item.getQuantity(), reservedQuantity = 0.
Actual Output: Inventory saved with quantity = 0 + item.getQuantity(), reservedQuantity = 0.

## Code of Test Case
```java
@Test
void blockFraudCustomer_NotFound_ThrowsNotFound() {
    // Arrange
    mockAdminAccess();
    UUID id = UUID.randomUUID();
    when(userRepository.findById(id)).thenReturn(Optional.empty());

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        customerManagementService.blockFraudCustomer(id));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
}

@Test
void blockFraudCustomer_NotCustomer_ThrowsBadRequest() {
    // Arrange
    mockAdminAccess();
    UUID id = UUID.randomUUID();
    User admin = new User();
    Role role = new Role();
    role.setName("BUSINESS_ADMIN");
    admin.setRole(role);
    when(userRepository.findById(id)).thenReturn(Optional.of(admin));

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        customerManagementService.blockFraudCustomer(id));
    assertEquals("Only customer accounts can be blocked by this action", exception.getMessage());
}

@Test
void blockFraudCustomer_Valid_BlocksAndCancelsOrders() {
    // Arrange
    mockAdminAccess();
    UUID id = UUID.randomUUID();
    User customer = new User();
    Role role = new Role();
    role.setName("CUSTOMER");
    customer.setRole(role);
    when(userRepository.findById(id)).thenReturn(Optional.of(customer));
    when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    Order order = new Order();
    order.setId(UUID.randomUUID());
    order.setStatus("PENDING");
    when(orderRepository.findByUserIdAndStatusIgnoreCase(id, "PENDING")).thenReturn(List.of(order));
    
    // Mock restoreInventory dependencies
    OrderItem item = new OrderItem();
    item.setQuantity(5);
    Product p = new Product(); p.setId(UUID.randomUUID());
    item.setProduct(p);
    when(orderItemRepository.findByOrderIdWithProduct(order.getId())).thenReturn(List.of(item));
    when(inventoryRepository.findById(p.getId())).thenReturn(Optional.empty());

    // Act
    ResUserDTO result = customerManagementService.blockFraudCustomer(id);

    // Assert
    assertEquals("LOCKED", customer.getAccountStatus());
    assertEquals("CANCELLED", order.getStatus());
    verify(inventoryRepository).save(argThat(inv -> inv.getQuantity() == 5));
    assertEquals("Customer account blocked and pending orders cancelled successfully", result.getMessage());
}

@Test
void restoreInventory_NullFields_HandlesGracefully() {
    // Arrange
    UUID orderId = UUID.randomUUID();
    OrderItem item = new OrderItem();
    item.setQuantity(5);
    Product p = new Product(); p.setId(UUID.randomUUID());
    item.setProduct(p);
    when(orderItemRepository.findByOrderIdWithProduct(orderId)).thenReturn(List.of(item));

    Inventory inv = new Inventory();
    inv.setQuantity(null);
    inv.setReservedQuantity(null);
    when(inventoryRepository.findById(p.getId())).thenReturn(Optional.of(inv));

    // Act
    ReflectionTestUtils.invokeMethod(customerManagementService, "restoreInventory", orderId);

    // Assert
    assertEquals(5, inv.getQuantity());
    assertEquals(0, inv.getReservedQuantity());
    verify(inventoryRepository).save(inv);
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
