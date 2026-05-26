---
name: unit-test-getCartItem-condition-coverage
description: Unit tests with 100% condition coverage for CartService.getCartItem method.
---

## Infomation
Service Name: CartService
Method Name: getCartItem(UUID cartId, UUID productId)
Mock class: 
   1. CartItemRepository cartItemRepository

Mock Data:
   - cartId: "33333333-3333-3333-3333-333333333333"
   - productId: "22222222-2222-2222-2222-222222222222"
   - cartItem: CartItem {id: {cartId: "33333333-3333-3333-3333-333333333333", productId: "22222222-2222-2222-2222-222222222222"}, quantity: 2}

## Testcase
### Testcase 1
Short Description: Test getCartItem when the cart item exists in the database.
Input: cartId = "33333333-3333-3333-3333-333333333333", productId = "22222222-2222-2222-2222-222222222222", cartItemRepository.findByCartIdAndProductId returns Optional.of(cartItem).
Expected Output: CartItem object (cartItem)
Actual Output: CartItem object (cartItem)

### Testcase 2
Short Description: Test getCartItem when the cart item does not exist in the database.
Input: cartId = "33333333-3333-3333-3333-333333333333", productId = "22222222-2222-2222-2222-222222222222", cartItemRepository.findByCartIdAndProductId returns Optional.empty().
Expected Output: BusinessException (404, "Cart item not found")
Actual Output: BusinessException (404, "Cart item not found")

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
