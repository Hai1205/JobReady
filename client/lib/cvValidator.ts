/**
 * CV Data Validator & Enhancer
 * 
 * Utilities to validate and enhance parsed CV data
 */

/**
 * Validate email format
 */
export function validateEmail(email: string): boolean {
    const emailRegex = /^[\w._%+-]+@[\w.-]+\.[a-zA-Z]{2,}$/;
    return emailRegex.test(email);
}

/**
 * Validate phone number (Vietnamese format)
 */
export function validatePhone(phone: string): boolean {
    const phoneRegex = /^(?:\+84|0)(?:\d{9,10})$/;
    return phoneRegex.test(phone.replace(/[\s-()]/g, ''));
}

/**
 * Format phone number to standard format
 */
export function formatPhone(phone: string): string {
    // Remove all non-digit characters
    const cleaned = phone.replace(/\D/g, '');

    // Vietnamese format: 0xxx-xxx-xxx or +84 xxx-xxx-xxx
    if (cleaned.startsWith('84') && cleaned.length === 11) {
        return `+84 ${cleaned.slice(2, 5)}-${cleaned.slice(5, 8)}-${cleaned.slice(8)}`;
    } else if (cleaned.startsWith('0') && cleaned.length === 10) {
        return `${cleaned.slice(0, 4)}-${cleaned.slice(4, 7)}-${cleaned.slice(7)}`;
    }

    return phone; // Return original if can't format
}

/**
 * Validate date format
 */
export function validateDate(date: string): boolean {
    // Accept formats: YYYY, MM/YYYY, YYYY-MM, or "Present"
    if (date.toLowerCase() === 'present') return true;

    const datePatterns = [
        /^\d{4}$/,                    // YYYY
        /^\d{1,2}\/\d{4}$/,          // MM/YYYY
        /^\d{4}-\d{2}$/,             // YYYY-MM
    ];

    return datePatterns.some(pattern => pattern.test(date));
}

/**
 * Format date to standard format (YYYY-MM)
 */
export function formatDate(date: string): string {
    if (date.toLowerCase() === 'present') return 'Present';

    // MM/YYYY -> YYYY-MM
    const mmYyyyMatch = date.match(/^(\d{1,2})\/(\d{4})$/);
    if (mmYyyyMatch) {
        const month = mmYyyyMatch[1].padStart(2, '0');
        return `${mmYyyyMatch[2]}-${month}`;
    }

    // YYYY -> YYYY-01
    const yyyyMatch = date.match(/^(\d{4})$/);
    if (yyyyMatch) {
        return `${yyyyMatch[1]}-01`;
    }

    return date;
}

/**
 * Clean and normalize text
 */
export function cleanText(text: string): string {
    return text
        .replace(/\s+/g, ' ')           // Replace multiple spaces with single space
        .replace(/\n+/g, '\n')          // Replace multiple newlines with single newline
        .trim();                         // Trim whitespace
}

/**
 * Validate CV completeness
 */
export function validateCV(cv: ICV): {
    isValid: boolean;
    errors: string[];
    warnings: string[];
} {
    const errors: string[] = [];
    const warnings: string[] = [];

    // Required fields
    if (!cv.personalInfo.fullname || cv.personalInfo.fullname === 'Chưa có thông tin') {
        errors.push('Thiếu họ tên');
    }

    if (!cv.personalInfo.email) {
        errors.push('Thiếu email');
    } else if (!validateEmail(cv.personalInfo.email)) {
        errors.push('Email không hợp lệ');
    }

    if (!cv.personalInfo.phone) {
        warnings.push('Thiếu số điện thoại');
    } else if (!validatePhone(cv.personalInfo.phone)) {
        warnings.push('Số điện thoại không hợp lệ');
    }

    // Experience validation
    if (cv.experiences.length === 0) {
        warnings.push('Chưa có kinh nghiệm làm việc');
    } else {
        cv.experiences.forEach((exp, index) => {
            if (!exp.company) warnings.push(`Kinh nghiệm ${index + 1}: Thiếu tên công ty`);
            if (!exp.position) warnings.push(`Kinh nghiệm ${index + 1}: Thiếu vị trí`);
            if (!validateDate(exp.startDate)) warnings.push(`Kinh nghiệm ${index + 1}: Ngày bắt đầu không hợp lệ`);
            if (!validateDate(exp.endDate)) warnings.push(`Kinh nghiệm ${index + 1}: Ngày kết thúc không hợp lệ`);
        });
    }

    // Education validation
    if (cv.educations.length === 0) {
        warnings.push('Chưa có học vấn');
    } else {
        cv.educations.forEach((edu, index) => {
            if (!edu.school) warnings.push(`Học vấn ${index + 1}: Thiếu tên trường`);
            if (!edu.degree) warnings.push(`Học vấn ${index + 1}: Thiếu bằng cấp`);
        });
    }

    // Skills validation
    if (cv.skills.length === 0) {
        warnings.push('Chưa có kỹ năng');
    }

    return {
        isValid: errors.length === 0,
        errors,
        warnings
    };
}

/**
 * Enhance parsed CV data with formatting and validation
 */
export function enhanceCV(cv: ICV): ICV {
    return {
        ...cv,
        personalInfo: {
            ...cv.personalInfo,
            fullname: cleanText(cv.personalInfo.fullname),
            email: cv.personalInfo.email.toLowerCase().trim(),
            phone: cv.personalInfo.phone ? formatPhone(cv.personalInfo.phone) : '',
            location: cleanText(cv.personalInfo.location),
            summary: cleanText(cv.personalInfo.summary || ''),
        },
        experiences: cv.experiences.map(exp => ({
            ...exp,
            company: cleanText(exp.company),
            position: cleanText(exp.position),
            startDate: formatDate(exp.startDate),
            endDate: formatDate(exp.endDate),
            description: cleanText(exp.description),
        })),
        educations: cv.educations.map(edu => ({
            ...edu,
            school: cleanText(edu.school),
            degree: cleanText(edu.degree),
            field: cleanText(edu.field),
            startDate: formatDate(edu.startDate),
            endDate: formatDate(edu.endDate),
        })),
        skills: cv.skills
            .map(skill => cleanText(skill))
            .filter(skill => skill.length > 0)
            .filter((skill, index, self) => self.indexOf(skill) === index), // Remove duplicates
    };
}

/**
 * Get CV completeness score (0-100)
 */
export function getCompletenessScore(cv: ICV): number {
    let score = 0;
    const maxScore = 100;

    // Personal info (40 points)
    if (cv.personalInfo.fullname && cv.personalInfo.fullname !== 'Chưa có thông tin') score += 10;
    if (cv.personalInfo.email && validateEmail(cv.personalInfo.email)) score += 10;
    if (cv.personalInfo.phone && validatePhone(cv.personalInfo.phone)) score += 5;
    if (cv.personalInfo.location) score += 5;
    if (cv.personalInfo.summary) score += 10;

    // Experience (30 points)
    if (cv.experiences.length > 0) {
        score += 15;
        const hasCompleteExp = cv.experiences.some(exp =>
            exp.company && exp.position && exp.startDate && exp.endDate && exp.description
        );
        if (hasCompleteExp) score += 15;
    }

    // Education (20 points)
    if (cv.educations.length > 0) {
        score += 10;
        const hasCompleteEdu = cv.educations.some(edu =>
            edu.school && edu.degree && edu.field && edu.startDate && edu.endDate
        );
        if (hasCompleteEdu) score += 10;
    }

    // Skills (10 points)
    if (cv.skills.length >= 3) score += 10;
    else if (cv.skills.length > 0) score += 5;

    return Math.min(score, maxScore);
}

/**
 * Suggest improvements for CV
 */
export function suggestImprovements(cv: ICV): string[] {
    const suggestions: string[] = [];

    const validation = validateCV(cv);
    const score = getCompletenessScore(cv);

    if (score < 50) {
        suggestions.push('CV của bạn còn thiếu nhiều thông tin quan trọng');
    }

    if (!cv.personalInfo.summary) {
        suggestions.push('Thêm phần tóm tắt về bản thân để gây ấn tượng với nhà tuyển dụng');
    }

    if (cv.experiences.length === 0) {
        suggestions.push('Thêm kinh nghiệm làm việc để tăng cơ hội được tuyển dụng');
    } else {
        const expWithoutDesc = cv.experiences.filter(exp => !exp.description);
        if (expWithoutDesc.length > 0) {
            suggestions.push('Thêm mô tả chi tiết cho các kinh nghiệm làm việc');
        }
    }

    if (cv.skills.length < 3) {
        suggestions.push('Thêm nhiều kỹ năng hơn (ít nhất 5-7 kỹ năng)');
    }

    if (validation.warnings.length > 0) {
        suggestions.push(...validation.warnings.map(w => `⚠️ ${w}`));
    }

    return suggestions;
}
