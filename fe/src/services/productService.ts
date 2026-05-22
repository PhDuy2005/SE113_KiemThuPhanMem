import api from '../api/apiClient';
import { Product, ProductStatus } from '../models/ui_types/product';

// ─── BE Response Types ──────────────────────────────────────
interface ProductImageDto {
  id: string;
  imageUrl: string;
  isPrimary: boolean;
}

interface ProductResponseDto {
  id: string;
  name: string;
  description: string;
  price: number;
  brand: string;
  categoryId: string;
  images: ProductImageDto[];
  stockStatus?: string;
  availableQuantity?: number;
  inventory?: { availableQuantity: number };
  status: ProductStatus;
  rating: number;
}

interface ProductDetailDto extends ProductResponseDto {
  stockStatus: string;
  availableQuantity: number;
}

// ─── Mapping BE → FE ────────────────────────────────────────
const mapProduct = (dto: ProductResponseDto): Product => ({
  id: dto.id,
  name: dto.name,
  description: dto.description,
  price: dto.price,
  brand: dto.brand,
  categoryId: dto.categoryId,
  imageUrl: dto.images?.find(img => img.isPrimary)?.imageUrl || dto.images?.[0]?.imageUrl,
  images: dto.images?.map(img => img.imageUrl),
  stock: dto.availableQuantity ?? dto.inventory?.availableQuantity ?? 0,
  status: dto.status,
  rating: dto.rating,
  createdAt: '',
});

const mapProductDetail = (dto: ProductDetailDto): Product => ({
  ...mapProduct(dto),
});

// ─── Public Interface (signatures kept compatible) ──────────
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
    const query = new URLSearchParams();

    if (params?.search) query.set('keyword', params.search);
    if (params?.categoryIds && params.categoryIds !== 'all') {
      query.set('categoryIds', params.categoryIds);
    }

    console.log(params);
    
    if (params?.sortBy) {
      // Map FE sort to BE SortOrder enum (ASC=1, DESC=2)
      // Note: BE only supports price sorting via ASC/DESC in SearchProductsAsync
      const sortMap: Record<string, string> = {
        price_asc: 'ASC',
        price_desc: 'DESC',
      };
      
      const mappedSort = sortMap[params.sortBy];
      if (mappedSort) {
        query.set('sortOrder', mappedSort);
      }
    }

    const queryStr = query.toString();
    const products = await api.get<ProductResponseDto[]>(
      `/product${queryStr ? `?${queryStr}` : ''}`,
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
    const query = new URLSearchParams();
    if (params?.keyword) query.set('keyword', params.keyword);
    if (params?.categoryId) query.set('categoryId', params.categoryId);
    if (params?.status) query.set('status', params.status);
    if (params?.pageNumber) query.set('pageNumber', params.pageNumber.toString());
    if (params?.pageSize) query.set('pageSize', params.pageSize.toString());

    const queryStr = query.toString();
    const response = await api.get<{ items: ProductResponseDto[], totalCount: number }>(
      `/admin/products${queryStr ? `?${queryStr}` : ''}`,
    );

    return {
      items: response.items.map(mapProduct),
      totalCount: response.totalCount
    };
  },

  getProductById: async (id: string): Promise<Product> => {
    const dto = await api.get<ProductDetailDto>(`/product/${id}`);
    return mapProductDetail(dto);
  },

  getAllBrands: async (): Promise<string[]> => {
    // BE doesn't have a dedicated brands endpoint
    // Derive from product list
    const products = await api.get<ProductResponseDto[]>('/product');
    const brands = products.map(p => p.brand).filter(Boolean);
    return Array.from(new Set(brands));
  },

  createProduct: async (product: any): Promise<Product> => {
    const formData = new FormData();
    formData.append('name', product.name);
    formData.append('description', product.description);
    formData.append('price', product.price.toString());
    formData.append('brand', product.brand);
    formData.append('categoryId', product.categoryId);
    formData.append('initialStock', (product.stock || 0).toString());

    if (product.imageFiles && product.imageFiles.length > 0) {
      for (let i = 0; i < product.imageFiles.length; i++) {
        formData.append('imageFiles', product.imageFiles[i]);
      }
    }

    const result = await api.post<ProductResponseDto>('/admin/products', formData);
    return mapProduct(result);
  },

  updateProduct: async (id: string, productData: Partial<Product>): Promise<Product> => {
    await api.put(`/admin/products/${id}`, {
      name: productData.name,
      description: productData.description,
      price: productData.price,
      brand: productData.brand,
      categoryId: productData.categoryId,
      images: productData.images?.map((url, i) => ({
        imageUrl: url,
        isPrimary: i === 0,
      })) || [],
    });
    // Refetch to get updated product
    return productService.getProductById(id);
  },

  deleteProduct: async (id: string): Promise<void> => {
    // BE only supports discontinue, not delete
    await api.patch(`/admin/products/${id}/status`);
  },

  toggleProductDiscontinue: async (id: string): Promise<Product> => {
    await api.patch(`/admin/products/${id}/status`);
    // Return a dummy object with the updated status to satisfy the mutation signature
    // and avoid calling the public GET endpoint which would return 404 for discontinued products.
    return {
      id,
      status: ProductStatus.DISCONTINUED,
    } as Product;
  },
  updateInventory: async (id: string, value: number, type: 'ADD' | 'SET'): Promise<void> => {
    await api.patch(`/admin/products/${id}/inventory`, {
      value,
      type: type === 'ADD' ? 1 : 2 // Map to BE enum values
    });
  },
};
