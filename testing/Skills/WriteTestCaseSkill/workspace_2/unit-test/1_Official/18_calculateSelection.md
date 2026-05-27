## Information
Service Name: CheckoutService
Method Name: calculateSelection(ReqCheckoutSelectionDTO request)
Mock class: 
   1. UserRepository
   2. CartRepository
   3. CartItemRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Logged In) | TC 2 (Blocked User) | TC 3 (Cart Not Found) | TC 4 (Empty Selection) | TC 5 (Invalid Products Selection) | TC 6 (Success Selection) |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Calculate selection when not logged in | Calculate selection when user account is BLOCKED | Calculate selection when user has no cart | Selection is null or empty list | Selected products are not in user's cart | Calculate selection successfully |
| **Inputs** | | | | | | |
| `request.selectedProductIds`| ["88888888-8888-8888-8888-888888888888"] | ["88888888-8888-8888-8888-888888888888"] | ["88888888-8888-8888-8888-888888888888"] | null | ["77777777-7777-7777-7777-777777777777"] | ["88888888-8888-8888-8888-888888888888"] |
| **Mock / Context Setup** | | | | | | |
| `SecurityContext` | empty (not logged in) | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" | logged in as "customer@example.com" |
| `currentUser.accountStatus`| null | "BLOCKED" | "ACTIVE" | "ACTIVE" | "ACTIVE" | "ACTIVE" |
| `CartRepository.findByUserId` | null | null | returns empty | returns Cart(id="1111...") | returns Cart(id="1111...") | returns Cart(id="1111...") |
| `CartItemRepository.findSelectedByCartIdWithProduct` | null | null | null | null | returns empty | returns [CartItem(quantity=2, product=Product(price=150000))] |
| **Expected Output** | BusinessException(401, "You must login first") | BusinessException(403, "User account is not active") | BusinessException(404, "Cart not found") | BusinessException(400, "You must select at least one product") | BusinessException(400, "Selected products are invalid") | resCheckoutSelectionDTO.tempTotalPrice = BigDecimal(300000), discountAmount = 0, totalPrice = BigDecimal(300000) |
