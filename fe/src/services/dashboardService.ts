import api from '../api/apiClient';
import { DashboardStats } from '../models/ui_types/dashboard';

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
interface RevenueChartDto {
  date: string;
  totalRevenue: number;
  orderCount: number;
}

interface ResRevenueReportDTO {
  startDate: string;
  endDate: string;
  totalRevenue: number;
  totalOrders: number;
  revenueByDate: Record<string, number>;
}

export interface TopSellingProductDto {
  productId: string;
  productName: string;
  totalQuantitySold: number;
  totalRevenue: number;
}

export interface OrderStatusDistributionDto {
  status: string;
  count: number;
}

export interface ReportSummaryDto {
  totalRevenue: number;
  completedOrders: number;
  pendingRevenue: number;
  topProductSharePercentage: number;
  topProductCategoryName: string;
  revenueTrend: RevenueChartDto[];
  topSellingProducts: TopSellingProductDto[];
  orderStatusDistribution: OrderStatusDistributionDto[];
}

export const dashboardService = {
  getSalesStats: async (): Promise<DashboardStats> => {
    const now = new Date();
    const thirtyDaysAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
    const startDateStr = thirtyDaysAgo.toISOString().split('T')[0];
    const endDateStr = now.toISOString().split('T')[0];

    let revenueData: ResRevenueReportDTO | null = null;
    let ordersItems: any[] = [];

    // 1. Fetch revenue data
    try {
      revenueData = await api.get<ResRevenueReportDTO>(
        `/business/reports/revenue?startDate=${startDateStr}&endDate=${endDateStr}`,
      );
    } catch (err) {
      console.warn('Failed to fetch statistics revenue', err);
    }

    // 2. Fetch admin orders to compute operational queue and metrics
    try {
      const ordersPaged = await api.get<ResultPaginationDTO<any>>(
        '/orders/staff/search?pageSize=100'
      );
      ordersItems = ordersPaged.result || [];
    } catch (err) {
      console.warn('Failed to fetch orders for dashboard metrics', err);
    }

    // Map to FE format
    const totalRevenue = revenueData?.totalRevenue || 0;
    const totalOrders = revenueData?.totalOrders || 0;

    // Calculate active customers count from order emails
    const uniqueCustomers = new Set(ordersItems.map(o => o.customerId));
    const activeCustomers = uniqueCustomers.size > 0 ? uniqueCustomers.size : 0;

    // Calculate operational status counts
    const pendingOrders = ordersItems.filter(o => String(o.status).toUpperCase() === 'PENDING').length;
    const shippingOrders = ordersItems.filter(o => String(o.status).toUpperCase() === 'SHIPPING').length;
    const deliveredOrders = ordersItems.filter(o => String(o.status).toUpperCase() === 'DELIVERED').length;

    // Map recent orders to order UI type
    const recentOrders = ordersItems.slice(0, 5).map(o => ({
      id: o.orderId,
      userId: o.customerId,
      status: o.status as any,
      totalProductAmount: o.totalProductAmount,
      shippingFee: o.shippingFee,
      discountAmount: o.discountAmount,
      totalAmount: o.totalAmount,
      shippingAddressSnapshot: '',
      createdAt: o.orderingTime,
      customerName: o.customerName,
      paymentMethodName: undefined,
      isPaymentFailed: o.paymentStatus === 'FAILED',
      items: [],
      payments: []
    }));

    // 3. Fallback for category distribution as API is missing
    const categoryDistribution = [
      { name: 'Laptops & PCs', value: 45 },
      { name: 'Smartphones & Tablets', value: 30 },
      { name: 'Components & Hardware', value: 15 },
      { name: 'Accessories', value: 10 }
    ];
    
    // Process revenue trend from map
    const revenueTrendList = [];
    if (revenueData && revenueData.revenueByDate) {
        for (const [date, rev] of Object.entries(revenueData.revenueByDate)) {
            revenueTrendList.push({
                month: date,
                revenue: rev,
                orders: 0 // Unfortunately order count by date is not provided
            });
        }
    }

    return {
      totalRevenue,
      totalOrders,
      avgOrderValue: totalOrders > 0 ? totalRevenue / totalOrders : 0,
      activeCustomers,
      revenueTrend: revenueTrendList,
      categoryDistribution,
      recentOrders,
      pendingOrders,
      shippingOrders,
      deliveredOrders,
      averageProcessingTime: '2.4h',
    };
  },
  
  getReportSummary: async (): Promise<ReportSummaryDto> => {
    // The specific /reports API is missing, we assemble from other reports or return dummy.
    // This expects to call /business/reports/best-selling-products among others
    try {
      const topProducts: any = await api.get(
        `/business/reports/best-selling-products?limit=5`,
      );
      return {
        totalRevenue: 0,
        completedOrders: 0,
        pendingRevenue: 0,
        topProductSharePercentage: 0,
        topProductCategoryName: '',
        revenueTrend: [],
        topSellingProducts: topProducts.products || [],
        orderStatusDistribution: [],
      };
    } catch {
      return {
        totalRevenue: 0,
        completedOrders: 0,
        pendingRevenue: 0,
        topProductSharePercentage: 0,
        topProductCategoryName: '',
        revenueTrend: [],
        topSellingProducts: [],
        orderStatusDistribution: [],
      };
    }
  }
};
