---
name: unit-test-hideReview-condition-coverage
description: Unit tests with 100% condition coverage for ReviewService.hideReview method.
---

## Infomation
Service Name: ReviewService
Method Name: hideReview(UUID reviewId, ReqHideReviewDTO request)
Mock class: 
   1. ReviewRepository reviewRepository
   2. UserRepository userRepository
   3. SecurityUtil (Static mock)

Mock Data:
   - reviewId: "550e8400-e29b-41d4-a716-446655440001"
   - staffUser: User { id: "STAFF1", accountStatus: "ACTIVE", role: { name: "STAFF" } }

## Testcase
### Testcase 1
Short Description: Test hideReview fails when violationReason is missing.
Input: request.violationReason = null.
Expected Output: BusinessException (400, "Violation reason is required")
Actual Output: BusinessException (400, "Violation reason is required")

### Testcase 2
Short Description: Test hideReview fails when reason is OTHER but description is missing.
Input: violationReason = "OTHER", violationDescription = "".
Expected Output: BusinessException (400, "Violation description is required when reason is OTHER")
Actual Output: BusinessException (400, "Violation description is required when reason is OTHER")

### Testcase 3
Short Description: Test hideReview succeeds for non-OTHER reason without description.
Input: violationReason = "SPAM", violationDescription = null.
Expected Output: ResReviewDTO with status "HIDDEN", violationReason = "SPAM".
Actual Output: ResReviewDTO with status "HIDDEN", violationReason = "SPAM".

### Testcase 4
Short Description: Test hideReview fails when review is not found.
Input: reviewRepository returns empty.
Expected Output: BusinessException (404, "Review not found")
Actual Output: BusinessException (404, "Review not found")

## Code of Test Case
```java
@Test
void hideReview_OtherReasonNoDescription_ThrowsBadRequest() {
    mockStaffAccess();
    ReqHideReviewDTO req = new ReqHideReviewDTO();
    req.setViolationReason("OTHER");
    req.setViolationDescription("  ");

    BusinessException exception = assertThrows(BusinessException.class, () -> 
        reviewService.hideReview(UUID.randomUUID(), req));
    assertEquals("Violation description is required when reason is OTHER", exception.getMessage());
}

@Test
void hideReview_SpamReason_Succeeds() {
    // Arrange
    mockStaffAccess();
    UUID reviewId = UUID.randomUUID();
    Review review = new Review(); review.setId(reviewId);
    review.setUser(new User()); review.setProduct(new Product());
    when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
    when(reviewRepository.save(any())).thenAnswer(i -> i.getArgument(0));

    ReqHideReviewDTO req = new ReqHideReviewDTO();
    req.setViolationReason("SPAM");

    // Act
    ResReviewDTO result = reviewService.hideReview(reviewId, req);

    // Assert
    assertEquals("HIDDEN", result.getStatus());
    assertEquals("SPAM", result.getViolationReason());
    assertNotNull(result.getHiddenAt());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
