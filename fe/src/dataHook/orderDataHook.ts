import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { orderService, CheckoutPreviewParams } from '../services/orderService';

export const useGetOrders = (pageNumber = 1, pageSize = 10) => {
  return useQuery({
    queryKey: ['orders', pageNumber, pageSize],
    queryFn: () => orderService.getOrders(pageNumber, pageSize),
  });
};

export const useGetAdminOrders = (status?: string, searchKeyword?: string, pageNumber = 1, pageSize = 10) => {
  return useQuery({
    queryKey: ['admin-orders', status, searchKeyword, pageNumber, pageSize],
    queryFn: () => orderService.getAdminOrders(status, searchKeyword, pageNumber, pageSize),
  });
};

export const useGetOrder = (id: string) => {
  return useQuery({
    queryKey: ['orders', id],
    queryFn: () => orderService.getOrderById(id),
    enabled: !!id,
  });
};

export const useGetAdminOrder = (id: string) => {
  return useQuery({
    queryKey: ['admin-orders', id],
    queryFn: () => orderService.getOrderStaffDetail(id),
    enabled: !!id,
  });
};

export const useCheckoutSummary = (params: CheckoutPreviewParams) => {
  return useQuery({
    queryKey: ['checkout-summary', params],
    queryFn: () => orderService.calculateCheckoutSummary(params),
    enabled: params.items.length > 0,
  });
};

export const useCreateOrder = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (orderData: any) => orderService.createOrder(orderData),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
    },
  });
};

export const useCancelOrder = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => orderService.cancelOrder(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      queryClient.invalidateQueries({ queryKey: ['orders', id] });
    },
  });
};

export const useUpdateOrderStatus = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, status }: { id: string, status: any }) => 
      orderService.updateOrderStatus(id, status),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      queryClient.invalidateQueries({ queryKey: ['admin-orders'] });
      queryClient.invalidateQueries({ queryKey: ['orders', variables.id] });
      queryClient.invalidateQueries({ queryKey: ['admin-orders', variables.id] });
    },
  });
};

export const useRepayOrder = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, paymentMethodId }: { id: string; paymentMethodId?: string }) =>
      orderService.repay(id, paymentMethodId),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['orders', variables.id] });
      queryClient.invalidateQueries({ queryKey: ['orders'] });
    },
  });
};
