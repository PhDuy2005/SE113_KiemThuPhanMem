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
  name: string;
  quantity: number;
  revenue: number;
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
  getSalesStats: async (startDate?: string, endDate?: string): Promise<DashboardStats> => {
    const now = new Date();
    const thirtyDaysAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
    const startDateStr = startDate || thirtyDaysAgo.toISOString().split('T')[0];
    const endDateStr = endDate || now.toISOString().split('T')[0];

    let revenueData: any = null;
    let ordersItems: any[] = [];

    // 1. Fetch revenue data
    try {
      revenueData = await api.get<any>(
        `/business/reports/revenue?startDate=${startDateStr}&endDate=${endDateStr}`,
      );
    } catch (err) {
      console.warn('Failed to fetch statistics revenue', err);
    }

    // 2. Fetch admin orders to compute operational queue and metrics
    try {
      const ordersPaged = await api.get<ResultPaginationDTO<any>>(
        '/orders/staff?pageSize=100'
      );
      ordersItems = ordersPaged.result || [];
    } catch (err) {
      console.warn('Failed to fetch orders for dashboard metrics', err);
    }

    // Map to FE format
    const totalRevenue = revenueData?.totalRevenue || 0;
    const totalOrders = revenueData?.completedOrderCount || 0;

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

    // 3. Fetch category distribution from backend API
    let categoryDistribution = [];
    try {
      const catData = await api.get<any>(
        `/business/reports/category-distribution?startDate=${startDateStr}&endDate=${endDateStr}`
      );
      categoryDistribution = catData.distribution || [];
    } catch (err) {
      console.warn('Failed to fetch category distribution', err);
    }
    
    // Process revenue trend from list
    const revenueTrendList = (revenueData?.chartData || []).map((pt: any) => {
      const d = new Date(pt.date);
      const month = isNaN(d.getTime()) 
        ? pt.date 
        : d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
      return {
        month,
        revenue: pt.value,
        orders: pt.orderCount
      };
    });

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
  
  getReportSummary: async (startDate?: string, endDate?: string): Promise<ReportSummaryDto> => {
    const now = new Date();
    const thirtyDaysAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
    const startDateStr = startDate || thirtyDaysAgo.toISOString().split('T')[0];
    const endDateStr = endDate || now.toISOString().split('T')[0];

    let revenueData: any = null;
    let topProductsData: any = null;
    let ordersItems: any[] = [];

    try {
      revenueData = await api.get<any>(
        `/business/reports/revenue?startDate=${startDateStr}&endDate=${endDateStr}`
      );
    } catch (err) {
      console.warn(err);
    }

    try {
      topProductsData = await api.get<any>(
        `/business/reports/best-selling-products?startDate=${startDateStr}&endDate=${endDateStr}&limit=5`
      );
    } catch (err) {
      console.warn(err);
    }

    try {
      const ordersPaged = await api.get<ResultPaginationDTO<any>>(
        '/orders/staff?pageSize=100'
      );
      ordersItems = ordersPaged.result || [];
    } catch (err) {
      console.warn(err);
    }

    const totalRevenue = revenueData?.totalRevenue || 0;
    const completedOrders = revenueData?.completedOrderCount || 0;

    // Process top products
    const topSellingProducts = (topProductsData?.rankingList || []).map((p: any) => ({
      productId: p.productId,
      name: p.productName,
      quantity: p.totalSold,
      revenue: p.revenue
    }));

    // Process revenue trend for chart
    const revenueTrend = (revenueData?.chartData || []).map((pt: any) => ({
      date: pt.date,
      totalRevenue: pt.value,
      orderCount: pt.orderCount
    }));

    // Process order status distribution
    const statusCounts: Record<string, number> = {};
    ordersItems.forEach(o => {
      const status = String(o.status).toUpperCase();
      statusCounts[status] = (statusCounts[status] || 0) + 1;
    });

    const orderStatusDistribution = Object.entries(statusCounts).map(([status, count]) => ({
      status,
      count
    }));

    return {
      totalRevenue,
      completedOrders,
      pendingRevenue: ordersItems
        .filter(o => o.status === 'PENDING')
        .reduce((sum, o) => sum + (o.totalAmount || 0), 0),
      topProductSharePercentage: topSellingProducts.length > 0 && totalRevenue > 0 
        ? Math.round((topSellingProducts[0].revenue / totalRevenue) * 100) 
        : 0,
      topProductCategoryName: topSellingProducts.length > 0 ? 'Best Seller' : 'N/A',
      revenueTrend,
      topSellingProducts,
      orderStatusDistribution,
    };
  }
};
