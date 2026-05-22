import api from '../api/apiClient';
import { PaymentMethod } from '../models/ui_types/paymentMethod';

export const paymentService = {
  getPaymentMethods: async (): Promise<PaymentMethod[]> => {
    return api.get<PaymentMethod[]>('/payment-method');
  },
};
