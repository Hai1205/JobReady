import { EHttpType, handleRequest, IApiResponse } from "@/lib/axiosInstance";
import { createStore, EStorageType, IBaseStore } from "@/lib/initialStore";

interface IPaymentDataResponse {
	invoice: IInvoice;
	invoices: IInvoice[];

	success: boolean;
	paymentUrl: string;
	orderId: string;
	txnRef: string;
	resultCode: number;
}

export interface IPaymentStore extends IBaseStore {
	invoice: IInvoice | null;
	invoices: IInvoice[];
	invoicesTable: IInvoice[];
	lastFetchTime: number | null;

	getAllInvoices: () => Promise<IApiResponse<IPaymentDataResponse>>;
	getUserInvoices: (userId: string) => Promise<IApiResponse<IPaymentDataResponse>>;
	fetchAllInvoicesInBackground: () => Promise<void>;
	getInvoice: (
		invoiceId: string
	) => Promise<IApiResponse<IPaymentDataResponse>>;
	createMoMoPayment: (userId: string, planId: string) => Promise<IApiResponse<IPaymentDataResponse>>;
	createVnPayPayment: (userId: string, planId: string) => Promise<IApiResponse<IPaymentDataResponse>>;
	createPayPalPayment: (userId: string, planId: string) => Promise<IApiResponse<IPaymentDataResponse>>;
}

const storeName = "payment";
const initialState = {
	invoice: null,
	invoices: [],
	invoicesTable: [],
	lastFetchTime: null as number | null,
};

// Cache expiration time: 3 minutes
const CACHE_DURATION = 3 * 60 * 1000;

export const usePaymentStore = createStore<IPaymentStore>(
	storeName,
	initialState,
	(set, get) => ({
		getAllInvoices: async (): Promise<IApiResponse<IPaymentDataResponse>> => {
			return await get().handleRequest(async () => {
				const res = await handleRequest<IPaymentDataResponse>(EHttpType.GET, `payments/invoices`);

				if (res.data && res.data.success && res.data.invoices) {
					set({
						invoicesTable: res.data.invoices,
						lastFetchTime: Date.now()
					});
				}

				return res;
			});
		},

		fetchAllInvoicesInBackground: async (): Promise<void> => {
			const state = get();
			const now = Date.now();

			// Check if cache is still valid
			if (state.invoicesTable.length > 0 && state.lastFetchTime) {
				const cacheAge = now - state.lastFetchTime;
				if (cacheAge < CACHE_DURATION) {
					console.log("Users cache is still valid, skipping fetch");
					return;
				}
			}

			// Check if already loading
			if (state.isLoading) {
				return;
			}

			await get().getAllInvoices();
		},

		getUserInvoices: async (userId: string): Promise<IApiResponse<IPaymentDataResponse>> => {
			return await get().handleRequest(async () => {
				const res = await handleRequest<IPaymentDataResponse>(EHttpType.GET, `payments/users/${userId}/invoices`);

				if (res.data && res.data.success && res.data.invoices) {
					set({
						invoicesTable: res.data.invoices,
						lastFetchTime: Date.now()
					});
				}

				return res;
			});
		},

		getInvoice: async (invoiceId: string): Promise<IApiResponse<IPaymentDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.GET, `payments/invoices/${invoiceId}`);
			});
		},

		createMoMoPayment: async (userId: string, planId: string): Promise<IApiResponse<IPaymentDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `payments/momo/${userId}/${planId}`);
			});
		},
		
		createVnPayPayment: async (userId: string, planId: string): Promise<IApiResponse<IPaymentDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `payments/vnpay/${userId}/${planId}`);
			});
		},
		
		createPayPalPayment: async (userId: string, planId: string): Promise<IApiResponse<IPaymentDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `payments/paypal/${userId}/${planId}`);
			});
		},

		reset: () => {
			set({ ...initialState });
		},
	}),
	{ storageType: EStorageType.LOCAL }
);