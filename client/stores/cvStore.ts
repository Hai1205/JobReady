import { EHttpType, handleRequest, IApiResponse } from "@/lib/axiosInstance";
import { createStore, IBaseStore } from "@/lib/initialStore";
import { renderCVToHTMLAsync } from "@/components/comons/cv-builder/CVRenderer";
import { toast } from "react-toastify";
import { testFormData } from "@/lib/utils";
import { exportCustomHTML, exportToPDF } from "@/services/pdfExportService";

interface ICVDataResponse {
	cv: ICV,
	cvs: ICV[],
}

export interface ICVStore extends IBaseStore {
	initialCV: ICV
	currentCV: ICV | null
	cvList: ICV[]
	userCVs: ICV[]
	CVsTable: ICV[]
	currentStep: number
	lastFetchTimeAllCVs: number | null
	lastFetchTimeUserCVs: number | null
	isLoadingAllCVs: boolean
	isLoadingUserCVs: boolean

	getAllCVs: () => Promise<IApiResponse<ICVDataResponse>>;
	fetchAllCVsInBackground: () => Promise<void>;
	getUserCVs: (userId: string) => Promise<IApiResponse<ICVDataResponse>>;
	fetchUserCVsInBackground: (userId: string) => Promise<void>;
	getCV: (
		cvId: string
	) => Promise<IApiResponse<ICVDataResponse>>;
	createCV: (
		userId: string,
	) => Promise<IApiResponse<ICVDataResponse>>;
	updateCV: (
		cvId: string,
		title: string,
		avatar: File,
		personalInfo: IPersonalInfo,
		experiences: IExperience[],
		educations: IEducation[],
		skills: string[],
		isVisibility: boolean,
		color: string,
		template: string
	) => Promise<IApiResponse<ICVDataResponse>>;
	deleteCV: (
		cvId: string
	) => Promise<IApiResponse<void>>;
	duplicateCV: (
		cvId: string
	) => Promise<IApiResponse<ICVDataResponse>>;

	handleSetCurrentCV: (cv: ICV | null) => void;
	handleUpdateCV: (cvData: Partial<ICV>) => void;
	handleSetCurrentStep: (step: number) => void;
	handleClearCVList: () => void;
	handleGeneratePDF: (cv: ICV, htmlContent?: string) => void;
	handleAddCVToUserCVs: (cv: ICV) => void;
	handleRemoveCVFromUserCVs: (cvId: string) => void;
	handleRemoveCVFromTable: (cvId: string) => void;
}

const storeName = "cv";
const initialState = {
	currentCV: null,
	cvList: [],
	userCVs: [],
	CVsTable: [],
	currentStep: 0,
	lastFetchTimeAllCVs: null as number | null,
	lastFetchTimeUserCVs: null as number | null,
	isLoadingAllCVs: false,
	isLoadingUserCVs: false,

	initialCV: {
		id: '',
		userId: '',
		avatar: null,
		title: '',
		personalInfo: {
			id: '',
			fullname: '',
			email: '',
			phone: '',
			location: '',
			summary: '',
			avatarUrl: '',
			avatarPublicId: '',
		},
		experiences: [
			{
				id: '',
				company: '',
				position: '',
				startDate: '',
				endDate: '',
				description: '',
			},
		],
		educations: [
			{
				id: '',
				school: '',
				degree: '',
				field: '',
				startDate: '',
				endDate: '',
			},
		],
		skills: [],
		isVisibility: true,
		color: '#000000',
		template: 'default',
		createdAt: '',
		updatedAt: '',
	}
};

// Cache expiration time: 3 minutes
const CACHE_DURATION = 3 * 60 * 1000;

export const useCVStore = createStore<ICVStore>(
	storeName,
	initialState,
	(set, get) => ({
		getAllCVs: async (): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				const res = await handleRequest<ICVDataResponse>(EHttpType.GET, `/cvs`);

				if (res.data && res.data.success && res.data.cvs) {
					set({
						CVsTable: res.data.cvs,
						lastFetchTimeAllCVs: Date.now()
					});
				}

				return res;
			});
		},

		fetchAllCVsInBackground: async (): Promise<void> => {
			const state = get();
			const now = Date.now();

			// Check if cache is still valid
			if (state.CVsTable.length > 0 && state.lastFetchTimeAllCVs) {
				const cacheAge = now - state.lastFetchTimeAllCVs;
				if (cacheAge < CACHE_DURATION) {
					console.log("All CVs cache is still valid, skipping fetch");
					return;
				}
			}

			// Check if already loading
			if (state.isLoadingAllCVs) {
				return;
			}

			set({ isLoadingAllCVs: true });

			try {
				const res = await handleRequest<ICVDataResponse>(EHttpType.GET, `/cvs`);

				if (res.data && res.data.success && res.data.cvs) {
					set({
						CVsTable: res.data.cvs,
						lastFetchTimeAllCVs: Date.now()
					});
				}
			} catch (error) {
				console.error("Failed to fetch all CVs in background:", error);
			} finally {
				set({ isLoadingAllCVs: false });
			}
		},

		getUserCVs: async (userId: string): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				const res = await handleRequest<ICVDataResponse>(EHttpType.GET, `/cvs/users/${userId}`);

				if (res.data && res.data.success && res.data.cvs) {
					set({
						userCVs: res.data.cvs,
						lastFetchTimeUserCVs: Date.now()
					});
				}

				return res;
			});
		},

		fetchUserCVsInBackground: async (userId: string): Promise<void> => {
			const state = get();
			const now = Date.now();

			// Check if cache is still valid
			if (state.userCVs.length > 0 && state.lastFetchTimeUserCVs) {
				const cacheAge = now - state.lastFetchTimeUserCVs;
				if (cacheAge < CACHE_DURATION) {
					console.log("User CVs cache is still valid, skipping fetch");
					return;
				}
			}

			// Check if already loading
			if (state.isLoadingUserCVs) {
				return;
			}

			set({ isLoadingUserCVs: true });

			try {
				const res = await handleRequest<ICVDataResponse>(EHttpType.GET, `/cvs/users/${userId}`);

				if (res.data && res.data.success && res.data.cvs) {
					set({
						userCVs: res.data.cvs,
						lastFetchTimeUserCVs: Date.now()
					});
				}
			} catch (error) {
				console.error("Failed to fetch user CVs in background:", error);
			} finally {
				set({ isLoadingUserCVs: false });
			}
		},

		getCV: async (cvId: string): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.GET, `/cvs/${cvId}`);
			});
		},

		createCV: async (
			userId: string
		): Promise<IApiResponse<ICVDataResponse>> => {
			get().handleSetCurrentCV(get().initialCV);

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `/cvs/users/${userId}`, new FormData());
			});
		},

		updateCV: async (
			cvId: string,
			title: string,
			avatar: File,
			personalInfo: IPersonalInfo,
			experiences: IExperience[],
			educations: IEducation[],
			skills: string[],
			isVisibility: boolean,
			color: string,
			template: string
		): Promise<IApiResponse<ICVDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				title,
				personalInfo,
				experiences,
				educations,
				skills,
				isVisibility,
				color,
				template
			}));
			if (avatar) formData.append("avatar", avatar);
			testFormData(formData);

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.PATCH, `/cvs/${cvId}`, formData);
			});
		},

		deleteCV: async (cvId: string): Promise<IApiResponse> => {
			return await get().handleRequest(async () => {
				const res = await handleRequest(EHttpType.DELETE, `/cvs/${cvId}`);

				if (res.data && res.data.success) {
					// Remove from user CVs if exists
					get().handleRemoveCVFromUserCVs(cvId);

					// Remove from CVs table if exists
					get().handleRemoveCVFromTable(cvId);
				}

				return res;
			});
		},

		duplicateCV: async (cvId: string): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				const res = await handleRequest<ICVDataResponse>(EHttpType.POST, `/cvs/${cvId}/duplicate`);

				if (res.data && res.data.cv) {
					set({ currentCV: res.data.cv });
					get().handleAddCVToUserCVs(res.data.cv);
				}

				return res;
			});
		},

		importFile: async (userId: string, file: File): Promise<IApiResponse<ICVDataResponse>> => {
			const formData = new FormData();
			formData.append("file", file);

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `/cvs/users/${userId}/import`, formData);
			});
		},

		handleUpdateCV: (cvData: Partial<ICV>) => {
			const currentState = get();
			set({
				currentCV: currentState.currentCV ? { ...currentState.currentCV, ...cvData, updatedAt: new Date().toISOString() } : null,
			} as Partial<ICVStore>);
		},

		handleSetCurrentCV: (cv: ICV | null): void => {
			set({ currentCV: cv });
		},

		handleSetCurrentStep: (step: number): void => {
			set({ currentStep: step });
		},

		handleClearCVList: (): void => {
			set({ cvList: [] });
		},

		handleGeneratePDF: async (currentCV: ICV, htmlContent?: string): Promise<void> => {
			try {
				const filename = `${currentCV.title.replace(
					/\s+/g,
					"_"
				)}.pdf`;

				// If HTML content is provided directly, use exportCustomHTML
				if (htmlContent) {
					await exportCustomHTML(htmlContent, filename);
					toast.success("Tải xuống CV thành công!");
					return;
				}

				// Check if we're in a browser environment
				if (typeof document === "undefined") {
					toast.error("PDF export chỉ khả dụng trên trình duyệt. Vui lòng thử lại trên client.");
					return;
				}

				// Try to find the preview element in the DOM
				const previewElement = document.getElementById("cv-preview-content");

				if (previewElement) {
					// Preview is available in DOM - use the existing method
					await exportToPDF("cv-preview-content", filename);
					toast.success("Tải xuống CV thành công!");
				} else {
					// Preview not in DOM - generate HTML from CV data
					toast.info("Đang tạo PDF...");
					const generatedHTML = await renderCVToHTMLAsync(currentCV);
					await exportCustomHTML(generatedHTML, filename);
					toast.success("Tải xuống CV thành công!");
				}
			} catch (error) {
				console.error("PDF generation error:", error);
				toast.error(
					`❌ Lỗi tạo PDF: ${error instanceof Error ? error.message : "Unknown error"
					}`
				);
			};
		},

		handleAddCVToUserCVs: (cv: ICV): void => {
			set({ userCVs: [cv, ...get().userCVs] });
		},

		handleRemoveCVFromUserCVs: (cvId: string): void => {
			set({
				userCVs: get().userCVs.filter((cv) => cv.id !== cvId),
			});
		},

		handleRemoveCVFromTable: (cvId: string): void => {
			set({
				CVsTable: get().CVsTable.filter((cv) => cv.id !== cvId),
			});
		},

		reset: () => {
			set({ ...initialState });
		},
	}),
);