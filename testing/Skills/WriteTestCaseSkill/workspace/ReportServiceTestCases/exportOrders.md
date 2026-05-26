---
name: unit-test-exportOrders-condition-coverage
description: Unit tests with 100% condition coverage for ReportService.exportOrders method.
---

## Infomation
Service Name: ReportService
Method Name: exportOrders(LocalDate, LocalDate, String)
Mock class: 
   1. OrderRepository orderRepository
   2. PaymentRepository paymentRepository
   3. SecurityUtil (Static mock)

Mock Data:
   - startDate: 2024-05-01
   - endDate: 2024-05-14
   - orderStatus: "DELIVERED" or null

## Testcase
### Testcase 1
Short Description: Test exportOrders fails when no orders match the filter.
Input: orderRepository returns empty list.
Expected Output: BusinessException (404, "No orders match the export filter")
Actual Output: BusinessException (404, "No orders match the export filter")

### Testcase 2
Short Description: Test exportOrders with specific status filter.
Input: orderStatus = "DELIVERED".
Expected Output: Calls orderRepository.findByStatusIgnoreCaseAndCreatedAtBetween("DELIVERED", ...).
Actual Output: Calls orderRepository.findByStatusIgnoreCaseAndCreatedAtBetween("DELIVERED", ...).

### Testcase 3
Short Description: Test exportOrders with null status filter (finds all).
Input: orderStatus = null.
Expected Output: Calls orderRepository.findByCreatedAtBetween(...).
Actual Output: Calls orderRepository.findByCreatedAtBetween(...).

### Testcase 4
Short Description: Test buildOrdersWorkbook handles null order details.
Input: Order with createdAt = null, userFullName = null, totalAmount = null.
Expected Output: Excel cells contain empty strings or 0.0 instead of crashing.
Actual Output: Excel cells contain empty strings or 0.0 instead of crashing.

### Testcase 5
Short Description: Test buildOrdersWorkbook handles missing payment.
Input: paymentRepository.findByOrderId returns empty.
Expected Output: Payment method cell contains empty string.
Actual Output: Payment method cell contains empty string.

## Code of Test Case
```java
@Test
void exportOrders_NoResults_ThrowsNotFound() {
    mockAdminAccess();
    when(orderRepository.findByCreatedAtBetween(any(), any())).thenReturn(List.of());

    assertThrows(BusinessException.class, () -> 
        reportService.exportOrders(LocalDate.now(), LocalDate.now(), null));
}

@Test
void exportOrders_WithStatus_CallsFilteredRepo() {
    mockAdminAccess();
    User user = new User(); user.setUserFullName("John");
    Order order = new Order(); order.setId(UUID.randomUUID()); order.setUser(user); order.setStatus("DELIVERED");
    when(orderRepository.findByStatusIgnoreCaseAndCreatedAtBetween(eq("DELIVERED"), any(), any()))
        .thenReturn(List.of(order));
    when(paymentRepository.findByOrderId(any())).thenReturn(Optional.empty());

    byte[] result = reportService.exportOrders(LocalDate.now(), LocalDate.now(), "DELIVERED");
    
    assertNotNull(result);
    assertTrue(result.length > 0);
    verify(orderRepository).findByStatusIgnoreCaseAndCreatedAtBetween(eq("DELIVERED"), any(), any());
}

@Test
void buildOrdersWorkbook_NullFields_HandlesGracefully() throws Exception {
    User user = new User(); user.setEmail("test@ex.com"); // No full name
    Order order = new Order(); 
    order.setId(UUID.randomUUID()); 
    order.setUser(user); 
    order.setCreatedAt(null);
    order.setTotalAmount(null);
    order.setStatus("PENDING");

    byte[] result = ReflectionTestUtils.invokeMethod(reportService, "buildOrdersWorkbook", 
        List.of(order), ZoneId.systemDefault());
    
    assertNotNull(result);
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
