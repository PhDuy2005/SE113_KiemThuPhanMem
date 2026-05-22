import api, { PagedResponse } from '../api/apiClient';
import { Order, OrderStatus } from '../models/ui_types/order';

// ─── BE Response Types ──────────────────────────────────────
interface OrderResponseDto {
  id: string;
  status: OrderStatus;
  totalAmount: number;
  createdAt: string;
  paymentMethodName?: string;
  isPaymentFailed?: boolean | null;
  checkoutUrl?: string;
}

interface OrderItemDto {
  productId: string;
  productName: string;
  productImageUrl?: string;
  price: number;
  quantity: number;
}

interface OrderDetailDto {
  id: string;
  status: OrderStatus;
  totalProductAmount: number;
  shippingFee: number;
  discountAmount: number;
  totalAmount: number;
  shippingAddressSnapshot: string;
  createdAt: string;
  approvedAt?: string;
  shippedAt?: string;
  deliveredAt?: string;
  paymentMethodName?: string;
  isPaymentFailed?: boolean | null;
  items: OrderItemDto[];
  payments?: {
    id: string;
    paymentMethodName: string;
    status: string;
    amount: number;
    transactionRef?: string;
  }[];
}

interface OrderStaffDto extends OrderResponseDto {
  customerName: string;
  customerPhone?: string;
}

interface OrderStaffDetailDto extends OrderDetailDto {
  customerEmail: string;
  customerPhone: string;
  customerFullName: string;
  payments: {
    id: string;
    paymentMethodName: string;
    status: string;
    amount: number;
    transactionRef?: string;
  }[];
}

interface OrderAdminSummaryDto {
  orderId: string;
  customerEmail: string;
  customerName: string;
  status: OrderStatus;
  totalAmount: number;
  createdAt: string;
  paymentMethodName: string;
  isPaymentFailed?: boolean | null;
}

// ─── Mapping ────────────────────────────────────────────────
const mapOrder = (dto: OrderResponseDto): Order => ({
  id: dto.id,
  userId: '',
  status: dto.status,
  totalProductAmount: 0,
  shippingFee: 0,
  discountAmount: 0,
  totalAmount: dto.totalAmount,
  shippingAddressSnapshot: '',
  createdAt: dto.createdAt,
  paymentMethodName: dto.paymentMethodName,
  isPaymentFailed: dto.isPaymentFailed,
  checkoutUrl: dto.checkoutUrl,
});

const mapOrderDetail = (dto: OrderDetailDto): Order => ({
  id: dto.id,
  userId: '',
  status: dto.status,
  totalProductAmount: dto.totalProductAmount,
  shippingFee: dto.shippingFee,
  discountAmount: dto.discountAmount,
  totalAmount: dto.totalAmount,
  shippingAddressSnapshot: dto.shippingAddressSnapshot,
  createdAt: dto.createdAt,
  paymentMethodName: dto.paymentMethodName,
  isPaymentFailed: dto.isPaymentFailed,
  items: dto.items.map(i => ({
    orderId: dto.id,
    productId: i.productId,
    productName: i.productName,
    imageUrl: i.productImageUrl,
    price: i.price,
    quantity: i.quantity,
  })),
  payments: dto.payments?.map(p => ({
    id: p.id,
    paymentMethodName: p.paymentMethodName,
    status: p.status as any,
    amount: p.amount,
    transactionRef: p.transactionRef,
  })) || [],
});

const mapStaffOrder = (dto: OrderStaffDto): Order => ({
  ...mapOrder(dto),
  customerName: dto.customerName,
});

const mapAdminOrder = (dto: OrderAdminSummaryDto): Order => ({
  id: dto.orderId,
  userId: '',
  status: dto.status,
  totalProductAmount: 0,
  shippingFee: 0,
  discountAmount: 0,
  totalAmount: dto.totalAmount,
  shippingAddressSnapshot: '',
  createdAt: dto.createdAt,
  customerName: dto.customerName,
  paymentMethodName: dto.paymentMethodName,
  isPaymentFailed: dto.isPaymentFailed,
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
  getOrders: async (pageNumber = 1, pageSize = 10): Promise<Order[]> => {
    const paged = await api.get<PagedResponse<OrderResponseDto>>(
      `/order?pageNumber=${pageNumber}&pageSize=${pageSize}`,
    );
    return paged.items.map(mapOrder);
  },

  getOrderById: async (id: string): Promise<Order> => {
    const dto = await api.get<OrderStaffDetailDto>(`/order/${id}`);
    const order = mapOrderDetail(dto);
    if (dto.payments && dto.payments.length > 0) {
      order.paymentMethodName = dto.payments[0].paymentMethodName;
    }
    return order;
  },

  calculateCheckoutSummary: async (params: CheckoutPreviewParams): Promise<CheckoutSummary> => {
    // BE doesn't have a preview endpoint — calculate client-side
    // Import productService lazily to avoid circular deps
    const { productService } = await import('./productService');
    let subtotal = 0;
    for (const item of params.items) {
      try {
        const product = await productService.getProductById(item.productId);
        subtotal += product.price * item.quantity;
      } catch {
        // Product not found, skip
      }
    }
    const shippingFee = 0;
    const discount = 0;
    return {
      subtotal,
      shippingFee,
      total: subtotal + shippingFee - discount,
      discount,
    };
  },

  createOrder: async (orderData: {
    productsWithQuantity: Record<string, number>;
    shippingAddressId: string;
    paymentMethodId: string;
    voucherCode?: string;
  }): Promise<Order> => {
    const dto = await api.post<OrderResponseDto>('/order', {
      productsWithQuantity: orderData.productsWithQuantity,
      shippingAddressId: orderData.shippingAddressId,
      paymentMethodId: orderData.paymentMethodId,
      voucherCode: orderData.voucherCode,
    });
    return mapOrder(dto);
  },

  cancelOrder: async (id: string): Promise<void> => {
    await api.post(`/order/${id}/cancel`);
  },

  updateOrderStatus: async (id: string, status: OrderStatus): Promise<void> => {
    // For backward compatibility — maps to specific staff endpoints
    switch (status) {
      case OrderStatus.APPROVED:
        await api.post(`/order/${id}/approve`);
        break;
      case OrderStatus.SHIPPING:
        await api.post(`/order/${id}/ship`);
        break;
      case OrderStatus.DELIVERED:
        await api.post(`/order/${id}/confirm-delivery`);
        break;
      case OrderStatus.CANCELLED:
        await api.post(`/order/${id}/staff-cancel`, { reason: 'Cancelled by staff' });
        break;
      default:
        throw new Error(`Unsupported status transition: ${status}`);
    }
  },

  // ── Staff APIs ───────────────────────────────────────────
  getPendingOrders: async (pageNumber = 1, pageSize = 20): Promise<Order[]> => {
    const paged = await api.get<PagedResponse<OrderStaffDto>>(
      `/order/pending?pageNumber=${pageNumber}&pageSize=${pageSize}`,
    );
    return paged.items.map(mapStaffOrder);
  },

  getOrderStaffDetail: async (id: string): Promise<Order> => {
    const dto = await api.get<OrderStaffDetailDto>(`/order/${id}/staff`);
    const order = mapOrderDetail(dto);
    order.customerName = dto.customerFullName;
    
    if (dto.payments && dto.payments.length > 0) {
      order.paymentMethodName = dto.payments[0].paymentMethodName;
    }
    
    return order;
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
    if (params.orderCode) query.set('orderCode', params.orderCode);
    if (params.customerName) query.set('customerName', params.customerName);
    if (params.phoneNumber) query.set('phoneNumber', params.phoneNumber);
    if (params.fromDate) query.set('fromDate', params.fromDate);
    if (params.toDate) query.set('toDate', params.toDate);
    query.set('pageNumber', String(params.pageNumber || 1));
    query.set('pageSize', String(params.pageSize || 20));

    const paged = await api.get<PagedResponse<OrderStaffDto>>(
      `/order/search?${query.toString()}`,
    );
    return paged.items.map(mapStaffOrder);
  },

  initiateRefund: async (id: string): Promise<void> => {
    await api.post(`/order/${id}/refund`);
  },

  getAdminOrders: async (pageNumber = 1, pageSize = 20): Promise<Order[]> => {
    const paged = await api.get<PagedResponse<OrderAdminSummaryDto>>(
      `/admin/orders?pageNumber=${pageNumber}&pageSize=${pageSize}`,
    );
    return paged.items.map(mapAdminOrder);
  },

  repay: async (id: string, paymentMethodId?: string): Promise<string | null> => {
    const res = await api.post<{ checkoutUrl: string | null }>(`/order/${id}/repay`, {
      paymentMethodId,
    });
    return res.checkoutUrl;
  },
};
