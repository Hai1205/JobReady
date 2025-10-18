import { SERVER_URL } from "@/services/constants";
import axios, { InternalAxiosRequestConfig, AxiosResponse } from "axios";
import { toast } from "react-toastify";

export const MAX_RETRIES = 0;

let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value?: unknown) => void;
  reject: (reason?: unknown) => void;
}> = [];

const processQueue = (error: unknown, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });

  failedQueue = [];
};

const getCookie = (name: string): string | null => {
  const matches = document.cookie.match(new RegExp(`(^| )${name}=([^;]+)`));
  return matches ? matches[2] : null;
};

export const getRefreshToken = (): string | null => {
  return getCookie('refresh_token') ||
    localStorage.getItem('refresh_token') ||
    sessionStorage.getItem('refresh_token');
};

const axiosInstance = axios.create({
  baseURL: `${SERVER_URL}`,
  // baseURL: `${SERVER_URL}/api/v1`,
  withCredentials: true,
  headers: {
    accept: "application/json",
    "Content-Type": "application/json",
  },
  timeout: 10000, // Add a timeout to prevent hanging requests
});

const getAccessToken = (item: string): string | null => {
  // Try multiple cookie names (case variations)
  const token = getCookie(item) ||
    getCookie('access_token') ||
    localStorage.getItem(item) ||
    sessionStorage.getItem(item);

  return token;
};

// Function to refresh access token
const refreshAccessToken = async (): Promise<string | null> => {
  // Check if refresh token exists before attempting refresh
  const refreshToken = getRefreshToken();
  if (!refreshToken) {
    console.log('⚠️ No refresh token available, cannot refresh access token');
    return null;
  }

  try {
    console.log('🔄 Attempting to refresh token...');
    const response = await axios.post(
      `${SERVER_URL}/auth/refresh-token`,
      {},
      { withCredentials: true }
    );

    console.log('✅ Token refreshed successfully');
    return getCookie('access_token');
  } catch (error) {
    console.error('❌ Token refresh failed:', error);
    // Clear auth state and redirect to login
    document.cookie = 'access_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
    document.cookie = 'refresh_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';

    if (typeof window !== 'undefined') {
      window.location.href = '/auth/login';
    }
    return null;
  }
};

axiosInstance.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getAccessToken("access_token");

    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    (config as { retryCount?: number }).retryCount = (config as { retryCount?: number }).retryCount || 0;

    return config;
  },
  (error) => Promise.reject(error)
);

axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },

  async (error: unknown) => {
    interface RetryConfig extends InternalAxiosRequestConfig {
      retryCount?: number;
      _retry?: boolean;
    }

    if (!axios.isAxiosError(error) || !error.config) {
      return Promise.reject(error);
    }

    const config = error.config as RetryConfig;

    // If 401 Unauthorized and not already retrying
    if (error.response?.status === 401 && !config._retry) {
      if (isRefreshing) {
        // If already refreshing, queue this request
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then(() => {
            const newToken = getAccessToken("access_token");
            if (newToken && config.headers) {
              config.headers.Authorization = `Bearer ${newToken}`;
            }
            return axiosInstance(config);
          })
          .catch((err) => {
            return Promise.reject(err);
          });
      }

      config._retry = true;
      isRefreshing = true;

      try {
        const newToken = await refreshAccessToken();

        if (newToken && config.headers) {
          config.headers.Authorization = `Bearer ${newToken}`;
        }

        processQueue(null, newToken);
        isRefreshing = false;

        return axiosInstance(config);
      } catch (refreshError) {
        processQueue(refreshError, null);
        isRefreshing = false;
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export interface IApiResponse<IData = unknown> {
  data?: IData & { success: boolean } | null;
  error?: string;
  message?: string;
  status?: number;
  success?: boolean;
}

export enum EHttpType {
  GET = "GET",
  POST = "POST",
  PUT = "PUT",
  PATCH = "PATCH",
  DELETE = "DELETE",
}

interface IAxiosError {
  message?: string;
  status?: number;
}

export const handleRequest = async <T = unknown>(
  type: EHttpType,
  route: string,
  data?: FormData | Record<string, unknown>,
  toastMessage?: boolean
): Promise<IApiResponse<T>> => {
  let response;

  try {
    // Get token for Authorization header
    const token = getAccessToken("access_token");

    console.log('🔑 REQUEST DEBUG:', {
      route,
      hasToken: !!token,
      token: token ? `${token.substring(0, 20)}...` : 'NO TOKEN',
      isFormData: data instanceof FormData
    });

    const headers: Record<string, string> = {};

    // Set Authorization header if token exists
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    // Set Content-Type based on data type (only for non-FormData)
    if (data && !(data instanceof FormData)) {
      headers['Content-Type'] = 'application/json';
    }
    // For FormData, let axios set Content-Type automatically with boundary

    switch (type) {
      case EHttpType.GET:
        response = await axiosInstance.get(route, { headers });
        break;

      case EHttpType.POST:
        response = await axiosInstance.post(route, data, { headers });
        break;

      case EHttpType.PUT:
        if (!data) {
          throw new Error("Data is required for PUT requests");
        }
        response = await axiosInstance.put(route, data, { headers });
        break;

      case EHttpType.PATCH:
        if (!data) {
          throw new Error("Data is required for PATCH requests");
        }
        response = await axiosInstance.patch(route, data, { headers });
        break;

      case EHttpType.DELETE:
        response = await axiosInstance.delete(route, { headers });
        break;

      default:
        throw new Error("Invalid request type");
    }

    if (toastMessage) {
      toast.success(toastMessage);
    }

    return { status: response.status, data: { ...(response.data as T), success: true } };
  } catch (error: unknown) {
    console.error("Error fetching data:", error);

    // Type guard to check if error is an Axios error
    if (axios.isAxiosError(error) && error.response) {
      const axiosErrorData = error.response.data as IAxiosError;

      if (axiosErrorData?.message) {
        toast.error(axiosErrorData.message);
      }

      // Define a more specific type for the response data
      interface ErrorResponseData {
        data?: T | null;
        message?: string;
        success?: boolean;
        [key: string]: unknown;
      }

      const responseData = error.response.data as ErrorResponseData;

      return {
        status: error.response.status,
        data: responseData?.data && typeof responseData.data === 'object'
          ? { ...responseData.data, success: !!responseData.success }
          : null,
        error: axiosErrorData?.message || error.message,
        message: axiosErrorData?.message || error.message || "An error occurred"
      };
    }

    // Handle non-Axios errors
    const errorMessage = error instanceof Error ? error.message : "An unknown error occurred";
    toast.error(errorMessage);

    return {
      status: 500,
      data: null,
      error: errorMessage,
      message: errorMessage
    };
  }
};

export const isSuccess = (status?: number) => status && status >= 200 && status < 300;

// Export refresh function for manual refresh
export { refreshAccessToken };

export default axiosInstance;