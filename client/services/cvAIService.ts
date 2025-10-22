import { EHttpType, handleRequest, IApiResponse } from "@/lib/axiosInstance";

/**
 * CV AI Service - Handles all AI-related CV operations
 * Centralizes API calls for analyze, improve, and job description matching
 */

// Backend Response type matching Response.java from cv-service
interface ICVDataResponse extends IBackendResponse { }

/**
 * Phân Tích with AI to get strengths, weaknesses, and suggestions
 * @param title - CV title
 * @param personalInfo - Personal information
 * @param experiences - Work experiences
 * @param educations - Education history
 * @param skills - Skills list
 */
export const analyzeCV = async (
    title: string,
    personalInfo: IPersonalInfo,
    experiences: IExperience[],
    educations: IEducation[],
    skills: string[]
): Promise<IApiResponse<ICVDataResponse>> => {
    const cvData = {
        title,
        personalInfo,
        experiences,
        educations,
        skills
    };

    const formData = new FormData();
    formData.append("data", JSON.stringify(cvData));

    return await handleRequest<ICVDataResponse>(
        EHttpType.POST,
        `/cvs/analyze`,
        formData
    );
};

/**
 * Improve a specific section of CV using AI
 * @param section - Section name (e.g., "summary", "experience", "education", "skills")
 * @param content - Current content of the section
 */
export const improveCV = async (
    section: string,
    content: string
): Promise<IApiResponse<ICVDataResponse>> => {
    const improveCVData = {
        section,
        content
    };

    const formData = new FormData();
    formData.append("data", JSON.stringify(improveCVData));

    return await handleRequest<ICVDataResponse>(
        EHttpType.POST,
        `/cvs/improve`,
        formData
    );
};

/**
 * Phân Tích against a job description
 * @param title - CV title
 * @param personalInfo - Personal information
 * @param experiences - Work experiences
 * @param educations - Education history
 * @param skills - Skills list
 * @param jobDescription - Job description text (optional if file is provided)
 * @param jdFile - Job description file (PDF/DOCX) (optional if text is provided)
 * @param language - Output language: 'en' or 'vi' (default: 'vi')
 */
export const analyzeCVWithJobDescription = async (
    title: string,
    personalInfo: IPersonalInfo,
    experiences: IExperience[],
    educations: IEducation[],
    skills: string[],
    jobDescription?: string,
    jdFile?: File,
    language: string = "vi"
): Promise<IApiResponse<ICVDataResponse>> => {
    const cvData = {
        title,
        personalInfo,
        experiences,
        educations,
        skills,
        jobDescription,
        language
    };

    const formData = new FormData();
    formData.append("data", JSON.stringify(cvData));

    if (jdFile) {
        formData.append("jdFile", jdFile);
    }

    return await handleRequest<ICVDataResponse>(
        EHttpType.POST,
        `/cvs/analyze-with-jd`,
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
): Promise<IApiResponse<ICVDataResponse>> => {
    const formData = new FormData();
    formData.append("file", file);

    return await handleRequest<ICVDataResponse>(
        EHttpType.POST,
        `/cvs/users/${userId}/import`,
        formData
    );
};
