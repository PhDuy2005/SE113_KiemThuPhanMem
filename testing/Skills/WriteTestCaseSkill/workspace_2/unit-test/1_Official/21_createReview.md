## Information
Service Name: ReviewService
Method Name: createReview(ReqCreateReviewDTO request)
Mock class: 
   1. UserRepository
   2. ProductRepository
   3. OrderRepository
   4. OrderItemRepository
   5. ReviewRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Missing Stars) | TC 2 (Order Not Owned) | TC 3 (Order Not Delivered) | TC 4 (Product Not In Order) | TC 5 (Success) |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Try to review without rating stars | Try to review an order that belongs to another user | Try to review an order that is still SHIPPING | Try to review a product that was not purchased in the specified order | Successfully submit a 5-star review |
| **Inputs** | | | | | |
| `request.productId` | "1111..." | "1111..." | "1111..." | "2222..." | "1111..." |
| `request.orderId` | "8888..." | "9999..." | "8888..." | "8888..." | "8888..." |
| `request.ratingStars` | 0 | 5 | 5 | 5 | 5 |
| `request.reviewComment`| "Good" | "Good" | "Good" | "Good" | "Good" |
| **Mock / Context Setup** | | | | | |
| `SecurityContext` | null | logged in as "cust@example.com" | logged in as "cust@example.com" | logged in as "cust@example.com" | logged in as "cust@example.com" |
| `userRepository.findByEmail`| null | returns User(id="550e...") | returns User(id="550e...") | returns User(id="550e...") | returns User(id="550e...") |
| `productRepository.findByIdAndStatusIgnoreCase`| not called | returns Product(id="1111...") | returns Product(id="1111...") | returns Product(id="2222...") | returns Product(id="1111...") |
| `orderRepository.findByIdAndUserId`| not called | returns empty | returns Order(id="8888...", status="SHIPPING") | returns Order(id="8888...", status="DELIVERED") | returns Order(id="8888...", status="DELIVERED") |
| `orderItemRepository.findByOrderIdWithProduct`| not called | not called | not called | returns [OrderItem(product=Product(id="1111..."))] | returns [OrderItem(product=Product(id="1111..."))] |
| `reviewRepository.save` | not called | not called | not called | not called | returns Review(id="7777...", rating=5) |
| **Expected Output** | BusinessException(400, "Please select at least one rating star before submitting") | BusinessException(403, "Order does not belong to current user") | BusinessException(400, "Only delivered orders can be reviewed") | BusinessException(400, "Product was not purchased in this order") | resReviewDTO.message = "Review submitted successfully" |
