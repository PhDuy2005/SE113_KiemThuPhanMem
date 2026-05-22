import api from '../api/apiClient';
import { Category } from '../models/ui_types/category';

// ─── BE returns Category entity directly ────────────────────
interface CategoryDto {
  id: string;
  name: string;
  parentId?: string;
  createdAt?: string;
}

const mapCategory = (dto: CategoryDto): Category => ({
  id: dto.id,
  name: dto.name,
  parentId: dto.parentId,
  createdAt: dto.createdAt,
});

export const categoryService = {
  getCategories: async (): Promise<Category[]> => {
    const categories = await api.get<CategoryDto[]>('/Category');
    return categories.map(mapCategory);
  },

  createCategory: async (data: Omit<Category, 'id'>): Promise<Category> => {
    await api.post('/Category', { name: data.name });
    // BE doesn't return the created category, refetch
    const categories = await categoryService.getCategories();
    return categories[categories.length - 1];
  },

  updateCategory: async (id: string, data: Partial<Category>): Promise<Category> => {
    // BE doesn't have an update endpoint
    // For now, throw a meaningful error
    throw new Error(`Update category is not supported by the server. Category ID: ${id}, Name: ${data.name}`);
  },

  deleteCategory: async (id: string, replacementId: string): Promise<void> => {
    await api.delete(`/Category/${id}?replacementCategoryId=${replacementId}`);
  },
};
