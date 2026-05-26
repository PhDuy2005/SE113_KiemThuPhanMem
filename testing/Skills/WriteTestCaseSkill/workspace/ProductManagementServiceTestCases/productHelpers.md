---
name: unit-test-product-helpers-condition-coverage
description: Unit tests with 100% condition coverage for ProductManagementService helper methods and storage edge cases.
---

## Infomation
Service Name: ProductManagementService
Method Name: cleanNullableText(String), storeProductImages(Product, List<MultipartFile>), storeProductImage(Product, MultipartFile, boolean)
Mock class: 
   1. Files (Static mock)
   2. MultipartFile multipartFile
   3. ProductImageRepository productImageRepository

Mock Data:
   - product: Product { id: UUID }

## Testcase
### Testcase 1
Short Description: Test cleanNullableText returns null for null or blank input.
Input: value = null, or value = "   ".
Expected Output: null
Actual Output: null

### Testcase 2
Short Description: Test cleanNullableText returns trimmed text for valid input.
Input: value = "  My Product  ".
Expected Output: "My Product"
Actual Output: "My Product"

### Testcase 3
Short Description: Test storeProductImages fails when directory cannot be created.
Input: Files.createDirectories(PRODUCT_IMAGE_DIR) throws IOException.
Expected Output: BusinessException (500, "Cannot initialize product image storage")
Actual Output: BusinessException (500, "Cannot initialize product image storage")

### Testcase 4
Short Description: Test storeProductImage fails when file cannot be copied.
Input: Files.copy(...) throws IOException.
Expected Output: BusinessException (500, "Cannot store product image")
Actual Output: BusinessException (500, "Cannot store product image")

## Code of Test Case
```java
@Test
void cleanNullableText_Conditions() {
    assertNull(ReflectionTestUtils.invokeMethod(productManagementService, "cleanNullableText", (Object) null));
    assertNull(ReflectionTestUtils.invokeMethod(productManagementService, "cleanNullableText", "   "));
    assertEquals("Value", ReflectionTestUtils.invokeMethod(productManagementService, "cleanNullableText", "  Value  "));
}

@Test
void storeProductImages_DirCreationFails_ThrowsException() {
    try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
        filesMock.when(() -> Files.createDirectories(any())).thenThrow(new IOException("Perm error"));
        
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            ReflectionTestUtils.invokeMethod(productManagementService, "storeProductImages", new Product(), List.of()));
        
        assertEquals("Cannot initialize product image storage", exception.getMessage());
    }
}

@Test
void storeProductImage_CopyFails_ThrowsException() throws Exception {
    try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        
        filesMock.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(CopyOption.class)))
                .thenThrow(new IOException("Disk error"));
        
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            ReflectionTestUtils.invokeMethod(productManagementService, "storeProductImage", new Product(), file, true));
        
        assertEquals("Cannot store product image", exception.getMessage());
    }
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
