import { EHttpType, handleRequest, IApiResponse } from "@/lib/axiosInstance";
import { createStore, IBaseStore } from "@/lib/initialStore";

interface ICVDataResponse {
	cv: ICV,
	cvs: ICV[]
}

export interface ICVStore extends IBaseStore {
	currentCV: ICV | null
	cvList: ICV[]
	currentStep: number
	aiSuggestions: IAISuggestion[]

	getAllCVs: () => Promise<IApiResponse<ICVDataResponse>>;
	getUserCVs: (userId: string) => Promise<IApiResponse<ICVDataResponse>>;
	getCV: (
		cvId: string
	) => Promise<IApiResponse<ICVDataResponse>>;
	createCV: (
		userId: string,
		title: string,
		personalInfo: IPersonalInfo,
		Experiences: IExperience[],
		Educations: IEducation[],
		Skills: string[],
	) => Promise<IApiResponse<ICVDataResponse>>;
	updateCV: (
		cvId: string,
		title: string,
		personalInfo: IPersonalInfo,
		Experiences: IExperience[],
		Educations: IEducation[],
		Skills: string[],
	) => Promise<IApiResponse<ICVDataResponse>>;
	deleteCV: (
		cvId: string
	) => Promise<IApiResponse<ICVDataResponse>>;
	importFile: (
		userId: string,
		file: File
	) => Promise<IApiResponse<ICVDataResponse>>;
	analyseCV: (
		cvId: string
	) => Promise<IApiResponse<ICVDataResponse>>;
	improveCV: (
		cvId: string, section: string, content: string
	) => Promise<IApiResponse<ICVDataResponse>>;

	handleUpdateCV: (cvData: Partial<ICV>) => void;
	handleSetCurrentStep: (step: number) => void;
	handleSetCurrentCV: (cv: ICV | null) => void;
	handleSetAISuggestions: (suggestions: IAISuggestion[]) => void
	handleApplySuggestion: (id: string) => void
	handleClearCVList: () => void
}

const storeName = "cv";
const initialState = {
	currentCV: null,
	cvList: [],
	currentStep: 0,
	aiSuggestions: [],
};

export const useCVStore = createStore<ICVStore>(
	storeName,
	initialState,
	(set, get) => ({
		getAllCVs: async (): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.GET, `/cvs`);
			});
		},

		getUserCVs: async (userId: string): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.GET, `/cvs/user/${userId}`);
			});
		},

		getCV: async (cvId: string): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.GET, `/cvs/${cvId}`);
			});
		},

		createCV: async (
			userId: string,
			title: string,
			personalInfo: IPersonalInfo,
			Experiences: IExperience[],
			Educations: IEducation[],
			Skills: string[],
		): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				const formData = new FormData();
				formData.append("title", title);
				formData.append("personalInfo", JSON.stringify(personalInfo));
				formData.append("Experiences", JSON.stringify(Experiences));
				formData.append("Educations", JSON.stringify(Educations));
				formData.append("Skills", JSON.stringify(Skills));

				return await handleRequest(EHttpType.POST, `/cvs/users/${userId}`, formData);
			});
		},

		updateCV: async (
			cvId: string,
			title: string,
			personalInfo: IPersonalInfo,
			Experiences: IExperience[],
			Educations: IEducation[],
			Skills: string[],
		): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				const formData = new FormData();
				formData.append("title", title);
				formData.append("personalInfo", JSON.stringify(personalInfo));
				formData.append("Experiences", JSON.stringify(Experiences));
				formData.append("Educations", JSON.stringify(Educations));
				formData.append("Skills", JSON.stringify(Skills));

				return await handleRequest(EHttpType.PATCH, `/cvs/${cvId}`, formData);
			});
		},

		deleteCV: async (cvId: string): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.DELETE, `/cvs/${cvId}`);
			});
		},

		importFile: async (userId: string, file: File): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				const formData = new FormData();
				formData.append("file", file);

				return await handleRequest(EHttpType.POST, `/cvs/users/${userId}/import`, formData);
			});
		},

		analyseCV: async (cvId: string): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `/cvs/analyse/${cvId}`);
			});
		},

		improveCV: async (cvId: string, section: string, content: string): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				const formData = new FormData();
				formData.append("section", section);
				formData.append("content", content);

				return await handleRequest(EHttpType.POST, `/cvs/improve/${cvId}`, formData);
			});
		},

		handleUpdateCV: (cvData: Partial<ICV>) => {
			const currentState = get();
			set({
				currentCV: currentState.currentCV ? { ...currentState.currentCV, ...cvData, updatedAt: new Date().toISOString() } : null,
			} as Partial<ICVStore>);
		},

		handleSetCurrentStep: (step: number): void => {
			set({ currentStep: step });
		},

		handleSetCurrentCV: (cv: ICV | null): void => {
			set({ currentCV: cv });
		},

		handleSetAISuggestions: (suggestions: IAISuggestion[]): void => {
			set({ aiSuggestions: suggestions });
		},


		reset: () => {
			set({ ...initialState });
		},
	}),
);