## Information
Service Name: ReviewService
Method Name: replyReview(UUID reviewId, ReqReplyReviewDTO request)
Mock class: 
   1. UserRepository
   2. ReviewRepository
   3. ReviewResponseRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Not Staff/Admin) | TC 2 (Missing Content) | TC 3 (Review Not Found) | TC 4 (Success) |
| :--- | :--- | :--- | :--- | :--- |
| **Short Description** | Try to reply to a review with CUSTOMER role | Try to reply without typing any content | Try to reply to a review that does not exist | Successfully reply to a customer review |
| **Inputs** | | | | |
| `reviewId` | "7777..." | "7777..." | "9999..." | "7777..." |
| `request.replyContent`| "Thanks" | "" | "Thanks" | "Thank you for buying!" |
| **Mock / Context Setup** | | | | |
| `SecurityContext` | logged in as "cust@example.com" | logged in as "staff@example.com" | logged in as "staff@example.com" | logged in as "staff@example.com" |
| `userRepository.findByEmail`| returns User(role=Role(name="CUSTOMER")) | returns User(role=Role(name="STAFF")) | returns User(role=Role(name="STAFF")) | returns User(role=Role(name="STAFF")) |
| `reviewRepository.findById` | not called | not called | returns empty | returns Review(id="7777...") |
| `reviewResponseRepository.save`| not called | not called | not called | returns ReviewResponse(id="8888...", content="Thank you for buying!") |
| `reviewRepository.save` | not called | not called | not called | executes successfully |
| **Expected Output** | BusinessException(403, "Only staff or business admin can perform this action") | BusinessException(400, "Reply content is required") | BusinessException(404, "Review not found") | resReviewResponseDTO.message = "Review response submitted successfully" |
