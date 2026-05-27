## Information
Service Name: CategoryService
Method Name: deleteCategory(UUID categoryId, UUID replacementCategoryId)
Mock class: 
   1. UserRepository
   2. CategoryRepository
   3. ProductRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Missing Replacement) | TC 2 (Same Category) | TC 3 (Target Not Found) | TC 4 (Replacement Not Found) | TC 5 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Try to delete category without providing a replacement category ID | Try to delete category but set the replacement ID to the same category ID | Try to delete a category that does not exist | Provide a valid replacement ID but it does not exist in DB | Successfully delete category and transfer products to replacement |
| **Inputs** | | | | | |
| `categoryId` | "1111..." | "1111..." | "9999..." | "1111..." | "1111..." |
| `replacementCategoryId` | null | "1111..." | "2222..." | "2222..." | "2222..." |
| **Mock / Context Setup** | | | | | |
| `SecurityContext` | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" |
| `userRepository.findByEmail` | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) |
| `categoryRepository.findById(categoryId)` | not called | not called | returns empty | returns Category(id="1111...") | returns Category(id="1111...") |
| `categoryRepository.findById(replacement)`| not called | not called | not called | returns empty | returns Category(id="2222...") |
| `productRepository.updateCategory` | not called | not called | not called | not called | executes successfully |
| `categoryRepository.delete` | not called | not called | not called | not called | executes successfully |
| **Expected Output** | BusinessException(400, "Replacement category is required") | BusinessException(400, "Replacement category must be different from target category") | BusinessException(404, "Category not found") | BusinessException(404, "Replacement category not found") | resCategoryDTO.message = "Category deleted successfully" |
