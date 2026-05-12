package com.uit.nhom7.KiemThuPhanMem.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqCreateReviewDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqHideReviewDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.requestDTO.ReqReplyReviewDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResProductReviewsDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResReviewDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResReviewResponseDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.responseDTO.ResultPaginationDTO;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Order;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Product;
import com.uit.nhom7.KiemThuPhanMem.domain.table.Review;
import com.uit.nhom7.KiemThuPhanMem.domain.table.ReviewResponse;
import com.uit.nhom7.KiemThuPhanMem.domain.table.User;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderItemRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.OrderRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ProductRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ReviewRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.ReviewResponseRepository;
import com.uit.nhom7.KiemThuPhanMem.repository.UserRepository;
import com.uit.nhom7.KiemThuPhanMem.util.SecurityUtil;
import com.uit.nhom7.KiemThuPhanMem.util.error.BusinessException;

@Service
public class ReviewService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewService.class);
    private static final String ACTIVE_ACCOUNT_STATUS = "ACTIVE";
    private static final String STAFF_ROLE = "STAFF";
    private static final String BUSINESS_ADMIN_ROLE = "BUSINESS_ADMIN";
    private static final String MSG49 = "Please select at least one rating star before submitting";
    private static final String MSG50 = "Review submitted successfully";
    private static final String MSG51 = "This product has no reviews yet";
    private static final String MSG66 = "There is no feedback data yet";
    private static final String MSG67 = "Review response submitted successfully";
    private static final String MSG68 = "Reply content is required";
    private static final String MSG69 = "Review hidden successfully";
    private static final String MSG70 = "Violation reason is required";
    private static final String MSG71 = "Violation description is required when reason is OTHER";
    private static final String OTHER_REASON = "OTHER";

    private final ReviewRepository reviewRepository;
    private final ReviewResponseRepository reviewResponseRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            ReviewResponseRepository reviewResponseRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewResponseRepository = reviewResponseRepository;
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

    @Transactional(readOnly = true)
    public ResultPaginationDTO getLatestFeedbacksForStaff(
            Integer starFilter,
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortOrder) {
        getCurrentStaffOrBusinessAdmin();
        Pageable pageable = PageRequest.of(
                Math.max(pageNumber - 1, 0),
                pageSize <= 0 ? 20 : pageSize,
                buildReviewSort(sortBy, sortOrder));

        Page<Review> reviews = starFilter == null
                ? reviewRepository.findAllWithUserAndProduct(pageable)
                : reviewRepository.findByRatingWithUserAndProduct(validateStarFilter(starFilter), pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotalPages(reviews.getTotalPages());
        meta.setTotalItems(reviews.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(reviews.getContent().stream()
                .map(review -> convertToDTO(review, null))
                .toList());
        result.setMessage(reviews.isEmpty() ? MSG66 : null);
        return result;
    }

    @Transactional
    public ResReviewResponseDTO replyReview(UUID reviewId, ReqReplyReviewDTO request) {
        User staff = getCurrentStaffOrBusinessAdmin();
        if (request == null || request.getReplyContent() == null || request.getReplyContent().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG68);
        }
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Review not found"));

        ReviewResponse response = ReviewResponse.builder()
                .review(review)
                .user(staff)
                .content(request.getReplyContent().trim())
                .build();
        ReviewResponse savedResponse = reviewResponseRepository.save(response);
        review.setUpdatedAt(Instant.now());
        reviewRepository.save(review);

        return convertResponseToDTO(savedResponse, MSG67);
    }

    @Transactional
    public ResReviewDTO hideReview(UUID reviewId, ReqHideReviewDTO request) {
        User staff = getCurrentStaffOrBusinessAdmin();
        if (request == null || request.getViolationReason() == null || request.getViolationReason().isBlank()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG70);
        }
        String violationReason = request.getViolationReason().trim();
        String violationDescription = cleanComment(request.getViolationDescription());
        if (OTHER_REASON.equalsIgnoreCase(violationReason) && violationDescription == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, MSG71);
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Review not found"));
        review.setStatus(Review.HIDDEN_STATUS);
        review.setViolationReason(violationReason);
        review.setViolationDescription(violationDescription);
        review.setHiddenAt(Instant.now());
        Review savedReview = reviewRepository.save(review);

        LOGGER.info("Audit action={}, staffId={}, reviewId={}, violationReason={}, violationDescription={}",
                "HIDE_REVIEW", staff.getId(), savedReview.getId(), violationReason, violationDescription);
        return convertToDTO(savedReview, MSG69);
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
        List<ResReviewResponseDTO> responses = reviewResponseRepository.findByReviewIdOrderByCreatedAtAsc(review.getId())
                .stream()
                .map(response -> convertResponseToDTO(response, null))
                .toList();
        return ResReviewDTO.builder()
                .id(review.getId())
                .userId(reviewer.getId())
                .reviewerName(reviewer.getUserFullName())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .ratingStars(review.getRating())
                .reviewComment(review.getComment())
                .status(review.getStatus())
                .responded(!responses.isEmpty())
                .violationReason(review.getViolationReason())
                .violationDescription(review.getViolationDescription())
                .hiddenAt(review.getHiddenAt())
                .createdAt(review.getCreatedAt())
                .responses(responses)
                .message(message)
                .build();
    }

    private ResReviewResponseDTO convertResponseToDTO(ReviewResponse response, String message) {
        return ResReviewResponseDTO.builder()
                .id(response.getId())
                .reviewId(response.getReview().getId())
                .staffId(response.getUser().getId())
                .staffName(response.getUser().getUserFullName())
                .content(response.getContent())
                .createdAt(response.getCreatedAt())
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

    private User getCurrentStaffOrBusinessAdmin() {
        User user = getCurrentActiveUser();
        String roleName = user.getRole() == null || user.getRole().getName() == null
                ? ""
                : user.getRole().getName().trim().toUpperCase(Locale.ROOT);
        if (!STAFF_ROLE.equals(roleName) && !BUSINESS_ADMIN_ROLE.equals(roleName)) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Only staff or business admin can perform this action");
        }
        return user;
    }

    private Sort buildReviewSort(String sortBy, String sortOrder) {
        String field = sortBy == null || sortBy.isBlank() ? "createdAt" : sortBy.trim();
        if (!List.of("createdAt", "rating", "updatedAt").contains(field)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "sortBy must be createdAt, rating or updatedAt");
        }
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    private Integer validateStarFilter(Integer starFilter) {
        if (starFilter < 1 || starFilter > 5) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "starFilter must be from 1 to 5");
        }
        return starFilter;
    }
}
