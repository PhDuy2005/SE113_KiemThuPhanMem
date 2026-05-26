---
name: unit-test-getProductReviews-condition-coverage
description: Unit tests with 100% condition coverage for ReviewService.getProductReviews method.
---

## Infomation
Service Name: ReviewService
Method Name: getProductReviews(UUID productId)
Mock class: 
   1. ProductRepository productRepository
   2. ReviewRepository reviewRepository
   3. ReviewResponseRepository reviewResponseRepository

Mock Data:
   - productId: "550e8400-e29b-41d4-a716-446655440001"
   - reviews: List of 2 reviews with ratings 4 and 5.

## Testcase
### Testcase 1
Short Description: Test getProductReviews fails when product is not found.
Input: productRepository returns empty.
Expected Output: BusinessException (404, "Product not found")
Actual Output: BusinessException (404, "Product not found")

### Testcase 2
Short Description: Test getProductReviews returns empty list message when no reviews exist.
Input: reviewRepository returns empty list.
Expected Output: ResProductReviewsDTO with averageRating = 0.0, message = "This product has no reviews yet".
Actual Output: ResProductReviewsDTO with averageRating = 0.0, message = "This product has no reviews yet".

### Testcase 3
Short Description: Test getProductReviews calculates correct average rating.
Input: 2 reviews with ratings 4 and 5.
Expected Output: averageRating = 4.5.
Actual Output: averageRating = 4.5.

### Testcase 4
Short Description: Test getProductReviews handles rounding correctly (Half Up).
Input: 3 reviews with ratings 4, 4, 5 (Avg = 4.333...).
Expected Output: averageRating = 4.3.
Actual Output: averageRating = 4.3.

## Code of Test Case
```java
@Test
void getProductReviews_CalculatesCorrectAverage() {
    // Arrange
    UUID productId = UUID.randomUUID();
    Product p = new Product(); p.setId(productId); p.setName("Tech");
    when(productRepository.findByIdAndStatusIgnoreCase(productId, "ACTIVE")).thenReturn(Optional.of(p));
    
    User u = new User(); u.setUserFullName("User");
    Review r1 = new Review(); r1.setUser(u); r1.setRating(4); r1.setProduct(p);
    Review r2 = new Review(); r2.setUser(u); r2.setRating(5); r2.setProduct(p);
    when(reviewRepository.findByProductIdAndStatusWithUser(productId, "VISIBLE")).thenReturn(List.of(r1, r2));

    // Act
    ResProductReviewsDTO result = reviewService.getProductReviews(productId);

    // Assert
    assertEquals(4.5, result.getAverageRating());
    assertEquals(2, result.getTotalReviews());
}

@Test
void calculateAverageRating_Empty_ReturnsZero() {
    double avg = ReflectionTestUtils.invokeMethod(reviewService, "calculateAverageRating", List.of());
    assertEquals(0.0, avg);
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
