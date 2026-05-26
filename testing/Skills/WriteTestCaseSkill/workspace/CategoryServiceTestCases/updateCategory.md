---
name: unit-test-updateCategory-condition-coverage
description: Unit tests with 100% condition coverage for CategoryService.updateCategory method.
---

## Infomation
Service Name: CategoryService
Method Name: updateCategory(UUID categoryId, ReqUpdateCategoryDTO request)
Mock class: 
   1. CategoryRepository categoryRepository
   2. ProductRepository productRepository
   3. UserRepository userRepository
   4. SecurityUtil (Static mock)

Mock Data:
   - categoryId: "550e8400-e29b-41d4-a716-446655441111"
   - existingCategory: Category { id: "550e8400-e29b-41d4-a716-446655441111", name: "Old Name" }

## Testcase
### Testcase 1
Short Description: Test updateCategory fails when category name is missing in request.
Input: categoryId, request = { categoryName: "" }
Expected Output: BusinessException (400, "Category name is required")
Actual Output: BusinessException (400, "Category name is required")

### Testcase 2
Short Description: Test updateCategory fails when category to update is not found.
Input: categoryId, request = { categoryName: "New Name" }, categoryRepository.findById(categoryId) returns Optional.empty()
Expected Output: BusinessException (404, "Category not found")
Actual Output: BusinessException (404, "Category not found")

### Testcase 3
Short Description: Test updateCategory fails when new category name already exists for another category.
Input: categoryId, request = { categoryName: "Duplicate" }, categoryRepository.existsByNameIgnoreCaseAndIdNot("Duplicate", categoryId) returns true
Expected Output: BusinessException (409, "Category name already exists")
Actual Output: BusinessException (409, "Category name already exists")

### Testcase 4
Short Description: Test updateCategory succeeds with valid data.
Input: categoryId, request = { categoryName: "New Name", categoryImage: "new.png", categoryDescription: "new desc" }, exists = false
Expected Output: ResCategoryDTO with success message "Category updated successfully"
Actual Output: ResCategoryDTO with success message "Category updated successfully"

## Code of Test Case
```java
@Test
void updateCategory_NotFound_ThrowsNotFound() {
    // Arrange
    mockAdminAccess();
    UUID categoryId = UUID.randomUUID();
    ReqUpdateCategoryDTO req = new ReqUpdateCategoryDTO();
    req.setCategoryName("New Name");
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        categoryService.updateCategory(categoryId, req));
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("Category not found", exception.getMessage());
}

@Test
void updateCategory_DuplicateName_ThrowsConflict() {
    // Arrange
    mockAdminAccess();
    UUID categoryId = UUID.randomUUID();
    ReqUpdateCategoryDTO req = new ReqUpdateCategoryDTO();
    req.setCategoryName("Duplicate");
    
    Category existing = new Category();
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existing));
    when(categoryRepository.existsByNameIgnoreCaseAndIdNot("Duplicate", categoryId)).thenReturn(true);

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        categoryService.updateCategory(categoryId, req));
    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    assertEquals("Category name already exists", exception.getMessage());
}

@Test
void updateCategory_Valid_ReturnsUpdated() {
    // Arrange
    mockAdminAccess();
    UUID categoryId = UUID.randomUUID();
    ReqUpdateCategoryDTO req = new ReqUpdateCategoryDTO();
    req.setCategoryName("New Name");
    req.setCategoryImage("new.png");

    Category category = Category.builder().id(categoryId).name("Old Name").build();
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
    when(categoryRepository.existsByNameIgnoreCaseAndIdNot("New Name", categoryId)).thenReturn(false);
    when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    ResCategoryDTO result = categoryService.updateCategory(categoryId, req);

    // Assert
    assertEquals("New Name", result.getCategoryName());
    assertEquals("new.png", result.getCategoryImage());
    assertEquals("Category updated successfully", result.getMessage());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
