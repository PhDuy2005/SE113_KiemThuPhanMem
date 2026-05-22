import api, { PagedResponse } from '../api/apiClient';
import { Voucher, VoucherType } from '../models/ui_types/voucher';

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

export const voucherService = {
  getVouchers: async (page = 1, pageSize = 20): Promise<PagedResponse<Voucher>> => {
    // API missing in BE. Assumes it will be at GET /business/vouchers
    const res = await api.get<ResultPaginationDTO<Voucher>>(`/business/vouchers?pageNumber=${page}&pageSize=${pageSize}`);
    return {
      items: res.result,
      pageNumber: res.meta.page,
      pageSize: res.meta.pageSize,
      totalCount: res.meta.totalItems,
      totalPages: res.meta.totalPages,
    };
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
    return await api.post<Voucher>('/business/vouchers', data);
  },

  deleteVoucher: async (id: string): Promise<void> => {
    // BE does not have DELETE. It has PATCH /business/vouchers/{id}/stop
    await api.patch(`/business/vouchers/${id}/stop`);
  },

  getAvailableVouchers: async (): Promise<Voucher[]> => {
    // Public endpoint for customers - missing in BE. Assumes /vouchers
    const response = await api.get<ResultPaginationDTO<Voucher>>('/vouchers');
    return response.result;
  },

  validateVoucher: async (code: string, orderAmount: number): Promise<Voucher> => {
    // There is no dedicated validate endpoint. Checkout apply-voucher is at /checkout/voucher
    // Here we assume it returns the applied voucher details if valid.
    const response = await api.post<Voucher>('/checkout/voucher', { 
      voucherCode: code,
      // orderAmount isn't strictly requested by applyVoucher in BE DTO typically, but passing it just in case
    });
    return response;
  },
};
