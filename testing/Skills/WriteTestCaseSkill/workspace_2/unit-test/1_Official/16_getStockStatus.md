## Information
Service Name: InventoryService
Method Name: getStockStatus(UUID productId)
Mock class: 
   1. ProductRepository
   2. InventoryRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Product Not Found) | TC 2 (In Stock Success) | TC 3 (Out of Stock Success) | TC 4 (Inventory Record Missing) |
| :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Check stock status for non-existent or inactive product | Product found with positive available stock | Product found but available stock is 0 | Product found but no inventory record exists in DB |
| **Inputs** | | | | |
| `productId` | "99999999-9999-9999-9999-999999999999" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" |
| **Mock / Context Setup** | | | | |
| `SecurityContext` | null | null | null | null |
| `ProductRepository.findByIdAndStatusIgnoreCase` | returns empty | returns Product(id="88888888-8888-8888-8888-888888888888", status="ACTIVE") | returns Product(id="88888888-8888-8888-8888-888888888888", status="ACTIVE") | returns Product(id="88888888-8888-8888-8888-888888888888", status="ACTIVE") |
| `InventoryRepository.findById` | null | returns Inventory(productId="88888888-8888-8888-8888-888888888888", quantity=10, reservedQuantity=3) | returns Inventory(productId="88888888-8888-8888-8888-888888888888", quantity=5, reservedQuantity=5) | returns empty |
| **Expected Output** | BusinessException(404, "Product not found") | resInventoryStockDTO.availableQuantity = 7, stockStatus = "IN_STOCK" | resInventoryStockDTO.availableQuantity = 0, stockStatus = "OUT_OF_STOCK" | resInventoryStockDTO.availableQuantity = 0, stockStatus = "OUT_OF_STOCK" |
