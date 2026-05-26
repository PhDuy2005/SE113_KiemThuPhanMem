---
name: unit-test-deleteCategory-condition-coverage
description: Unit tests with 100% condition coverage for CategoryService.deleteCategory method.
---

## Infomation
Service Name: CategoryService
Method Name: deleteCategory(UUID categoryId, UUID replacementCategoryId)
Mock class: 
   1. CategoryRepository categoryRepository
   2. ProductRepository productRepository
   3. UserRepository userRepository
   4. SecurityUtil (Static mock)

Mock Data:
   - categoryId: "550e8400-e29b-41d4-a716-446655441111"
   - replacementCategoryId: "550e8400-e29b-41d4-a716-446655442222"

## Testcase
### Testcase 1
Short Description: Test deleteCategory fails when replacementCategoryId is null.
Input: categoryId, replacementCategoryId = null
Expected Output: BusinessException (400, "Replacement category is required")
Actual Output: BusinessException (400, "Replacement category is required")

### Testcase 2
Short Description: Test deleteCategory fails when replacementCategoryId is same as categoryId.
Input: categoryId = "550e8400-e29b-41d4-a716-446655441111", replacementCategoryId = "550e8400-e29b-41d4-a716-446655441111"
Expected Output: BusinessException (400, "Replacement category must be different from target category")
Actual Output: BusinessException (400, "Replacement category must be different from target category")

### Testcase 3
Short Description: Test deleteCategory fails when target category is not found.
Input: categoryId, replacementCategoryId, categoryRepository.findById(categoryId) returns Optional.empty()
Expected Output: BusinessException (404, "Category not found")
Actual Output: BusinessException (404, "Category not found")

### Testcase 4
Short Description: Test deleteCategory fails when replacement category is not found.
Input: categoryId, replacementCategoryId, category exists but replacement category does not.
Expected Output: BusinessException (404, "Replacement category not found")
Actual Output: BusinessException (404, "Replacement category not found")

### Testcase 5
Short Description: Test deleteCategory succeeds and updates products.
Input: Valid categoryId and replacementCategoryId.
Expected Output: ResCategoryDTO with success message "Category deleted successfully"
Actual Output: ResCategoryDTO with success message "Category deleted successfully"

## Code of Test Case
```java
@Test
void deleteCategory_SameIds_ThrowsBadRequest() {
    // Arrange
    mockAdminAccess();
    UUID id = UUID.randomUUID();

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        categoryService.deleteCategory(id, id));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Replacement category must be different from target category", exception.getMessage());
}

@Test
void deleteCategory_TargetNotFound_ThrowsNotFound() {
    // Arrange
    mockAdminAccess();
    UUID targetId = UUID.randomUUID();
    UUID replacementId = UUID.randomUUID();
    when(categoryRepository.findById(targetId)).thenReturn(Optional.empty());

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        categoryService.deleteCategory(targetId, replacementId));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
}

@Test
void deleteCategory_ReplacementNotFound_ThrowsNotFound() {
    // Arrange
    mockAdminAccess();
    UUID targetId = UUID.randomUUID();
    UUID replacementId = UUID.randomUUID();
    when(categoryRepository.findById(targetId)).thenReturn(Optional.of(new Category()));
    when(categoryRepository.findById(replacementId)).thenReturn(Optional.empty());

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        categoryService.deleteCategory(targetId, replacementId));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Replacement category not found", exception.getMessage());
}

@Test
void deleteCategory_Valid_DeletesAndUpdatesProducts() {
    // Arrange
    mockAdminAccess();
    UUID targetId = UUID.randomUUID();
    UUID replacementId = UUID.randomUUID();
    Category category = Category.builder().id(targetId).name("Old").build();
    Category replacement = Category.builder().id(replacementId).name("New").build();

    when(categoryRepository.findById(targetId)).thenReturn(Optional.of(category));
    when(categoryRepository.findById(replacementId)).thenReturn(Optional.of(replacement));

    // Act
    ResCategoryDTO result = categoryService.deleteCategory(targetId, replacementId);

    // Assert
    verify(productRepository).updateCategory(targetId, replacementId);
    verify(categoryRepository).delete(category);
    assertEquals("Category deleted successfully", result.getMessage());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
