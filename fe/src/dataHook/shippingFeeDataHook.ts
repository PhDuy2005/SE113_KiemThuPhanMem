import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { shippingFeeService } from '../services/shippingFeeService';

export const useGetShippingFees = () => {
  return useQuery({
    queryKey: ['shipping-fees'],
    queryFn: () => shippingFeeService.getShippingFees(),
  });
};

export const useUpdateShippingFees = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: shippingFeeService.updateShippingFees,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['shipping-fees'] });
    },
  });
};
