import api from '../api/apiClient';
import { AuthUser, UserRole } from '../models/ui_types/user';

// ─── Types matching BE DTOs ─────────────────────────────────
interface LoginResponseDto {
  token: string;
  email: string;
  roles: string[];
}

interface UserMeResponseDto {
  id: string;
  email: string;
  status: string;
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
// BE returns roles as ["Customer"], ["Staff"], ["Admin"], etc.
// FE expects a single UserRole string.
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

// ─── Public Interfaces (unchanged signatures) ───────────────
export interface LoginParams {
  email: string;
  password?: string;
}

export interface RegisterParams {
  fullName: string;
  email: string;
  phone: string;
  password?: string;
}

export const authService = {
  login: async ({ email, password }: LoginParams): Promise<AuthUser> => {
    const data = await api.post<LoginResponseDto>('/auth/login', {
      email,
      password,
    });

    // Save JWT token for subsequent API calls
    localStorage.setItem('token', data.token);

    // Fetch full user profile to get id and name
    const userMe = await api.get<UserMeResponseDto>('/user/me');

    return {
      id: userMe.id,
      name: userMe.profile?.fullName || userMe.email,
      email: userMe.email,
      role: mapRole(userMe.roles),
      phone: userMe.profile?.phone,
      avatarUrl: userMe.profile?.avatarUrl,
    };
  },

  register: async (params: RegisterParams): Promise<AuthUser> => {
    await api.post('/auth/register', {
      email: params.email,
      password: params.password,
      confirmPassword: params.password,
    });

    // After registration, auto-login is not guaranteed (email verification may be needed)
    // Return a temporary user object; FE should redirect to verify-email page
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
    await api.put('/auth/reset-password', {
      email,
      token,
      newPassword: password,
      confirmPassword,
    });
  },

  verifyEmail: async (token: string, email: string = ''): Promise<void> => {
    await api.post('/auth/verify-email', { email, token });
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

  /**
   * Fetch current user from token (used by AuthContext on page load).
   * Returns null if token is invalid/expired.
   */
  getCurrentUser: async (): Promise<AuthUser | null> => {
    const token = localStorage.getItem('token');
    if (!token) return null;

    try {
      const userMe = await api.get<UserMeResponseDto>('/user/me');
      return {
        id: userMe.id,
        name: userMe.profile?.fullName || userMe.email,
        email: userMe.email,
        role: mapRole(userMe.roles),
        phone: userMe.profile?.phone,
        avatarUrl: userMe.profile?.avatarUrl,
      };
    } catch {
      // Token expired or invalid — clean up
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
