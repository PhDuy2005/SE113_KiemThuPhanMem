import api from '../api/apiClient';
import { Category } from '../models/ui_types/category';

// ─── BE returns ResCategoryDTO ─────────────────────────────────
interface CategoryDto {
  id: string;
  categoryName: string;
  categoryImage?: string;
  categoryDescription?: string;
  createdAt?: string;
  updatedAt?: string;
  // Note: parentId is missing in BE, so it will be undefined for now
  parentId?: string;
}

const mapCategory = (dto: CategoryDto): Category => ({
  id: dto.id,
  name: dto.categoryName,
  parentId: dto.parentId,
  createdAt: dto.createdAt,
});

export const categoryService = {
  getCategories: async (): Promise<Category[]> => {
    try {
      const categories = await api.get<CategoryDto[]>('/categories');
      return categories.map(mapCategory);
    } catch (e) {
      console.warn("Could not fetch categories. Using mock data.");
      return [
        { id: '1', name: 'Smartphones' },
        { id: '2', name: 'Laptops' },
        { id: '3', name: 'Accessories' },
      ];
    }
  },

  createCategory: async (data: Omit<Category, 'id'>): Promise<Category> => {
    const res = await api.post<CategoryDto>('/business/categories', {
      categoryName: data.name,
      categoryImage: '', // Placeholder
      categoryDescription: '', // Placeholder
    });
    return mapCategory(res);
  },

  updateCategory: async (id: string, data: Partial<Category>): Promise<Category> => {
    const res = await api.put<CategoryDto>(`/business/categories/${id}`, {
      categoryName: data.name || '',
      categoryImage: '', // Placeholder
      categoryDescription: '', // Placeholder
    });
    return mapCategory(res);
  },

  deleteCategory: async (id: string, replacementId: string): Promise<void> => {
    const url = replacementId
      ? `/business/categories/${id}?replacementCategoryId=${replacementId}`
      : `/business/categories/${id}`;
    await api.delete(url);
  },
};
