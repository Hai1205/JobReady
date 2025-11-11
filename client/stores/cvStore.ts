import { EHttpType, handleRequest, IApiResponse } from "@/lib/axiosInstance";
import { createStore, IBaseStore } from "@/lib/initialStore";
import { renderCVToHTMLAsync } from "@/components/comons/cv-builder/CVRenderer";
import { toast } from "react-toastify";
import { testFormData } from "@/lib/utils";
import { applySuggestionToCV, getSectionDisplayName } from "@/lib/suggestionApplier";
import { exportCustomHTML, exportToPDF } from "@/services/pdfExportService";

interface ICVDataResponse {
	cv: ICV,
	cvs: ICV[],
}

interface IAIDataResponse {
	suggestions: IAISuggestion[],
	analyze: IAnalyzeResult,
	improvedSection: string,
	matchScore: number,
	missingKeywords?: string[],
	parsedJobDescription?: IJobDescriptionResult,
}

export interface ICVStore extends IBaseStore {
	currentCV: ICV | null
	cvList: ICV[]
	userCVs: ICV[]
	CVsTable: ICV[]
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
	analyzeCV: (
		title: string,
		personalInfo: IPersonalInfo,
		experiences: IExperience[],
		educations: IEducation[],
		skills: string[],
	) => Promise<IApiResponse<IAIDataResponse>>;
	analyzeCVWithJD: (
		jobDescription: string,
		jdFile: File | null,
		language: string,
		title: string,
		personalInfo: IPersonalInfo,
		experiences: IExperience[],
		educations: IEducation[],
		skills: string[],
	) => Promise<IApiResponse<IAIDataResponse>>;
	improveCV: (
		section: string,
		content: string,
	) => Promise<IApiResponse<IAIDataResponse>>;

	handleSetCurrentCV: (cv: ICV | null) => void;
	handleUpdateCV: (cvData: Partial<ICV>) => void;
	handleSetCurrentStep: (step: number) => void;
	handleSetAISuggestions: (suggestions: IAISuggestion[]) => void;
	handleSetJobDescription: (jd: string) => void;
	handleApplySuggestion: (id: string) => void;
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
	aiSuggestions: [],
	jobDescription: "",
};

export const useCVStore = createStore<ICVStore>(
	storeName,
	initialState,
	(set, get) => ({
		getAllCVs: async (): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				const res = await handleRequest<ICVDataResponse>(EHttpType.GET, `/cvs`);

				if (res.data && res.data.success && res.data.cvs) {
					set({ CVsTable: res.data.cvs });
				}

				return res;
			});
		},

		getUserCVs: async (userId: string): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				const res = await handleRequest<ICVDataResponse>(EHttpType.GET, `/cvs/users/${userId}`);
				console.log("User CVs:", res);

				if (res.data && res.data.success && res.data.cvs) {
					set({ userCVs: res.data.cvs });
				}

				return res;
			});
		},

		getCV: async (cvId: string): Promise<IApiResponse<ICVDataResponse>> => {
			return await get().handleRequest(async () => {
				return await handleRequest(EHttpType.GET, `/cvs/${cvId}`);
			});
		},

		createCV: async (
			userId: string
		): Promise<IApiResponse<ICVDataResponse>> => {
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

		analyzeCV: async (
			title: string,
			personalInfo: IPersonalInfo,
			experiences: IExperience[],
			educations: IEducation[],
			skills: string[],): Promise<IApiResponse<IAIDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				title,
				personalInfo,
				experiences,
				educations,
				skills,
			}));

			return await get().handleRequest(async () => {
				const res = await handleRequest<IAIDataResponse>(EHttpType.POST, `/ai/analyze`, formData);
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
		): Promise<IApiResponse<IAIDataResponse>> => {
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
				const res = await handleRequest<IAIDataResponse>(EHttpType.POST, `/ai/analyze-with-jd`, formData);
				console.log("Analyze CV with JD Response:", res);
				return res;
			});
		},

		improveCV: async (
			section: string,
			content: string
		): Promise<IApiResponse<IAIDataResponse>> => {
			const formData = new FormData();
			formData.append("data", JSON.stringify({
				section,
				content,
			}));

			return await get().handleRequest(async () => {
				return await handleRequest<IAIDataResponse>(EHttpType.POST, `/ai/improve`, formData);
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

		handleSetJobDescription: (jd: string): void => {
			set({ jobDescription: jd });
		},

		handleSetAISuggestions: (suggestions: IAISuggestion[]): void => {
			set({ aiSuggestions: suggestions });
		},

		handleApplySuggestion: (id: string): void => {
			const currentState = get();
			const suggestion = currentState.aiSuggestions.find(s => s.id === id);

			if (!suggestion) {
				toast.error("Không tìm thấy gợi ý");
				return;
			}

			// Apply the suggestion to the appropriate CV state
			const updatedCVCreate = applySuggestionToCV(currentState.currentCV, suggestion);

			// Check if any CV was updated
			if (!updatedCVCreate) {
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
				currentCV: updatedCVCreate
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