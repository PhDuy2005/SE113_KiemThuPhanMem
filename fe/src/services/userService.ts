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
  name: string;
  avatarUrl?: string;
  accountStatus: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  created_at?: string;
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

const mapRole = (roleName: string | undefined): UserRole => {
  if (!roleName) return 'Customer';
  const norm = roleName.trim().toUpperCase();
  if (norm === 'TECHNICAL ADMIN' || norm === 'ADMIN' || norm === 'TECHNICAL_ADMIN') return 'Technical Admin';
  if (norm === 'BUSINESS ADMIN' || norm === 'BUSINESS_ADMIN') return 'BUSINESS_ADMIN';
  if (norm === 'STAFF') return 'Staff';
  return 'Customer';
};

const mapUser = (dto: ResUserDTO): User => {
  let mappedStatus = UserStatus.ACTIVE;
  if (dto.accountStatus === 'LOCKED') {
    mappedStatus = UserStatus.BLOCKED;
  } else if (dto.accountStatus === 'PENDING') {
    mappedStatus = UserStatus.PENDING;
  } else if (dto.accountStatus === 'ACTIVE') {
    mappedStatus = UserStatus.ACTIVE;
  }

  return {
    id: dto.id,
    email: dto.email,
    status: mappedStatus,
    createdAt: dto.created_at || '',
    fullName: dto.name || dto.email.split('@')[0],
    phone: dto.phoneNumber || '',
    avatarUrl: dto.avatarUrl,
    dateOfBirth: dto.dateOfBirth,
    role: mapRole(dto.role?.name),
  };
};

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

  getCustomers: async (pageNumber = 1, pageSize = 10): Promise<{ items: User[], totalCount: number, totalPages: number, pageNumber: number, pageSize: number }> => {
    const paged = await api.get<ResultPaginationDTO<ResUserDTO>>(
      `/business/customers?pageNumber=${pageNumber}&pageSize=${pageSize}`,
    );
    return {
      items: paged.result.map(mapUser),
      totalCount: paged.meta.totalItems,
      totalPages: paged.meta.totalPages || 1,
      pageNumber: paged.meta.page || 1,
      pageSize: paged.meta.pageSize || 10,
    };
  },

  getStaff: async (pageNumber = 1, pageSize = 10): Promise<{ items: User[], totalCount: number, totalPages: number, pageNumber: number, pageSize: number }> => {
    const paged = await api.get<ResultPaginationDTO<ResUserDTO>>(
      `/business/staff?pageNumber=${pageNumber}&pageSize=${pageSize}`,
    );
    return {
      items: paged.result.map(mapUser),
      totalCount: paged.meta.totalItems,
      totalPages: paged.meta.totalPages || 1,
      pageNumber: paged.meta.page || 1,
      pageSize: paged.meta.pageSize || 10,
    };
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
    // BE expects ReqCreateStaffDTO specifically: { email, fullName, roleId }
    const dto = await api.post<ResUserDTO>('/business/staff', {
      email: userData.email,
      fullName: userData.fullName,
      roleId: 2, // 2 is the auto-incremented seed ID for the STAFF role
    });
    return mapUser(dto);
  },

  updateUser: async (id: string, userData: Partial<User>): Promise<User> => {
    const beRole = userData.role === 'Technical Admin' ? 'TECHNICAL_ADMIN' : 
                   userData.role === 'BUSINESS_ADMIN' ? 'BUSINESS_ADMIN' : 
                   userData.role === 'Staff' ? 'STAFF' : 
                   userData.role === 'Customer' ? 'CUSTOMER' : undefined;

    const beRoleId = userData.role === 'Customer' ? 1 :
                     userData.role === 'Staff' ? 2 :
                     userData.role === 'BUSINESS_ADMIN' ? 3 : undefined;

    const dto = await api.put<ResUserDTO>(`/business/users/${id}`, {
      fullName: userData.fullName,
      phoneNumber: userData.phone,
      roleId: beRoleId,
      roleName: beRole,
      accountStatus: userData.status,
    });
    return mapUser(dto);
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
    if (currentStatus === UserStatus.BLOCKED) {
      // Unblock the account using the general user update endpoint
      const res = await api.put<ResUserDTO>(`/business/users/${id}`, { accountStatus: 'ACTIVE' });
      return mapUser(res);
    }

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
