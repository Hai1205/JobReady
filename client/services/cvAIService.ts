import { EHttpType, handleRequest, IApiResponse } from "@/lib/axiosInstance";

/**
 * CV AI Service - Handles all AI-related CV operations
 * Centralizes API calls for analyze, improve, and job description matching
 */

// Response types matching backend ResponseData structure
export interface AIAnalyzeResponse {
    statusCode: number;
    message: string;
    data: {
        analyze: string;
        suggestions: IAISuggestion[];
    };
}

export interface AIImproveResponse {
    statusCode: number;
    message: string;
    data: {
        improvedSection: string;
    };
}

export interface AIAnalyzeWithJDResponse {
    statusCode: number;
    message: string;
    data: {
        analyze: string;
        matchScore: number;
        parsedJobDescription: IJobDescriptionResult;
        missingKeywords: string[];
    };
}

export interface CVImportResponse {
    statusCode: number;
    message: string;
    data: {
        cv: ICV;
        extractedText: string;
    };
}

export interface IJobDescriptionResult {
    jobTitle: string;
    company: string;
    jobLevel: string;
    jobType: string;
    salary: string;
    location: string;
    responsibilities: string[];
    requirements: string[];
    requiredSkills: string[];
    preferredSkills: string[];
    benefits: string[];
}

/**
 * Analyze CV with AI to get strengths, weaknesses, and suggestions
 * @param cvId - UUID of the CV to analyze
 */
export const analyzeCV = async (
    cvId: string
): Promise<IApiResponse<AIAnalyzeResponse>> => {
    return await handleRequest<AIAnalyzeResponse>(
        EHttpType.POST,
        `/cvs/analyze/${cvId}`
    );
};

/**
 * Improve a specific section of CV using AI
 * @param cvId - UUID of the CV
 * @param section - Section name (e.g., "summary", "experience", "education", "skills")
 * @param content - Current content of the section
 */
export const improveCV = async (
    cvId: string,
    section: string,
    content: string
): Promise<IApiResponse<AIImproveResponse>> => {
    const formData = new FormData();
    formData.append("section", section);
    formData.append("content", content);

    return await handleRequest<AIImproveResponse>(
        EHttpType.POST,
        `/cvs/improve/${cvId}`,
        formData
    );
};

/**
 * Analyze CV against a job description
 * @param cvId - UUID of the CV
 * @param jobDescription - Job description text (optional if file is provided)
 * @param jdFile - Job description file (PDF/DOCX) (optional if text is provided)
 * @param language - Output language: 'en' or 'vi' (default: 'vi')
 */
export const analyzeCVWithJobDescription = async (
    cvId: string,
    jobDescription?: string,
    jdFile?: File,
    language: string = "vi"
): Promise<IApiResponse<AIAnalyzeWithJDResponse>> => {
    const formData = new FormData();

    if (jobDescription) {
        formData.append("jobDescription", jobDescription);
    }

    if (jdFile) {
        formData.append("jdFile", jdFile);
    }

    formData.append("language", language);

    return await handleRequest<AIAnalyzeWithJDResponse>(
        EHttpType.POST,
        `/cvs/analyze-with-jd/${cvId}`,
        formData
    );
};

/**
 * Import CV from file (PDF/DOCX)
 * @param userId - UUID of the user
 * @param file - CV file to import
 */
export const importCVFile = async (
    userId: string,
    file: File
): Promise<IApiResponse<CVImportResponse>> => {
    const formData = new FormData();
    formData.append("file", file);

    return await handleRequest<CVImportResponse>(
        EHttpType.POST,
        `/cvs/users/${userId}/import`,
        formData
    );
};
