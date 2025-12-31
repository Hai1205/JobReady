import { EHttpType, handleRequest, IApiResponse } from "@/lib/axiosInstance";
import { createStore, EStorageType, IBaseStore } from "@/lib/initialStore";

interface IPlanDataResponse {
	plan: IPlan;
	plans: IPlan[];
}

export interface IPlanStore extends IBaseStore {
	plan: IPlan | null;
	plans: IPlan[];
	plansTable: IPlan[];
	lastFetchTime: number | null;

	getAllPlans: () => Promise<IApiResponse<IPlanDataResponse>>;
	fetchAllPlansInBackground: () => Promise<void>;
	getPlan: (
		planId: string
	) => Promise<IApiResponse<IPlanDataResponse>>;
	createPlan: (
		name: string,
		type: string,
		price: number,
		currency: string,
		period: string,
		description: string,
		features: string[],
		isRecommended: boolean,
		isPopular: boolean,
	) => Promise<IApiResponse<IPlanDataResponse>>;
	updatePlan: (
		planId: string,
		name: string,
		type: string,
		price: number,
		currency: string,
		period: string,
		description: string,
		features: string[],
		isRecommended: boolean,
		isPopular: boolean,
	) => Promise<IApiResponse<IPlanDataResponse>>;
	deletePlan: (
		planId: string
	) => Promise<IApiResponse>;

	handleRemovePlanFromTable: (planId: string) => Promise<void>;
	handleAddPlanToTable: (plan: IPlan) => Promise<void>;
	handleUpdatePlanInTable: (plan: IPlan) => Promise<void>;
}

const storeName = "plan";
const initialState = {
	plan: null,
	plans: [],
	plansTable: [],
	lastFetchTime: null as number | null,
};

// Cache expiration time: 3 minutes
const CACHE_DURATION = 3 * 60 * 1000;

export const usePlanStore = createStore<IPlanStore>(
	storeName,
	initialState,
	(set, get) => ({
		getAllPlans: async (): Promise<IApiResponse<IPlanDataResponse>> => {
			return await get().handleRequest(async () => {
				const res = await handleRequest<IPlanDataResponse>(EHttpType.GET, `/plans`);

				if (res.data && res.data.success && res.data.plans) {
					set({
						plansTable: res.data.plans,
						lastFetchTime: Date.now()
					});
				}

				return res;
			});
		},

		fetchAllPlansInBackground: async (): Promise<void> => {
			const state = get();
			const now = Date.now();

			// Check if cache is still valid
			if (state.plansTable.length > 0 && state.lastFetchTime) {
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

			await get().getAllPlans();
		},

		getPlan: async (planId: string): Promise<IApiResponse<IPlanDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.GET, `/plans/${planId}`);
			});
		},

		createPlan: async (
			name: string,
			type: string,
			price: number,
			currency: string,
			period: string,
			description: string,
			features: string[],
			isRecommended: boolean,
			isPopular: boolean,
		): Promise<IApiResponse<IPlanDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				name,
				type,
				price,
				currency,
				period,
				description,
				features,
				isRecommended,
				isPopular,
			}));

			return await get().handleRequest(async () => {
				const res = await handleRequest<IPlanDataResponse>(EHttpType.POST, `/plans`, formData);

				if (res.data && res.data.success && res.data.plan) {
					get().handleAddPlanToTable(res.data.plan);
				}

				return res;
			});
		},

		updatePlan: async (
			planId: string,
			name: string,
			type: string,
			price: number,
			currency: string,
			period: string,
			description: string,
			features: string[],
			isRecommended: boolean,
			isPopular: boolean,
		): Promise<IApiResponse<IPlanDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				name,
				type,
				price,
				currency,
				period,
				description,
				features,
				isRecommended,
				isPopular,
			}));

			return await get().handleRequest(async () => {
				const res = await handleRequest<IPlanDataResponse>(EHttpType.PATCH, `/plans/${planId}`, formData);
				console.log('updatePlan res:', res);
				const { success, plan } = res.data || {};

				if (success && plan) {
					get().handleUpdatePlanInTable(plan);
				}

				return res;
			});
		},

		deletePlan: async (planId: string): Promise<IApiResponse> => {
			// Remove from table immediately
			get().handleRemovePlanFromTable(planId);

			// Call API in background
			get().handleRequest(async () => {
				const res = await handleRequest(EHttpType.DELETE, `/plans/${planId}`);
				// If API fails, we could add back, but for now, assume success
				return res;
			});

			// Return success immediately
			return { data: { success: true } } as IApiResponse;
		},

		handleRemovePlanFromTable: (planId: string): void => {
			set({
				plansTable: get().plansTable.filter((plan) => plan.id !== planId),
			});
		},

		handleAddPlanToTable: (plan: IPlan): void => {
			set({ plansTable: [plan, ...get().plansTable] });
		},

		handleUpdatePlanInTable: (plan: IPlan): void => {
			set({
				plansTable: get().plansTable.map((u) =>
					u.id === plan.id ? plan : u
				),
			});
		},

		reset: () => {
			set({ ...initialState });
		},
	}),
	{ storageType: EStorageType.LOCAL }
);