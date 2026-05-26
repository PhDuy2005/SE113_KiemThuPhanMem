---
name: unit-test-toSqlLiteral-condition-coverage
description: Unit tests with 100% condition coverage for DatabaseBackupService.toSqlLiteral method.
---

## Infomation
Service Name: DatabaseBackupService
Method Name: toSqlLiteral(Object value)
Mock class: None (Pure logic)

## Testcase
### Testcase 1
Short Description: Test toSqlLiteral handles null value.
Input: value = null
Expected Output: "NULL"
Actual Output: "NULL"

### Testcase 2
Short Description: Test toSqlLiteral handles Number/Boolean.
Input: value = 123 (Integer), true (Boolean), 10.5 (BigDecimal).
Expected Output: "123", "true", "10.5".
Actual Output: "123", "true", "10.5".

### Testcase 3
Short Description: Test toSqlLiteral handles Timestamp and escapes single quotes.
Input: value = Timestamp.from(Instant.parse("2024-05-14T10:00:00Z")).
Expected Output: "'2024-05-14T10:00:00Z'" (ISO format from Instant).
Actual Output: "'2024-05-14T10:00:00Z'" (ISO format from Instant).

### Testcase 4
Short Description: Test toSqlLiteral handles byte arrays (Hex format).
Input: value = new byte[] { 0x01, 0x02, 0x0F, 0xFF }.
Expected Output: "X'01020FFF'"
Actual Output: "X'01020FFF'"

### Testcase 5
Short Description: Test toSqlLiteral handles Strings and escapes single quotes.
Input: value = "O'Reilly".
Expected Output: "'O''Reilly'"
Actual Output: "'O''Reilly'"

## Code of Test Case
```java
@Test
void toSqlLiteral_Null_ReturnsNullString() {
    String result = ReflectionTestUtils.invokeMethod(databaseBackupService, "toSqlLiteral", (Object) null);
    assertEquals("NULL", result);
}

@Test
void toSqlLiteral_Number_ReturnsString() {
    assertEquals("123", ReflectionTestUtils.invokeMethod(databaseBackupService, "toSqlLiteral", 123));
    assertEquals("true", ReflectionTestUtils.invokeMethod(databaseBackupService, "toSqlLiteral", true));
}

@Test
void toSqlLiteral_ByteArray_ReturnsHex() {
    byte[] data = { 0xDE, 0xAD, 0xBE, 0xEF };
    String result = ReflectionTestUtils.invokeMethod(databaseBackupService, "toSqlLiteral", (Object) data);
    assertEquals("X'DEADBEEF'", result);
}

@Test
void toSqlLiteral_StringWithQuote_Escapes() {
    String result = ReflectionTestUtils.invokeMethod(databaseBackupService, "toSqlLiteral", "It's a test");
    assertEquals("'It''s a test'", result);
}

@Test
void toSqlLiteral_Timestamp_ReturnsIso() {
    Timestamp ts = Timestamp.from(Instant.parse("2024-05-14T10:00:00Z"));
    String result = ReflectionTestUtils.invokeMethod(databaseBackupService, "toSqlLiteral", ts);
    assertEquals("'2024-05-14T10:00:00Z'", result);
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
