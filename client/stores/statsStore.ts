import { EHttpType, handleRequest, IApiResponse } from "@/lib/axiosInstance";
import { createStore, IBaseStore } from "@/lib/initialStore";

export interface IStatsResponse {
	dashboardStats: IDashboardStats;
}

export interface IStatsStore extends IBaseStore {
	// Cached data
	dashboardStats: IDashboardStats | null;
	lastFetchTime: number | null;
	isLoading: boolean;

	// Actions
	getDashboardStats: () => Promise<IApiResponse<IStatsResponse>>;
	fetchDashboardStatsInBackground: () => Promise<void>;
}

const storeName = "stats";
const initialState = {
	dashboardStats: null as IDashboardStats | null,
	lastFetchTime: null as number | null,
	isLoading: false,
};

// Cache expiration time: 5 minutes
const CACHE_DURATION = 5 * 60 * 1000;

export const useStatsStore = createStore<IStatsStore>(
	storeName,
	initialState,
	(set, get) => ({
		getDashboardStats: async (): Promise<IApiResponse<IStatsResponse>> => {
			return await get().handleRequest(async () => {
				const res = await handleRequest<IStatsResponse>(EHttpType.GET, `/stats/dashboard`);

				// Cache the result in store
				if (res.data && res.data.success) {
					set({
						dashboardStats: res.data.dashboardStats,
						lastFetchTime: Date.now()
					});
				}

				return res;
			});
		},

		fetchDashboardStatsInBackground: async (): Promise<void> => {
			const state = get();
			const now = Date.now();

			// Check if cache is still valid
			if (state.dashboardStats && state.lastFetchTime) {
				const cacheAge = now - state.lastFetchTime;
				if (cacheAge < CACHE_DURATION) {
					console.log("Stats cache is still valid, skipping fetch");
					return;
				}
			}

			// Check if already loading
			if (state.isLoading) {
				return;
			}

			set({ isLoading: true });

			try {
				const res = await handleRequest<IStatsResponse>(EHttpType.GET, `/stats/dashboard`);

				if (res.data && res.data.success) {
					set({
						dashboardStats: res.data.dashboardStats,
						lastFetchTime: Date.now()
					});
				}
			} catch (error) {
				console.error("Failed to fetch stats in background:", error);
			} finally {
				set({ isLoading: false });
			}
		},

		reset: () => {
			set({ ...initialState });
		},
	}),
);