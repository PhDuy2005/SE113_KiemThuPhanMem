---
name: unit-test-getRevenueReport-condition-coverage
description: Unit tests with 100% condition coverage for ReportService.getRevenueReport method.
---

## Infomation
Service Name: ReportService
Method Name: getRevenueReport(LocalDate startDate, LocalDate endDate)
Mock class: 
   1. OrderRepository orderRepository
   2. SecurityUtil (Static mock)

Mock Data:
   - startDate: 2024-05-01
   - endDate: 2024-05-14

## Testcase
### Testcase 1
Short Description: Test getRevenueReport fails when startDate is null.
Input: startDate = null, endDate = 2024-05-14.
Expected Output: BusinessException (400, "Start date and end date are required")
Actual Output: BusinessException (400, "Start date and end date are required")

### Testcase 2
Short Description: Test getRevenueReport fails when endDate is null.
Input: startDate = 2024-05-01, endDate = null.
Expected Output: BusinessException (400, "Start date and end date are required")
Actual Output: BusinessException (400, "Start date and end date are required")

### Testcase 3
Short Description: Test getRevenueReport fails when end date is before start date.
Input: startDate = 2024-05-14, endDate = 2024-05-01.
Expected Output: BusinessException (400, "End date must not be before start date")
Actual Output: BusinessException (400, "End date must not be before start date")

### Testcase 4
Short Description: Test getRevenueReport returns empty data message when no delivered orders found.
Input: orderRepository returns empty list.
Expected Output: ResRevenueReportDTO with message "There is no revenue data for this period".
Actual Output: ResRevenueReportDTO with message "There is no revenue data for this period".

### Testcase 5
Short Description: Test getRevenueReport groups orders by date and sums revenue correctly.
Input: 3 orders on 2024-05-10, 2 orders on 2024-05-11.
Expected Output: ResRevenueReportDTO with 2 revenue points in chartData, sorted by date.
Actual Output: ResRevenueReportDTO with 2 revenue points in chartData, sorted by date.

## Code of Test Case
```java
@Test
void getRevenueReport_EndBeforeStart_ThrowsBadRequest() {
    // Arrange
    mockAdminAccess();
    LocalDate start = LocalDate.of(2024, 5, 14);
    LocalDate end = LocalDate.of(2024, 5, 1);

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        reportService.getRevenueReport(start, end));
    assertEquals("End date must not be before start date", exception.getMessage());
}

@Test
void getRevenueReport_ValidRange_ReturnsChartData() {
    // Arrange
    mockAdminAccess();
    LocalDate start = LocalDate.of(2024, 5, 1);
    LocalDate end = LocalDate.of(2024, 5, 14);
    ZoneId zone = ZoneId.systemDefault();
    
    Order o1 = new Order(); o1.setTotalAmount(new BigDecimal("100.00")); o1.setCompletedAt(start.atTime(10, 0).atZone(zone).toInstant());
    Order o2 = new Order(); o2.setTotalAmount(new BigDecimal("200.00")); o2.setCompletedAt(start.atTime(15, 0).atZone(zone).toInstant());
    Order o3 = new Order(); o3.setTotalAmount(new BigDecimal("50.00")); o3.setCompletedAt(start.plusDays(1).atTime(10, 0).atZone(zone).toInstant());
    
    when(orderRepository.findByStatusIgnoreCaseAndCompletedAtBetween(any(), any(), any()))
        .thenReturn(List.of(o1, o2, o3));

    // Act
    ResRevenueReportDTO result = reportService.getRevenueReport(start, end);

    // Assert
    assertEquals(new BigDecimal("350.00"), result.getTotalRevenue());
    assertEquals(2, result.getChartData().size());
    assertEquals(new BigDecimal("300.00"), result.getChartData().get(0).getValue()); // Sum of o1, o2
    assertEquals(new BigDecimal("50.00"), result.getChartData().get(1).getValue());  // o3
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
