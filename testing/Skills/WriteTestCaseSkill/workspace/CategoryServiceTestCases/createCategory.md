---
name: unit-test-createCategory-condition-coverage
description: Unit tests with 100% condition coverage for CategoryService.createCategory method.
---

## Infomation
Service Name: CategoryService
Method Name: createCategory(ReqCreateCategoryDTO request)
Mock class: 
   1. CategoryRepository categoryRepository
   2. ProductRepository productRepository
   3. UserRepository userRepository
   4. SecurityUtil (Static mock)

Mock Data:
   - request: ReqCreateCategoryDTO { categoryName: "Electronics", categoryImage: "http://image.com", categoryDescription: "Desc" }
   - category: Category { id: "550e8400-e29b-41d4-a716-446655441111", name: "Electronics" }

## Testcase
### Testcase 1
Short Description: Test createCategory fails when request is null.
Input: request = null
Expected Output: BusinessException (400, "Category name is required")
Actual Output: BusinessException (400, "Category name is required")

### Testcase 2
Short Description: Test createCategory fails when category name is null.
Input: request = { categoryName: null }
Expected Output: BusinessException (400, "Category name is required")
Actual Output: BusinessException (400, "Category name is required")

### Testcase 3
Short Description: Test createCategory fails when category name is blank.
Input: request = { categoryName: "   " }
Expected Output: BusinessException (400, "Category name is required")
Actual Output: BusinessException (400, "Category name is required")

### Testcase 4
Short Description: Test createCategory fails when category name already exists.
Input: request = { categoryName: "Electronics" }, categoryRepository.existsByNameIgnoreCase("Electronics") returns true
Expected Output: BusinessException (409, "Category name already exists")
Actual Output: BusinessException (409, "Category name already exists")

### Testcase 5
Short Description: Test createCategory succeeds with valid data and non-blank image/desc.
Input: request = { categoryName: "Electronics", categoryImage: "img.png", categoryDescription: "desc" }, exists = false
Expected Output: ResCategoryDTO with success message "Category created successfully"
Actual Output: ResCategoryDTO with success message "Category created successfully"

### Testcase 6
Short Description: Test createCategory succeeds with null image and description (cleaned to null).
Input: request = { categoryName: "Books", categoryImage: null, categoryDescription: "" }, exists = false
Expected Output: ResCategoryDTO with imageUrl = null and description = null.
Actual Output: ResCategoryDTO with imageUrl = null and description = null.

## Code of Test Case
```java
@Test
void createCategory_NullRequest_ThrowsBadRequest() {
    // Arrange
    mockAdminAccess();

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        categoryService.createCategory(null));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Category name is required", exception.getMessage());
}

@Test
void createCategory_DuplicateName_ThrowsConflict() {
    // Arrange
    mockAdminAccess();
    ReqCreateCategoryDTO req = new ReqCreateCategoryDTO();
    req.setCategoryName("Electronics");
    when(categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(true);

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        categoryService.createCategory(req));
    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    assertEquals("Category name already exists", exception.getMessage());
}

@Test
void createCategory_Valid_ReturnsCreated() {
    // Arrange
    mockAdminAccess();
    ReqCreateCategoryDTO req = new ReqCreateCategoryDTO();
    req.setCategoryName("Electronics");
    req.setCategoryImage("img.png");
    req.setCategoryDescription("desc");

    when(categoryRepository.existsByNameIgnoreCase("Electronics")).thenReturn(false);
    when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    ResCategoryDTO result = categoryService.createCategory(req);

    // Assert
    assertEquals("Electronics", result.getCategoryName());
    assertEquals("img.png", result.getCategoryImage());
    assertEquals("Category created successfully", result.getMessage());
}

@Test
void createCategory_EmptyFields_CleansToNull() {
    // Arrange
    mockAdminAccess();
    ReqCreateCategoryDTO req = new ReqCreateCategoryDTO();
    req.setCategoryName("Books");
    req.setCategoryImage(""); // Should be cleaned to null
    req.setCategoryDescription("   "); // Should be cleaned to null

    when(categoryRepository.existsByNameIgnoreCase("Books")).thenReturn(false);
    when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    ResCategoryDTO result = categoryService.createCategory(req);

    // Assert
    assertNull(result.getCategoryImage());
    assertNull(result.getCategoryDescription());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
