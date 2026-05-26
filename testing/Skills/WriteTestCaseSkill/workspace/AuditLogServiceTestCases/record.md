---
name: unit-test-record-condition-coverage
description: Unit tests with 100% condition coverage for AuditLogService.record method.
---

## Infomation
Service Name: AuditLogService
Method Name: record(String actionType, String targetType, String targetId, String oldValue, String newValue, String detail)
Mock class: 
   1. AuditLogRepository auditLogRepository
   2. UserRepository userRepository
   3. SecurityUtil (Static mock)

Mock Data:
   - user: User { id: "550e8400-e29b-41d4-a716-446655440001", email: "user@example.com" }

## Testcase
### Testcase 1
Short Description: Test record with authenticated user found in database.
Input: actionType = "UPDATE", targetType = "PRODUCT", targetId = "101", oldValue = "Old", newValue = "New", detail = "Changed price", SecurityUtil returns "user@example.com", userRepository returns Optional.of(user).
Expected Output: auditLogRepository.save called with actor = user, actorEmail = "user@example.com", actionType = "UPDATE".
Actual Output: auditLogRepository.save called with actor = user, actorEmail = "user@example.com", actionType = "UPDATE".

### Testcase 2
Short Description: Test record with no authenticated user (system actor).
Input: actionType = "CLEANUP", targetType = null, targetId = null, oldValue = null, newValue = null, detail = "Daily cleanup", SecurityUtil returns Optional.empty().
Expected Output: auditLogRepository.save called with actor = null, actorEmail = "system", actionType = "CLEANUP".
Actual Output: auditLogRepository.save called with actor = null, actorEmail = "system", actionType = "CLEANUP".

### Testcase 3
Short Description: Test record fails with null actionType.
Input: actionType = null, other fields valid.
Expected Output: BusinessException (400, "Action type is required")
Actual Output: BusinessException (400, "Action type is required")

### Testcase 4
Short Description: Test record fails with blank actionType.
Input: actionType = "   ", other fields valid.
Expected Output: BusinessException (400, "Action type is required")
Actual Output: BusinessException (400, "Action type is required")

### Testcase 5
Short Description: Test record handles blank fields as null via cleanNullable.
Input: actionType = "DELETE", targetType = "  ", targetId = "  ", oldValue = "  ", newValue = "  ", detail = "  ".
Expected Output: auditLogRepository.save called with targetType = null, targetId = null, oldValue = null, newValue = null, detail = null.
Actual Output: auditLogRepository.save called with targetType = null, targetId = null, oldValue = null, newValue = null, detail = null.

### Testcase 6
Short Description: Test record with actor email not found in database.
Input: actionType = "LOGIN", SecurityUtil returns "unknown@example.com", userRepository returns Optional.empty().
Expected Output: auditLogRepository.save called with actor = null, actorEmail = "unknown@example.com".
Actual Output: auditLogRepository.save called with actor = null, actorEmail = "unknown@example.com".

## Code of Test Case
```java
@Test
void record_AuthenticatedUser_SavesWithActor() {
    // Arrange
    String email = "user@example.com";
    User user = new User();
    user.setEmail(email);
    
    try (MockedStatic<SecurityUtil> securityMock = mockStatic(SecurityUtil.class)) {
        securityMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of(email));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        auditLogService.record("UPDATE", "PRODUCT", "101", "Old", "New", "Detail");

        // Assert
        verify(auditLogRepository).save(argThat(log -> 
            log.getActor().equals(user) && 
            log.getActorEmail().equals(email) &&
            log.getActionType().equals("UPDATE")
        ));
    }
}

@Test
void record_NoUser_SavesAsSystem() {
    // Arrange
    try (MockedStatic<SecurityUtil> securityMock = mockStatic(SecurityUtil.class)) {
        securityMock.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.empty());
        when(userRepository.findByEmail("system")).thenReturn(Optional.empty());

        // Act
        auditLogService.record("CLEANUP", null, null, null, null, "Daily cleanup");

        // Assert
        verify(auditLogRepository).save(argThat(log -> 
            log.getActor() == null && 
            log.getActorEmail().equals("system")
        ));
    }
}

@Test
void record_BlankActionType_ThrowsException() {
    // Act & Assert
    assertThrows(BusinessException.class, () -> 
        auditLogService.record("   ", null, null, null, null, null));
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
