import { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router';
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from '../../components/ui/card';
import { Button } from '../../components/ui/button';
import { Badge } from '../../components/ui/badge';
import { Modal } from '../../components/ui/modal';
import { Trash2, Plus, Minus, Loader2 } from 'lucide-react';
import { useCreateOrder, useCheckoutSummary } from '../../dataHook/orderDataHook';
import { useGetCart, useUpdateCartQuantity, useRemoveFromCart, useClearCart } from '../../dataHook/cartDataHook';
import { toast } from 'sonner';

export function ShoppingCartPage() {
  const navigate = useNavigate();
  
  const { data: cartItems = [], isLoading, isError } = useGetCart();
  const { mutate: updateQuantity, isPending: isUpdating } = useUpdateCartQuantity();
  const { mutate: removeItem, isPending: isRemoving } = useRemoveFromCart();
  const { mutate: clearCart } = useClearCart();
  const { mutate: createOrder } = useCreateOrder();

  const [selectedProductIds, setSelectedProductIds] = useState<string[]>([]);
  const [hasInitializedSelection, setHasInitializedSelection] = useState(false);

  // Automatically select all items on initial load of the cart
  useEffect(() => {
    if (cartItems.length > 0 && !hasInitializedSelection) {
      setSelectedProductIds(cartItems.map(item => item.productId));
      setHasInitializedSelection(true);
    }
  }, [cartItems, hasInitializedSelection]);

  // Handle selection toggling
  const toggleItemSelection = (productId: string) => {
    setSelectedProductIds(prev => 
      prev.includes(productId)
        ? prev.filter(id => id !== productId)
        : [...prev, productId]
    );
  };

  const isAllSelected = cartItems.length > 0 && selectedProductIds.length === cartItems.length;

  const toggleSelectAll = () => {
    if (isAllSelected) {
      setSelectedProductIds([]);
    } else {
      setSelectedProductIds(cartItems.map(item => item.productId));
    }
  };

  const selectedItemsForSummary = useMemo(() => {
    return cartItems
      .filter(item => selectedProductIds.includes(item.productId))
      .map(i => ({ productId: i.productId, quantity: i.quantity }));
  }, [cartItems, selectedProductIds]);

  const { data: summary } = useCheckoutSummary({
    items: selectedItemsForSummary
  });

  const subtotal = summary?.subtotal || 0;
  const total = summary?.total || 0;

  useEffect(() => {
    if (isError) {
      toast.error('Failed to load cart');
    }
  }, [isError]);

  const handleCheckout = () => {
    const selectedItems = cartItems.filter(item => selectedProductIds.includes(item.productId));
    if (selectedItems.length === 0) {
      toast.error('Please select at least one item to proceed');
      return;
    }
    navigate('/customer/checkout', { state: { items: selectedItems, fromCart: true } });
  };

  const handleUpdateQuantity = (productId: string, quantity: number) => {
    updateQuantity({ productId, quantity }, {
      onError: (err: any) => toast.error(err.message || 'Failed to update quantity')
    });
  };

  const handleRemoveItem = (productId: string) => {
    removeItem(productId, {
      onSuccess: () => {
        setSelectedProductIds(prev => prev.filter(id => id !== productId));
        toast.success('Item removed');
      },
      onError: () => toast.error('Failed to remove item')
    });
  };

  const handleConfirmCheckout = () => {
    const selectedItems = cartItems.filter(item => selectedProductIds.includes(item.productId));
    if (selectedItems.length === 0) {
      toast.error('Please select at least one item to proceed');
      return;
    }
    createOrder({
      items: selectedItems,
      total,
      subtotal
    }, {
      onSuccess: () => {
        clearCart(undefined, {
          onSuccess: () => {
            toast.success('Order placed successfully!');
            navigate('/customer/orders');
          }
        });
      },
      onError: () => {
        toast.error('Failed to place order');
      }
    });
  };

  if (isLoading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  if (cartItems.length === 0) {
    return (
      <div className="space-y-6">
        <h1 className="text-3xl font-bold">Shopping Cart</h1>
        <Card>
          <CardContent className="flex flex-col items-center justify-center py-12">
            <p className="text-lg text-muted-foreground">Your cart is empty</p>
            <p className="text-sm text-muted-foreground">Add some products to get started</p>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <h1 className="text-3xl font-bold">Shopping Cart</h1>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        <div className="lg:col-span-2 space-y-4">
          {/* Select All Bar */}
          <div className="flex items-center justify-between rounded-xl border border-border bg-card p-4 shadow-sm">
            <label className="flex items-center gap-3 cursor-pointer">
              <input
                type="checkbox"
                checked={isAllSelected}
                onChange={toggleSelectAll}
                className="h-5 w-5 rounded border-border text-primary focus:ring-primary cursor-pointer transition-all"
              />
              <span className="text-sm font-bold uppercase tracking-wider text-muted-foreground select-none">
                Select All ({cartItems.length} items)
              </span>
            </label>
            {selectedProductIds.length > 0 && (
              <span className="text-xs font-bold bg-primary/10 text-primary px-3 py-1 rounded-full uppercase tracking-wider">
                Selected {selectedProductIds.length}
              </span>
            )}
          </div>

          {cartItems.map(item => (
            <Card key={item.productId} className={`transition-all duration-300 ${selectedProductIds.includes(item.productId) ? 'ring-1 ring-primary border-primary/50' : ''}`}>
              <CardContent className="flex gap-4 p-6 items-center">
                {/* Individual Checkbox */}
                <input
                  type="checkbox"
                  checked={selectedProductIds.includes(item.productId)}
                  onChange={() => toggleItemSelection(item.productId)}
                  className="h-5 w-5 rounded border-border text-primary focus:ring-primary cursor-pointer transition-all flex-shrink-0"
                />

                <img
                  src={item.imageUrl}
                  alt={item.productName}
                  className="h-24 w-24 rounded-lg object-cover"
                />
                <div className="flex-1">
                  <div className="flex items-start justify-between">
                    <div>
                      <h3 className="font-semibold">{item.productName}</h3>
                      <p className="text-sm text-muted-foreground">Product ID: {item.productId}</p>
                    </div>
                    <button
                      onClick={() => handleRemoveItem(item.productId)}
                      disabled={isRemoving}
                      className="rounded-lg p-2 text-muted-foreground hover:bg-destructive/10 hover:text-destructive disabled:opacity-50"
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                  <div className="mt-4 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <button
                        onClick={() => handleUpdateQuantity(item.productId, item.quantity - 1)}
                        disabled={item.quantity <= 1 || isUpdating}
                        className="flex h-8 w-8 items-center justify-center rounded-lg border border-border hover:bg-accent disabled:opacity-50"
                      >
                        <Minus className="h-4 w-4" />
                      </button>
                      <span className="w-12 text-center">{item.quantity}</span>
                      <button
                        onClick={() => handleUpdateQuantity(item.productId, item.quantity + 1)}
                        disabled={isUpdating}
                        className="flex h-8 w-8 items-center justify-center rounded-lg border border-border hover:bg-accent disabled:opacity-50"
                      >
                        <Plus className="h-4 w-4" />
                      </button>
                    </div>
                    <div className="text-xl font-bold">
                      ${((item.price || 0) * item.quantity).toLocaleString()}
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        <div>
          <Card>
            <CardHeader>
              <CardTitle>Order Summary</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex justify-between">
                <span className="text-muted-foreground">Subtotal</span>
                <span>${subtotal.toFixed(2)}</span>
              </div>
              <div className="border-t border-border pt-4">
                <div className="flex justify-between">
                  <span className="font-semibold">Total</span>
                  <span className="text-xl font-bold">${total.toFixed(2)}</span>
                </div>
              </div>
            </CardContent>
            <CardFooter>
              <Button onClick={handleCheckout} className="w-full" disabled={selectedProductIds.length === 0}>
                Proceed to Checkout
              </Button>
            </CardFooter>
          </Card>
        </div>
      </div>

    </div>
  );
}
