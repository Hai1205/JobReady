/**
 * Detect language of CV content
 * Returns 'vi' for Vietnamese, 'en' for English
 */

const VIETNAMESE_CHARS = /[àáảãạăắằẳẵặâấầẩẫậèéẻẽẹêếềểễệìíỉĩịòóỏõọôốồổỗộơớờởỡợùúủũụưứừửữựỳýỷỹỵđÀÁẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬÈÉẺẼẸÊẾỀỂỄỆÌÍỈĨỊÒÓỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÙÚỦŨỤƯỨỪỬỮỰỲÝỶỸỴĐ]/;

const VIETNAMESE_KEYWORDS = [
    'kinh nghiệm',
    'học vấn',
    'kỹ năng',
    'mục tiêu',
    'thông tin',
    'liên hệ',
    'chứng chỉ',
    'dự án',
    'năm',
    'tháng',
    'công ty',
    'trường',
    'đại học',
    'chức vụ',
    'vị trí',
];

const ENGLISH_KEYWORDS = [
    'experience',
    'education',
    'skills',
    'objective',
    'summary',
    'contact',
    'certificate',
    'project',
    'year',
    'month',
    'company',
    'school',
    'university',
    'position',
    'role',
];

/**
 * Detect language from CV object
 */
export const detectCVLanguage = (cv: ICV | null): 'vi' | 'en' => {
    if (!cv) return 'vi'; // Default to Vietnamese

    // Combine all text content from CV
    const textContent = [
        cv.title,
        cv.personalInfo?.summary,
        cv.personalInfo?.fullname,
        cv.personalInfo?.location,
        ...cv.experiences.map(exp => `${exp.company} ${exp.position} ${exp.description}`),
        ...cv.educations.map(edu => `${edu.school} ${edu.degree} ${edu.field}`),
        ...cv.skills,
    ].filter(Boolean).join(' ').toLowerCase();

    // Check for Vietnamese characters
    if (VIETNAMESE_CHARS.test(textContent)) {
        return 'vi';
    }

    // Count Vietnamese vs English keywords
    let viCount = 0;
    let enCount = 0;

    VIETNAMESE_KEYWORDS.forEach(keyword => {
        if (textContent.includes(keyword)) {
            viCount++;
        }
    });

    ENGLISH_KEYWORDS.forEach(keyword => {
        if (textContent.includes(keyword)) {
            enCount++;
        }
    });

    // If Vietnamese keywords dominate, return 'vi'
    if (viCount > enCount) {
        return 'vi';
    }

    // If English keywords dominate or equal, return 'en'
    if (enCount >= viCount && enCount > 0) {
        return 'en';
    }

    // Default to Vietnamese if no clear indication
    return 'vi';
};

/**
 * Detect language from text content
 */
export const detectTextLanguage = (text: string): 'vi' | 'en' => {
    if (!text || text.trim().length === 0) return 'vi';

    const lowerText = text.toLowerCase();

    // Check for Vietnamese characters
    if (VIETNAMESE_CHARS.test(text)) {
        return 'vi';
    }

    // Count Vietnamese vs English keywords
    let viCount = 0;
    let enCount = 0;

    VIETNAMESE_KEYWORDS.forEach(keyword => {
        if (lowerText.includes(keyword)) {
            viCount++;
        }
    });

    ENGLISH_KEYWORDS.forEach(keyword => {
        if (lowerText.includes(keyword)) {
            enCount++;
        }
    });

    // If Vietnamese keywords dominate, return 'vi'
    if (viCount > enCount) {
        return 'vi';
    }

    // If English keywords dominate or equal, return 'en'
    if (enCount >= viCount && enCount > 0) {
        return 'en';
    }

    // Default to Vietnamese
    return 'vi';
};
