import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Badge } from '../../components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../../components/ui/table';
import { useGetOrders } from '../../../dataHook/orderDataHook';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../../components/ui/select";
import { Loader2, Eye } from 'lucide-react';
import { useNavigate } from 'react-router';
import { Button } from '../../components/ui/button';

export function OrderHistoryPage() {
  const navigate = useNavigate();
  const { data: orders = [], isLoading } = useGetOrders();

  const getStatusVariant = (status: string) => {
    switch (status) {
      case 'DELIVERED': return 'success';
      case 'SHIPPING': return 'info';
      case 'APPROVED': return 'warning';
      case 'PENDING': return 'pending';
      case 'CANCELLED': return 'danger';
      default: return 'default';
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold">Order History</h1>
        <p className="text-muted-foreground">View your past orders</p>
      </div>

      {isLoading ? (
        <div className="flex h-64 items-center justify-center">
          <div className="flex flex-col items-center gap-2">
            <Loader2 className="h-8 w-8 animate-spin text-primary" />
            <p className="text-muted-foreground">Loading orders...</p>
          </div>
        </div>
      ) : (
        <Card>
          <CardHeader>
            <CardTitle>Your Orders</CardTitle>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Order ID</TableHead>
                  <TableHead>Date</TableHead>
                  <TableHead>Items</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Total</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                 {orders.map(order => (
                  <TableRow key={order.id}>
                    <TableCell className="font-medium">{order.id}</TableCell>
                    <TableCell>{new Date(order.createdAt).toLocaleDateString()}</TableCell>
                    <TableCell>
                      <div className="text-sm">
                        {(order.items || []).slice(0, 2).map((item, idx) => (
                          <div key={idx}>{item.productName} x{item.quantity}</div>
                        ))}
                        {(order.items || []).length > 2 && (
                          <div className="text-muted-foreground">+{(order.items || []).length - 2} more</div>
                        )}
                      </div>
                    </TableCell>
                    <TableCell>
                      <Badge variant={getStatusVariant(order.status)}>
                        {order.status}
                      </Badge>
                    </TableCell>
                    <TableCell className="font-semibold">${(order.totalAmount || 0).toLocaleString()}</TableCell>
                    <TableCell>
                      <Button variant="ghost" size="sm" onClick={() => navigate(`/customer/orders/${order.id}`)}>
                        <Eye className="h-4 w-4" />
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
