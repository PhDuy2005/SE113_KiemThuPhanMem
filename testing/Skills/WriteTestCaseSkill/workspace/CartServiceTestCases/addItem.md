---
name: unit-test-addItem-condition-coverage
description: Unit tests with 100% condition coverage for CartService.addItem method.
---

## Infomation
Service Name: CartService
Method Name: addItem(ReqAddCartItemDTO)
Mock class: 
   1. CartRepository cartRepository
   2. CartItemRepository cartItemRepository
   3. InventoryRepository inventoryRepository
   4. ProductRepository productRepository
   5. UserRepository userRepository
   6. SecurityUtil (Security context mock)

Mock Data:
   - email: "user@example.com"
   - currentUser: User {id: "11111111-1111-1111-1111-111111111111", email: "user@example.com", accountStatus: "ACTIVE"}
   - product: Product {id: "22222222-2222-2222-2222-222222222222", price: 100.00}
   - cart: Cart {id: "33333333-3333-3333-3333-333333333333", user: currentUser}
   - inventory: Inventory {id: "22222222-2222-2222-2222-222222222222", availableQuantity: 10}
   - reqAddCartItemDTO: {productId: "22222222-2222-2222-2222-222222222222", quantity: 2}

## Testcase
### Testcase 1
Short Description: Test addItem fails when user is not logged in (SecurityUtil returns empty).
Input: SecurityUtil.getCurrentUserLogin() returns Optional.empty()
Expected Output: BusinessException (401, "You must login first")
Actual Output: BusinessException (401, "You must login first")

### Testcase 2
Short Description: Test addItem fails when user email from security context is not found in database.
Input: SecurityUtil.getCurrentUserLogin() returns Optional.of("user@example.com"), userRepository.findByEmail("user@example.com") returns Optional.empty()
Expected Output: BusinessException (401, "User session is invalid")
Actual Output: BusinessException (401, "User session is invalid")

### Testcase 3
Short Description: Test addItem fails when user account status is null.
Input: User {email: "user@example.com", accountStatus: null}
Expected Output: BusinessException (403, "User account is not active")
Actual Output: BusinessException (403, "User account is not active")

### Testcase 4
Short Description: Test addItem fails when user account status is not "ACTIVE" (e.g., "INACTIVE").
Input: User {email: "user@example.com", accountStatus: "INACTIVE"}
Expected Output: BusinessException (403, "User account is not active")
Actual Output: BusinessException (403, "User account is not active")

### Testcase 5
Short Description: Test addItem fails when product is not found or not in ACTIVE status.
Input: ReqAddCartItemDTO {productId: "22222222-2222-2222-2222-222222222222", quantity: 1}, productRepository.findByIdAndStatusIgnoreCase returns Optional.empty()
Expected Output: BusinessException (404, "Product not found")
Actual Output: BusinessException (404, "Product not found")

### Testcase 6
Short Description: Test addItem fails when product has no inventory record (available quantity defaults to 0).
Input: request.quantity = 1, inventoryRepository.findById returns Optional.empty()
Expected Output: BusinessException (400, "Insufficient stock. Available quantity: 0")
Actual Output: BusinessException (400, "Insufficient stock. Available quantity: 0")

### Testcase 7
Short Description: Test addItem fails when requested quantity exceeds available quantity (new cart, new item).
Input: request.quantity = 10, inventory.availableQuantity = 5, cartRepository.findByUserId returns empty, cartItemRepository.findByCartIdAndProductId returns empty
Expected Output: BusinessException (400, "Insufficient stock. Available quantity: 5")
Actual Output: BusinessException (400, "Insufficient stock. Available quantity: 5")

### Testcase 8
Short Description: Test addItem fails when total quantity (existing + requested) exceeds available quantity.
Input: request.quantity = 3, existing cartItem.quantity = 3, inventory.availableQuantity = 5
Expected Output: BusinessException (400, "Insufficient stock. Available quantity: 5")
Actual Output: BusinessException (400, "Insufficient stock. Available quantity: 5")

### Testcase 9
Short Description: Test addItem succeeds when adding new product to a new cart with sufficient stock.
Input: request.quantity = 2, inventory.availableQuantity = 10, cartRepository.findByUserId returns empty, cartItemRepository.findByCartIdAndProductId returns empty
Expected Output: ResCartItemActionDTO {cartId: "33333333-3333-3333-3333-333333333333", productId: "22222222-2222-2222-2222-222222222222", quantity: 2, message: "Product added to cart successfully"}
Actual Output: ResCartItemActionDTO {cartId: "33333333-3333-3333-3333-333333333333", productId: "22222222-2222-2222-2222-222222222222", quantity: 2, message: "Product added to cart successfully"}

### Testcase 10
Short Description: Test addItem succeeds when updating existing product quantity in an existing cart with sufficient stock.
Input: request.quantity = 3, existing cartItem.quantity = 2, inventory.availableQuantity = 10
Expected Output: ResCartItemActionDTO {cartId: "33333333-3333-3333-3333-333333333333", productId: "22222222-2222-2222-2222-222222222222", quantity: 5, message: "Product added to cart successfully"}
Actual Output: ResCartItemActionDTO {cartId: "33333333-3333-3333-3333-333333333333", productId: "22222222-2222-2222-2222-222222222222", quantity: 5, message: "Product added to cart successfully"}

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
