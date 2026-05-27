## Information
Service Name: ReviewService
Method Name: getProductReviews(UUID productId)
Mock class: 
   1. ProductRepository
   2. ReviewRepository

## Testcase Specification Matrix

| Row / Testcase Column | TC 1 (Product Not Found) | TC 2 (No Reviews Yet) | TC 3 (Success) |
| :--- | :--- | :--- | :--- |
| **Short Description** | Try to fetch reviews for a product that does not exist or is inactive | Product exists but has zero visible reviews | Product has visible reviews and average rating is calculated correctly |
| **Inputs** | | | |
| `productId` | "9999..." | "1111..." | "1111..." |
| **Mock / Context Setup** | | | |
| `productRepository.findByIdAndStatusIgnoreCase`| returns empty | returns Product(id="1111...") | returns Product(id="1111...") |
| `reviewRepository.findByProductIdAndStatusWithUser`| not called | returns empty list | returns [Review(rating=4, user=User()), Review(rating=5, user=User())] |
| **Expected Output** | BusinessException(404, "Product not found") | resProductReviewsDTO.message = "This product has no reviews yet", reviewList is empty, averageRating = 0.0 | resProductReviewsDTO.message = "Reviews loaded successfully", totalReviews = 2, averageRating = 4.5 |
