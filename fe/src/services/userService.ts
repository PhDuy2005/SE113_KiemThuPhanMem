import api, { PagedResponse } from '../api/apiClient';
import { User, UserRole, UserStatus } from '../models/ui_types/user';
import { AuditLog } from '../models/ui_types/auditLog';

// ─── BE Response Types ──────────────────────────────────────
interface UserDto {
  id: string;
  email: string;
  status: UserStatus;
  createdAt: string;
  roles: string[];
  profile: {
    fullName: string;
    phone: string;
    avatarUrl?: string;
    dateOfBirth?: string;
  } | null;
}

// ─── Role Mapping ───────────────────────────────────────────
const mapRole = (roles: any[] | null | undefined): UserRole => {
  if (!roles || !Array.isArray(roles)) return 'Customer';
  const roleNames = roles.map(r => {
    if (!r) return '';
    return typeof r === 'string' ? r : (r.name || '');
  });
  if (roleNames.includes('Technical Admin') || roleNames.includes('Admin')) return 'Technical Admin';
  if (roleNames.includes('Business Admin')) return 'Business Admin';
  if (roleNames.includes('Staff')) return 'Staff';
  return 'Customer';
};

const mapUser = (dto: any): User => ({
  id: dto.id,
  email: dto.email,
  status: dto.status,
  createdAt: dto.createdAt,
  fullName: dto.profile?.fullName || dto.fullName || dto.email.split('@')[0],
  phone: dto.profile?.phone || dto.phone || '',
  avatarUrl: dto.profile?.avatarUrl,
  dateOfBirth: dto.profile?.dateOfBirth,
  role: mapRole(dto.roles),
});

export const userService = {
  getUsers: async (): Promise<User[]> => {
    try {
      const [customersPaged, staffPaged] = await Promise.all([
        api.get<PagedResponse<UserDto>>('/admin/users/customers?pageNumber=1&pageSize=100'),
        api.get<PagedResponse<UserDto>>('/admin/users/staff?pageNumber=1&pageSize=100')
      ]);
      const customers = customersPaged.items.map(mapUser);
      const staff = staffPaged.items.map(mapUser);
      const allUsers = [...customers, ...staff];
      const seen = new Set();
      return allUsers.filter(u => {
        const duplicate = seen.has(u.id);
        seen.add(u.id);
        return !duplicate;
      });
    } catch {
      const paged = await api.get<PagedResponse<UserDto>>(
        '/admin/users/customers?pageNumber=1&pageSize=100',
      );
      return paged.items.map(mapUser);
    }
  },

  getCustomers: async (): Promise<User[]> => {
    const paged = await api.get<PagedResponse<UserDto>>(
      '/admin/users/customers?pageNumber=1&pageSize=100',
    );
    return paged.items.map(mapUser);
  },

  getStaff: async (): Promise<User[]> => {
    const paged = await api.get<PagedResponse<UserDto>>(
      '/admin/users/staff?pageNumber=1&pageSize=100',
    );
    return paged.items.map(mapUser);
  },

  getUserById: async (_id: string): Promise<User> => {
    // BE doesn't have a get-user-by-id admin endpoint
    // Use current user endpoint as fallback
    const dto = await api.get<UserDto>('/user/me');
    return mapUser(dto);
  },

  createUser: async (userData: Omit<User, 'id' | 'createdAt'>): Promise<User> => {
    // Map UI role back to BE role string
    const beRole = userData.role === 'Technical Admin' ? 'Technical Admin' : 
                   userData.role === 'Business Admin' ? 'Business Admin' : 'Staff';

    const dto = await api.post<UserDto>('/admin/users', {
      email: userData.email,
      password: userData.password || 'TemporaryPassword123!',
      roles: [beRole]
    });
    return mapUser(dto);
  },

  updateUser: async (id: string, userData: Partial<User>): Promise<User> => {
    // This is for admin updates of any user
    const dto = await api.put<UserDto>(`/admin/users/${id}`, userData);
    return mapUser(dto);
  },

  updateProfile: async (data: {
    fullName?: string;
    phone?: string;
    avatarUrl?: string;
    dateOfBirth?: string;
    avatarFile?: File;
  }): Promise<void> => {
    const formData = new FormData();
    if (data.fullName !== undefined && data.fullName !== null) formData.append('fullName', data.fullName);
    if (data.phone !== undefined && data.phone !== null) formData.append('phone', data.phone);
    if (data.avatarUrl !== undefined && data.avatarUrl !== null) formData.append('avatarUrl', data.avatarUrl);
    if (data.dateOfBirth !== undefined && data.dateOfBirth !== null) formData.append('dateOfBirth', data.dateOfBirth);
    if (data.avatarFile !== undefined && data.avatarFile !== null) formData.append('avatarFile', data.avatarFile);

    await api.put('/user-profile', formData);
  },

  toggleUserStatus: async (id: string, currentStatus: UserStatus): Promise<User> => {
    const isBlocking = currentStatus !== UserStatus.BLOCKED;
    if (!isBlocking) {
      await api.post(`/admin/users/${id}/unlock`);
    } else {
      await api.post(`/admin/users/${id}/lock`, { until: null });
    }

    // Try to find in customers first
    const customers = await userService.getCustomers();
    let updated = customers.find(u => u.id === id);
    
    // If not in customers, try staff
    if (!updated) {
      const staff = await userService.getStaff();
      updated = staff.find(u => u.id === id);
    }

    if (!updated) {
      // Fallback: return manual update if refetch fails to find user (should not happen usually)
      return {
        id,
        status: isBlocking ? UserStatus.BLOCKED : UserStatus.ACTIVE,
        // These fields might be stale but at least we don't throw
        email: '', 
        fullName: '',
        role: 'Customer',
        createdAt: new Date().toISOString()
      } as User;
    }
    return updated;
  },

  getAuditLogs: async (pageNumber = 1, pageSize = 50): Promise<PagedResponse<AuditLog>> => {
    return await api.get<PagedResponse<AuditLog>>(
      `/admin/audit-logs?pageNumber=${pageNumber}&pageSize=${pageSize}`
    );
  },
};
