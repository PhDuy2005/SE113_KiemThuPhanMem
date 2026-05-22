import api, { PagedResponse } from '../api/apiClient';
import { Review, ReviewStatus } from '../models/ui_types/review';

// ─── BE Response Types ──────────────────────────────────────
interface ReviewResponseItemDto {
  id: string;
  reviewId: string;
  userId: string;
  userName: string;
  content: string;
  createdAt: string;
}

interface ReviewStaffDto {
  id: string;
  rating: number;
  comment: string;
  productName?: string;
  status: string;
  violationReason?: string;
  createdAt: string;
  profile: {
    fullName: string;
    avatarUrl?: string;
  };
  responses: ReviewResponseItemDto[];
}

interface ReviewItemDto {
  id: string;
  rating: number;
  comment?: string;
  createdAt: string;
  profile: {
    fullName: string;
    avatarUrl?: string;
  };
  responses: ReviewResponseItemDto[];
}

interface ProductReviewsResponseDto {
  averageRating: number;
  totalCount: number;
  pageNumber: number;
  pageSize: number;
  items: ReviewItemDto[];
}

// ─── Mapping ────────────────────────────────────────────────
const mapStaffReview = (dto: ReviewStaffDto): Review => ({
  id: dto.id,
  userId: '',
  userName: dto.profile?.fullName || 'Anonymous',
  userAvatar: dto.profile?.avatarUrl,
  productId: '',
  rating: dto.rating,
  comment: dto.comment || '',
  status: (dto.status as ReviewStatus) || ReviewStatus.VISIBLE,
  createdAt: dto.createdAt,
  productName: dto.productName,
  responses: dto.responses?.map(r => ({
    id: r.id,
    reviewId: r.reviewId,
    userId: r.userId,
    userName: r.userName,
    content: r.content,
    createdAt: r.createdAt,
  })) || [],
});

export const reviewService = {
  getReviewsByProductId: async (productId: string, pageNumber = 1, pageSize = 100): Promise<{ reviews: Review[], averageRating: number, totalCount: number }> => {
    const data = await api.get<ProductReviewsResponseDto>(
      `/Review/product/${productId}?pageNumber=${pageNumber}&pageSize=${pageSize}`,
    );
    const reviews = data.items.map((dto): Review => ({
      id: dto.id,
      userId: '',
      userName: dto.profile?.fullName || 'Anonymous',
      userAvatar: dto.profile?.avatarUrl,
      productId: productId,
      rating: dto.rating,
      comment: dto.comment || '',
      status: ReviewStatus.VISIBLE,
      createdAt: dto.createdAt,
      responses: dto.responses?.map(r => ({
        id: r.id,
        reviewId: r.reviewId,
        userId: r.userId,
        userName: r.userName,
        content: r.content,
        createdAt: r.createdAt,
      })) || [],
    }));
    return {
      reviews,
      averageRating: data.averageRating,
      totalCount: data.totalCount,
    };
  },

  getAllReviews: async (pageNumber = 1, pageSize = 20): Promise<Review[]> => {
    const paged = await api.get<PagedResponse<ReviewStaffDto>>(
      `/Review/latest?pageNumber=${pageNumber}&pageSize=${pageSize}`,
    );
    return paged.items.map(mapStaffReview);
  },

  submitReview: async (reviewData: { orderId: string; productId: string; ratingStars: number; reviewComment: string }): Promise<void> => {
    await api.post('/Review', {
      orderId: reviewData.orderId,
      productId: reviewData.productId,
      ratingStars: reviewData.ratingStars,
      reviewComment: reviewData.reviewComment,
    });
  },

  moderateReview: async (id: string, status: ReviewStatus, reason = 'Hidden by staff'): Promise<void> => {
    if (status === ReviewStatus.HIDDEN) {
      await api.put(`/Review/${id}/hide`, { reason });
    }
  },

  replyToReview: async (id: string, reply: string): Promise<void> => {
    await api.post(`/Review/${id}/reply`, { content: reply });
  },
};
