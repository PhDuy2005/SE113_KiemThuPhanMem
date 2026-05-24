import api, { PagedResponse } from '../api/apiClient';
import { Review, ReviewStatus } from '../models/ui_types/review';

// ─── BE Pagination Type ─────────────────────────────────────
interface ResultPaginationDTO<T> {
  meta: {
    page: number;
    pageSize: number;
    totalPages: number;
    totalItems: number;
  };
  result: T[];
  message: string;
}

// ─── BE Response Types ──────────────────────────────────────
interface ResReviewResponseDTO {
  id: string;
  reviewId: string;
  staffId: string;
  staffName: string;
  content: string;
  createdAt: string;
}

interface ResReviewDTO {
  id: string;
  userId: string;
  reviewerName: string;
  userAvatar?: string;
  productId: string;
  productName: string;
  ratingStars: number;
  reviewComment: string;
  status: string;
  createdAt: string;
  responses: ResReviewResponseDTO[];
}

interface ResProductReviewsDTO {
  productId?: string;
  averageRating: number;
  totalReviews: number;
  reviewList: ResReviewDTO[];
  message?: string;
}

// ─── Mapping ────────────────────────────────────────────────
const mapReview = (dto: ResReviewDTO): Review => ({
  id: dto.id,
  userId: dto.userId,
  userName: dto.reviewerName || 'Anonymous',
  userAvatar: dto.userAvatar,
  productId: dto.productId,
  productName: dto.productName,
  rating: dto.ratingStars || 0,
  comment: dto.reviewComment || '',
  status: (dto.status as ReviewStatus) || ReviewStatus.VISIBLE,
  createdAt: dto.createdAt,
  responses: dto.responses?.map(r => ({
    id: r.id,
    reviewId: r.reviewId,
    userId: r.staffId,
    userName: r.staffName || 'Official Response',
    content: r.content,
    createdAt: r.createdAt,
  })) || [],
});

export const reviewService = {
  getReviewsByProductId: async (productId: string, pageNumber = 1, pageSize = 100): Promise<{ reviews: Review[], averageRating: number, totalCount: number }> => {
    // Note: BE API does not currently accept pagination parameters for this endpoint
    const data = await api.get<ResProductReviewsDTO>(
      `/products/${productId}/reviews`,
    );
    const reviews = data.reviewList || [];
    return {
      reviews: reviews.map(mapReview),
      averageRating: data.averageRating || 0,
      totalCount: data.totalReviews || 0,
    };
  },

  getAllReviews: async (pageNumber = 1, pageSize = 20): Promise<PagedResponse<Review>> => {
    const paged = await api.get<ResultPaginationDTO<ResReviewDTO>>(
      `/staff/reviews?pageNumber=${pageNumber}&pageSize=${pageSize}`,
    );
    return {
      items: paged.result.map(mapReview),
      pageNumber: paged.meta.page,
      pageSize: paged.meta.pageSize,
      totalCount: paged.meta.totalItems,
      totalPages: paged.meta.totalPages,
    };
  },

  submitReview: async (reviewData: { orderId: string; productId: string; ratingStars: number; reviewComment: string }): Promise<void> => {
    await api.post('/reviews', {
      orderId: reviewData.orderId,
      productId: reviewData.productId,
      ratingStars: reviewData.ratingStars,
      reviewComment: reviewData.reviewComment,
    });
  },

  moderateReview: async (id: string, status: ReviewStatus, reason = 'SPAM', description = 'Hidden by staff'): Promise<void> => {
    if (status === ReviewStatus.HIDDEN) {
      await api.patch(`/staff/reviews/${id}/hide`, {
        violationReason: reason,
        violationDescription: description
      });
    }
  },

  replyToReview: async (id: string, reply: string): Promise<void> => {
    await api.post(`/staff/reviews/${id}/responses`, { content: reply });
  },
};
