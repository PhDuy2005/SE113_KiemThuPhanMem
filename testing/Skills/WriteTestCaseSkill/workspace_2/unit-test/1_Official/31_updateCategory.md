## Information
Service Name: CategoryService
Method Name: updateCategory(UUID categoryId, ReqUpdateCategoryDTO request)
Mock class: 
   1. UserRepository
   2. CategoryRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Name Required) | TC 2 (Category Not Found) | TC 3 (Duplicate Name) | TC 4 (Success) |
| :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Try to update category with empty name | Try to update a category that does not exist | Try to update category with a name that already exists for another category | Successfully update category name and info |
| **Inputs** | | | | |
| `categoryId` | "1111..." | "9999..." | "1111..." | "1111..." |
| `request.categoryName` | " " | "Laptop" | "Smartphone" | "Laptop Gaming" |
| **Mock / Context Setup** | | | | |
| `SecurityContext` | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" |
| `userRepository.findByEmail` | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) |
| `categoryRepository.findById`| not called | returns empty | returns Category(id="1111...", name="Laptop") | returns Category(id="1111...", name="Laptop") |
| `categoryRepository.existsByNameIgnoreCaseAndIdNot` | not called | not called | returns true | returns false |
| `categoryRepository.save` | not called | not called | not called | returns Category(id="1111...", name="Laptop Gaming") |
| **Expected Output** | BusinessException(400, "Category name is required") | BusinessException(404, "Category not found") | BusinessException(409, "Category name already exists") | resCategoryDTO.message = "Category updated successfully", name is updated |
