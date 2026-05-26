---
name: unit-test-getLatestFeedbacksForStaff-condition-coverage
description: Unit tests with 100% condition coverage for ReviewService.getLatestFeedbacksForStaff method.
---

## Infomation
Service Name: ReviewService
Method Name: getLatestFeedbacksForStaff(...)
Mock class: 
   1. ReviewRepository reviewRepository
   2. UserRepository userRepository
   3. SecurityUtil (Static mock)

Mock Data:
   - reviewsPage: Page<Review> containing [review1]

## Testcase
### Testcase 1
Short Description: Test getLatestFeedbacksForStaff with null starFilter.
Input: starFilter = null.
Expected Output: Calls reviewRepository.findAllWithUserAndProduct.
Actual Output: Calls reviewRepository.findAllWithUserAndProduct.

### Testcase 2
Short Description: Test getLatestFeedbacksForStaff with valid starFilter.
Input: starFilter = 5.
Expected Output: Calls reviewRepository.findByRatingWithUserAndProduct(5, ...).
Actual Output: Calls reviewRepository.findByRatingWithUserAndProduct(5, ...).

### Testcase 3
Short Description: Test validateStarFilter fails for invalid stars.
Input: starFilter = 0, or starFilter = 6.
Expected Output: BusinessException (400, "starFilter must be from 1 to 5")
Actual Output: BusinessException (400, "starFilter must be from 1 to 5")

### Testcase 4
Short Description: Test buildReviewSort fails for invalid sortBy field.
Input: sortBy = "comment" (not in whitelist).
Expected Output: BusinessException (400, "sortBy must be createdAt, rating or updatedAt")
Actual Output: BusinessException (400, "sortBy must be createdAt, rating or updatedAt")

### Testcase 5
Short Description: Test buildReviewSort uses default createdAt and DESC order.
Input: sortBy = null, sortOrder = null.
Expected Output: Sort by "createdAt" DESC.
Actual Output: Sort by "createdAt" DESC.

### Testcase 6
Short Description: Test buildReviewSort uses ASC order.
Input: sortOrder = "ASC".
Expected Output: Sort.Direction.ASC.
Actual Output: Sort.Direction.ASC.

## Code of Test Case
```java
@Test
void getLatestFeedbacksForStaff_NullFilter_FindsAll() {
    // Arrange
    mockStaffAccess();
    when(reviewRepository.findAllWithUserAndProduct(any())).thenReturn(Page.empty());

    // Act
    ResultPaginationDTO result = reviewService.getLatestFeedbacksForStaff(null, 1, 10, null, null);

    // Assert
    verify(reviewRepository).findAllWithUserAndProduct(any());
}

@Test
void validateStarFilter_Invalid_ThrowsBadRequest() {
    assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(reviewService, "validateStarFilter", 0));
    assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(reviewService, "validateStarFilter", 6));
}

@Test
void buildReviewSort_InvalidField_ThrowsBadRequest() {
    BusinessException exception = assertThrows(BusinessException.class, () -> 
        ReflectionTestUtils.invokeMethod(reviewService, "buildReviewSort", "invalidField", "ASC"));
    assertEquals("sortBy must be createdAt, rating or updatedAt", exception.getMessage());
}

@Test
void buildReviewSort_Conditions() {
    Sort sort = ReflectionTestUtils.invokeMethod(reviewService, "buildReviewSort", "rating", "ASC");
    assertTrue(sort.getOrderFor("rating").isAscending());
    
    Sort sortDefault = ReflectionTestUtils.invokeMethod(reviewService, "buildReviewSort", null, null);
    assertTrue(sortDefault.getOrderFor("createdAt").isDescending());
}
```

# Note
- Do not write java code for testing, just write testcase in markdown file.
- HTTP Status Code is in number format
- With Input, not use ambigous value, use exact value, example: use "password123" not "[PASSWORD]"; "550e8400-e29b-41d4-a716-446655440000" not "[Random-GUID]"
