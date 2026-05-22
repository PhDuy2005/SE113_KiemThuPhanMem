import api from '../api/apiClient';
import { PaymentMethod } from '../models/ui_types/paymentMethod';

export const paymentService = {
  getPaymentMethods: async (): Promise<PaymentMethod[]> => {
    // API missing in BE. There is no endpoint to list available payment methods.
    // The FE expects an array of payment methods to display in the checkout flow.
    // Fallback to mock data for now.
    return [
      { id: '1', name: 'Credit/Debit Card', description: 'Pay via VNPay gateway', isActive: true },
      { id: '2', name: 'Cash on Delivery', description: 'Pay when you receive the order', isActive: true },
    ];
  },
};
