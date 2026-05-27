## Information
Service Name: CartService
Method Name: addItem(ReqAddCartItemDTO request)
Mock class: 
   1. UserRepository
   2. ProductRepository
   3. InventoryRepository
   4. CartRepository
   5. CartItemRepository

Mock Data:
- currentUser: User(id="550e8400-e29b-41d4-a716-446655440000", email="customer@example.com", accountStatus="ACTIVE")

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Logged In) | TC 2 (Blocked User) | TC 3 (Product Not Found) | TC 4 (New Cart Success) | TC 5 (Merge Item Success) | TC 6 (Insufficient Stock) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Add item when user is not logged in | Add item when user account is BLOCKED | Add item when product does not exist | Add to new cart successfully | Merge quantity to existing cart item | Requested quantity exceeds available stock |
| **Inputs** | | | | | | |
| `request.productId` | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" | "99999999-9999-9999-9999-999999999999" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" |
| `request.quantity` | 1 | 1 | 1 | 3 | 3 | 9 |
| **Mock / Context Setup** | | | | | | |
| `SecurityContext` | empty (not logged in) | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" |
| `currentUser.accountStatus`| null | "BLOCKED" | "ACTIVE" | "ACTIVE" | "ACTIVE" | "ACTIVE" |
| `ProductRepository` | null | null | returns empty | returns activeProduct | returns activeProduct | returns activeProduct |
| `InventoryRepository` | null | null | null | returns Inventory(available=10)| returns Inventory(available=10)| returns Inventory(available=10)|
| `CartRepository` | null | null | null | returns empty | returns existingCart | returns existingCart |
| `CartItemRepository` | null | null | null | returns empty | returns existingCartItem(qty=2) | returns existingCartItem(qty=2) |
| **Expected Output** | BusinessException(401, "You must login first") | BusinessException(403, "User account is not active") | BusinessException(404, "Product not found") | resCartItemActionDTO.qty = 3, message = "Product added successfully" | resCartItemActionDTO.qty = 5, message = "Product added successfully" | BusinessException(400, "Insufficient stock. Available quantity: 10") |

> [!NOTE]
> **Product Details:**
> - `activeProduct` = Product(id="88888888-8888-8888-8888-888888888888", name="Laptop Gaming", price=BigDecimal(25000000), status="ACTIVE")
> 
> **Cart & Item Details:**
> - `existingCart` = Cart(id="11111111-1111-1111-1111-111111111111", user=currentUser)
> - `existingCartItem` = CartItem(id=CartItemId(cartId="11111111-1111-1111-1111-111111111111", productId="88888888-8888-8888-8888-888888888888"), cart=existingCart, product=activeProduct, quantity=2)
