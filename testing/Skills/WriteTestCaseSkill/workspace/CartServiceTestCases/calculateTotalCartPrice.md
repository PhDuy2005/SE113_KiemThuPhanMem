---
name: unit-test-calculateTotalCartPrice-condition-coverage
description: Unit tests with 100% condition coverage for CartService.calculateTotalCartPrice method.
---

## Infomation
Service Name: CartService
Method Name: calculateTotalCartPrice(UUID cartId)
Mock class: 
   1. CartItemRepository cartItemRepository

Mock Data:
   - cartId: "33333333-3333-3333-3333-333333333333"
   - product1: Product {id: "11111111-1111-1111-1111-111111111111", price: 100.00}
   - product2: Product {id: "22222222-2222-2222-2222-222222222222", price: 50.00}
   - cartItem1: CartItem {product: product1, quantity: 2}
   - cartItem2: CartItem {product: product2, quantity: 3}

## Testcase
### Testcase 1
Short Description: Test calculateTotalCartPrice when the cart is empty (no items).
Input: cartId = "33333333-3333-3333-3333-333333333333", cartItemRepository.findByCartIdWithProduct returns an empty list.
Expected Output: BigDecimal.ZERO (0)
Actual Output: BigDecimal.ZERO (0)

### Testcase 2
Short Description: Test calculateTotalCartPrice when the cart has a single item.
Input: cartId = "33333333-3333-3333-3333-333333333333", cartItemRepository.findByCartIdWithProduct returns [cartItem1].
Expected Output: 200.00 (100.00 * 2)
Actual Output: 200.00 (100.00 * 2)

### Testcase 3
Short Description: Test calculateTotalCartPrice when the cart has multiple items.
Input: cartId = "33333333-3333-3333-3333-333333333333", cartItemRepository.findByCartIdWithProduct returns [cartItem1, cartItem2].
Expected Output: 350.00 (100.00 * 2 + 50.00 * 3)
Actual Output: 350.00 (100.00 * 2 + 50.00 * 3)

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
