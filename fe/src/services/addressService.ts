import api from '../api/apiClient';
import { Address } from '../models/ui_types/address';

export const addressService = {
  getAddresses: async (): Promise<Address[]> => {
    return await api.get<Address[]>('/shipping-address');
  },

  createAddress: async (address: Omit<Address, 'id'>): Promise<void> => {
    await api.post('/shipping-address', {
      province: address.province,
      ward: address.ward,
      detail: address.detail,
    });
  },

  updateAddress: async (id: string, updates: Partial<Address>): Promise<void> => {
    await api.put(`/shipping-address/${id}`, {
      province: updates.province,
      ward: updates.ward,
      detail: updates.detail,
    });
  },

  deleteAddress: async (id: string): Promise<void> => {
    await api.delete(`/shipping-address/${id}`);
  },

  setDefaultAddress: async (id: string): Promise<void> => {
    await api.patch(`/shipping-address/${id}/default`);
  },
};
