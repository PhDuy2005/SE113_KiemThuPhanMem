import api from '../api/apiClient';

export interface ShippingFeeConfig {
  id: string;
  provinceCode: string;
  province: string;
  shippingFee: number;
  createdAt: string;
  updatedAt: string;
}

export const shippingFeeService = {
  getShippingFees: async (): Promise<ShippingFeeConfig[]> => {
    const res = await api.get<any[]>('/business/shipping-fees');
    return res.map(item => ({
      id: item.id,
      provinceCode: item.provinceCode,
      province: item.province,
      shippingFee: Number(item.shippingFee) || 0,
      createdAt: item.createdAt,
      updatedAt: item.updatedAt,
    }));
  },

  updateShippingFees: async (
    updates: { provinceCode: string; province: string; shippingFee: string }[]
  ): Promise<ShippingFeeConfig[]> => {
    const res = await api.put<any[]>('/business/shipping-fees', updates);
    return res.map(item => ({
      id: item.id,
      provinceCode: item.provinceCode,
      province: item.province,
      shippingFee: Number(item.shippingFee) || 0,
      createdAt: item.createdAt,
      updatedAt: item.updatedAt,
    }));
  },
};
