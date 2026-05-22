import api from '../api/apiClient';
import { AuthUser, UserRole } from '../models/ui_types/user';

// ─── Types matching BE DTOs ─────────────────────────────────
interface LoginResponseDto {
  access_token: string;
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

// ─── Public Interfaces ──────────────────────────────────────
export interface LoginParams {
  email: string;
  password?: string;
}

export interface RegisterParams {
  fullName: string;
  email: string;
  phone: string;
  password?: string;
  confirmPassword?: string;
}

export const authService = {
  login: async ({ email, password }: LoginParams): Promise<AuthUser> => {
    const data = await api.post<LoginResponseDto>('/auth/login', {
      email,
      password,
    });

    // Save JWT token for subsequent API calls
    localStorage.setItem('token', data.access_token);

    return {
      id: data.user.id,
      name: data.user.name || data.user.email,
      email: data.user.email,
      phone: data.user.phoneNumber,
      role: mapRole(data.role?.roleName),
    };
  },

  register: async (params: RegisterParams): Promise<AuthUser> => {
    await api.post('/auth/register', {
      email: params.email,
      password: params.password,
      confirmPassword: params.confirmPassword || params.password,
    });

    return {
      id: '',
      name: params.fullName,
      email: params.email,
      role: 'Customer',
    };
  },

  resetPassword: async (email: string): Promise<void> => {
    await api.post('/auth/forgot-password', { email });
  },

  confirmResetPassword: async (
    password: string,
    confirmPassword: string,
    token: string,
    email: string = '',
  ): Promise<void> => {
    await api.post('/auth/reset-password', {
      token,
      newPassword: password,
      confirmPassword,
    });
  },

  verifyEmail: async (token: string, email: string = ''): Promise<void> => {
    await api.get(`/auth/verify?token=${encodeURIComponent(token)}`);
  },

  changePassword: async (
    oldPassword: string,
    newPassword: string,
    confirmPassword: string,
  ): Promise<void> => {
    await api.put('/auth/change-password', {
      currentPassword: oldPassword,
      newPassword,
      confirmPassword,
    });
  },

  getCurrentUser: async (): Promise<AuthUser | null> => {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
      const data = await api.get<UserGetAccountDto>('/auth/account');
      return {
        id: data.user.id,
        name: data.user.name || data.user.email,
        email: data.user.email,
        phone: data.user.phoneNumber,
        role: mapRole(data.role?.roleName),
      };
    } catch {
      localStorage.removeItem('token');
      return null;
    }
  },

  logout: async (): Promise<void> => {
    try {
      await api.post('/auth/logout');
    } catch (error) {
      console.error('Logout error on server:', error);
    } finally {
      localStorage.removeItem('token');
      localStorage.removeItem('tech_sales_user');
    }
  },
};
