import api from '../api/apiClient';
import { PaymentMethod } from '../models/ui_types/paymentMethod';

export const paymentService = {
  getPaymentMethods: async (): Promise<PaymentMethod[]> => {
    const response = await api.get<any[]>('/payments/methods');
    return response.map(item => ({
      id: item.id,
      name: item.name,
      description: item.type === 'CASH' ? 'Pay when you receive the order' : 'Pay via online payment gateway',
      isActive: true
    }));
  },
};
