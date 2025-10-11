import { EAISuggestionType, EUserRole, EUserStatus } from "./enum";

declare global {
    interface IUser {
        id: string
        email: string
        name: string
        role: EUserRole
        status: EUserStatus
    }

    interface ICV {
        id: string
        title: string
        personalInfo: IPersonalInfo
        experience: IExperience[]
        education: IEducation[]
        skills: string[]
        createdAt: string
        updatedAt: string
    }

    interface IPersonalInfo {
        fullName: string
        email: string
        phone: string
        location: string
        summary: string
    }

    interface IExperience {
        id: string
        company: string
        position: string
        startDate: string
        endDate: string
        description: string
    }

    interface IEducation {
        id: string
        school: string
        degree: string
        field: string
        startDate: string
        endDate: string
    }

    interface IAISuggestion {
        id: string
        type: EAISuggestionType
        section: string
        lineNumber?: number
        message: string
        suggestion: string
        applied: boolean
    }
}
export { };