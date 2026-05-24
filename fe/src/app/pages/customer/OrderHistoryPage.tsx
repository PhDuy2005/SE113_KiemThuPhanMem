import { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '../../components/ui/card';
import { Badge } from '../../components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '../../components/ui/table';
import { useGetOrders } from '../../../dataHook/orderDataHook';
import { Loader2, Eye, ChevronLeft, ChevronRight } from 'lucide-react';
import { useNavigate } from 'react-router';
import { Button } from '../../components/ui/button';

export function OrderHistoryPage() {
  const navigate = useNavigate();
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [pageInput, setPageInput] = useState('1');
  const [pageSizeInput, setPageSizeInput] = useState('10');
  
  const { data, isLoading } = useGetOrders(page, pageSize);
  const orders = data?.items || [];
  const totalPages = data?.totalPages || 1;
  const totalItems = data?.totalCount || 0;

  // Sync pageInput with page state
  useEffect(() => {
    setPageInput(String(page));
  }, [page]);

  // Sync pageSizeInput with pageSize state
  useEffect(() => {
    setPageSizeInput(String(pageSize));
  }, [pageSize]);

  // Debounce page size input changes
  useEffect(() => {
    const num = Number(pageSizeInput);
    if (isNaN(num) || num <= 0) return;
    
    const handler = setTimeout(() => {
      if (num !== pageSize) {
        setPageSize(num);
        setPage(1); // Reset to page 1
      }
    }, 500); // 500ms delay
    
    return () => clearTimeout(handler);
  }, [pageSizeInput, pageSize]);

  // Debounce page input changes
  useEffect(() => {
    const num = Number(pageInput);
    if (isNaN(num) || num <= 0 || num > totalPages) return;
    
    const handler = setTimeout(() => {
      if (num !== page) {
        setPage(num);
      }
    }, 500); // 500ms delay
    
    return () => clearTimeout(handler);
  }, [pageInput, page, totalPages]);

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
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight uppercase">Order History</h1>
          <p className="text-xs text-muted-foreground uppercase font-bold tracking-widest opacity-60">View and track your past orders</p>
        </div>
      </div>

      {isLoading ? (
        <div className="flex h-64 items-center justify-center bg-card border border-border rounded-2xl shadow-sm">
          <div className="flex flex-col items-center gap-2">
            <Loader2 className="h-8 w-8 animate-spin text-primary" />
            <p className="text-[10px] text-muted-foreground font-bold uppercase tracking-widest">Retrieving Purchase History...</p>
          </div>
        </div>
      ) : (
        <Card className="border-border shadow-sm rounded-2xl overflow-hidden bg-card">
          <CardHeader className="border-b border-border bg-muted/20">
            <CardTitle className="text-xs font-bold uppercase tracking-widest">Your Orders ({totalItems})</CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow className="bg-muted/10">
                  <TableHead className="text-[10px] font-bold uppercase tracking-wider py-4 pl-6">Order ID</TableHead>
                  <TableHead className="text-[10px] font-bold uppercase tracking-wider py-4">Date</TableHead>
                  <TableHead className="text-[10px] font-bold uppercase tracking-wider py-4">Status</TableHead>
                  <TableHead className="text-[10px] font-bold uppercase tracking-wider py-4">Total</TableHead>
                  <TableHead className="text-[10px] font-bold uppercase tracking-wider py-4 pr-6 text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {orders.length > 0 ? (
                  orders.map(order => (
                    <TableRow key={order.id} className="hover:bg-muted/20 transition-colors">
                      <TableCell className="font-bold py-4 pl-6 text-xs uppercase tracking-tight text-foreground">{order.id}</TableCell>
                      <TableCell className="text-xs py-4 text-muted-foreground font-medium">{new Date(order.createdAt).toLocaleDateString()}</TableCell>
                      <TableCell className="py-4">
                        <Badge variant={getStatusVariant(order.status)} className="text-[9px] px-2 py-0.5 font-bold uppercase tracking-wider">
                          {order.status}
                        </Badge>
                      </TableCell>
                      <TableCell className="font-bold py-4 text-xs text-foreground">${(order.totalAmount || 0).toLocaleString()}</TableCell>
                      <TableCell className="py-4 pr-6 text-right">
                        <Button 
                          variant="ghost" 
                          size="sm" 
                          onClick={() => navigate(`/customer/orders/${order.id}`)}
                          className="h-8 w-8 p-0 rounded-full hover:bg-primary hover:text-primary-foreground transition-all"
                        >
                          <Eye className="h-4 w-4" />
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))
                ) : (
                  <TableRow>
                    <TableCell colSpan={5} className="text-center py-12 text-xs text-muted-foreground uppercase font-bold tracking-widest">
                      No past orders found
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>

            {/* Premium Pagination Section */}
            <div className="flex flex-col sm:flex-row items-center justify-between gap-4 p-4 border-t border-border bg-muted/10">
              {/* Left Side: Page Size Selector & Total Records */}
              <div className="flex items-center gap-4">
                <span className="text-[10px] text-muted-foreground uppercase font-black tracking-widest">
                  Total: {totalItems} orders
                </span>
                
                <div className="flex items-center gap-2">
                  <span className="text-[10px] text-muted-foreground uppercase font-black tracking-widest">
                    Page Size:
                  </span>
                  <input
                    type="number"
                    value={pageSizeInput}
                    min={1}
                    max={100}
                    onChange={(e) => setPageSizeInput(e.target.value)}
                    className="w-16 h-8 text-xs font-bold text-center border border-border rounded-xl bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all"
                  />
                </div>
              </div>

              {/* Right Side: Prev, Quick Jump Input, and Next */}
              <div className="flex items-center gap-3">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setPage(prev => Math.max(prev - 1, 1))}
                  disabled={page === 1}
                  className="h-8 px-3 rounded-xl border-border bg-card hover:bg-accent text-xs font-bold uppercase tracking-wider gap-1"
                >
                  <ChevronLeft className="h-3 w-3" />
                  Prev
                </Button>

                <div className="flex items-center gap-2">
                  <span className="text-[10px] text-muted-foreground uppercase font-black tracking-widest">
                    Page
                  </span>
                  <input
                    type="number"
                    value={pageInput}
                    min={1}
                    max={totalPages}
                    onChange={(e) => setPageInput(e.target.value)}
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') {
                        const val = Math.max(1, Math.min(totalPages, Number(pageInput)));
                        setPage(val);
                        setPageInput(String(val));
                      }
                    }}
                    onBlur={() => {
                      const val = Math.max(1, Math.min(totalPages, Number(pageInput)));
                      setPage(val);
                      setPageInput(String(val));
                    }}
                    className="w-12 h-8 text-xs font-bold text-center border border-border rounded-xl bg-card text-foreground focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all"
                  />
                  <span className="text-[10px] text-muted-foreground uppercase font-black tracking-widest">
                    of {totalPages}
                  </span>
                </div>

                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setPage(prev => Math.min(prev + 1, totalPages))}
                  disabled={page >= totalPages}
                  className="h-8 px-3 rounded-xl border-border bg-card hover:bg-accent text-xs font-bold uppercase tracking-wider gap-1"
                >
                  Next
                  <ChevronRight className="h-3 w-3" />
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
