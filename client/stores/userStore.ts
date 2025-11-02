import { EHttpType, handleRequest, IApiResponse } from "@/lib/axiosInstance";
import { createStore, EStorageType, IBaseStore } from "@/lib/initialStore";
import { useAuthStore } from "./authStore";
import { mockUsers } from "@/services/mockData";

interface IUserDataResponse {
	user: IUser;
	users: IUser[];
	isActive: boolean;
}

export interface IUserStore extends IBaseStore {
	user: IUser | null;
	users: IUser[];
	usersTable: IUser[];

	getAllUsers: () => Promise<IApiResponse<IUserDataResponse>>;
	getUser: (
		userId: string
	) => Promise<IApiResponse<IUserDataResponse>>;
	createUser: (
		email: string,
		password: string,
		fullname: string,
		avatar: File | null,
		role: string,
		status: string,
	) => Promise<IApiResponse<IUserDataResponse>>;
	updateUser: (
		userId: string,
		fullname: string,
		avatar: File | null,
		role: string,
		status: string,
	) => Promise<IApiResponse<IUserDataResponse>>;
	deleteUser: (
		userId: string
	) => Promise<IApiResponse>;

	handleRemoveUserFromTable: (userId: string) => Promise<void>;
	handleAddUserToTable: (user: IUser) => Promise<void>;
	handleUpdateUserInTable: (user: IUser) => Promise<void>;
}

const storeName = "user";
const initialState = {
	user: null,
	users: [],
	usersTable: [],
};

export const useUserStore = createStore<IUserStore>(
	storeName,
	initialState,
	(set, get) => ({
		getAllUsers: async (): Promise<IApiResponse<IUserDataResponse>> => {
			return await get().handleRequest(async () => {
				const res = await handleRequest<IUserDataResponse>(EHttpType.GET, `/users`);
				
				if (res.data && res.data.success && res.data.users) {
					set({ usersTable: res.data.users });
				}

				return res;
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
			avatar: File | null,
			role: string,
			status: string,
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
				const res = await handleRequest<IUserDataResponse>(EHttpType.POST, `/users`, formData);

				if (res.data && res.data.success && res.data.user) {
					get().handleAddUserToTable(res.data.user);
				}

				return res;
			});
		},

		updateUser: async (
			userId: string,
			fullname: string,
			avatar: File | null,
			role: string,
			status: string,
		): Promise<IApiResponse<IUserDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				fullname,
				role,
				status,
			}));
			if (avatar) formData.append("avatar", avatar);

			return await get().handleRequest(async () => {
				const res = await handleRequest<IUserDataResponse>(EHttpType.PATCH, `/users/${userId}`, formData);

				const { success, user } = res.data || {};
				const { isAdmin, userAuth, handleSetUserAuth } = useAuthStore.getState();

				if (success && user) {
					if (isAdmin) get().handleUpdateUserInTable(user);
					if (userAuth?.id === userId) handleSetUserAuth(user);
				}

				return res;
			});
		},

		deleteUser: async (userId: string): Promise<IApiResponse> => {
			return await get().handleRequest(async () => {
				const res = await handleRequest(EHttpType.DELETE, `/users/${userId}`);

				if (res.data && res.data.success) {
					get().handleRemoveUserFromTable(userId);
				}

				return res;
			});
		},

		handleRemoveUserFromTable: (userId: string): void => {
			set({
				usersTable: get().usersTable.filter((user) => user.id !== userId),
			});
		},

		handleAddUserToTable: (user: IUser): void => {
			set({ usersTable: [user, ...get().usersTable] });
		},

		handleUpdateUserInTable: (user: IUser): void => {
			set({
				usersTable: get().usersTable.map((u) =>
					u.id === user.id ? user : u
				),
			});
		},

		reset: () => {
			set({ ...initialState });
		},
	}),
);