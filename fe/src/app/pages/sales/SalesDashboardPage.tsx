import { useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { StatsCard } from '../../components/analytics/StatsCard';
import { Badge } from '../../components/ui/badge';
import { 
  ShoppingCart, 
  Users, 
  Loader2, 
  Package, 
  Truck, 
  CheckCircle2, 
  Clock,
  MessageSquare,
  TrendingUp,
  Star
} from 'lucide-react';
import { useGetSalesStats } from '../../../dataHook/dashboardDataHook';
import { useGetAllReviews } from '../../../dataHook/reviewDataHook';
import { toast } from 'sonner';
import { useNavigate } from 'react-router';

export function SalesDashboardPage() {
  const navigate = useNavigate();
  const { data: stats, isLoading, isError } = useGetSalesStats();
  const { data: reviewsPaged, isLoading: isReviewsLoading } = useGetAllReviews();
  const reviewsData = reviewsPaged?.items || [];

  useEffect(() => {
    if (isError) {
      toast.error('Failed to load operational protocols');
    }
  }, [isError]);

  const renderStars = (rating: number) => {
    return (
      <div className="flex items-center gap-0.5">
        {[1, 2, 3, 4, 5].map((star) => (
          <Star
            key={star}
            className={`h-3 w-3 ${
              star <= rating ? 'fill-amber-400 text-amber-400' : 'text-muted-foreground/20'
            }`}
          />
        ))}
      </div>
    );
  };

  const getStatusVariant = (status: string) => {
    switch (status.toUpperCase()) {
      case 'DELIVERED': return 'success';
      case 'SHIPPING': return 'info';
      case 'APPROVED': return 'warning';
      case 'PENDING': return 'pending';
      case 'CANCELLED': return 'danger';
      default: return 'default';
    }
  };

  if (isLoading || !stats) {
    return (
      <div className="flex h-full items-center justify-center py-20">
        <div className="flex flex-col items-center gap-2">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
          <p className="text-muted-foreground uppercase tracking-widest text-[10px] font-bold">Synchronizing Operational Data...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6 pb-12">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight uppercase">Operational Dashboard</h1>
          <p className="text-sm text-muted-foreground font-bold uppercase tracking-widest opacity-60">Logistics & Service Performance</p>
        </div>
        <div className="flex items-center gap-2 rounded-full bg-blue-500/10 px-4 py-1.5 border border-blue-500/20 shadow-sm">
          <span className="relative flex h-2 w-2">
            <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-blue-400 opacity-75"></span>
            <span className="relative inline-flex h-2 w-2 rounded-full bg-blue-500"></span>
          </span>
          <span className="text-[10px] font-black uppercase tracking-[0.2em] text-blue-600">
            Active Session
          </span>
        </div>
      </div>

      {/* Main Stats Grid - Operational Focused */}
      <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4">
        <StatsCard
          title="Pending Approval"
          value={stats.pendingOrders}
          change="Action required"
          changeType={stats.pendingOrders > 5 ? "negative" : "positive"}
          icon={Package}
        />
        <StatsCard
          title="In Transit"
          value={stats.shippingOrders}
          change="Real-time tracking"
          changeType="neutral"
          icon={Truck}
        />
        <StatsCard
          title="Delivered Today"
          value={stats.deliveredOrders}
          change="+14.2% efficiency"
          changeType="positive"
          icon={CheckCircle2}
        />
        <StatsCard
          title="Avg Processing"
          value={stats.averageProcessingTime || "2.4h"}
          change="-15m from yesterday"
          changeType="positive"
          icon={Clock}
        />
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* Order Summary (One Half) */}
        <Card className="border-border shadow-sm rounded-2xl overflow-hidden bg-card flex flex-col">
          <CardHeader className="border-b border-border bg-muted/20 flex flex-row items-center justify-between py-4">
            <div>
              <CardTitle className="text-xs font-bold uppercase tracking-widest">Recent Orders</CardTitle>
              <p className="text-[10px] text-muted-foreground font-medium uppercase mt-0.5">Real-time purchase activity</p>
            </div>
            <button 
              onClick={() => navigate('/sales/orders')}
              className="text-[10px] font-bold uppercase tracking-widest text-primary hover:underline"
            >
              Manage Orders
            </button>
          </CardHeader>
          <CardContent className="p-0 flex-1">
            <div className="divide-y divide-border">
              {stats.recentOrders && stats.recentOrders.length > 0 ? (
                stats.recentOrders.slice(0, 5).map(order => (
                  <div key={order.id} className="flex items-center justify-between p-4 hover:bg-muted/30 transition-colors">
                    <div className="space-y-1">
                      <p className="text-xs font-bold uppercase tracking-tight text-foreground">{order.id}</p>
                      <div className="flex items-center gap-2">
                        <span className="text-[10px] text-muted-foreground font-medium uppercase">{order.customerName}</span>
                        <span className="text-[9px] text-muted-foreground/60">•</span>
                        <span className="text-[10px] text-muted-foreground font-medium">{new Date(order.createdAt).toLocaleDateString()}</span>
                      </div>
                    </div>
                    <Badge variant={getStatusVariant(order.status)} className="text-[9px] px-2 py-0.5 font-bold uppercase tracking-wider">
                      {order.status}
                    </Badge>
                  </div>
                ))
              ) : (
                <div className="p-8 text-center text-xs text-muted-foreground uppercase font-bold tracking-wider">
                  No recent orders found
                </div>
              )}
            </div>
          </CardContent>
        </Card>

        {/* Review Summary (Other Half) */}
        <Card className="border-border shadow-sm rounded-2xl overflow-hidden bg-card flex flex-col">
          <CardHeader className="border-b border-border bg-muted/20 flex flex-row items-center justify-between py-4">
            <div>
              <CardTitle className="text-xs font-bold uppercase tracking-widest">Customer Feedback</CardTitle>
              <p className="text-[10px] text-muted-foreground font-medium uppercase mt-0.5">Recent reviews & ratings</p>
            </div>
            <button 
              onClick={() => navigate('/sales/reviews')}
              className="text-[10px] font-bold uppercase tracking-widest text-primary hover:underline"
            >
              Moderate Reviews
            </button>
          </CardHeader>
          <CardContent className="p-0 flex-1">
            {isReviewsLoading ? (
              <div className="flex items-center justify-center p-12">
                <Loader2 className="h-5 w-5 animate-spin text-muted-foreground" />
              </div>
            ) : reviewsData && reviewsData.length > 0 ? (
              <div className="divide-y divide-border">
                {reviewsData.slice(0, 5).map(review => (
                  <div key={review.id} className="p-4 hover:bg-muted/30 transition-colors flex flex-col gap-1.5">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <span className="text-xs font-bold text-foreground">{review.userName}</span>
                        <span className="text-[9px] text-muted-foreground/60">•</span>
                        <span className="text-[10px] text-muted-foreground font-medium">{review.productName}</span>
                      </div>
                      {renderStars(review.rating)}
                    </div>
                    <p className="text-[11px] text-muted-foreground line-clamp-2 italic font-medium">
                      "{review.comment}"
                    </p>
                    <div className="flex items-center justify-between mt-0.5">
                      <span className="text-[9px] text-muted-foreground/60 uppercase font-bold">
                        {new Date(review.createdAt).toLocaleDateString()}
                      </span>
                      <Badge variant={review.status === 'VISIBLE' ? 'success' : 'danger'} className="text-[8px] px-1.5 py-0 font-bold uppercase tracking-wider">
                        {review.status}
                      </Badge>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="p-8 text-center text-xs text-muted-foreground uppercase font-bold tracking-wider">
                No recent reviews found
              </div>
            )}
          </CardContent>
        </Card>
      </div>

      {/* Secondary Stats */}
      <div className="grid grid-cols-1 gap-6 md:grid-cols-3">
          <Card className="border-border shadow-sm rounded-2xl bg-card p-6 flex items-center gap-5">
              <div className="h-12 w-12 rounded-2xl bg-primary/10 flex items-center justify-center text-primary">
                  <Users className="h-6 w-6" />
              </div>
              <div>
                  <p className="text-[10px] font-bold uppercase tracking-widest text-muted-foreground">Active Customers</p>
                  <p className="text-2xl font-black">{stats.activeCustomers.toLocaleString()}</p>
              </div>
          </Card>
          <Card className="border-border shadow-sm rounded-2xl bg-card p-6 flex items-center gap-5">
              <div className="h-12 w-12 rounded-2xl bg-orange-500/10 flex items-center justify-center text-orange-500">
                  <ShoppingCart className="h-6 w-6" />
              </div>
              <div>
                  <p className="text-[10px] font-bold uppercase tracking-widest text-muted-foreground">Total Lifecycle Orders</p>
                  <p className="text-2xl font-black">{stats.totalOrders}</p>
              </div>
          </Card>
          <Card className="border-border shadow-sm rounded-2xl bg-card p-6 flex items-center gap-5">
              <div className="h-12 w-12 rounded-2xl bg-purple-500/10 flex items-center justify-center text-purple-500">
                  <TrendingUp className="h-6 w-6" />
              </div>
              <div>
                  <p className="text-[10px] font-bold uppercase tracking-widest text-muted-foreground">Market Reach</p>
                  <p className="text-2xl font-black">Global</p>
              </div>
          </Card>
      </div>
    </div>
  );
}
