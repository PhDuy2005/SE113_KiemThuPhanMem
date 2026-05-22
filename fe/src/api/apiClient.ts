/**
 * API Client tập trung cho TechSales FE.
 * Xử lý: Base URL, JWT Token, Response parsing, Error handling.
 *
 * Backend trả về chuẩn: { success: boolean, message: string, data: T }
 */

const BASE_URL = (import.meta as any).env.VITE_API_BASE_URL;

// ─── Types ───────────────────────────────────────────────────
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface PagedResponse<T> {
  items: T[];
  pageNumber: number;
  pageSize: number;
  totalCount: number;
  totalPages: number;
}

export class ApiError extends Error {
  status: number;
  errors?: Record<string, string[]>;

  constructor(message: string, status: number, errors?: Record<string, string[]>) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.errors = errors;
  }
}

// ─── Core Request Function ──────────────────────────────────
async function apiRequest<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('token');

  const headers: Record<string, string> = {
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...(options.headers as Record<string, string> || {}),
  };

  if (!(options.body instanceof FormData)) {
    headers['Content-Type'] = 'application/json';
  }

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  // Handle no-content responses (204)
  if (response.status === 204) {
    return undefined as T;
  }

  let result: any;
  try {
    const text = await response.text();
    result = text ? JSON.parse(text) : {};
  } catch (err) {
    throw new ApiError(
      `Failed to parse response: ${response.statusText}`,
      response.status
    );
  }

  if (!response.ok) {
    throw new ApiError(
      result.message || `HTTP Error ${response.status}: ${response.statusText}`,
      response.status,
      result.data,
    );
  }

  // Backend wraps response in { success, message, data }
  return result.data as T;
}

// ─── Convenience Methods ────────────────────────────────────
export const api = {
  get: <T>(endpoint: string) =>
    apiRequest<T>(endpoint, { method: 'GET' }),

  post: <T>(endpoint: string, body?: unknown, headers?: Record<string, string>) =>
    apiRequest<T>(endpoint, {
      method: 'POST',
      body: body instanceof FormData ? body : (body ? JSON.stringify(body) : undefined),
      headers,
    }),

  put: <T>(endpoint: string, body?: unknown, headers?: Record<string, string>) =>
    apiRequest<T>(endpoint, {
      method: 'PUT',
      body: body instanceof FormData ? body : (body ? JSON.stringify(body) : undefined),
      headers,
    }),

  patch: <T>(endpoint: string, body?: unknown, headers?: Record<string, string>) =>
    apiRequest<T>(endpoint, {
      method: 'PATCH',
      body: body instanceof FormData ? body : (body ? JSON.stringify(body) : undefined),
      headers,
    }),

  delete: <T>(endpoint: string) =>
    apiRequest<T>(endpoint, { method: 'DELETE' }),
};

export default api;
