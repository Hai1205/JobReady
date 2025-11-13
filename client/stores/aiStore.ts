import { EHttpType, handleRequest, IApiResponse } from "@/lib/axiosInstance";
import { createStore, IBaseStore } from "@/lib/initialStore";
import { applySuggestionToCV, getSectionDisplayName } from "@/lib/suggestionApplier";
import { toast } from "react-toastify";

interface IAIDataResponse {
	suggestions: IAISuggestion[],
	analyze: IAnalyzeResult,
	improvedSection: string,
	matchScore: number,
	missingKeywords?: string[],
	parsedJobDescription?: IJobDescriptionResult,
}

export interface IAIStore extends IBaseStore {
	aiSuggestions: IAISuggestion[]
	jobDescription: string
	isAnalyzing: boolean

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

	handleSetAISuggestions: (suggestions: IAISuggestion[]) => void;
	handleSetJobDescription: (jd: string) => void;
	handleApplySuggestion: (id: string) => void;
	handleSetIsAnalyzing: (isAnalyzing: boolean) => void;
}

const storeName = "ai";
const initialState = {
	aiSuggestions: [],
	jobDescription: "",
	isAnalyzing: false,
};

export const useAIStore = createStore<IAIStore>(
	storeName,
	initialState,
	(set, get) => ({
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

			// Mark suggestion as applied
			const updatedSuggestions = currentState.aiSuggestions.map(s =>
				s.id === id ? { ...s, applied: true } : s
			);

			// Update state
			set({
				aiSuggestions: updatedSuggestions,
			} as Partial<IAIStore>);

			const sectionName = getSectionDisplayName(suggestion.section);
			toast.success(`✅ Đã áp dụng gợi ý cho "${sectionName}"`);
		},

		handleSetIsAnalyzing: (isAnalyzing: boolean): void => {
			set({ isAnalyzing });
		},

		reset: () => {
			set({ ...initialState });
		},
	}),
);