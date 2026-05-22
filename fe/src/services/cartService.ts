import api from '../api/apiClient';
import { CartItem } from '../models/ui_types/cart';

// ─── BE Response Types ──────────────────────────────────────
// Expected from the missing GET /cart API
interface CartProductDto {
  id: string;
  name: string;
  brand: string;
  price: number;
  images: { id: string; imageUrl: string; isPrimary: boolean }[];
}

interface CartItemDto {
  productId: string;
  quantity: number;
  createdAt: string;
  updatedAt: string;
  product: CartProductDto | null;
}

interface CartResponseDto {
  userId: string;
  items: CartItemDto[];
  totalPrice: number;
  totalItemsCount: number;
}

// ─── Mapping BE → FE ────────────────────────────────────────
const mapCartItems = (dto: CartResponseDto): CartItem[] => {
  return dto.items.map(item => ({
    cartId: dto.userId,
    productId: item.productId,
    quantity: item.quantity,
    createdAt: item.createdAt,
    updatedAt: item.updatedAt,
    productName: item.product?.name,
    price: item.product?.price,
    imageUrl: item.product?.images?.find(img => img.isPrimary)?.imageUrl
      || item.product?.images?.[0]?.imageUrl,
  }));
};

export const cartService = {
  getCart: async (): Promise<CartItem[]> => {
    // API missing in BE. Assumes it will be at /cart
    const cart = await api.get<CartResponseDto>('/cart');
    return mapCartItems(cart);
  },

  addToCart: async (productId: string): Promise<CartItem[]> => {
    await api.post('/cart/items', {
      productId,
      quantity: 1,
    });
    // Refetch cart to get updated state
    return cartService.getCart();
  },

  updateQuantity: async (productId: string, quantity: number): Promise<CartItem[]> => {
    if (quantity < 1) {
      // Remove item if quantity drops below 1
      return cartService.removeItem(productId);
    }
    await api.put(`/cart/items/${productId}`, { newQuantity: quantity });
    return cartService.getCart();
  },

  removeItem: async (productId: string): Promise<CartItem[]> => {
    await api.delete(`/cart/items/${productId}`);
    return cartService.getCart();
  },

  clearCart: async (): Promise<void> => {
    // API missing in BE. Assumes it will be at /cart
    await api.delete('/cart');
  },
};
