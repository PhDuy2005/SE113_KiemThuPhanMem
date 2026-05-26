---
name: unit-test-getBestSellingProductsReport-condition-coverage
description: Unit tests with 100% condition coverage for ReportService.getBestSellingProductsReport method.
---

## Infomation
Service Name: ReportService
Method Name: getBestSellingProductsReport(LocalDate startDate, LocalDate endDate, Integer limit)
Mock class: 
   1. OrderItemRepository orderItemRepository
   2. SecurityUtil (Static mock)

Mock Data:
   - startDate: 2024-05-01
   - endDate: 2024-05-14
   - limit: 5

## Testcase
### Testcase 1
Short Description: Test getBestSellingProductsReport fails when startDate or endDate is null.
Input: startDate = null, or endDate = null.
Expected Output: BusinessException (400, "Start date and end date are required")
Actual Output: BusinessException (400, "Start date and end date are required")

### Testcase 2
Short Description: Test getBestSellingProductsReport fails when end date is before start date.
Input: startDate = 2024-05-14, endDate = 2024-05-01.
Expected Output: BusinessException (400, "End date must not be before start date")
Actual Output: BusinessException (400, "End date must not be before start date")

### Testcase 3
Short Description: Test getBestSellingProductsReport applies limit correctly if ranking size exceeds limit.
Input: 10 unique products sold, limit = 5.
Expected Output: rankingList.size() = 5.
Actual Output: rankingList.size() = 5.

### Testcase 4
Short Description: Test getBestSellingProductsReport handles null limit (no sublisting).
Input: limit = null.
Expected Output: Returns all ranked products.
Actual Output: Returns all ranked products.

### Testcase 5
Short Description: Test buildBestSellingRanking handles null quantities and prices for individual items.
Input: OrderItem with quantity = null, price = null.
Expected Output: totalSold = 0, revenue = 0 for that product entry.
Actual Output: totalSold = 0, revenue = 0 for that product entry.

### Testcase 6
Short Description: Test getBestSellingProductsReport returns message when no products were sold.
Input: rankingList is empty.
Expected Output: ResBestSellingProductsReportDTO with message "There is no best-selling product data for this period".
Actual Output: ResBestSellingProductsReportDTO with message "There is no best-selling product data for this period".

## Code of Test Case
```java
@Test
void getBestSellingProductsReport_AppliesLimit() {
    // Arrange
    mockAdminAccess();
    LocalDate start = LocalDate.of(2024, 5, 1);
    LocalDate end = LocalDate.of(2024, 5, 14);
    
    List<OrderItem> items = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        Product p = new Product(); 
        p.setId(UUID.randomUUID()); 
        p.setName("Product " + i);
        OrderItem oi = new OrderItem(); 
        oi.setProduct(p); 
        oi.setQuantity(1); 
        oi.setPrice(new BigDecimal("10.00"));
        items.add(oi);
    }
    when(orderItemRepository.findByOrderStatusAndOrderCreatedAtBetweenWithProduct(any(), any(), any()))
        .thenReturn(items);

    // Act
    ResBestSellingProductsReportDTO result = reportService.getBestSellingProductsReport(start, end, 5);

    // Assert
    assertEquals(5, result.getRankingList().size());
    assertEquals(1, result.getRankingList().get(0).getRank());
}

@Test
void getBestSellingProductsReport_NullData_ReturnsMessage() {
    // Arrange
    mockAdminAccess();
    when(orderItemRepository.findByOrderStatusAndOrderCreatedAtBetweenWithProduct(any(), any(), any()))
        .thenReturn(List.of());

    // Act
    ResBestSellingProductsReportDTO result = reportService.getBestSellingProductsReport(LocalDate.now(), LocalDate.now(), 10);

    // Assert
    assertEquals("There is no best-selling product data for this period", result.getMessage());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
