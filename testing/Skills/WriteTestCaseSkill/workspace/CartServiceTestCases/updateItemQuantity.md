---
name: unit-test-updateItemQuantity-condition-coverage
description: Unit tests with 100% condition coverage for CartService.updateItemQuantity method.
---

## Infomation
Service Name: CartService
Method Name: updateItemQuantity(UUID productId, ReqUpdateCartItemDTO request)
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

## Testcase
### Testcase 1
Short Description: Test updateItemQuantity fails when user is not logged in.
Input: SecurityUtil.getCurrentUserLogin() returns Optional.empty()
Expected Output: BusinessException (401, "You must login first")
Actual Output: BusinessException (401, "You must login first")

### Testcase 2
Short Description: Test updateItemQuantity fails when user account is not active.
Input: User {email: "user@example.com", accountStatus: "INACTIVE"}
Expected Output: BusinessException (403, "User account is not active")
Actual Output: BusinessException (403, "User account is not active")

### Testcase 3
Short Description: Test updateItemQuantity fails when the specified product is not in the cart.
Input: cartItemRepository.findByCartIdAndProductId returns Optional.empty()
Expected Output: BusinessException (404, "Cart item not found")
Actual Output: BusinessException (404, "Cart item not found")

### Testcase 4
Short Description: Test updateItemQuantity when new quantity exceeds available quantity and stock is available (> 0).
Input: ReqUpdateCartItemDTO {newQuantity: 10}, inventory.availableQuantity = 5.
Expected Output: BusinessException (400, "Insufficient stock. Available quantity: 5"), CartItem quantity updated to 5 in DB.
Actual Output: BusinessException (400, "Insufficient stock. Available quantity: 5"), CartItem quantity updated to 5 in DB.

### Testcase 5
Short Description: Test updateItemQuantity when new quantity exceeds available quantity and stock is empty (0).
Input: ReqUpdateCartItemDTO {newQuantity: 10}, inventory.availableQuantity = 0.
Expected Output: BusinessException (400, "Insufficient stock. Available quantity: 0"), CartItem deleted from DB.
Actual Output: BusinessException (400, "Insufficient stock. Available quantity: 0"), CartItem deleted from DB.

### Testcase 6
Short Description: Test updateItemQuantity succeeds when sufficient stock is available.
Input: ReqUpdateCartItemDTO {newQuantity: 5}, inventory.availableQuantity = 10.
Expected Output: ResCartItemActionDTO {cartId: "33333333-3333-3333-3333-333333333333", quantity: 5, availableQuantity: 10, message: "Cart item quantity updated successfully"}
Actual Output: ResCartItemActionDTO {cartId: "33333333-3333-3333-3333-333333333333", quantity: 5, availableQuantity: 10, message: "Cart item quantity updated successfully"}

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
