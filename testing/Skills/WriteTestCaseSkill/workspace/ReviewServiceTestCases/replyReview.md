---
name: unit-test-replyReview-condition-coverage
description: Unit tests with 100% condition coverage for ReviewService.replyReview method.
---

## Infomation
Service Name: ReviewService
Method Name: replyReview(UUID reviewId, ReqReplyReviewDTO request)
Mock class: 
   1. ReviewRepository reviewRepository
   2. ReviewResponseRepository reviewResponseRepository
   3. SecurityUtil (Static mock)

Mock Data:
   - reviewId: "550e8400-e29b-41d4-a716-446655440001"
   - staffUser: User { id: "STAFF1", accountStatus: "ACTIVE", role: { name: "STAFF" } }

## Testcase
### Testcase 1
Short Description: Test replyReview fails when request or content is missing.
Input: request = null, or request.replyContent = "".
Expected Output: BusinessException (400, "Reply content is required")
Actual Output: BusinessException (400, "Reply content is required")

### Testcase 2
Short Description: Test replyReview fails when review is not found.
Input: reviewRepository returns empty.
Expected Output: BusinessException (404, "Review not found")
Actual Output: BusinessException (404, "Review not found")

### Testcase 3
Short Description: Test replyReview succeeds and updates review timestamp.
Input: Valid request and existing review.
Expected Output: ResReviewResponseDTO with content and success message.
Actual Output: ResReviewResponseDTO with content and success message.

## Code of Test Case
```java
@Test
void replyReview_MissingContent_ThrowsBadRequest() {
    mockStaffAccess();
    ReqReplyReviewDTO req = new ReqReplyReviewDTO();
    req.setReplyContent("");

    BusinessException exception = assertThrows(BusinessException.class, () -> 
        reviewService.replyReview(UUID.randomUUID(), req));
    assertEquals("Reply content is required", exception.getMessage());
}

@Test
void replyReview_Valid_Succeeds() {
    // Arrange
    mockStaffAccess();
    UUID reviewId = UUID.randomUUID();
    Review review = new Review(); review.setId(reviewId);
    when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
    
    ReqReplyReviewDTO req = new ReqReplyReviewDTO();
    req.setReplyContent("Thank you for your feedback!");

    ReviewResponse resp = new ReviewResponse();
    resp.setReview(review);
    resp.setUser(new User()); // Staff
    resp.setContent(req.getReplyContent());
    when(reviewResponseRepository.save(any())).thenReturn(resp);

    // Act
    ResReviewResponseDTO result = reviewService.replyReview(reviewId, req);

    // Assert
    assertEquals("Thank you for your feedback!", result.getContent());
    assertNotNull(review.getUpdatedAt());
    verify(reviewRepository).save(review);
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
