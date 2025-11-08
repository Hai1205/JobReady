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
        color: string
        template: string
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
        applied: boolean
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

    interface IStats {
        totalCVs?: number
        totalUsers?: number
        [key: string]: number | undefined
    }
}
export { };