import { SERVER_URL } from "@/services/constants";
import axios, { InternalAxiosRequestConfig, AxiosResponse } from "axios";
import { toast } from "react-toastify";

export const MAX_RETRIES = 1;

const getCookie = (name: string): string | null => {
  const matches = document.cookie.match(new RegExp(`(^| )${name}=([^;]+)`));
  return matches ? matches[2] : null;
};

const axiosInstance = axios.create({
  baseURL: `${SERVER_URL}/api/v1`,
  withCredentials: true,
  headers: {
    accept: "application/json",
    "Content-Type": "application/json",
  },
  timeout: 10000, // Add a timeout to prevent hanging requests
});

const getAccessToken = (item: string): string | null => {
  return getCookie(item) || localStorage.getItem(item) || sessionStorage.getItem(item);
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
    }

    if (!axios.isAxiosError(error) || !error.config) {
      return Promise.reject(error);
    }

    const config = error.config as RetryConfig;

    // Add detailed logging
    console.error('Axios Error Details:', {
      url: config?.url,
      baseURL: config?.baseURL,
      method: config?.method,
      headers: config?.headers,
      status: error.response?.status,
      statusText: error.response?.statusText,
      errorMessage: error.message,
      errorName: error.name,
      errorCode: error.code
    });

    if ((config?.retryCount ?? 0) < MAX_RETRIES) {
      config.retryCount = (config.retryCount ?? 0) + 1;
      console.log(`Retrying request (${config.retryCount}/${MAX_RETRIES}): ${config.method} ${config.url}`);
      return axiosInstance(config);
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
    const headers = data instanceof FormData
      ? { 'Content-Type': 'multipart/form-data' }
      : { 'Content-Type': 'application/json' };

    switch (type) {
      case EHttpType.GET:
        response = await axiosInstance.get(route);
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
        response = await axiosInstance.delete(route);
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

export default axiosInstance;