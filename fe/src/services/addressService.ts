import api from '../api/apiClient';
import { Address } from '../models/ui_types/address';

interface ShippingAddressDto {
  id: string;
  provinceCode?: string;
  province: string;
  wardCode?: string;
  ward: string;
  detail: string;
  isDefault: boolean;
}

const mapAddress = (dto: ShippingAddressDto): Address => ({
  id: dto.id,
  province: dto.province,
  provinceCode: dto.provinceCode,
  ward: dto.ward,
  wardCode: dto.wardCode,
  detail: dto.detail,
  isDefault: dto.isDefault,
});

export interface Province {
  code: string;
  name: string;
  nameWithType: string;
}

export interface Ward {
  code: string;
  parentCode: string;
  name: string;
  nameWithType: string;
}

export const addressService = {
  getAddresses: async (): Promise<Address[]> => {
    const res = await api.get<ShippingAddressDto[]>('/addresses');
    return res.map(mapAddress).sort((a, b) => (b.isDefault ? 1 : 0) - (a.isDefault ? 1 : 0));
  },

  createAddress: async (address: Omit<Address, 'id'>): Promise<void> => {
    await api.post('/addresses', {
      province: address.province,
      ward: address.ward,
      detail: address.detail,
    });
  },

  updateAddress: async (id: string, updates: Partial<Address>): Promise<void> => {
    await api.put(`/addresses/${id}`, {
      province: updates.province,
      ward: updates.ward,
      detail: updates.detail,
    });
  },

  deleteAddress: async (id: string): Promise<void> => {
    await api.delete(`/addresses/${id}`);
  },

  setDefaultAddress: async (id: string): Promise<void> => {
    await api.put(`/addresses/${id}/default`, {});
  },

  getProvinces: async (): Promise<Province[]> => {
    return await api.get<Province[]>('/address-data/provinces');
  },

  getWards: async (provinceCode: string): Promise<Ward[]> => {
    return await api.get<Ward[]>(`/address-data/provinces/${provinceCode}/wards`);
  },
};
