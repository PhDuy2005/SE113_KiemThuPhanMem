---
name: unit-test-getReplacementCategories-condition-coverage
description: Unit tests with 100% condition coverage for CategoryService.getReplacementCategories method.
---

## Infomation
Service Name: CategoryService
Method Name: getReplacementCategories(UUID targetId)
Mock class: 
   1. CategoryRepository categoryRepository
   2. ProductRepository productRepository
   3. UserRepository userRepository
   4. SecurityUtil (Static mock)

Mock Data:
   - targetId: "550e8400-e29b-41d4-a716-446655441111"
   - otherCategory: Category { id: "550e8400-e29b-41d4-a716-446655442222", name: "Other" }

## Testcase
### Testcase 1
Short Description: Test getReplacementCategories fails when target category does not exist.
Input: targetId = "550e8400-e29b-41d4-a716-446655441111", categoryRepository.findById(targetId) returns Optional.empty()
Expected Output: BusinessException (404, "Category not found")
Actual Output: BusinessException (404, "Category not found")

### Testcase 2
Short Description: Test getReplacementCategories returns other categories excluding the target.
Input: targetId = "550e8400-e29b-41d4-a716-446655441111", target category exists, categoryRepository.findByIdNot(targetId) returns [otherCategory]
Expected Output: List containing otherCategory DTO.
Actual Output: List containing otherCategory DTO.

## Code of Test Case
```java
@Test
void getReplacementCategories_TargetNotFound_ThrowsNotFound() {
    // Arrange
    mockAdminAccess();
    UUID targetId = UUID.fromString("550e8400-e29b-41d4-a716-446655441111");
    when(categoryRepository.findById(targetId)).thenReturn(Optional.empty());

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        categoryService.getReplacementCategories(targetId));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Category not found", exception.getMessage());
}

@Test
void getReplacementCategories_Valid_ReturnsOthers() {
    // Arrange
    mockAdminAccess();
    UUID targetId = UUID.fromString("550e8400-e29b-41d4-a716-446655441111");
    Category target = Category.builder().id(targetId).name("Target").build();
    Category other = Category.builder().id(UUID.randomUUID()).name("Other").build();
    
    when(categoryRepository.findById(targetId)).thenReturn(Optional.of(target));
    when(categoryRepository.findByIdNot(targetId)).thenReturn(List.of(other));

    // Act
    List<ResCategoryDTO> result = categoryService.getReplacementCategories(targetId);

    // Assert
    assertEquals(1, result.size());
    assertEquals("Other", result.get(0).getCategoryName());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
