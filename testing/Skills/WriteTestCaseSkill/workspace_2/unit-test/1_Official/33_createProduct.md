## Information
Service Name: ProductManagementService
Method Name: createProduct(String name, String description, BigDecimal price, UUID categoryId, Integer stock, String brand, List<MultipartFile> images)
Mock class: 
   1. UserRepository
   2. CategoryRepository
   3. ProductRepository
   4. InventoryRepository
   5. ProductImageRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Admin) | TC 2 (Missing Required) | TC 3 (Invalid Price/Stock) | TC 4 (Invalid Image) | TC 5 (Category Not Found) | TC 6 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Try to create product with CUSTOMER role | Try to create product but missing product name | Try to create product with negative price or stock | Try to upload a .gif file or file > 5MB | Valid input but Category ID does not exist | Successfully create product and save images |
| **Inputs** | | | | | | |
| `name` | "Iphone 15" | "" | "Iphone 15" | "Iphone 15" | "Iphone 15" | "Iphone 15" |
| `price` | BigDecimal(2000) | BigDecimal(2000) | BigDecimal(-100) | BigDecimal(2000) | BigDecimal(2000) | BigDecimal(2000) |
| `categoryId` | "1111..." | "1111..." | "1111..." | "1111..." | "9999..." | "1111..." |
| `stock` | 100 | 100 | -5 | 100 | 100 | 100 |
| `images` | [MultipartFile(name="1.png")] | [MultipartFile(name="1.png")] | [MultipartFile(name="1.png")] | [MultipartFile(name="1.gif")] | [MultipartFile(name="1.png")] | [MultipartFile(name="1.png")] |
| **Mock / Context Setup** | | | | | | |
| `SecurityContext` | logged in as "cust@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" |
| `userRepository.findByEmail` | returns User(role=Role(name="CUSTOMER"), accountStatus="ACTIVE") | returns User(role=Role(name="BUSINESS_ADMIN"), accountStatus="ACTIVE") | returns User(role=Role(name="BUSINESS_ADMIN"), accountStatus="ACTIVE") | returns User(role=Role(name="BUSINESS_ADMIN"), accountStatus="ACTIVE") | returns User(role=Role(name="BUSINESS_ADMIN"), accountStatus="ACTIVE") | returns User(role=Role(name="BUSINESS_ADMIN"), accountStatus="ACTIVE") |
| `categoryRepository.findById`| not called | not called | not called | not called | returns empty | returns Category(id="1111...") |
| `productRepository.save` | not called | not called | not called | not called | not called | returns Product(id="8888...", status="ACTIVE") |
| `inventoryRepository.save`| not called | not called | not called | not called | not called | returns Inventory(productId="8888...", quantity=100) |
| **Expected Output** | BusinessException(403, "Only business admin can perform this action") | BusinessException(400, "Required field is missing") | BusinessException(400, "Price must be greater than 0") | BusinessException(400, "Product image must be jpg, png or webp and no larger than 5MB") | BusinessException(404, "Category not found") | resProductDTO.message = "Product created successfully", stock = 100, images are saved |
