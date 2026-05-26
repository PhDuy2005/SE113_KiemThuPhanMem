---
name: unit-test-getAuditLogs-condition-coverage
description: Unit tests with 100% condition coverage for AuditLogService.getAuditLogs method.
---

## Infomation
Service Name: AuditLogService
Method Name: getAuditLogs(UUID userId, String actionType, Instant startTimestamp, Instant endTimestamp, int pageNumber, int pageSize)
Mock class: 
   1. AuditLogRepository auditLogRepository
   2. UserRepository userRepository
   3. SecurityUtil (Static mock)

Mock Data:
   - adminUser: User { id: "550e8400-e29b-41d4-a716-446655440000", email: "admin@example.com", accountStatus: "ACTIVE", role: Role { name: "BUSINESS_ADMIN" } }
   - auditLog: AuditLog { id: 1L, actionType: "LOGIN", createdAt: Instant.parse("2024-05-14T10:00:00Z") }
   - logsPage: Page<AuditLog> containing [auditLog]
   - emptyPage: Page<AuditLog> containing []

## Testcase
### Testcase 1
Short Description: Test getAuditLogs fails when endTimestamp is before startTimestamp.
Input: userId = null, actionType = "LOGIN", startTimestamp = "2024-05-14T12:00:00Z", endTimestamp = "2024-05-14T10:00:00Z", pageNumber = 1, pageSize = 10.
Expected Output: BusinessException (400, "End timestamp must not be before start timestamp")
Actual Output: BusinessException (400, "End timestamp must not be before start timestamp")

### Testcase 2
Short Description: Test getAuditLogs with pageSize <= 0 (defaults to 10) and valid timestamps.
Input: userId = null, actionType = "LOGIN", startTimestamp = "2024-05-14T08:00:00Z", endTimestamp = "2024-05-14T10:00:00Z", pageNumber = 1, pageSize = 0.
Expected Output: ResultPaginationDTO with pageSize 10, meta.totalItems = 1.
Actual Output: ResultPaginationDTO with pageSize 10, meta.totalItems = 1.

### Testcase 3
Short Description: Test getAuditLogs with null startTimestamp (valid).
Input: userId = null, actionType = "LOGIN", startTimestamp = null, endTimestamp = "2024-05-14T10:00:00Z", pageNumber = 1, pageSize = 10.
Expected Output: ResultPaginationDTO with results, message = null.
Actual Output: ResultPaginationDTO with results, message = null.

### Testcase 4
Short Description: Test getAuditLogs with null endTimestamp (valid).
Input: userId = null, actionType = "LOGIN", startTimestamp = "2024-05-14T08:00:00Z", endTimestamp = null, pageNumber = 1, pageSize = 10.
Expected Output: ResultPaginationDTO with results, message = null.
Actual Output: ResultPaginationDTO with results, message = null.

### Testcase 5
Short Description: Test getAuditLogs returns empty results with message.
Input: userId = null, actionType = "NON_EXISTENT", startTimestamp = null, endTimestamp = null, pageNumber = 1, pageSize = 10, auditLogRepository returns emptyPage.
Expected Output: ResultPaginationDTO with results = [], message = "No audit logs found".
Actual Output: ResultPaginationDTO with results = [], message = "No audit logs found".

### Testcase 6
Short Description: Test getAuditLogs with pageNumber = 0 (defaults to page 0 in repository).
Input: userId = null, actionType = "LOGIN", startTimestamp = null, endTimestamp = null, pageNumber = 0, pageSize = 10.
Expected Output: ResultPaginationDTO with meta.page = 1.
Actual Output: ResultPaginationDTO with meta.page = 1.

## Code of Test Case
```java
@Test
void getAuditLogs_EndBeforeStart_ThrowsException() {
    // Arrange
    UUID userId = null;
    String actionType = "LOGIN";
    Instant start = Instant.parse("2024-05-14T12:00:00Z");
    Instant end = Instant.parse("2024-05-14T10:00:00Z");
    
    mockAdminAccess();

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        auditLogService.getAuditLogs(userId, actionType, start, end, 1, 10));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("End timestamp must not be before start timestamp", exception.getMessage());
}

@Test
void getAuditLogs_PageSizeZero_UsesDefault() {
    // Arrange
    mockAdminAccess();
    Page<AuditLog> page = new PageImpl<>(List.of(new AuditLog()));
    when(auditLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

    // Act
    ResultPaginationDTO result = auditLogService.getAuditLogs(null, "LOGIN", null, null, 1, 0);

    // Assert
    assertEquals(10, result.getMeta().getPageSize());
    verify(auditLogRepository).findAll(any(Specification.class), argThat(p -> p.getPageSize() == 10));
}

@Test
void getAuditLogs_EmptyResults_ReturnsMessage() {
    // Arrange
    mockAdminAccess();
    when(auditLogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());

    // Act
    ResultPaginationDTO result = auditLogService.getAuditLogs(null, "LOGIN", null, null, 1, 10);

    // Assert
    assertEquals("No audit logs found", result.getMessage());
    assertTrue(result.getResult().isEmpty());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
