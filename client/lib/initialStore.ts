import { toast } from "react-toastify";
import { create } from "zustand";
import { persist, createJSONStorage, PersistOptions, PersistStorage } from "zustand/middleware";
import Cookies from 'js-cookie';

export interface IBaseStore {
  isLoading: boolean;
  error: string | null;
  status: number;
  message: string | null;

  handleRequest: <R>(apiCall: () => Promise<R>) => Promise<R>;
  reset: () => void;
}

export const baseInitialState = {
  isLoading: false,
  error: null,
  status: 0,
  message: null,
};

type TVariables = Record<string, unknown>;

export enum EStorageType {
  LOCAL = "LOCAL",
  SESSION = "SESSION",
  COOKIE = "COOKIE",
}

const createCookieStorage = <T>(): PersistStorage<T> => ({
  getItem: (name: string) => {
    try {
      const value = Cookies.get(name);
      return value ? JSON.parse(value) : null;
    } catch {
      return null;
    }
  },
  setItem: (name: string, value: any) => {
    try {
      Cookies.set(name, JSON.stringify(value), { expires: 7 });
    } catch {
    }
  },
  removeItem: (name: string) => {
    Cookies.remove(name);
  },
});

export function createStore<T extends IBaseStore, U = TVariables>(
  storeName: string,
  initialState: TVariables,
  storeActions: (set: (state: Partial<T>) => void, get: () => T) => U,
  options?: {
    persistOptions?: Partial<PersistOptions<T>>;
    storageType?: EStorageType;
  }
) {
  const storageType = options?.storageType ?? EStorageType.SESSION;
  const storage = (() => {
    switch (storageType) {
      case EStorageType.SESSION:
        return createJSONStorage<T>(() => sessionStorage);
      case EStorageType.COOKIE:
        return createCookieStorage<T>();
      case EStorageType.LOCAL:
      default:
        return createJSONStorage<T>(() => localStorage);
    }
  })();

  return create<T>()(
    persist(
      (set, get) => {
        const handleRequest = async <R>(apiCall: () => Promise<R>): Promise<R> => {
          set({ isLoading: true, error: null } as T);

          try {
            return await apiCall();
          } catch (error: unknown) {
            console.error(error);
            const message =
              (error as { response?: { data?: { message?: string } } }).response
                ?.data?.message || (error as Error).message;
            set({ error: message } as T);

            if (message) toast.error(message);

            throw error;
          } finally {
            set({ isLoading: false } as T);
          }
        };

        const reset = () => {
          set({ ...baseInitialState, ...initialState } as T);
        };

        return {
          ...baseInitialState,
          ...initialState,
          ...storeActions((state) => set(state as T), get as () => T),
          handleRequest,
          reset,
        } as unknown as T;
      },
      {
        name: `${storeName}-storage`,
        storage,
        // Skip hydration for certain keys to prevent unnecessary re-renders
        partialize: (state) => {
          const { isLoading, error, status, message, ...rest } = state as T & {
            isLoading: boolean;
            error: string | null;
            status: number;
            message: string | null;
          };
          return rest as T;
        },
        ...options?.persistOptions,
      }
    )
  );
}
