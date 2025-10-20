import { EHttpType, handleRequest, IApiResponse } from "@/lib/axiosInstance";
import { createStore, IBaseStore } from "@/lib/initialStore";
import { PDFExportService } from "@/services/pdfExportService";
import { toast } from "react-toastify";

interface ICVDataResponse {
	cv: ICV,
	cvs: ICV[]
}

export interface ICVStore extends IBaseStore {
	currentCV: ICV | null
	cvList: ICV[]
	currentStep: number
	aiSuggestions: IAISuggestion[]
	jobDescription: string

	getAllCVs: () => Promise<IApiResponse<ICVDataResponse>>;
	getUserCVs: (userId: string) => Promise<IApiResponse<ICVDataResponse>>;
	getCV: (
		cvId: string
	) => Promise<IApiResponse<ICVDataResponse>>;
	createCV: (
		userId: string,
		title: string,
		avatar: File,
		personalInfo: IPersonalInfo,
		experiences: IExperience[],
		educations: IEducation[],
		skills: string[],
	) => Promise<IApiResponse<ICVDataResponse>>;
	updateCV: (
		cvId: string,
		title: string,
		avatar: File,
		personalInfo: IPersonalInfo,
		experiences: IExperience[],
		educations: IEducation[],
		skills: string[],
	) => Promise<IApiResponse<ICVDataResponse>>;
	deleteCV: (
		cvId: string
	) => Promise<IApiResponse<ICVDataResponse>>;
	importFile: (
		userId: string,
		file: File
	) => Promise<IApiResponse<ICVDataResponse>>;
	analyzeCV: (
		cvId: string
	) => Promise<IApiResponse<ICVDataResponse>>;
	analyzeCVWithJD: (
		cvId: string,
		jobDescription: string,
		jdFile?: File,
		language?: string
	) => Promise<IApiResponse<ICVDataResponse>>;
	improveCV: (
		cvId: string, section: string, content: string
	) => Promise<IApiResponse<ICVDataResponse>>;

	handleUpdateCV: (cvData: Partial<ICV>) => void;
	handleSetCurrentStep: (step: number) => void;
	handleSetCurrentCV: (cv: ICV | null) => void;
	handleSetAISuggestions: (suggestions: IAISuggestion[]) => void;
	handleSetJobDescription: (jd: string) => void;
	handleApplySuggestion: (id: string) => void;
	handleClearCVList: () => void;
	handleGeneratePDF: (cv: ICV) => void;
}

const storeName = "cv";
const initialState = {
	currentCV: null,
	cvList: [],
	currentStep: 0,
	aiSuggestions: [],
	jobDescription: "",
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
				return await handleRequest(EHttpType.GET, `/cvs/users/${userId}`);
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
			avatar: File,
			personalInfo: IPersonalInfo,
			experiences: IExperience[],
			educations: IEducation[],
			skills: string[],
		): Promise<IApiResponse<ICVDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				title,
				personalInfo,
				experiences,
				educations,
				skills
			}));
			if (avatar) formData.append("avatar", avatar);

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `/cvs/users/${userId}`, formData);
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
		): Promise<IApiResponse<ICVDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				title,
				personalInfo,
				experiences,
				educations,
				skills
			}));
			if (avatar) formData.append("avatar", avatar);

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.PATCH, `/cvs/${cvId}`, formData);
			});
		},

		deleteCV: async (cvId: string): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.DELETE, `/cvs/${cvId}`);
			});
		},

		importFile: async (userId: string, file: File): Promise<IApiResponse<ICVDataResponse>> => {
			const formData = new FormData();
			formData.append("file", file);

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `/cvs/users/${userId}/import`, formData);
			});
		},

		analyzeCV: async (cvId: string): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `/cvs/analyze/${cvId}`, undefined);
			});
		},

		analyzeCVWithJD: async (
			cvId: string,
			jobDescription: string,
			jdFile?: File,
			language: string = "vi"
		): Promise<IApiResponse<ICVDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				jobDescription,
				language,
			}));
			if (jdFile) formData.append("jdFile", jdFile);

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `/cvs/analyze-with-jd/${cvId}`, formData);
			});
		},

		improveCV: async (cvId: string, section: string, content: string): Promise<IApiResponse<ICVDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				section,
				content,
			}));

			return await get().handleRequest(async () => {
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

		handleSetJobDescription: (jd: string): void => {
			set({ jobDescription: jd });
		},

		handleApplySuggestion: (id: string): void => {
			const currentState = get();
			const updatedSuggestions = currentState.aiSuggestions.map(suggestion =>
				suggestion.id === id ? { ...suggestion, applied: true } : suggestion
			);
			set({ aiSuggestions: updatedSuggestions });
		},

		handleClearCVList: (): void => {
			set({ cvList: [] });
		},

		handleGeneratePDF: async (currentCV: ICV): Promise<void> => {
			try {
				const filename = `CV_${currentCV.title.replace(
					/\s+/g,
					"_"
				)}.pdf`;

				// Export using PDFShift API
				await PDFExportService.exportToPDF("cv-preview-content", filename);

				toast.success("Tải xuống CV thành công!");
			} catch (error) {
				console.error("PDF generation error:", error);
				toast.error(
					`❌ Lỗi tạo PDF: ${error instanceof Error ? error.message : "Unknown error"
					}`
				);
			};
		},

		reset: () => {
			set({ ...initialState });
		},
	}),
);