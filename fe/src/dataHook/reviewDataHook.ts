import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { reviewService } from '../services/reviewService';
import { Review, ReviewStatus } from '../models/ui_types/review';

export const useGetProductReviews = (productId: string) => {
  return useQuery({
    queryKey: ['reviews', productId],
    queryFn: () => reviewService.getReviewsByProductId(productId),
    enabled: !!productId,
  });
};

export const useGetAllReviews = (pageNumber = 1, pageSize = 20) => {
  return useQuery({
    queryKey: ['reviews', 'all', pageNumber, pageSize],
    queryFn: () => reviewService.getAllReviews(pageNumber, pageSize),
  });
};

export const useSubmitReview = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (reviewData: { orderId: string; productId: string; ratingStars: number; reviewComment: string }) => 
      reviewService.submitReview(reviewData),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['reviews', variables.productId] });
      queryClient.invalidateQueries({ queryKey: ['reviews', 'all'] });
      queryClient.invalidateQueries({ queryKey: ['product', variables.productId] });
    },
  });
};

export const useModerateReview = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, status, reason, description }: { id: string, status: ReviewStatus, reason?: string, description?: string }) => 
      reviewService.moderateReview(id, status, reason, description),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reviews'] });
    },
  });
};

export const useReplyToReview = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, reply }: { id: string, reply: string }) => 
      reviewService.replyToReview(id, reply),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reviews'] });
    },
  });
};
