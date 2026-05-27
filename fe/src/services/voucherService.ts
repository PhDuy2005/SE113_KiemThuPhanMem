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

// ─── BE Response Type ───────────────────────────────────────
interface ResVoucherDTO {
  id: string;
  voucherCode: string;
  discountType: string;
  discountValue: number;
  quantity: number;
  usedCount: number;
  minOrderAmount: number;
  startDate?: string;
  endDate?: string;
  active: boolean;
  status: string;
  createdAt: string;
}

// ─── DTO Mapper ─────────────────────────────────────────────
const mapVoucher = (dto: ResVoucherDTO): Voucher => {
  return {
    id: dto.id,
    code: dto.voucherCode,
    type: dto.discountType === 'PERCENT' ? VoucherType.PERCENTAGE : VoucherType.FIXED,
    value: dto.discountValue,
    maxUsage: dto.quantity,
    usedCount: dto.usedCount || 0,
    minOrderAmount: dto.minOrderAmount || 0,
    startDate: dto.startDate,
    endDate: dto.endDate,
    isActive: dto.active,
    createdAt: dto.createdAt || '',
  };
};

export const voucherService = {
  getVouchers: async (page = 1, pageSize = 20): Promise<PagedResponse<Voucher>> => {
    const res = await api.get<ResultPaginationDTO<ResVoucherDTO>>(
      `/business/vouchers?pageNumber=${page}&pageSize=${pageSize}`
    );
    return {
      items: res.result.map(mapVoucher),
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
    // Map UI keys to BE ReqCreateVoucherDTO property keys
    const bePayload = {
      voucherCode: data.code,
      discountType: data.type,
      discountValue: data.value,
      quantity: data.maxUsage,
      minOrderAmount: data.minOrderAmount,
      startDate: data.startDate,
      endDate: data.endDate,
    };

    const dto = await api.post<ResVoucherDTO>('/business/vouchers', bePayload);
    return mapVoucher(dto);
  },

  deleteVoucher: async (id: string): Promise<void> => {
    await api.patch(`/business/vouchers/${id}/stop`);
  },

  getAvailableVouchers: async (): Promise<Voucher[]> => {
    const response = await api.get<ResultPaginationDTO<ResVoucherDTO>>('/vouchers');
    return response.result.map(mapVoucher);
  },

  validateVoucher: async (code: string, _orderAmount: number): Promise<Voucher> => {
    const response = await api.post<ResVoucherDTO>('/checkout/voucher', { 
      voucherCode: code,
    });
    return mapVoucher(response);
  },
};
