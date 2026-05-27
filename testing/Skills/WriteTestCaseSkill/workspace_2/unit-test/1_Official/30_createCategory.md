## Information
Service Name: CategoryService
Method Name: createCategory(ReqCreateCategoryDTO request)
Mock class: 
   1. UserRepository
   2. CategoryRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Admin) | TC 2 (Name Required) | TC 3 (Duplicate Name) | TC 4 (Success) |
| :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Try to create category with CUSTOMER role | Try to create category without providing a name | Try to create a category with a name that already exists | Successfully create a new category |
| **Inputs** | | | | |
| `request.categoryName` | "Laptop" | "" | "Laptop" | "Laptop" |
| `request.categoryDescription` | "Laptop Devices" | "Laptop Devices" | "Laptop Devices" | "Laptop Devices" |
| **Mock / Context Setup** | | | | |
| `SecurityContext` | logged in as "cust@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" |
| `userRepository.findByEmail` | returns User(role=Role(name="CUSTOMER")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) |
| `categoryRepository.existsByNameIgnoreCase` | not called | not called | returns true | returns false |
| `categoryRepository.save` | not called | not called | not called | returns Category(id="1111...", name="Laptop") |
| **Expected Output** | BusinessException(403, "Only business admin can perform this action") | BusinessException(400, "Category name is required") | BusinessException(409, "Category name already exists") | resCategoryDTO.message = "Category created successfully", name = "Laptop" |
