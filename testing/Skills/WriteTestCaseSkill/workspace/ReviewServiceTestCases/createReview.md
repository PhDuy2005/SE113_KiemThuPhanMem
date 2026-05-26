---
name: unit-test-createReview-condition-coverage
description: Unit tests with 100% condition coverage for ReviewService.createReview method.
---

## Infomation
Service Name: ReviewService
Method Name: createReview(ReqCreateReviewDTO request)
Mock class: 
   1. ReviewRepository reviewRepository
   2. ProductRepository productRepository
   3. OrderRepository orderRepository
   4. OrderItemRepository orderItemRepository
   5. SecurityUtil (Static mock)

Mock Data:
   - productId: "550e8400-e29b-41d4-a716-446655440001"
   - orderId: "550e8400-e29b-41d4-a716-446655440002"
   - customerUser: User { id: "CUST1", accountStatus: "ACTIVE" }

## Testcase
### Testcase 1
Short Description: Test createReview fails when rating is missing or <= 0.
Input: request = { ratingStars: 0 }.
Expected Output: BusinessException (400, "Please select at least one rating star before submitting")
Actual Output: BusinessException (400, "Please select at least one rating star before submitting")

### Testcase 2
Short Description: Test createReview fails when rating is > 5.
Input: request = { ratingStars: 6 }.
Expected Output: BusinessException (400, "Rating must be from 1 to 5")
Actual Output: BusinessException (400, "Rating must be from 1 to 5")

### Testcase 3
Short Description: Test createReview fails when order does not belong to user.
Input: orderRepository returns empty for given orderId and currentUserId.
Expected Output: BusinessException (403, "Order does not belong to current user")
Actual Output: BusinessException (403, "Order does not belong to current user")

### Testcase 4
Short Description: Test createReview fails when order is not delivered.
Input: Order status is "SHIPPING".
Expected Output: BusinessException (400, "Only delivered orders can be reviewed")
Actual Output: BusinessException (400, "Only delivered orders can be reviewed")

### Testcase 5
Short Description: Test createReview fails when product was not purchased in the order.
Input: Order contains other products, but not the requested productId.
Expected Output: BusinessException (400, "Product was not purchased in this order")
Actual Output: BusinessException (400, "Product was not purchased in this order")

### Testcase 6
Short Description: Test createReview succeeds with valid input.
Input: Valid rating, delivered order, product was purchased.
Expected Output: ResReviewDTO with success message.
Actual Output: ResReviewDTO with success message.

## Code of Test Case
```java
@Test
void createReview_InvalidRating_ThrowsBadRequest() {
    ReqCreateReviewDTO req = new ReqCreateReviewDTO();
    req.setRatingStars(0);
    assertThrows(BusinessException.class, () -> reviewService.createReview(req));
    
    req.setRatingStars(6);
    assertThrows(BusinessException.class, () -> reviewService.createReview(req));
}

@Test
void createReview_OrderNotDelivered_ThrowsBadRequest() {
    // Arrange
    mockCustomerAccess();
    UUID productId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();
    ReqCreateReviewDTO req = new ReqCreateReviewDTO();
    req.setProductId(productId);
    req.setOrderId(orderId);
    req.setRatingStars(5);

    Product product = new Product(); product.setId(productId);
    when(productRepository.findByIdAndStatusIgnoreCase(productId, "ACTIVE")).thenReturn(Optional.of(product));
    
    Order order = new Order(); order.setId(orderId); order.setStatus("SHIPPING");
    when(orderRepository.findByIdAndUserId(any(), any())).thenReturn(Optional.of(order));

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> reviewService.createReview(req));
    assertEquals("Only delivered orders can be reviewed", exception.getMessage());
}

@Test
void createReview_ProductNotPurchased_ThrowsBadRequest() {
    // Arrange
    mockCustomerAccess();
    UUID productId = UUID.randomUUID();
    UUID orderId = UUID.randomUUID();
    ReqCreateReviewDTO req = new ReqCreateReviewDTO();
    req.setProductId(productId);
    req.setOrderId(orderId);
    req.setRatingStars(5);

    Product product = new Product(); product.setId(productId);
    when(productRepository.findByIdAndStatusIgnoreCase(productId, "ACTIVE")).thenReturn(Optional.of(product));
    
    Order order = new Order(); order.setId(orderId); order.setStatus("DELIVERED");
    when(orderRepository.findByIdAndUserId(any(), any())).thenReturn(Optional.of(order));
    
    when(orderItemRepository.findByOrderIdWithProduct(orderId)).thenReturn(List.of()); // No items

    // Act & Assert
    BusinessException exception = assertThrows(BusinessException.class, () -> reviewService.createReview(req));
    assertEquals("Product was not purchased in this order", exception.getMessage());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
