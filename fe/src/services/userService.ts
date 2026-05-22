import api from '../api/apiClient';
import { User, UserRole, UserStatus } from '../models/ui_types/user';
import { AuditLog } from '../models/ui_types/auditLog';

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

// ─── BE Response Types ──────────────────────────────────────
interface ResUserDTO {
  id: string;
  email: string;
  userFullName: string;
  avatarUrl?: string;
  status: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  role: {
    id: string;
    name: string;
  } | null;
}

interface UserGetAccountDto {
  user: {
    id: string;
    email: string;
    name: string;
    phoneNumber?: string;
  };
  role: {
    roleId: number;
    roleName: string;
  } | null;
}

// ─── Role Mapping ───────────────────────────────────────────
const mapRole = (roleName: string | undefined): UserRole => {
  if (!roleName) return 'Customer';
  if (roleName === 'Technical Admin' || roleName === 'Admin') return 'Technical Admin';
  if (roleName === 'Business Admin' || roleName === 'BUSINESS_ADMIN') return 'BUSINESS_ADMIN';
  if (roleName === 'Staff') return 'Staff';
  return 'Customer';
};

const mapUser = (dto: ResUserDTO): User => ({
  id: dto.id,
  email: dto.email,
  status: (dto.status as UserStatus) || UserStatus.ACTIVE,
  createdAt: '', // Missing in DTO
  fullName: dto.userFullName || dto.email.split('@')[0],
  phone: dto.phoneNumber || '',
  avatarUrl: dto.avatarUrl,
  dateOfBirth: dto.dateOfBirth,
  role: mapRole(dto.role?.name),
});

export const userService = {
  getUsers: async (): Promise<User[]> => {
    try {
      // NOTE: BE currently does not have an API to list staff. We only fetch customers for now.
      const customersPaged = await api.get<ResultPaginationDTO<ResUserDTO>>('/business/customers?pageNumber=1&pageSize=100');
      
      const customers = customersPaged.result.map(mapUser);
      return customers;
    } catch {
      return [];
    }
  },

  getCustomers: async (): Promise<User[]> => {
    const paged = await api.get<ResultPaginationDTO<ResUserDTO>>(
      '/business/customers?pageNumber=1&pageSize=100',
    );
    return paged.result.map(mapUser);
  },

  getStaff: async (): Promise<User[]> => {
    // API missing in BE. Assumes an endpoint like GET /business/staff
    return [];
  },

  getUserById: async (_id: string): Promise<User> => {
    // BE doesn't have a get-user-by-id admin endpoint
    // Use current user endpoint as fallback
    const dto = await api.get<UserGetAccountDto>('/auth/account');
    return {
      id: dto.user.id,
      email: dto.user.email,
      fullName: dto.user.name, // Map 'name' from BE to 'fullName'
      phone: dto.user.phoneNumber || '', // Map 'phoneNumber' from BE
      role: mapRole(dto.role?.roleName), // Map 'roleName' from BE
      status: UserStatus.ACTIVE,
      createdAt: '',
    };
  },

  createUser: async (userData: Omit<User, 'id' | 'createdAt'>): Promise<User> => {
    const beRole = userData.role === 'Technical Admin' ? 'Technical Admin' : 
                   userData.role === 'Business Admin' ? 'Business Admin' : 'Staff';

    // BE expects creating staff specifically
    const dto = await api.post<ResUserDTO>('/business/staff', {
      email: userData.email,
      fullName: userData.fullName,
      password: userData.password || 'TemporaryPassword123!',
      phoneNumber: userData.phone,
      roleName: beRole,
    });
    return mapUser(dto);
  },

  updateUser: async (id: string, userData: Partial<User>): Promise<User> => {
    // API missing in BE. There is no endpoint for admin to update a user's details.
    return userService.getUserById(id);
  },

  updateProfile: async (data: {
    fullName?: string;
    phone?: string;
    avatarUrl?: string;
    dateOfBirth?: string;
    avatarFile?: File;
  }): Promise<void> => {
    // Using the authenticated user's profile update endpoint
    await api.put('/auth/profile', {
      fullName: data.fullName,
      phoneNumber: data.phone,
    });
  },

  toggleUserStatus: async (id: string, currentStatus: UserStatus): Promise<User> => {
    // Based on user role, we need to call lock/block endpoint
    // Assuming staff lock or customer block
    try {
      // Attempt to block customer
      const res = await api.patch<ResUserDTO>(`/business/customers/${id}/block-fraud`);
      return mapUser(res);
    } catch {
      // Attempt to lock staff
      const res = await api.patch<ResUserDTO>(`/business/staff/${id}/lock`);
      return mapUser(res);
    }
  },

  getAuditLogs: async (pageNumber = 1, pageSize = 50): Promise<{ items: AuditLog[], totalCount: number }> => {
    const res = await api.get<ResultPaginationDTO<AuditLog>>(
      `/business/audit-logs?pageNumber=${pageNumber}&pageSize=${pageSize}`
    );
    return {
      items: res.result,
      totalCount: res.meta.totalItems,
    };
  },
};
