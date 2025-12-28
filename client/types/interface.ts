import { EAISuggestionType, EUserRole, EUserStatus } from "./enum";

declare global {
    // Pagination types
    interface IPageable {
        page: number;
        size: number;
        sort?: string;
    }

    interface IPageResponse<T> {
        content: T[];
        totalElements: number;
        totalPages: number;
        currentPage: number;
        pageSize: number;
        hasNext: boolean;
        hasPrevious: boolean;
        first: boolean;
        last: boolean;
    }

    interface IUser {
        id: string
        username: string
        email: string
        fullname: string
        phone?: string
        location?: string
        birth?: string
        summary?: string
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
        isVisibility: boolean
        color: string
        template: string
        font: string
        createdAt?: string
        updatedAt?: string
    }

    interface IPersonalInfo {
        id?: string
        fullname: string
        email: string
        phone: string
        location: string
        birth: string
        summary: string
        avatarUrl?: string
        avatarPublicId?: string
    }

    interface IExperience {
        id?: string
        company: string
        position: string
        startDate: string
        endDate: string
        description: string
    }

    interface IEducation {
        id?: string
        school: string
        degree: string
        field: string
        startDate: string
        endDate: string
    }

    interface IAISuggestion {
        id: string
        type: EAISuggestionType | string
        section: string
        lineNumber?: number
        message: string
        suggestion: string
        data?: {
            skills?: string[]
            text?: string
            description?: string
            startDate?: string
            endDate?: string
            field?: string
            degree?: string
            [key: string]: any
        }
        applied?: boolean
        rejected?: boolean
    }

    interface IAnalyzeResult {
        overallScore: number
        strengths: string[]
        weaknesses: string[]
        suggestions: IAISuggestion[]
    }

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

    export interface IDashboardStats {
        totalUsers: number;
        activeUsers: number;
        pendingUsers: number;
        bannedUsers: number;
        usersCreatedThisMonth: number;
        totalCVs: number;
        publicCVs: number;
        privateCVs: number;
        cvsCreatedThisMonth: number;
        recentActivities: IActivityInfo[];
    }

    export interface IActivityInfo {
        id: string;
        type: string;
        description: string;
        timestamp: string;
        userId: string;
    }
}
export { };