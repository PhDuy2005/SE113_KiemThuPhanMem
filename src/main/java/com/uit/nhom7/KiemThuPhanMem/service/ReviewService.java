package com.uit.nhom7.KiemThuPhanMem.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateReviewDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProductReviewsDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResReviewDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Order;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Review;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderItemRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ReviewRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class ReviewService {
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String MSG49 = "Please select at least one rating star before submitting";
    private static final String MSG50 = "Review submitted successfully";
    private static final String MSG51 = "This product has no reviews yet";

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResReviewDTO createReview(ReqCreateReviewDTO request) {
        if (request.getRatingStars() == null || request.getRatingStars() <= 0) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG49);
        }
        if (request.getRatingStars() > 5) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Rating must be from 1 to 5");
        }

        User currentUser = getCurrentActiveUser();
        Product product = productRepository
                .findByIdAndStatusIgnoreCase(request.getProductId(), Product.ACTIVE_STATUS)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found"));
        Order order = orderRepository.findByIdAndUserId(request.getOrderId(), currentUser.getId())
                .orElseThrow(() -> new BusinessException(HttpStatus.FORBIDDEN, "Order does not belong to current user"));

        if (!Order.DELIVERED_STATUS.equalsIgnoreCase(order.getStatus())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Only delivered orders can be reviewed");
        }

        boolean productWasPurchased = orderItemRepository.findByOrderIdWithProduct(order.getId()).stream()
                .anyMatch(item -> item.getProduct().getId().equals(product.getId()));
        if (!productWasPurchased) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Product was not purchased in this order");
        }

        Review review = Review.builder()
                .user(currentUser)
                .product(product)
                .rating(request.getRatingStars())
                .comment(cleanComment(request.getReviewComment()))
                .status(Review.VISIBLE_STATUS)
                .build();

        return convertToDTO(reviewRepository.save(review), MSG50);
    }

    @Transactional(readOnly = true)
    public ResProductReviewsDTO getProductReviews(UUID productId) {
        productRepository.findByIdAndStatusIgnoreCase(productId, Product.ACTIVE_STATUS)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found"));

        List<Review> reviews = reviewRepository.findByProductIdAndStatusWithUser(productId, Review.VISIBLE_STATUS);
        List<ResReviewDTO> reviewDTOs = reviews.stream()
                .map(review -> convertToDTO(review, null))
                .toList();

        return ResProductReviewsDTO.builder()
                .productId(productId)
                .averageRating(calculateAverageRating(reviews))
                .totalReviews(reviews.size())
                .reviewList(reviewDTOs)
                .message(reviews.isEmpty() ? MSG51 : "Reviews loaded successfully")
                .build();
    }

    private User getCurrentActiveUser() {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "You must login first"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "User session is invalid"));
        if (user.getAccountStatus() == null
                || !ACTIVE_ACCOUNT_STATUS.equals(user.getAccountStatus().trim().toUpperCase(Locale.ROOT))) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "User account is not active");
        }
        return user;
    }

    private ResReviewDTO convertToDTO(Review review, String message) {
        User reviewer = review.getUser();
        return ResReviewDTO.builder()
                .id(review.getId())
                .userId(reviewer.getId())
                .reviewerName(reviewer.getUserFullName())
                .productId(review.getProduct().getId())
                .ratingStars(review.getRating())
                .reviewComment(review.getComment())
                .status(review.getStatus())
                .createdAt(review.getCreatedAt())
                .message(message)
                .build();
    }

    private double calculateAverageRating(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        return BigDecimal.valueOf(average)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private String cleanComment(String comment) {
        if (comment == null || comment.isBlank()) {
            return null;
        }
        return comment.trim();
    }
}
