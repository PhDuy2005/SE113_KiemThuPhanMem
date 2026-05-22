import api from '../api/apiClient';
import { Address } from '../models/ui_types/address';

export const addressService = {
  getAddresses: async (): Promise<Address[]> => {
    // API missing in BE. Assumes it will be at GET /addresses
    // Currently BE only has GET /orders/{orderId}/addresses
    return await api.get<Address[]>('/addresses');
  },

  createAddress: async (address: Omit<Address, 'id'>): Promise<void> => {
    await api.post('/addresses', {
      province: address.province,
      ward: address.ward,
      detail: address.detail,
    });
  },

  updateAddress: async (id: string, updates: Partial<Address>): Promise<void> => {
    // API missing in BE. There is no PUT /addresses/{id}
    await api.put(`/addresses/${id}`, {
      province: updates.province,
      ward: updates.ward,
      detail: updates.detail,
    });
  },

  deleteAddress: async (id: string): Promise<void> => {
    // API missing in BE. There is no DELETE /addresses/{id}
    await api.delete(`/addresses/${id}`);
  },

  setDefaultAddress: async (id: string): Promise<void> => {
    await api.put(`/addresses/${id}/default`, {});
  },
};
