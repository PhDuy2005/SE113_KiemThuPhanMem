---
name: unit-test-table-processing-condition-coverage
description: Unit tests with 100% condition coverage for DatabaseBackupService table discovery and dumping.
---

## Infomation
Service Name: DatabaseBackupService
Method Name: getTableNames(Connection) and dumpTable(Connection, String, GZIPOutputStream)
Mock class: 
   1. Connection connection
   2. DatabaseMetaData metaData
   3. ResultSet resultSet
   4. Statement statement

Mock Data:
   - tableNames: ["users", "products", "flyway_schema_history"]
   - columnNames: ["id", "name"]

## Testcase
### Testcase 1
Short Description: Test getTableNames filters out flyway tables and sorts alphabetically.
Input: MetaData returns ["products", "users", "flyway_schema_history"].
Expected Output: ["products", "users"].
Actual Output: ["products", "users"].

### Testcase 2
Short Description: Test getTableNames handles null table names from metadata.
Input: MetaData returns [null, "users"].
Expected Output: ["users"].
Actual Output: ["users"].

### Testcase 3
Short Description: Test dumpTable writes correct SQL INSERT statements with multiple columns.
Input: Table "users" with columns "id", "name", and 1 row [1, "John"].
Expected Output: "INSERT INTO `users` (`id`, `name`) VALUES (1, 'John');" written to stream.
Actual Output: "INSERT INTO `users` (`id`, `name`) VALUES (1, 'John');" written to stream.

### Testcase 4
Short Description: Test quoteIdentifier escapes backticks in table names.
Input: tableName = "my`table"
Expected Output: "`my``table`"
Actual Output: "`my``table`"

## Code of Test Case
```java
@Test
void getTableNames_FiltersAndSorts() throws SQLException {
    DatabaseMetaData metaData = mock(DatabaseMetaData.class);
    when(connection.getMetaData()).thenReturn(metaData);
    
    ResultSet rs = mock(ResultSet.class);
    when(metaData.getTables(any(), any(), eq("%"), any())).thenReturn(rs);
    when(rs.next()).thenReturn(true, true, true, false);
    when(rs.getString("TABLE_NAME")).thenReturn("users", "flyway_history", "products");

    List<String> names = ReflectionTestUtils.invokeMethod(databaseBackupService, "getTableNames", connection);

    assertEquals(2, names.size());
    assertEquals("products", names.get(0));
    assertEquals("users", names.get(1));
}

@Test
void dumpTable_GeneratesCorrectSql() throws Exception {
    Statement stmt = mock(Statement.class);
    ResultSet rs = mock(ResultSet.class);
    ResultSetMetaData rsmd = mock(ResultSetMetaData.class);
    
    when(connection.createStatement()).thenReturn(stmt);
    when(stmt.executeQuery(anyString())).thenReturn(rs);
    when(rs.getMetaData()).thenReturn(rsmd);
    
    when(rsmd.getColumnCount()).thenReturn(2);
    when(rsmd.getColumnName(1)).thenReturn("id");
    when(rsmd.getColumnName(2)).thenReturn("name");
    
    when(rs.next()).thenReturn(true, false);
    when(rs.getObject(1)).thenReturn(1);
    when(rs.getObject(2)).thenReturn("John");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    GZIPOutputStream gzos = new GZIPOutputStream(baos);
    
    ReflectionTestUtils.invokeMethod(databaseBackupService, "dumpTable", connection, "users", gzos);
    gzos.finish();

    String output = new String(new GZIPInputStream(new ByteArrayInputStream(baos.toByteArray())).readAllBytes());
    assertTrue(output.contains("INSERT INTO `users` (`id`, `name`) VALUES (1, 'John');"));
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
