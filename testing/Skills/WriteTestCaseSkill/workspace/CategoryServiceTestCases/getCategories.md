---
name: unit-test-getCategories-condition-coverage
description: Unit tests with 100% condition coverage for CategoryService.getCategories method.
---

## Infomation
Service Name: CategoryService
Method Name: getCategories()
Mock class: 
   1. CategoryRepository categoryRepository
   2. ProductRepository productRepository
   3. UserRepository userRepository
   4. SecurityUtil (Static mock)

Mock Data:
   - category1: Category { id: "550e8400-e29b-41d4-a716-446655441111", name: "Electronics" }
   - category2: Category { id: "550e8400-e29b-41d4-a716-446655442222", name: "Books" }

## Testcase
### Testcase 1
Short Description: Test getCategories returns list of categories for authorized admin.
Input: Authorized BUSINESS_ADMIN, categoryRepository.findAll() returns [category1, category2]
Expected Output: List of 2 ResCategoryDTOs.
Actual Output: List of 2 ResCategoryDTOs.

### Testcase 2
Short Description: Test getCategories returns empty list when no categories exist.
Input: Authorized BUSINESS_ADMIN, categoryRepository.findAll() returns []
Expected Output: Empty List.
Actual Output: Empty List.

## Code of Test Case
```java
@Test
void getCategories_Authorized_ReturnsList() {
    // Arrange
    mockAdminAccess(); // Helper to mock getCurrentBusinessAdmin success
    Category cat1 = Category.builder().id(UUID.randomUUID()).name("Electronics").build();
    Category cat2 = Category.builder().id(UUID.randomUUID()).name("Books").build();
    when(categoryRepository.findAll()).thenReturn(List.of(cat1, cat2));

    // Act
    List<ResCategoryDTO> result = categoryService.getCategories();

    // Assert
    assertEquals(2, result.size());
    assertEquals("Electronics", result.get(0).getCategoryName());
    assertEquals("Books", result.get(1).getCategoryName());
}

@Test
void getCategories_Empty_ReturnsEmptyList() {
    // Arrange
    mockAdminAccess();
    when(categoryRepository.findAll()).thenReturn(List.of());

    // Act
    List<ResCategoryDTO> result = categoryService.getCategories();

    // Assert
    assertTrue(result.isEmpty());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
