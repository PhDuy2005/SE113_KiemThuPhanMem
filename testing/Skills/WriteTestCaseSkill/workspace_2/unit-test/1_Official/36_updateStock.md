## Information
Service Name: ProductManagementService
Method Name: updateStock(UUID productId, Integer newStock)
Mock class: 
   1. UserRepository
   2. ProductRepository
   3. InventoryRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Negative Stock) | TC 2 (Product Not Found) | TC 3 (Set Stock to 0) | TC 4 (Set Stock > 0) |
| :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Try to update stock to a negative number | Try to update stock for a product that doesn't exist | Update stock to 0 (Should trigger OUT_OF_STOCK status) | Update stock to a positive number for an out-of-stock product |
| **Inputs** | | | | |
| `productId` | "8888..." | "9999..." | "8888..." | "8888..." |
| `newStock` | -10 | 50 | 0 | 50 |
| **Mock / Context Setup** | | | | |
| `SecurityContext` | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" |
| `userRepository.findByEmail` | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) |
| `productRepository.findById` | not called | returns empty | returns Product(id="8888...", status="ACTIVE") | returns Product(id="8888...", status="OUT_OF_STOCK") |
| `inventoryRepository.findById` | not called | not called | returns Inventory(quantity=10) | returns Inventory(quantity=0) |
| `inventoryRepository.save` | not called | not called | returns Inventory(quantity=0) | returns Inventory(quantity=50) |
| `productRepository.save` | not called | not called | returns Product(id="8888...", status="OUT_OF_STOCK") | returns Product(id="8888...", status="ACTIVE") |
| **Expected Output** | BusinessException(400, "Stock must not be negative") | BusinessException(404, "Product not found") | resProductDTO.message = "Product stock updated successfully", Product status becomes "OUT_OF_STOCK" | resProductDTO.message = "Product stock updated successfully", Product status becomes "ACTIVE" |
