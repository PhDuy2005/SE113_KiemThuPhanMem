---
name: unit-test-buildSpecification-condition-coverage
description: Unit tests with 100% condition coverage for AuditLogService.buildSpecification method.
---

## Infomation
Service Name: AuditLogService
Method Name: buildSpecification(UUID userId, String actionType, Instant startTimestamp, Instant endTimestamp)
Mock class: 
   1. Root root
   2. CriteriaQuery query
   3. CriteriaBuilder criteriaBuilder
   4. Predicate predicate

Mock Data:
   - userId: "550e8400-e29b-41d4-a716-446655440002"
   - actionType: "LOGIN"
   - startTimestamp: "2024-05-14T08:00:00Z"
   - endTimestamp: "2024-05-14T10:00:00Z"

## Testcase
### Testcase 1
Short Description: Test buildSpecification with all parameters null.
Input: userId = null, actionType = null, startTimestamp = null, endTimestamp = null.
Expected Output: Specification that returns a conjunction (no filters).
Actual Output: Specification that returns a conjunction (no filters).

### Testcase 2
Short Description: Test buildSpecification with userId provided.
Input: userId = "550e8400-e29b-41d4-a716-446655440002", others null.
Expected Output: Specification includes equality check for actor.id.
Actual Output: Specification includes equality check for actor.id.

### Testcase 3
Short Description: Test buildSpecification with actionType provided.
Input: actionType = "login", others null.
Expected Output: Specification includes equality check for actionType (normalized to "LOGIN").
Actual Output: Specification includes equality check for actionType (normalized to "LOGIN").

### Testcase 4
Short Description: Test buildSpecification with actionType as blank string.
Input: actionType = "   ", others null.
Expected Output: Specification does NOT include actionType filter.
Actual Output: Specification does NOT include actionType filter.

### Testcase 5
Short Description: Test buildSpecification with startTimestamp provided.
Input: startTimestamp = "2024-05-14T08:00:00Z", others null.
Expected Output: Specification includes greaterThanOrEqualTo check for createdAt.
Actual Output: Specification includes greaterThanOrEqualTo check for createdAt.

### Testcase 6
Short Description: Test buildSpecification with endTimestamp provided.
Input: endTimestamp = "2024-05-14T10:00:00Z", others null.
Expected Output: Specification includes lessThanOrEqualTo check for createdAt.
Actual Output: Specification includes lessThanOrEqualTo check for createdAt.

## Code of Test Case
```java
@Test
void buildSpecification_AllNull_ReturnsConjunction() {
    // Arrange
    Specification<AuditLog> spec = ReflectionTestUtils.invokeMethod(auditLogService, "buildSpecification", null, null, null, null);
    
    Root<AuditLog> root = mock(Root.class);
    CriteriaQuery<?> query = mock(CriteriaQuery.class);
    CriteriaBuilder cb = mock(CriteriaBuilder.class);
    Predicate conj = mock(Predicate.class);
    when(cb.conjunction()).thenReturn(conj);

    // Act
    Predicate result = spec.toPredicate(root, query, cb);

    // Assert
    assertEquals(conj, result);
    verify(cb, never()).equal(any(), any());
}

@Test
void buildSpecification_WithUserId_AddsFilter() {
    // Arrange
    UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    Specification<AuditLog> spec = ReflectionTestUtils.invokeMethod(auditLogService, "buildSpecification", userId, null, null, null);
    
    Root<AuditLog> root = mock(Root.class);
    Path path = mock(Path.class);
    when(root.get("actor")).thenReturn(path);
    when(path.get("id")).thenReturn(path);
    
    CriteriaBuilder cb = mock(CriteriaBuilder.class);
    when(cb.conjunction()).thenReturn(mock(Predicate.class));

    // Act
    spec.toPredicate(root, mock(CriteriaQuery.class), cb);

    // Assert
    verify(cb).equal(path, userId);
}

@Test
void buildSpecification_BlankActionType_SkipsFilter() {
    // Arrange
    Specification<AuditLog> spec = ReflectionTestUtils.invokeMethod(auditLogService, "buildSpecification", null, "   ", null, null);
    CriteriaBuilder cb = mock(CriteriaBuilder.class);
    when(cb.conjunction()).thenReturn(mock(Predicate.class));

    // Act
    spec.toPredicate(mock(Root.class), mock(CriteriaQuery.class), cb);

    // Assert
    verify(cb, never()).equal(any(), eq("   "));
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
