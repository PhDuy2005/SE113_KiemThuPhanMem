## Information
Service Name: CartService
Method Name: removeItem(UUID productId)
Mock class: 
   1. UserRepository
   2. CartRepository
   3. CartItemRepository
   4. InventoryRepository

Mock Data:
- currentUser: User(id="550e8400-e29b-41d4-a716-446655440000", email="customer@example.com", accountStatus="ACTIVE")

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Logged In) | TC 2 (Blocked User) | TC 3 (Cart Not Found) | TC 4 (Item Not Found) | TC 5 (Success Normal) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Remove item when user is not logged in | Remove item when user account is BLOCKED | Remove item when cart does not exist | Remove item when item not found in cart | Remove item successfully from cart |
| **Inputs** | | | | | |
| `productId` | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" | "88888888-8888-8888-8888-888888888888" |
| **Mock / Context Setup** | | | | | |
| `SecurityContext` | empty (not logged in) | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" |
| `currentUser.accountStatus`| null | "BLOCKED" | "ACTIVE" | "ACTIVE" | "ACTIVE" |
| `CartRepository.findByUserId` | null | null | returns empty | returns Cart(id="11111111-1111-1111-1111-111111111111", user=currentUser) | returns Cart(id="11111111-1111-1111-1111-111111111111", user=currentUser) |
| `CartItemRepository.findByCartIdAndProductId` | null | null | null | returns empty | returns CartItem(id=CartItemId(cartId="11111111-1111-1111-1111-111111111111", productId="88888888-8888-8888-8888-888888888888"), quantity=2) |
| `InventoryRepository.findById`| null | null | null | null | returns Inventory(availableQuantity=10) |
| **Expected Output** | BusinessException(401, "You must login first") | BusinessException(403, "User account is not active") | BusinessException(404, "Cart not found") | BusinessException(404, "Cart item not found") | resCartItemActionDTO.productId = "88888888-8888-8888-8888-888888888888", quantity = 0, message = "Cart item removed successfully" |
