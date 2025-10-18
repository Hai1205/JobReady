import { EHttpType, handleRequest, IApiResponse } from "@/lib/axiosInstance";
import { createStore, EStorageType, IBaseStore } from "@/lib/initialStore";
import Cookies from 'js-cookie';
import { EUserRole } from "@/types/enum";
import { useCVStore } from "./cvStore";
import { useUserStore } from "./userStore";

interface IAuthDataResponse {
	data: {
		user: IUser;
		isActive: boolean;
	}
}

export interface IAuthStore extends IBaseStore {
	userAuth: IUser | null;
	isAdmin: boolean;

	register: (fullname: string, email: string, password: string) => Promise<IApiResponse>;
	login: (email: string, password: string) => Promise<IApiResponse<IAuthDataResponse>>;
	logout: () => Promise<IApiResponse>;
	RefreshToken: () => Promise<IApiResponse>;
	sendOTP: (email: string) => Promise<IApiResponse>;
	verifyOTP: (email: string, otp: string) => Promise<IApiResponse>;
	resetPassword: (email: string) => Promise<IApiResponse>;
	forgotPassword: (email: string, password: string, confirmPassword: string) => Promise<IApiResponse>;
	changePassword: (email: string, oldPassword: string, password: string, confirmPassword: string) => Promise<IApiResponse>;

	handleSetUserAuth: (user: IUser) => void;
}

const storeName = "auth";
const initialState = {
	userAuth: null,
	isAdmin: false,
};

export const useAuthStore = createStore<IAuthStore>(
	storeName,
	initialState,
	(set, get) => ({
		RefreshToken: async (): Promise<IApiResponse> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, "/auth/refresh-token");
			});
		},

		register: async (fullname: string, email: string, password: string): Promise<IApiResponse<IAuthDataResponse>> => {
			const formData = new FormData();
			formData.append("fullname", fullname);
			formData.append("email", email);
			formData.append("password", password);

			return await get().handleRequest(async () => {
				return await handleRequest<IAuthDataResponse>(EHttpType.POST, `/auth/register`, formData);
			});
		},

		login: async (email: string, password: string): Promise<IApiResponse<IAuthDataResponse>> => {
			const formData = new FormData();
			formData.append("email", email);
			formData.append("password", password);

			return await get().handleRequest(async () => {
				const response = await handleRequest<IAuthDataResponse>(EHttpType.POST, `/auth/login`, formData);
				if (response && response?.data?.data) {
					const user = response.data.data.user;
					set({
						userAuth: user,
						isAdmin: user.role === EUserRole.ADMIN,
					});
				}

				return response;
			});
		},

		logout: async (): Promise<IApiResponse> => {
			return await get().handleRequest(async () => {
				const response = await handleRequest(EHttpType.POST, `/auth/logout`);

				// Clear all auth cookies
				Cookies.remove('access_token');
				Cookies.remove('refresh_token');

				get().reset();

				return response;
			});
		},

		sendOTP: async (email: string): Promise<IApiResponse> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `/auth/send-otp/${email}`);
			});
		},

		verifyOTP: async (email: string, otp: string): Promise<IApiResponse> => {
			const formData = new FormData();
			formData.append("otp", otp);

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `/auth/verify-otp/${email}`, formData);

			});
		},

		resetPassword: async (email: string): Promise<IApiResponse> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `/auth/reset-password/${email}`);

			});
		},

		forgotPassword: async (email: string, password: string, confirmPassword: string): Promise<IApiResponse> => {
			const formData = new FormData();
			formData.append("password", password);
			formData.append("confirmPassword", confirmPassword);

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.PATCH, `/auth/forgot-password/${email}`, formData);
			});
		},

		changePassword: async (email: string, oldPassword: string, password: string, confirmPassword: string): Promise<IApiResponse> => {
			const formData = new FormData();
			formData.append("oldPassword", oldPassword);
			formData.append("newPassword", password);
			formData.append("confirmPassword", confirmPassword);

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.PATCH, `/auth/change-password/${email}`, formData);
			});
		},

		handleSetUserAuth: (user: IUser): void => {
			if (user) {
				set({
					userAuth: user,
					isAdmin: user.role === EUserRole.ADMIN
				});
			}
		},

		reset: () => {
			set({ ...initialState });
			useCVStore.getState().reset();
			useUserStore.getState().reset();
		},
	}),
	{ storageType: EStorageType.COOKIE }
);