---
name: unit-test-integrity-check-condition-coverage
description: Unit tests with 100% condition coverage for DatabaseBackupService integrity check methods.
---

## Infomation
Service Name: DatabaseBackupService
Method Name: calculateSha256(Path) and verifyBackup(Path, long, String)
Mock class: 
   1. Files (Static mock)
   2. GZIPInputStream, BufferedReader (Mocks or real with test data)

Mock Data:
   - testFile: Path.of("test.sql.gz")
   - validContent: "-- TechSales database backup\n..."
   - invalidContent: "Random data"

## Testcase
### Testcase 1
Short Description: Test verifyBackup returns false if size is <= 0.
Input: size = 0, checksum = "abc".
Expected Output: false
Actual Output: false

### Testcase 2
Short Description: Test verifyBackup returns false if checksum is null or blank.
Input: size = 100, checksum = "".
Expected Output: false
Actual Output: false

### Testcase 3
Short Description: Test verifyBackup returns true if first line contains header.
Input: File with first line "-- TechSales database backup", size = 100, checksum = "valid".
Expected Output: true
Actual Output: true

### Testcase 4
Short Description: Test verifyBackup returns false if header does not match.
Input: File with first line "Wrong header", size = 100, checksum = "valid".
Expected Output: false
Actual Output: false

### Testcase 5
Short Description: Test verifyBackup returns false on IOException.
Input: Files.newInputStream(path) throws IOException.
Expected Output: false
Actual Output: false

## Code of Test Case
```java
@Test
void verifyBackup_SizeZero_ReturnsFalse() {
    boolean result = ReflectionTestUtils.invokeMethod(databaseBackupService, "verifyBackup", 
        Path.of("test"), 0L, "checksum");
    assertFalse(result);
}

@Test
void verifyBackup_ChecksumBlank_ReturnsFalse() {
    boolean result = ReflectionTestUtils.invokeMethod(databaseBackupService, "verifyBackup", 
        Path.of("test"), 100L, "  ");
    assertFalse(result);
}

@Test
void verifyBackup_ValidHeader_ReturnsTrue() throws Exception {
    Path tempFile = Files.createTempFile("backup", ".gz");
    try (GZIPOutputStream gzos = new GZIPOutputStream(Files.newOutputStream(tempFile))) {
        gzos.write("-- TechSales database backup\n".getBytes(StandardCharsets.UTF_8));
    }
    
    boolean result = ReflectionTestUtils.invokeMethod(databaseBackupService, "verifyBackup", 
        tempFile, 100L, "checksum");
    
    assertTrue(result);
    Files.deleteIfExists(tempFile);
}

@Test
void verifyBackup_InvalidHeader_ReturnsFalse() throws Exception {
    Path tempFile = Files.createTempFile("backup", ".gz");
    try (GZIPOutputStream gzos = new GZIPOutputStream(Files.newOutputStream(tempFile))) {
        gzos.write("Invalid header\n".getBytes(StandardCharsets.UTF_8));
    }
    
    boolean result = ReflectionTestUtils.invokeMethod(databaseBackupService, "verifyBackup", 
        tempFile, 100L, "checksum");
    
    assertFalse(result);
    Files.deleteIfExists(tempFile);
}

@Test
void calculateSha256_ValidFile_ReturnsHex() throws Exception {
    Path tempFile = Files.createTempFile("test", ".txt");
    Files.writeString(tempFile, "hello");
    
    String result = ReflectionTestUtils.invokeMethod(databaseBackupService, "calculateSha256", tempFile);
    
    // SHA-256 of "hello"
    assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", result);
    Files.deleteIfExists(tempFile);
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
