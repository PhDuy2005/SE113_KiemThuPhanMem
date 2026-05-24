import api, { PagedResponse } from '../api/apiClient';
import { Order, OrderStatus } from '../models/ui_types/order';
import { formatImageUrl } from '../utils/format';

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

// ─── BE Response Types ──────────────────────────────────────
interface ResOrderDTO {
  orderId: string;
  customerId: string;
  customerName: string;
  status: OrderStatus;
  totalProductAmount: number;
  shippingFee: number;
  discountAmount: number;
  totalAmount: number;
  paymentId: string;
  paymentMethodName?: string;
  paymentStatus: string;
  trackingNumber?: string;
  orderingTime: string;
  completedAt?: string;
  cancelReason?: string;
  cancelledAt?: string;
  refundStatus?: string;
  message?: string;
}

interface ResOrderItemDTO {
  productId: string;
  productName: string;
  productImageUrl?: string;
  price: number;
  quantity: number;
}

interface ResOrderDetailDTO {
  orderId: string;
  customerId: string;
  customerName: string;
  customerPhone?: string;
  status: OrderStatus;
  totalProductAmount: number;
  shippingFee: number;
  discountAmount: number;
  totalAmount: number;
  shippingAddressSnapshot: string;
  trackingNumber?: string;
  paymentMethod: string;
  paymentStatus: string;
  createdAt: string;
  updatedAt?: string;
  completedAt?: string;
  cancelReason?: string;
  cancelledAt?: string;
  refundStatus?: string;
  items: ResOrderItemDTO[];
  message?: string;
}

// ─── Mapping ────────────────────────────────────────────────
const mapOrder = (dto: ResOrderDTO): Order => ({
  id: dto.orderId,
  userId: dto.customerId,
  status: dto.status,
  totalProductAmount: dto.totalProductAmount,
  shippingFee: dto.shippingFee,
  discountAmount: dto.discountAmount,
  totalAmount: dto.totalAmount,
  shippingAddressSnapshot: '',
  createdAt: dto.orderingTime,
  paymentMethodName: dto.paymentMethodName,
  isPaymentFailed: dto.paymentStatus === 'FAILED',
  checkoutUrl: undefined,
  customerName: dto.customerName,
  payments: dto.paymentStatus ? [{
    id: dto.paymentId || 'default',
    paymentMethodName: dto.paymentMethodName || '',
    status: dto.paymentStatus as any,
    amount: dto.totalAmount
  }] : [],
});

const mapOrderDetail = (dto: ResOrderDetailDTO): Order => ({
  id: dto.orderId,
  userId: dto.customerId,
  status: dto.status,
  totalProductAmount: dto.totalProductAmount,
  shippingFee: dto.shippingFee,
  discountAmount: dto.discountAmount,
  totalAmount: dto.totalAmount,
  shippingAddressSnapshot: dto.shippingAddressSnapshot,
  createdAt: dto.createdAt,
  paymentMethodName: dto.paymentMethod,
  isPaymentFailed: dto.paymentStatus === 'FAILED',
  customerName: dto.customerName,
  payments: dto.paymentStatus ? [{
    id: 'default',
    paymentMethodName: dto.paymentMethod || '',
    status: dto.paymentStatus as any,
    amount: dto.totalAmount
  }] : [],
  items: dto.items.map(i => ({
    orderId: dto.orderId,
    productId: i.productId,
    productName: i.productName,
    imageUrl: formatImageUrl(i.productImageUrl),
    price: i.price,
    quantity: i.quantity,
  })),
});

// ─── Public Interfaces ──────────────────────────────────────
export interface CheckoutPreviewParams {
  items: { productId: string; quantity: number }[];
  couponCode?: string;
}

export interface CheckoutSummary {
  subtotal: number;
  shippingFee: number;
  total: number;
  discount: number;
}

export const orderService = {
  // ── Customer APIs ────────────────────────────────────────
  getOrders: async (pageNumber = 1, pageSize = 10): Promise<PagedResponse<Order>> => {
    const paged = await api.get<ResultPaginationDTO<ResOrderDTO>>(
      `/orders?pageNumber=${pageNumber}&pageSize=${pageSize}`,
    );
    return {
      items: paged.result.map(mapOrder),
      pageNumber: paged.meta.page,
      pageSize: paged.meta.pageSize,
      totalCount: paged.meta.totalItems,
      totalPages: paged.meta.totalPages,
    };
  },

  getOrderById: async (id: string): Promise<Order> => {
    const dto = await api.get<ResOrderDetailDTO>(`/orders/${id}`);
    return mapOrderDetail(dto);
  },

  calculateCheckoutSummary: async (params: CheckoutPreviewParams): Promise<CheckoutSummary> => {
    const selectedProductIds = params.items.map(i => i.productId);
    // Uses the new API endpoint
    // Note: ResCheckoutSelectionDTO only returns tempTotalPrice.
    // shippingFee, total, and discount are missing.
    const res = await api.post<any>('/checkout/selection', {
      selectedProductIds,
    });
    
    return {
      subtotal: res.tempTotalPrice || 0,
      shippingFee: 0, // Missing in API
      total: res.tempTotalPrice || 0, // Missing in API
      discount: 0, // Missing in API
    };
  },

  getShippingFee: async (provinceCode?: string, provinceName?: string): Promise<number> => {
    if (!provinceCode && !provinceName) return 0;
    try {
      const params = new URLSearchParams();
      if (provinceCode) params.append('provinceCode', provinceCode);
      if (provinceName) params.append('provinceName', provinceName);
      
      const res = await api.get<number>(`/checkout/shipping-fee?${params.toString()}`);
      return res;
    } catch (e) {
      console.error('Error fetching shipping fee:', e);
      return 0;
    }
  },

  createOrder: async (orderData: {
    productsWithQuantity: Record<string, number>;
    shippingAddressId: string;
    paymentMethodId: string;
    voucherCode?: string;
  }): Promise<Order> => {
    // New API uses selectedProductIds, assuming products are already in cart
    const selectedProductIds = Object.keys(orderData.productsWithQuantity);
    
    const dto = await api.post<ResOrderDTO>('/checkout/confirm', {
      selectedProductIds,
      shippingAddressId: orderData.shippingAddressId,
      paymentMethodId: orderData.paymentMethodId,
      voucherCode: orderData.voucherCode,
    });
    
    const order = mapOrder(dto);
    
    // Fetch checkout URL if payment method requires online payment
    try {
      const paymentRes = await api.post<any>('/payments/online', {
        orderId: dto.orderId,
        paymentMethodId: orderData.paymentMethodId,
        totalAmount: dto.totalAmount,
      });
      if (paymentRes.paymentUrl) {
        order.checkoutUrl = paymentRes.paymentUrl;
      }
    } catch (e) {
      // Ignore if payment initialization fails or is not applicable
    }
    
    return order;
  },

  cancelOrder: async (id: string): Promise<void> => {
    await api.post(`/orders/${id}/cancel`);
  },

  updateOrderStatus: async (id: string, status: OrderStatus): Promise<void> => {
    switch (status) {
      case OrderStatus.APPROVED:
        await api.patch(`/orders/staff/${id}/approve`);
        break;
      case OrderStatus.SHIPPING:
        await api.patch(`/orders/staff/${id}/shipping`, { trackingNumber: 'N/A' });
        break;
      case OrderStatus.DELIVERED:
        await api.patch(`/orders/staff/${id}/delivered`);
        break;
      case OrderStatus.CANCELLED:
        await api.patch(`/orders/staff/${id}/cancel`, { reason: 'Cancelled by staff' });
        break;
      default:
        throw new Error(`Unsupported status transition: ${status}`);
    }
  },

  // ── Staff APIs ───────────────────────────────────────────
  getPendingOrders: async (pageNumber = 1, pageSize = 20): Promise<Order[]> => {
    const paged = await api.get<ResultPaginationDTO<ResOrderDTO>>(
      `/orders/staff/pending?pageNumber=${pageNumber}&pageSize=${pageSize}`,
    );
    return paged.result.map(mapOrder);
  },

  getOrderStaffDetail: async (id: string): Promise<Order> => {
    // There is no specific /staff/{id} endpoint anymore. Using staff GET endpoint.
    const dto = await api.get<ResOrderDetailDTO>(`/orders/staff/${id}`);
    return mapOrderDetail(dto);
  },

  searchOrders: async (params: {
    orderCode?: string;
    customerName?: string;
    phoneNumber?: string;
    fromDate?: string;
    toDate?: string;
    pageNumber?: number;
    pageSize?: number;
  }): Promise<Order[]> => {
    const query = new URLSearchParams();
    if (params.orderCode) query.set('searchKeyword', params.orderCode);
    else if (params.customerName) query.set('searchKeyword', params.customerName);
    query.set('pageNumber', String(params.pageNumber || 1));
    query.set('pageSize', String(params.pageSize || 20));

    const paged = await api.get<ResultPaginationDTO<ResOrderDTO>>(
      `/orders/staff/search?${query.toString()}`,
    );
    return paged.result.map(mapOrder);
  },

  initiateRefund: async (id: string): Promise<void> => {
    await api.patch(`/orders/staff/${id}/refund`);
  },

  getAdminOrders: async (status?: string, searchKeyword?: string, pageNumber = 1, pageSize = 10): Promise<PagedResponse<Order>> => {
    if (searchKeyword && searchKeyword.trim()) {
      const paged = await api.get<ResultPaginationDTO<ResOrderDTO>>(
        `/orders/staff/search?searchKeyword=${encodeURIComponent(searchKeyword.trim())}&pageNumber=${pageNumber}&pageSize=${pageSize}`,
      );
      return {
        items: paged.result.map(mapOrder),
        pageNumber: paged.meta.page,
        pageSize: paged.meta.pageSize,
        totalCount: paged.meta.totalItems,
        totalPages: paged.meta.totalPages,
      };
    }

    const params = new URLSearchParams();
    if (status && status !== 'all') {
      params.append('status', status);
    }
    params.append('pageNumber', String(pageNumber));
    params.append('pageSize', String(pageSize));
    
    const paged = await api.get<ResultPaginationDTO<ResOrderDTO>>(
      `/orders/staff?${params.toString()}`,
    );
    return {
      items: paged.result.map(mapOrder),
      pageNumber: paged.meta.page,
      pageSize: paged.meta.pageSize,
      totalCount: paged.meta.totalItems,
      totalPages: paged.meta.totalPages,
    };
  },

  repay: async (id: string, paymentMethodId?: string): Promise<string | null> => {
    // NOTE: This API is currently missing in BE. Assumes POST /orders/{id}/repay
    const res = await api.post<{ checkoutUrl: string | null }>(`/orders/${id}/repay`, {
      paymentMethodId,
    });
    return res.checkoutUrl;
  },
};
