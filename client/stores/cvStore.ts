import { EHttpType, handleRequest, IApiResponse } from "@/lib/axiosInstance";
import { createStore, IBaseStore } from "@/lib/initialStore";
import { PDFExportService } from "@/services/pdfExportService";
import { renderCVToHTMLAsync } from "@/components/comons/cv-builder/CVRenderer";
import { toast } from "react-toastify";
import { testFormData } from "@/lib/utils";
import { applySuggestionToCV, getSectionDisplayName } from "@/lib/suggestionApplier";

interface ICVDataResponse {
	cv: ICV,
	cvs: ICV[]
}

export interface ICVStore extends IBaseStore {
	currentCVCreate: ICV | null
	currentCVUpdate: ICV | null
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
		privacy: string
	) => Promise<IApiResponse<ICVDataResponse>>;
	// ) => Promise<void>;
	updateCV: (
		cvId: string,
		title: string,
		avatar: File,
		personalInfo: IPersonalInfo,
		experiences: IExperience[],
		educations: IEducation[],
		skills: string[],
		privacy: string,
	) => Promise<IApiResponse<ICVDataResponse>>;
	// ) => Promise<void>;
	deleteCV: (
		cvId: string
	) => Promise<IApiResponse<void>>;
	duplicateCV: (
		cvId: string
	) => Promise<IApiResponse<ICVDataResponse>>;
	importFile: (
		userId: string,
		file: File
	) => Promise<IApiResponse<ICVDataResponse>>;
	analyzeCV: (
		title: string,
		personalInfo: IPersonalInfo,
		experiences: IExperience[],
		educations: IEducation[],
		skills: string[],
	) => Promise<IApiResponse<ICVDataResponse>>;
	analyzeCVWithJD: (
		jobDescription: string,
		jdFile: File | null,
		language: string,
		title: string,
		personalInfo: IPersonalInfo,
		experiences: IExperience[],
		educations: IEducation[],
		skills: string[],
	) => Promise<IApiResponse<ICVDataResponse>>;
	improveCV: (
		section: string,
		content: string,
		title: string,
		personalInfo: IPersonalInfo,
		experiences: IExperience[],
		educations: IEducation[],
		skills: string[],
	) => Promise<IApiResponse<ICVDataResponse>>;

	handleUpdateCV: (cvData: Partial<ICV>) => void;
	handleUpdateCVCreate: (cvData: Partial<ICV>) => void;
	handleUpdateCVUpdate: (cvData: Partial<ICV>) => void;
	handleSetCurrentStep: (step: number) => void;
	handleSetCurrentCVCreate: (cv: ICV | null) => void;
	handleSetCurrentCVUpdate: (cv: ICV | null) => void;
	handleSetAISuggestions: (suggestions: IAISuggestion[]) => void;
	handleSetJobDescription: (jd: string) => void;
	handleApplySuggestion: (id: string) => void;
	handleClearCVList: () => void;
	handleGeneratePDF: (cv: ICV, htmlContent?: string) => void;
}

const storeName = "cv";
const initialState = {
	currentCVCreate: null,
	currentCVUpdate: null,
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
			privacy: string,
			// ): Promise<void> => {
		): Promise<IApiResponse<ICVDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				title,
				personalInfo,
				experiences,
				educations,
				skills,
				privacy
			}));
			if (avatar) formData.append("avatar", avatar);
			testFormData(formData);

			return await get().handleRequest(async () => {
				const res = await handleRequest<ICVDataResponse>(EHttpType.POST, `/cvs/users/${userId}`, formData);
				console.log("Create CV Response:", res);
				return res;
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
			privacy: string
		): Promise<IApiResponse<ICVDataResponse>> => {
			// ): Promise<void> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				title,
				personalInfo,
				experiences,
				educations,
				skills,
				privacy
			}));
			if (avatar) formData.append("avatar", avatar);
			testFormData(formData);

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.PATCH, `/cvs/${cvId}`, formData);
			});
		},

		deleteCV: async (cvId: string): Promise<IApiResponse<void>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.DELETE, `/cvs/${cvId}`);
			});
		},

		duplicateCV: async (cvId: string): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `/cvs/${cvId}/duplicate`);
			});
		},

		importFile: async (userId: string, file: File): Promise<IApiResponse<ICVDataResponse>> => {
			const formData = new FormData();
			formData.append("file", file);

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `/cvs/users/${userId}/import`, formData);
			});
		},

		analyzeCV: async (
			title: string,
			personalInfo: IPersonalInfo,
			experiences: IExperience[],
			educations: IEducation[],
			skills: string[],): Promise<IApiResponse<ICVDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				title,
				personalInfo,
				experiences,
				educations,
				skills,
			}));

			return await get().handleRequest(async () => {
				const res = await handleRequest<ICVDataResponse>(EHttpType.POST, `/cvs/analyze`, formData);
				console.log("Analyze CV Response:", res);
				return res;
			});
		},

		analyzeCVWithJD: async (
			jobDescription: string,
			jdFile: File | null,
			language: string = "vi",
			title: string,
			personalInfo: IPersonalInfo,
			experiences: IExperience[],
			educations: IEducation[],
			skills: string[],
		): Promise<IApiResponse<ICVDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				jobDescription,
				language,
				title,
				personalInfo,
				experiences,
				educations,
				skills,
			}));
			if (jdFile) formData.append("jdFile", jdFile);

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `/cvs/analyze-with-jd`, formData);
			});
		},

		improveCV: async (
			section: string,
			content: string
		): Promise<IApiResponse<ICVDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				section,
				content,
			}));

			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.POST, `/cvs/improve`, formData);
			});
		},

		handleUpdateCV: (cvData: Partial<ICV>) => {
			// Deprecated - use handleUpdateCVCreate or handleUpdateCVUpdate instead
			const currentState = get();
			if (currentState.currentCVCreate) {
				set({
					currentCVCreate: { ...currentState.currentCVCreate, ...cvData, updatedAt: new Date().toISOString() },
				} as Partial<ICVStore>);
			} else if (currentState.currentCVUpdate) {
				set({
					currentCVUpdate: { ...currentState.currentCVUpdate, ...cvData, updatedAt: new Date().toISOString() },
				} as Partial<ICVStore>);
			}
		},

		handleUpdateCVCreate: (cvData: Partial<ICV>) => {
			const currentState = get();
			set({
				currentCVCreate: currentState.currentCVCreate ? { ...currentState.currentCVCreate, ...cvData, updatedAt: new Date().toISOString() } : null,
			} as Partial<ICVStore>);
		},

		handleUpdateCVUpdate: (cvData: Partial<ICV>) => {
			const currentState = get();
			set({
				currentCVUpdate: currentState.currentCVUpdate ? { ...currentState.currentCVUpdate, ...cvData, updatedAt: new Date().toISOString() } : null,
			} as Partial<ICVStore>);
		},

		handleSetCurrentStep: (step: number): void => {
			set({ currentStep: step });
		},

		handleSetCurrentCVCreate: (cv: ICV | null): void => {
			set({ currentCVCreate: cv });
		},

		handleSetCurrentCVUpdate: (cv: ICV | null): void => {
			set({ currentCVUpdate: cv });
		}, handleSetAISuggestions: (suggestions: IAISuggestion[]): void => {
			set({ aiSuggestions: suggestions });
		},

		handleSetJobDescription: (jd: string): void => {
			set({ jobDescription: jd });
		},

		handleApplySuggestion: (id: string): void => {
			const currentState = get();
			const suggestion = currentState.aiSuggestions.find(s => s.id === id);

			if (!suggestion) {
				toast.error("Không tìm thấy gợi ý");
				return;
			}

			// Apply the suggestion to the appropriate CV state
			const updatedCVCreate = applySuggestionToCV(currentState.currentCVCreate, suggestion);
			const updatedCVUpdate = applySuggestionToCV(currentState.currentCVUpdate, suggestion);

			// Check if any CV was updated
			if (!updatedCVCreate && !updatedCVUpdate) {
				toast.warning(`Không thể áp dụng gợi ý cho phần "${suggestion.section}"`);
				return;
			}

			// Mark suggestion as applied
			const updatedSuggestions = currentState.aiSuggestions.map(s =>
				s.id === id ? { ...s, applied: true } : s
			);

			// Update state
			set({
				aiSuggestions: updatedSuggestions,
				currentCVCreate: updatedCVCreate,
				currentCVUpdate: updatedCVUpdate
			} as Partial<ICVStore>);

			const sectionName = getSectionDisplayName(suggestion.section);
			toast.success(`✅ Đã áp dụng gợi ý cho "${sectionName}"`);
		}, handleClearCVList: (): void => {
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
					await PDFExportService.exportCustomHTML(htmlContent, filename);
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
					await PDFExportService.exportToPDF("cv-preview-content", filename);
					toast.success("Tải xuống CV thành công!");
				} else {
					// Preview not in DOM - generate HTML from CV data
					toast.info("Đang tạo PDF...");
					const generatedHTML = await renderCVToHTMLAsync(currentCV);
					await PDFExportService.exportCustomHTML(generatedHTML, filename);
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

		reset: () => {
			set({ ...initialState });
		},
	}),
);