## Information
Service Name: ReviewService
Method Name: hideReview(UUID reviewId, ReqHideReviewDTO request)
Mock class: 
   1. UserRepository
   2. ReviewRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Missing Reason) | TC 2 (Reason OTHER, Missing Desc) | TC 3 (Review Not Found) | TC 4 (Success) |
| :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Try to hide review without selecting a violation reason | Reason is set to "OTHER" but no description is provided | Try to hide a review that does not exist | Successfully hide a review for violating policies |
| **Inputs** | | | | |
| `reviewId` | "7777..." | "7777..." | "9999..." | "7777..." |
| `request.violationReason` | "" | "OTHER" | "INAPPROPRIATE_LANGUAGE" | "INAPPROPRIATE_LANGUAGE" |
| `request.violationDescription`| null | null | null | null |
| **Mock / Context Setup** | | | | |
| `SecurityContext` | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" | logged in as "admin@example.com" |
| `userRepository.findByEmail`| returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) | returns User(role=Role(name="BUSINESS_ADMIN")) |
| `reviewRepository.findById` | not called | not called | returns empty | returns Review(id="7777...", status="VISIBLE", product=Product(), user=User()) |
| `reviewRepository.save` | not called | not called | not called | returns Review(id="7777...", status="HIDDEN", violationReason="INAPPROPRIATE_LANGUAGE", product=Product(), user=User()) |
| **Expected Output** | BusinessException(400, "Violation reason is required") | BusinessException(400, "Violation description is required when reason is OTHER") | BusinessException(404, "Review not found") | resReviewDTO.message = "Review hidden successfully", status becomes "HIDDEN" |
