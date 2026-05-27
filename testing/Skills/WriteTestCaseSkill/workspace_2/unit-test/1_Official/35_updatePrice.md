## Information
Service Name: ProductManagementService
Method Name: updatePrice(UUID productId, BigDecimal newPrice)
Mock class: 
   1. UserRepository
   2. ProductRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Admin) | TC 2 (Invalid Price) | TC 3 (Product Not Found) | TC 4 (Same Price) | TC 5 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Try to update price with CUSTOMER role | Try to update price with negative or zero value | Try to update price for a product that does not exist | Try to update with the exact same price as current | Successfully update product price |
| **Inputs** | | | | | |
| `productId` | "88888888..." | "88888888..." | "99999999..." | "88888888..." | "88888888..." |
| `newPrice` | BigDecimal(50000) | BigDecimal(-100) | BigDecimal(50000) | BigDecimal(20000) | BigDecimal(50000) |
| **Mock / Context Setup** | | | | | |
| `SecurityContext` | logged in as "cust@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" |
| `userRepository.findByEmail` | returns User(role=Role(name="CUSTOMER")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) |
| `productRepository.findById` | not called | not called | returns empty | returns Product(id="8888...", price=BigDecimal(20000)) | returns Product(id="8888...", price=BigDecimal(20000)) |
| `productRepository.save` | not called | not called | not called | not called | returns Product(id="8888...", price=BigDecimal(50000)) |
| **Expected Output** | BusinessException(403, "Only business admin can perform this action") | BusinessException(400, "Price must be greater than 0") | BusinessException(404, "Product not found") | BusinessException(400, "New price must be different from current price") | resProductDTO.message = "Product price updated successfully", price is updated to 50000 |
