import { EAISuggestionType, EPrivacy, EUserRole, EUserStatus } from "./enum";

declare global {
    interface IUser {
        id: string
        username: string
        email: string
        fullname: string
        avatarUrl?: string
        role: EUserRole
        status: EUserStatus
    }

    interface ICV {
        id: string
        userId?: string
        avatar?: File | null
        title: string
        personalInfo: IPersonalInfo
        experiences: IExperience[]
        educations: IEducation[]
        skills: string[]
        privacy: EPrivacy
        createdAt?: string
        updatedAt?: string
    }

    interface IPersonalInfo {
        id?: string
        fullname: string
        email: string
        phone: string
        location: string
        summary: string
        // avatar?: File | null  // For upload
        avatarUrl?: string    // From backend
        avatarPublicId?: string // Cloudinary ID
    }

    interface IExperience {
        id?: string  // Optional for new entries, UUID string from backend
        company: string
        position: string
        startDate: string  // Format: YYYY-MM
        endDate: string    // Format: YYYY-MM or "Present"
        description: string
    }

    interface IEducation {
        id?: string  // Optional for new entries, UUID string from backend
        school: string
        degree: string
        field: string
        startDate: string  // Format: YYYY-MM
        endDate: string    // Format: YYYY-MM
    }

    interface IAISuggestion {
        id: string
        type: EAISuggestionType | string  // "improvement", "warning", "error"
        section: string  // "summary", "experience", "education", "skills"
        lineNumber?: number
        message: string
        suggestion: string
        applied: boolean
    }

    // Job Description Analysis Result
    interface IJobDescriptionResult {
        jobTitle: string
        company: string
        jobLevel: string
        jobType: string
        salary: string
        location: string
        responsibilities: string[]
        requirements: string[]
        requiredSkills: string[]
        preferredSkills: string[]
        benefits: string[]
    }

    // Backend Response Structure (matches Response.java)
    interface IBackendResponse {
        statusCode: number
        message: string

        // CV related data
        cv?: ICV
        cvs?: ICV[]
        experience?: IExperience
        experiences?: IExperience[]
        education?: IEducation
        educations?: IEducation[]
        skills?: string[]

        // AI Analyze and Improvement
        analyze?: string
        improvedSection?: string
        suggestions?: IAISuggestion[]
        extractedText?: string
        matchScore?: number
        parsedJobDescription?: IJobDescriptionResult
        missingKeywords?: string[]

        // Pagination and stats
        pagination?: IPagination
        stats?: IStats
        additionalData?: Record<string, unknown>
    }

    // Axios Response wrapper
    interface IAPIResponse<T = IBackendResponse> {
        data: T
        status: number
        statusText: string
        headers: Record<string, string>
    }

    // Pagination structure
    interface IPagination {
        currentPage: number
        pageSize: number
        totalPages: number
        totalItems: number
    }

    // Stats structure
    interface IStats {
        totalCVs?: number
        totalUsers?: number
        [key: string]: number | undefined
    }
}
export { };