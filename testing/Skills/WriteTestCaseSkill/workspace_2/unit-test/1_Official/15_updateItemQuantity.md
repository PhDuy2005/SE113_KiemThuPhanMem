## Information
Service Name: CartService
Method Name: updateItemQuantity(UUID productId, ReqUpdateCartItemDTO request)
Mock class: 
   1. UserRepository
   2. CartRepository
   3. CartItemRepository
   4. InventoryRepository

Mock Data:
- currentUser: User(id="550e8400-e29b-41d4-a716-446655440000", email="customer@example.com", accountStatus="ACTIVE")

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Logged In) | TC 2 (Blocked User) | TC 3 (Cart Not Found) | TC 4 (Item Not Found) | TC 5 (Success Normal) | TC 6 (Cap to Stock) | TC 7 (Delete out of stock) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Update when not logged in | Update when user blocked | Update when cart does not exist | Update when item is not in cart | Update quantity within stock level | Cap quantity to stock level when stock > 0 | Delete item from cart when stock is 0 |
| **Inputs** | | | | | | | |
| `productId` | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" |
| `request.newQuantity`| 5 | 5 | 5 | 5 | 4 | 15 | 15 |
| **Mock / Context Setup** | | | | | | | |
| `SecurityContext` | empty (not logged in) | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" |
| `currentUser.accountStatus`| null | "BLOCKED" | "ACTIVE" | "ACTIVE" | "ACTIVE" | "ACTIVE" | "ACTIVE" |
| `CartRepository` | null | null | returns empty | returns existingCart | returns existingCart | returns existingCart | returns existingCart |
| `CartItemRepository` | null | null | null | returns empty | returns existingCartItem(qty=2) | returns existingCartItem(qty=2) | returns existingCartItem(qty=2) |
| `InventoryRepository`| null | null | null | null | returns Inventory(available=10) | returns Inventory(available=6) | returns Inventory(available=0) |
| **Expected Output** | BusinessException(401, "You must login first") | BusinessException(403, "User account is not active") | BusinessException(404, "Cart not found") | BusinessException(404, "Cart item not found") | resCartItemActionDTO.qty = 4 | BusinessException(400, "Insufficient stock. Available quantity: 6") AND cartItem.qty = 6 | BusinessException(400, "Insufficient stock. Available quantity: 0") AND cartItem is deleted |

> [!NOTE]
> **Cart & Item Details:**
> - `existingCart` = Cart(id="11111111-1111-1111-1111-111111111111", user=currentUser)
> - `existingCartItem` = CartItem(id=CartItemId(cartId="11111111-1111-1111-1111-111111111111", productId="88888888-8888-8888-8888-888888888888"), cart=existingCart, product=Product(id="88888888-8888-8888-8888-888888888888", price=BigDecimal(100000)), quantity=2)
