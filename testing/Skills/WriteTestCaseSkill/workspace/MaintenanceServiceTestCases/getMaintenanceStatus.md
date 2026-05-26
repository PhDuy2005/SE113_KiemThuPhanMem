---
name: unit-test-getMaintenanceStatus-condition-coverage
description: Unit tests with 100% condition coverage for MaintenanceService.getMaintenanceStatus method.
---

## Infomation
Service Name: MaintenanceService
Method Name: getMaintenanceStatus()
Mock class: 
   1. SystemConfigRepository systemConfigRepository
   2. UserRepository userRepository
   3. SecurityUtil (Static mock)

Mock Data:
   - configWithUpdate: SystemConfig { key: "System_Status", value: "ACTIVE", createdAt: T1, updatedAt: T2 }
   - configNoUpdate: SystemConfig { key: "System_Status", value: "MAINTENANCE", createdAt: T1, updatedAt: null }

## Testcase
### Testcase 1
Short Description: Test getMaintenanceStatus returns status and updatedAt when updatedAt is not null.
Input: Config has updatedAt = "2024-05-14T12:00:00Z".
Expected Output: ResMaintenanceDTO with status "ACTIVE", updatedAt = "2024-05-14T12:00:00Z".
Actual Output: ResMaintenanceDTO with status "ACTIVE", updatedAt = "2024-05-14T12:00:00Z".

### Testcase 2
Short Description: Test getMaintenanceStatus returns status and createdAt when updatedAt is null.
Input: Config has updatedAt = null, createdAt = "2024-05-14T10:00:00Z".
Expected Output: ResMaintenanceDTO with status "MAINTENANCE", updatedAt = "2024-05-14T10:00:00Z".
Actual Output: ResMaintenanceDTO with status "MAINTENANCE", updatedAt = "2024-05-14T10:00:00Z".

### Testcase 3
Short Description: Test getMaintenanceStatus creates default config if not exists.
Input: repository.findById returns empty.
Expected Output: New config saved with value "ACTIVE".
Actual Output: New config saved with value "ACTIVE".

## Code of Test Case
```java
@Test
void getMaintenanceStatus_WithUpdate_ReturnsUpdatedAt() {
    // Arrange
    mockAdminAccess();
    Instant now = Instant.now();
    SystemConfig config = SystemConfig.builder()
            .key("System_Status")
            .value("ACTIVE")
            .updatedAt(now)
            .build();
    when(systemConfigRepository.findById("System_Status")).thenReturn(Optional.of(config));

    // Act
    ResMaintenanceDTO result = maintenanceService.getMaintenanceStatus();

    // Assert
    assertEquals("ACTIVE", result.getStatus());
    assertEquals(now, result.getUpdatedAt());
}

@Test
void getMaintenanceStatus_NoUpdate_ReturnsCreatedAt() {
    // Arrange
    mockAdminAccess();
    Instant created = Instant.now().minusSeconds(100);
    SystemConfig config = SystemConfig.builder()
            .key("System_Status")
            .value("MAINTENANCE")
            .createdAt(created)
            .updatedAt(null)
            .build();
    when(systemConfigRepository.findById("System_Status")).thenReturn(Optional.of(config));

    // Act
    ResMaintenanceDTO result = maintenanceService.getMaintenanceStatus();

    // Assert
    assertEquals("MAINTENANCE", result.getStatus());
    assertEquals(created, result.getUpdatedAt());
}

@Test
void getMaintenanceStatus_NewConfig_SavesDefault() {
    // Arrange
    mockAdminAccess();
    when(systemConfigRepository.findById("System_Status")).thenReturn(Optional.empty());
    when(systemConfigRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    // Act
    ResMaintenanceDTO result = maintenanceService.getMaintenanceStatus();

    // Assert
    assertEquals("ACTIVE", result.getStatus());
    verify(systemConfigRepository).save(argThat(c -> "ACTIVE".equals(c.getValue())));
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
