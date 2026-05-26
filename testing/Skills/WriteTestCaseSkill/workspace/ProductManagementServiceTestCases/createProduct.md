---
name: unit-test-createProduct-condition-coverage
description: Unit tests with 100% condition coverage for ProductManagementService.createProduct and validations.
---

## Infomation
Service Name: ProductManagementService
Method Name: createProduct(...) and internal validations
Mock class: 
   1. CategoryRepository categoryRepository
   2. ProductRepository productRepository
   3. InventoryRepository inventoryRepository
   4. ProductImageRepository productImageRepository
   5. SecurityUtil (Static mock)
   6. MultipartFile multipartFile

Mock Data:
   - categoryId: "550e8400-e29b-41d4-a716-446655440001"
   - validPrice: 100.00
   - validStock: 50

## Testcase
### Testcase 1
Short Description: Test validateCreateProductInput fails for missing required fields.
Input: name = null, price = 100.00, categoryId = UUID, stock = 50.
Expected Output: BusinessException (400, "Required field is missing")
Actual Output: BusinessException (400, "Required field is missing")

### Testcase 2
Short Description: Test validateCreateProductInput fails for invalid price.
Input: price = -10.00.
Expected Output: BusinessException (400, "Price must be greater than 0")
Actual Output: BusinessException (400, "Price must be greater than 0")

### Testcase 3
Short Description: Test validateCreateProductInput fails for negative stock.
Input: stock = -5.
Expected Output: BusinessException (400, "Stock must not be negative")
Actual Output: BusinessException (400, "Stock must not be negative")

### Testcase 4
Short Description: Test validateCreateProductInput fails for missing/empty images.
Input: images = null or empty list.
Expected Output: BusinessException (400, "At least one product image is required")
Actual Output: BusinessException (400, "At least one product image is required")

### Testcase 5
Short Description: Test validateImageFile fails for oversized image.
Input: Image size = 6MB (Max is 5MB).
Expected Output: BusinessException (400, "Product image must be jpg, png or webp and no larger than 5MB")
Actual Output: BusinessException (400, "Product image must be jpg, png or webp and no larger than 5MB")

### Testcase 6
Short Description: Test validateImageFile fails for invalid extension.
Input: File name "image.gif".
Expected Output: BusinessException (400, "Product image must be jpg, png or webp and no larger than 5MB")
Actual Output: BusinessException (400, "Product image must be jpg, png or webp and no larger than 5MB")

### Testcase 7
Short Description: Test getExtension returns empty string for file without extension.
Input: File name "image_file" (no dot).
Expected Output: ""
Actual Output: ""

### Testcase 8
Short Description: Test createProduct fails if category not found.
Input: Valid inputs, but categoryRepository.findById returns empty.
Expected Output: BusinessException (404, "Category not found")
Actual Output: BusinessException (404, "Category not found")

## Code of Test Case
```java
@Test
void validateCreateProductInput_MissingName_ThrowsBadRequest() {
    assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(productManagementService, "validateCreateProductInput", 
            null, new BigDecimal("100"), UUID.randomUUID(), 10, List.of()));
}

@Test
void validateImageFile_Oversized_ThrowsBadRequest() {
    MultipartFile file = mock(MultipartFile.class);
    when(file.getSize()).thenReturn(6L * 1024 * 1024);
    when(file.getOriginalFilename()).thenReturn("test.jpg");
    
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(productManagementService, "validateImageFile", file));
    assertEquals("Product image must be jpg, png or webp and no larger than 5MB", exception.getMessage());
}

@Test
void getExtension_NoDot_ReturnsEmpty() {
    MultipartFile file = mock(MultipartFile.class);
    when(file.getOriginalFilename()).thenReturn("testfile");
    String ext = ReflectionTestUtils.invokeMethod(productManagementService, "getExtension", file);
    assertEquals("", ext);
}

@Test
void createProduct_CategoryNotFound_ThrowsNotFound() {
    mockAdminAccess();
    UUID catId = UUID.randomUUID();
    MultipartFile file = mock(MultipartFile.class);
    when(file.isEmpty()).thenReturn(false);
    when(file.getSize()).thenReturn(1024L);
    when(file.getOriginalFilename()).thenReturn("test.png");
    
    when(categoryRepository.findById(catId)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> 
        productManagementService.createProduct("Name", "Desc", new BigDecimal("100"), catId, 10, "Brand", List.of(file)));
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
