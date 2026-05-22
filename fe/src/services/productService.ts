import api from '../api/apiClient';
import { Product, ProductStatus } from '../models/ui_types/product';

// ─── BE Response Types ──────────────────────────────────────
interface ResProductDTO {
  id: string;
  name: string;
  description: string;
  price: number;
  status: string;
  brand: string;
  categoryId: string;
  stock: number;
  imageUrls: string[];
  message: string;
}

// ─── Mapping BE → FE ────────────────────────────────────────
const mapProduct = (dto: ResProductDTO): Product => ({
  id: dto.id,
  name: dto.name,
  description: dto.description,
  price: dto.price,
  brand: dto.brand,
  categoryId: dto.categoryId,
  imageUrl: dto.imageUrls?.[0] || '',
  images: dto.imageUrls || [],
  stock: dto.stock || 0,
  status: dto.status as ProductStatus,
  rating: 0, // BE no longer returns rating
  createdAt: '',
});

// ─── Public Interface ──────────────────────────────────────
export interface ProductQueryParams {
  search?: string;
  categoryIds?: string;
  minPrice?: number;
  maxPrice?: number;
  brand?: string;
  sortBy?: 'price_asc' | 'price_desc' | 'rating' | 'newest';
  limit?: number;
}

export const productService = {
  getProducts: async (params?: ProductQueryParams): Promise<Product[]> => {
    if (params?.search) {
      const res = await api.get<ResProductDTO[]>(`/products/search?keyword=${params.search}&limit=${params.limit || 100}`);
      return res.map(mapProduct);
    }

    const query = new URLSearchParams();
    if (params?.categoryIds && params.categoryIds !== 'all') {
      query.set('categoryIds', params.categoryIds);
    }
    
    if (params?.sortBy) {
      const sortMap: Record<string, string> = {
        price_asc: 'asc',
        price_desc: 'desc',
      };
      const mappedSort = sortMap[params.sortBy];
      if (mappedSort) {
        query.set('sortPrice', mappedSort);
      }
    }

    const queryStr = query.toString();
    const products = await api.get<ResProductDTO[]>(
      `/products${queryStr ? `?${queryStr}` : ''}`,
    );

    let result = products.map(mapProduct);

    // Client-side filtering for params not supported by BE
    if (params?.minPrice !== undefined) {
      result = result.filter(p => p.price >= params.minPrice!);
    }
    if (params?.maxPrice !== undefined && params.maxPrice > 0) {
      result = result.filter(p => p.price <= params.maxPrice!);
    }
    if (params?.brand && params.brand !== 'all') {
      const brandQuery = params.brand.toLowerCase();
      result = result.filter(p => p.brand?.toLowerCase().includes(brandQuery));
    }
    if (params?.limit) {
      result = result.slice(0, params.limit);
    }

    return result;
  },

  getAdminProducts: async (params?: { keyword?: string; categoryId?: string; status?: ProductStatus; pageNumber?: number; pageSize?: number }): Promise<{ items: Product[], totalCount: number }> => {
    // API missing in BE. Assumes an admin endpoint would be like /business/products
    // Currently fallback to public getProducts since there is no admin listing API.
    const products = await productService.getProducts({ search: params?.keyword });
    return {
      items: products,
      totalCount: products.length
    };
  },

  getProductById: async (id: string): Promise<Product> => {
    const dto = await api.get<ResProductDTO>(`/products/${id}`);
    return mapProduct(dto);
  },

  getAllBrands: async (): Promise<string[]> => {
    const products = await productService.getProducts();
    const brands = products.map(p => p.brand).filter(Boolean);
    return Array.from(new Set(brands));
  },

  createProduct: async (product: any): Promise<Product> => {
    const formData = new FormData();
    formData.append('name', product.name);
    if (product.description) formData.append('description', product.description);
    formData.append('price', product.price.toString());
    formData.append('brand', product.brand || '');
    formData.append('categoryId', product.categoryId);
    formData.append('stock', (product.stock || 0).toString());

    if (product.imageFiles && product.imageFiles.length > 0) {
      for (let i = 0; i < product.imageFiles.length; i++) {
        formData.append('images', product.imageFiles[i]);
      }
    }

    const result = await api.post<ResProductDTO>('/business/products', formData);
    return mapProduct(result);
  },

  updateProduct: async (id: string, productData: Partial<Product>): Promise<Product> => {
    // NOTE: BE only supports updating price and stock. Update general info is missing.
    if (productData.price !== undefined) {
      await api.patch(`/business/products/${id}/price`, { newPrice: productData.price });
    }
    if (productData.stock !== undefined) {
      await api.patch(`/business/products/${id}/stock`, { newStock: productData.stock });
    }
    return productService.getProductById(id);
  },

  deleteProduct: async (id: string): Promise<void> => {
    await productService.toggleProductDiscontinue(id);
  },

  toggleProductDiscontinue: async (id: string): Promise<Product> => {
    await api.patch(`/business/products/${id}/discontinue`);
    return {
      id,
      status: ProductStatus.DISCONTINUED,
    } as Product;
  },
  
  updateInventory: async (id: string, value: number, type: 'ADD' | 'SET'): Promise<void> => {
    // In BE, update stock takes the new absolute value.
    if (type === 'SET') {
      await api.patch(`/business/products/${id}/stock`, { newStock: value });
    } else {
      const current = await productService.getProductById(id);
      await api.patch(`/business/products/${id}/stock`, { newStock: current.stock + value });
    }
  },
};
