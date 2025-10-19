import { EHttpType, handleRequest, IApiResponse } from "@/lib/axiosInstance";
import { createStore, EStorageType, IBaseStore } from "@/lib/initialStore";

interface IUserDataResponse {
	user: IUser;
	users: IUser[];
	isActive: boolean;
}

export interface IUserStore extends IBaseStore {
	user: IUser | null;
	users: IUser[];

	getAllUsers: () => Promise<IApiResponse<IUserDataResponse>>;
	getUser: (
		userId: string
	) => Promise<IApiResponse<IUserDataResponse>>;
	createUser: (
		email: string,
		password: string,
		fullname: string,
		role: string,
		status: string,
		avatar: File,
	) => Promise<IApiResponse<IUserDataResponse>>;
	updateUser: (
		userId: string,
		fullname: string,
		role: string,
		status: string,
		avatar: File,
	) => Promise<IApiResponse<IUserDataResponse>>;
	deleteUser: (
		userId: string
	) => Promise<IApiResponse<IUserDataResponse>>;
}

const storeName = "user";
const initialState = {
	user: null,
	users: [],
};

export const useUserStore = createStore<IUserStore>(
	storeName,
	initialState,
	(set, get) => ({
		getAllUsers: async (): Promise<IApiResponse<IUserDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.GET, `/users`);
			});
		},

		getUser: async (userId: string): Promise<IApiResponse<IUserDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.GET, `/users/${userId}`);
			});
		},

		createUser: async (
			email: string,
			password: string,
			fullname: string,
			role: string,
			status: string,
			avatar: File,
		): Promise<IApiResponse<IUserDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				email,
				password,
				fullname,
				role,
				status,
			}));
			if (avatar) formData.append("avatar", avatar);

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `/users`, formData);
			});
		},

		updateUser: async (
			userId: string,
			fullname: string,
			role: string,
			status: string,
			avatar: File,
		): Promise<IApiResponse<IUserDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				fullname,
				role,
				status,
			}));
			if (avatar) formData.append("avatar", avatar);

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.PATCH, `/users/${userId}`, formData);
			});
		},

		deleteUser: async (userId: string): Promise<IApiResponse<IUserDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.DELETE, `/users/${userId}`);
			});
		},

		reset: () => {
			set({ ...initialState });
		},
	}),
);