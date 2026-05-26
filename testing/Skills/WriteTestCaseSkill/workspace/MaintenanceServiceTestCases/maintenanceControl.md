---
name: unit-test-maintenance-control-condition-coverage
description: Unit tests with 100% condition coverage for MaintenanceService maintenance control methods.
---

## Infomation
Service Name: MaintenanceService
Method Name: enableMaintenance(ReqEnableMaintenanceDTO request) and isMaintenanceMode()
Mock class: 
   1. SystemConfigRepository systemConfigRepository
   2. AuditLogService auditLogService
   3. DatabaseBackupService databaseBackupService
   4. UserRepository userRepository
   5. SecurityUtil (Static mock)

Mock Data:
   - backupResult: BackupResult { fileName: "backup.sql.gz", checksumSha256: "sha256", sizeBytes: 1024L }
   - config: SystemConfig { key: "System_Status", value: "ACTIVE" }

## Testcase
### Testcase 1
Short Description: Test enableMaintenance fails when request is null.
Input: request = null
Expected Output: BusinessException (400, "Maintenance confirmation is required")
Actual Output: BusinessException (400, "Maintenance confirmation is required")

### Testcase 2
Short Description: Test enableMaintenance fails when not confirmed.
Input: request = { confirmed: false }
Expected Output: BusinessException (400, "Maintenance confirmation is required")
Actual Output: BusinessException (400, "Maintenance confirmation is required")

### Testcase 3
Short Description: Test enableMaintenance succeeds and records audit log.
Input: Valid confirmed request, backup succeeds.
Expected Output: ResMaintenanceDTO with success message, status "MAINTENANCE", and backup details.
Actual Output: ResMaintenanceDTO with success message, status "MAINTENANCE", and backup details.

### Testcase 4
Short Description: Test enableMaintenance rolls back status on backup failure.
Input: backupToLocalStorage throws RuntimeException.
Expected Output: BusinessException (500, "Emergency rollback: backup failed, maintenance mode was not enabled"), status rolled back to old value.
Actual Output: BusinessException (500, "Emergency rollback: backup failed, maintenance mode was not enabled"), status rolled back to old value.

### Testcase 5
Short Description: Test isMaintenanceMode returns true when status is "MAINTENANCE".
Input: systemConfigRepository returns config with value "MAINTENANCE".
Expected Output: true
Actual Output: true

### Testcase 6
Short Description: Test isMaintenanceMode returns false when status is "ACTIVE".
Input: systemConfigRepository returns config with value "ACTIVE".
Expected Output: false
Actual Output: false

## Code of Test Case
```java
@Test
void enableMaintenance_NotConfirmed_ThrowsBadRequest() {
    // Arrange
    mockAdminAccess();
    ReqEnableMaintenanceDTO req = new ReqEnableMaintenanceDTO();
    req.setConfirmed(false);

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        maintenanceService.enableMaintenance(req));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
}

@Test
void enableMaintenance_BackupFails_RollsBack() {
    // Arrange
    mockAdminAccess();
    ReqEnableMaintenanceDTO req = new ReqEnableMaintenanceDTO();
    req.setConfirmed(true);

    SystemConfig config = new SystemConfig();
    config.setValue("ACTIVE");
    when(systemConfigRepository.findById("System_Status")).thenReturn(Optional.of(config));
    when(databaseBackupService.backupToLocalStorage()).thenThrow(new RuntimeException("IO Error"));

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        maintenanceService.enableMaintenance(req));
    
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    verify(systemConfigRepository, atLeastOnce()).save(argThat(c -> "ACTIVE".equals(c.getValue())));
    verify(auditLogService).record(eq("UPDATE"), eq("SYSTEM_STATUS"), any(), any(), any(), contains("Emergency rollback"));
}

@Test
void enableMaintenance_Valid_Succeeds() {
    // Arrange
    mockAdminAccess();
    ReqEnableMaintenanceDTO req = new ReqEnableMaintenanceDTO();
    req.setConfirmed(true);

    SystemConfig config = new SystemConfig();
    config.setValue("ACTIVE");
    when(systemConfigRepository.findById("System_Status")).thenReturn(Optional.of(config));
    
    BackupResult backup = new BackupResult("file.gz", "path", 1000L, "hash");
    when(databaseBackupService.backupToLocalStorage()).thenReturn(backup);
    when(systemConfigRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    // Act
    ResMaintenanceDTO result = maintenanceService.enableMaintenance(req);

    // Assert
    assertEquals("MAINTENANCE", result.getStatus());
    assertEquals("file.gz", result.getBackupFileName());
    verify(auditLogService).record(eq("UPDATE"), eq("SYSTEM_STATUS"), any(), eq("ACTIVE"), eq("MAINTENANCE"), any());
}

@Test
void isMaintenanceMode_ReturnsCorrectValue() {
    SystemConfig config = new SystemConfig();
    config.setValue("MAINTENANCE");
    when(systemConfigRepository.findById("System_Status")).thenReturn(Optional.of(config));
    assertTrue(maintenanceService.isMaintenanceMode());

    config.setValue("ACTIVE");
    assertFalse(maintenanceService.isMaintenanceMode());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
