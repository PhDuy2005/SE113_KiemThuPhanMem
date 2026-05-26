---
name: unit-test-getCurrentUserCart-condition-coverage
description: Unit tests with 100% condition coverage for CartService.getCurrentUserCart method.
---

## Infomation
Service Name: CartService
Method Name: getCurrentUserCart(User currentUser)
Mock class: 
   1. CartRepository cartRepository

Mock Data:
   - userId: "11111111-1111-1111-1111-111111111111"
   - currentUser: User {id: "11111111-1111-1111-1111-111111111111", email: "user@example.com"}
   - cart: Cart {id: "33333333-3333-3333-3333-333333333333", user: currentUser}

## Testcase
### Testcase 1
Short Description: Test getCurrentUserCart when the cart for the given user exists in the database.
Input: currentUser = User {id: "11111111-1111-1111-1111-111111111111"}, cartRepository.findByUserId returns Optional.of(cart).
Expected Output: Cart object (cart)
Actual Output: Cart object (cart)

### Testcase 2
Short Description: Test getCurrentUserCart when the cart for the given user does not exist in the database.
Input: currentUser = User {id: "11111111-1111-1111-1111-111111111111"}, cartRepository.findByUserId returns Optional.empty().
Expected Output: BusinessException (404, "Cart not found")
Actual Output: BusinessException (404, "Cart not found")

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
