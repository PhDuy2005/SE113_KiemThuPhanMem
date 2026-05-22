import api, { PagedResponse } from '../api/apiClient';
import { Voucher, VoucherType } from '../models/ui_types/voucher';

export const voucherService = {
  getVouchers: async (page = 1, pageSize = 20): Promise<PagedResponse<Voucher>> => {
    return await api.get<PagedResponse<Voucher>>(`/admin/vouchers?pageNumber=${page}&pageSize=${pageSize}`);
  },

  createVoucher: async (data: {
    code: string;
    type: VoucherType;
    value: number;
    maxUsage: number;
    minOrderAmount: number;
    startDate?: string;
    endDate?: string;
  }): Promise<Voucher> => {
    return await api.post<Voucher>('/admin/vouchers', data);
  },

  deleteVoucher: async (id: string): Promise<void> => {
    await api.delete(`/admin/vouchers/${id}`);
  },

  getAvailableVouchers: async (): Promise<Voucher[]> => {
    // Public endpoint for customers
    const response = await api.get<PagedResponse<Voucher>>('/vouchers');
    return response.items;
  },

  validateVoucher: async (code: string, orderAmount: number): Promise<Voucher> => {
    const response = await api.post<Voucher>('/vouchers/validate', { code, orderAmount });
    return response;
  },
};
