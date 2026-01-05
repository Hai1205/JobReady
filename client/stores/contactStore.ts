import { EHttpType, handleRequest, IApiResponse } from "@/lib/axiosInstance";
import { createStore, IBaseStore } from "@/lib/initialStore";

interface IContactDataResponse {
	contact?: IContact;
	contacts?: IContact[];
}

export interface IContactStore extends IBaseStore {
	contactTable: IContact[];

	getAllContacts: () => Promise<IApiResponse<IContactDataResponse>>;
	getContact: (
		contactId: string
	) => Promise<IApiResponse<IContactDataResponse>>;
	deleteContact: (
		contactId: string
	) => Promise<IApiResponse<IContactDataResponse>>;
	submitContact: (
		name: string,
		email: string,
		plan: string,
		phone: string,
		message: string,
	) => Promise<IApiResponse<IContactDataResponse>>;
	resolveContact: (
		userId: string,
		contactId: string,
	) => Promise<IApiResponse<IContactDataResponse>>;

	handleRemoveContactFromTable: (contactId: string) => Promise<void>;
	handleUpdateContactInTable: (contact: IContact) => Promise<void>;
}

const storeName = "contact";
const initialState = {
	contactTable: [],
};

export const useContactStore = createStore<IContactStore>(
	storeName,
	initialState,
	(set, get) => ({
		getAllContacts: async (): Promise<IApiResponse<IContactDataResponse>> => {
			return await get().handleRequest(async () => {
				const res = await handleRequest<IContactDataResponse>(EHttpType.GET, `/contacts`);
				if (res.data && res.data.contacts) {
					set({ contactTable: res.data.contacts });
				}
				return res;
			});
		},

		getContact: async (contactId: string): Promise<IApiResponse<IContactDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.GET, `/contacts/${contactId}`);
			});
		},

		deleteContact: async (contactId: string): Promise<IApiResponse> => {
			return await get().handleRequest(async () => {
				const res = await handleRequest(EHttpType.DELETE, `/contacts/${contactId}`);

				if (res.data && res.data.success) {
					get().handleRemoveContactFromTable(contactId);
				}

				return res;
			});
		},

		submitContact: async (
			name: string,
			email: string,
			plan: string,
			phone: string,
			message: string,
		): Promise<IApiResponse<IContactDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				name,
				email,
				plan,
				phone,
				message,
			}));

			return await get().handleRequest(async () => {
				return await handleRequest<IContactDataResponse>(EHttpType.POST, `/contacts`, formData);
			});
		},

		resolveContact: async (
			userId: string,
			contactId: string,
		): Promise<IApiResponse<IContactDataResponse>> => {
			return await get().handleRequest(async () => {
				const res = await handleRequest<IContactDataResponse>(EHttpType.POST, `/contacts/${contactId}/resolve/${userId}`);

				if (res.data && res.data.success && res.data.contact) {
					get().handleUpdateContactInTable(res.data.contact);
				}

				return res;
			});
		},

		handleRemoveContactFromTable: (contactId: string): void => {
			set({
				contactTable: get().contactTable.filter((contact) => contact.id !== contactId),
			});
		},

		handleUpdateContactInTable: (contact: IContact): void => {
			set({
				contactTable: get().contactTable.map((c) =>
					c.id === contact.id ? contact : c
				),
			});
		},

		reset: () => {
			set({ ...initialState });
		},
	})
);