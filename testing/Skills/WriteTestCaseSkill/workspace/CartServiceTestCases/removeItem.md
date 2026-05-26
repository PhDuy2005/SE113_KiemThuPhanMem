---
name: unit-test-removeItem-condition-coverage
description: Unit tests with 100% condition coverage for CartService.removeItem method.
---

## Infomation
Service Name: CartService
Method Name: removeItem(UUID productId)
Mock class: 
   1. CartRepository cartRepository
   2. CartItemRepository cartItemRepository
   3. InventoryRepository inventoryRepository
   4. UserRepository userRepository
   5. SecurityUtil (Security context mock)

Mock Data:
   - email: "user@example.com"
   - currentUser: User {id: "11111111-1111-1111-1111-111111111111", email: "user@example.com", accountStatus: "ACTIVE"}
   - productId: "22222222-2222-2222-2222-222222222222"
   - cart: Cart {id: "33333333-3333-3333-3333-333333333333", user: currentUser}
   - cartItem: CartItem {id: {cartId: "33333333-3333-3333-3333-333333333333", productId: "22222222-2222-2222-2222-222222222222"}, quantity: 2}
   - inventory: Inventory {id: "22222222-2222-2222-2222-222222222222", availableQuantity: 10}

## Testcase
### Testcase 1
Short Description: Test removeItem fails when user is not logged in.
Input: SecurityUtil.getCurrentUserLogin() returns Optional.empty()
Expected Output: BusinessException (401, "You must login first")
Actual Output: BusinessException (401, "You must login first")

### Testcase 2
Short Description: Test removeItem fails when user account is not active.
Input: User {email: "user@example.com", accountStatus: "INACTIVE"}
Expected Output: BusinessException (403, "User account is not active")
Actual Output: BusinessException (403, "User account is not active")

### Testcase 3
Short Description: Test removeItem fails when user's cart is not found.
Input: cartRepository.findByUserId returns Optional.empty()
Expected Output: BusinessException (404, "Cart not found")
Actual Output: BusinessException (404, "Cart not found")

### Testcase 4
Short Description: Test removeItem fails when the specified product is not in the cart.
Input: cartItemRepository.findByCartIdAndProductId returns Optional.empty()
Expected Output: BusinessException (404, "Cart item not found")
Actual Output: BusinessException (404, "Cart item not found")

### Testcase 5
Short Description: Test removeItem succeeds and returns inventory quantity when inventory record exists.
Input: Valid user, cart, and cart item; inventoryRepository.findById returns Optional.of(inventory) (10 items).
Expected Output: ResCartItemActionDTO {cartId: "33333333-3333-3333-3333-333333333333", quantity: 0, availableQuantity: 10, message: "Cart item removed successfully"}
Actual Output: ResCartItemActionDTO {cartId: "33333333-3333-3333-3333-333333333333", quantity: 0, availableQuantity: 10, message: "Cart item removed successfully"}

### Testcase 6
Short Description: Test removeItem succeeds and returns 0 available quantity when inventory record does not exist.
Input: Valid user, cart, and cart item; inventoryRepository.findById returns Optional.empty().
Expected Output: ResCartItemActionDTO {cartId: "33333333-3333-3333-3333-333333333333", quantity: 0, availableQuantity: 0, message: "Cart item removed successfully"}
Actual Output: ResCartItemActionDTO {cartId: "33333333-3333-3333-3333-333333333333", quantity: 0, availableQuantity: 0, message: "Cart item removed successfully"}

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
