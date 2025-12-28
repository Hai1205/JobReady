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
    // Common English words in CVs
    'developed',
    'managed',
    'led',
    'created',
    'implemented',
    'achieved',
    'improved',
    'increased',
    'decreased',
    'responsible',
    'team',
    'with',
    'from',
    'date',
    'present',
    'bachelor',
    'master',
    'degree',
    'technology',
    'development',
    'senior',
    'junior',
    'engineer',
    'developer',
    'software',
    'web',
    'mobile',
];

/**
 * Detect language from CV object
 */
export const detectCVLanguage = (cv: ICV | null): 'vi' | 'en' => {
    if (!cv) return 'en'; // Default to English for better UX

    // Combine all text content from CV
    const textContent = [
        cv.title,
        cv.personalInfo?.summary,
        cv.personalInfo?.fullname,
        cv.personalInfo?.location,
        ...cv.experiences.map(exp => `${exp.company} ${exp.position} ${exp.description}`),
        ...cv.educations.map(edu => `${edu.school} ${edu.degree} ${edu.field}`),
        ...cv.skills,
    ].filter(Boolean).join(' ');

    const lowerTextContent = textContent.toLowerCase();

    // Debug logs
    console.log('🔍 Language Detection Debug:');
    console.log('Text sample:', lowerTextContent.substring(0, 200));

    // Count Vietnamese vs English keywords FIRST
    let viCount = 0;
    let enCount = 0;

    const foundViKeywords: string[] = [];
    const foundEnKeywords: string[] = [];

    VIETNAMESE_KEYWORDS.forEach(keyword => {
        if (lowerTextContent.includes(keyword)) {
            viCount++;
            foundViKeywords.push(keyword);
        }
    });

    ENGLISH_KEYWORDS.forEach(keyword => {
        if (lowerTextContent.includes(keyword)) {
            enCount++;
            foundEnKeywords.push(keyword);
        }
    });

    console.log('Vietnamese keywords found:', viCount, foundViKeywords.slice(0, 5));
    console.log('English keywords found:', enCount, foundEnKeywords.slice(0, 5));

    // Check for Vietnamese characters
    const hasVietnameseChars = VIETNAMESE_CHARS.test(textContent);

    // Priority 1: If strong keyword difference, trust it
    if (enCount >= 3 && enCount > viCount * 2) {
        console.log('Detected: English (strong keyword signal)');
        return 'en';
    }

    if (viCount >= 3 && viCount > enCount * 2) {
        console.log('Detected: Vietnamese (strong keyword signal)');
        return 'vi';
    }

    // Priority 2: Check Vietnamese chars only if keywords are unclear
    if (hasVietnameseChars && viCount >= enCount) {
        console.log('Detected: Vietnamese (by chars + keywords)');
        return 'vi';
    }

    // Priority 3: Keyword count
    if (viCount > enCount) {
        console.log('Detected: Vietnamese (by keywords)');
        return 'vi';
    }

    // Priority 4: Default to English if English keywords found or neutral
    if (enCount > 0 || (enCount === 0 && viCount === 0)) {
        console.log('Detected: English');
        return 'en';
    }

    // Final default
    console.log('Detected: English (default)');
    return 'en';
};

/**
 * Detect language from text content
 */
export const detectTextLanguage = (text: string): 'vi' | 'en' => {
    if (!text || text.trim().length === 0) return 'en';

    const lowerText = text.toLowerCase();

    // Check for Vietnamese characters - strong indicator
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
    if (viCount > enCount && viCount > 0) {
        return 'vi';
    }

    // If English keywords found or no clear indication, return 'en'
    if (enCount > 0 || (enCount === 0 && viCount === 0)) {
        return 'en';
    }

    // Default to English
    return 'en';
};
