## Information
Service Name: ProductManagementService
Method Name: discontinueProduct(UUID productId)
Mock class: 
   1. UserRepository
   2. ProductRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Admin) | TC 2 (Product Not Found) | TC 3 (Success) |
| :--- | :--- | :--- | :--- |
| **Short Description** | Try to discontinue a product with CUSTOMER role | Try to discontinue a product that does not exist in DB | Successfully mark a product as DISCONTINUED |
| **Inputs** | | | |
| `productId` | "88888888-8888-8888-8888-888888888888" | "99999999-9999-9999-9999-999999999999" | "88888888-8888-8888-8888-888888888888" |
| **Mock / Context Setup** | | | |
| `SecurityContext` | logged in as "cust@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" |
| `userRepository.findByEmail` | returns User(role=Role(name="CUSTOMER")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) |
| `productRepository.findById` | not called | returns empty | returns Product(id="8888...", status="ACTIVE") |
| `productRepository.save` | not called | not called | returns Product(id="8888...", status="DISCONTINUED") |
| **Expected Output** | BusinessException(403, "Only business admin can perform this action") | BusinessException(404, "Product not found") | resProductDTO.message = "Product discontinued successfully", product status changes to "DISCONTINUED" |
