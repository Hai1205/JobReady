import * as pdfjsLib from 'pdfjs-dist';
import mammoth from 'mammoth';
import { v4 as uuidv4 } from 'uuid';

// Configure PDF.js worker
if (typeof window !== 'undefined') {
    pdfjsLib.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjsLib.version}/pdf.worker.min.js`;
}

interface ParsedCV {
    title: string;
    personalInfo: IPersonalInfo;
    experiences: IExperience[];
    educations: IEducation[];
    skills: string[];
}

/**
 * Parse PDF file to extract text content
 */
async function parsePDF(file: File): Promise<string> {
    try {
        const arrayBuffer = await file.arrayBuffer();
        const pdf = await pdfjsLib.getDocument({ data: arrayBuffer }).promise;

        let fullText = '';

        for (let i = 1; i <= pdf.numPages; i++) {
            const page = await pdf.getPage(i);
            const textContent = await page.getTextContent();
            const pageText = textContent.items
                .map((item: any) => item.str)
                .join(' ');
            fullText += pageText + '\n';
        }

        return fullText;
    } catch (error) {
        console.error('Error parsing PDF:', error);
        throw new Error('Không thể đọc file PDF');
    }
}

/**
 * Parse DOCX file to extract text content
 */
async function parseDOCX(file: File): Promise<string> {
    try {
        const arrayBuffer = await file.arrayBuffer();
        const result = await mammoth.extractRawText({ arrayBuffer });
        return result.value;
    } catch (error) {
        console.error('Error parsing DOCX:', error);
        throw new Error('Không thể đọc file DOCX');
    }
}

/**
 * Extract personal information from text
 */
function extractPersonalInfo(text: string): IPersonalInfo {
    const lines = text.split('\n').map(line => line.trim()).filter(line => line);

    // Extract email using regex
    const emailRegex = /[\w._%+-]+@[\w.-]+\.[a-zA-Z]{2,}/;
    const emailMatch = text.match(emailRegex);
    const email = emailMatch ? emailMatch[0] : '';

    // Extract phone using regex (Vietnamese and international formats)
    const phoneRegex = /(?:\+84|0)(?:\d{9,10})|(?:\(\d{3}\)\s?\d{3}-?\d{4})/;
    const phoneMatch = text.match(phoneRegex);
    const phone = phoneMatch ? phoneMatch[0] : '';

    // Extract name (usually first non-empty line or line before email)
    let fullname = '';
    for (let i = 0; i < Math.min(5, lines.length); i++) {
        const line = lines[i];
        // Skip lines that look like job titles or contact info
        if (!line.includes('@') &&
            !line.match(/\d{9,}/) &&
            line.length > 3 &&
            line.length < 50 &&
            !line.toLowerCase().includes('cv') &&
            !line.toLowerCase().includes('resume')) {
            fullname = line;
            break;
        }
    }

    // Extract location (look for address keywords)
    const locationKeywords = ['địa chỉ', 'address', 'location', 'nơi ở', 'thành phố', 'city'];
    let location = '';
    for (const line of lines) {
        const lowerLine = line.toLowerCase();
        if (locationKeywords.some(keyword => lowerLine.includes(keyword))) {
            location = line.split(':').pop()?.trim() || '';
            break;
        }
    }

    return {
        fullname: fullname || 'Chưa có thông tin',
        email,
        phone,
        location,
        summary: '',
        avatarUrl: '',
    };
}

/**
 * Extract experiences from text
 */
function extractExperiences(text: string): IExperience[] {
    const experiences: IExperience[] = [];
    const lines = text.split('\n').map(line => line.trim()).filter(line => line);

    // Keywords to identify experience section
    const expKeywords = [
        'kinh nghiệm', 'experience', 'work history', 'employment',
        'công việc', 'làm việc', 'career history'
    ];

    let inExpSection = false;
    let currentExp: Partial<IExperience> = {};

    for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        const lowerLine = line.toLowerCase();

        // Check if we're entering experience section
        if (expKeywords.some(keyword => lowerLine.includes(keyword))) {
            inExpSection = true;
            continue;
        }

        // Check if we're leaving experience section
        if (inExpSection && (
            lowerLine.includes('học vấn') ||
            lowerLine.includes('education') ||
            lowerLine.includes('kỹ năng') ||
            lowerLine.includes('skills')
        )) {
            break;
        }

        if (inExpSection) {
            // Try to match date patterns (MM/YYYY, YYYY-MM, etc.)
            const datePattern = /(\d{1,2}\/\d{4}|\d{4}-\d{2}|\d{4})/g;
            const dates = line.match(datePattern);

            if (dates && dates.length >= 1) {
                // Save previous experience if exists
                if (currentExp.company) {
                    experiences.push({
                        id: uuidv4(),
                        company: currentExp.company || '',
                        position: currentExp.position || '',
                        startDate: currentExp.startDate || '',
                        endDate: currentExp.endDate || 'Present',
                        description: currentExp.description || ''
                    });
                }

                // Start new experience
                currentExp = {
                    startDate: dates[0],
                    endDate: dates[1] || 'Present',
                    description: ''
                };
            } else if (line.length > 3 && !currentExp.company) {
                // This might be company name
                currentExp.company = line;
            } else if (line.length > 3 && !currentExp.position) {
                // This might be position
                currentExp.position = line;
            } else if (currentExp.company && line.length > 10) {
                // This might be description
                currentExp.description = (currentExp.description || '') + line + ' ';
            }
        }
    }

    // Add last experience
    if (currentExp.company) {
        experiences.push({
            id: uuidv4(),
            company: currentExp.company || '',
            position: currentExp.position || '',
            startDate: currentExp.startDate || '',
            endDate: currentExp.endDate || 'Present',
            description: (currentExp.description || '').trim()
        });
    }

    return experiences;
}

/**
 * Extract education from text
 */
function extractEducations(text: string): IEducation[] {
    const educations: IEducation[] = [];
    const lines = text.split('\n').map(line => line.trim()).filter(line => line);

    // Keywords to identify education section
    const eduKeywords = [
        'học vấn', 'education', 'academic', 'training',
        'bằng cấp', 'học vị', 'trường'
    ];

    let inEduSection = false;
    let currentEdu: Partial<IEducation> = {};

    for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        const lowerLine = line.toLowerCase();

        // Check if we're entering education section
        if (eduKeywords.some(keyword => lowerLine.includes(keyword))) {
            inEduSection = true;
            continue;
        }

        // Check if we're leaving education section
        if (inEduSection && (
            lowerLine.includes('kỹ năng') ||
            lowerLine.includes('skills') ||
            lowerLine.includes('chứng chỉ') ||
            lowerLine.includes('certificates')
        )) {
            break;
        }

        if (inEduSection) {
            // Try to match date patterns
            const datePattern = /(\d{1,2}\/\d{4}|\d{4}-\d{2}|\d{4})/g;
            const dates = line.match(datePattern);

            if (dates && dates.length >= 1) {
                // Save previous education if exists
                if (currentEdu.school) {
                    educations.push({
                        id: uuidv4(),
                        school: currentEdu.school || '',
                        degree: currentEdu.degree || '',
                        field: currentEdu.field || '',
                        startDate: currentEdu.startDate || '',
                        endDate: currentEdu.endDate || ''
                    });
                }

                // Start new education
                currentEdu = {
                    startDate: dates[0],
                    endDate: dates[1] || dates[0]
                };
            } else if (line.length > 5 && !currentEdu.school) {
                // This might be school name
                currentEdu.school = line;
            } else if (line.length > 3 && !currentEdu.degree) {
                // This might be degree
                currentEdu.degree = line;
            } else if (line.length > 3 && !currentEdu.field) {
                // This might be field
                currentEdu.field = line;
            }
        }
    }

    // Add last education
    if (currentEdu.school) {
        educations.push({
            id: uuidv4(),
            school: currentEdu.school || '',
            degree: currentEdu.degree || '',
            field: currentEdu.field || '',
            startDate: currentEdu.startDate || '',
            endDate: currentEdu.endDate || ''
        });
    }

    return educations;
}

/**
 * Extract skills from text
 */
function extractSkills(text: string): string[] {
    const skills: string[] = [];
    const lines = text.split('\n').map(line => line.trim()).filter(line => line);

    // Keywords to identify skills section
    const skillKeywords = [
        'kỹ năng', 'skills', 'technical skills', 'competencies',
        'năng lực', 'chuyên môn'
    ];

    let inSkillSection = false;

    for (let i = 0; i < lines.length; i++) {
        const line = lines[i];
        const lowerLine = line.toLowerCase();

        // Check if we're entering skills section
        if (skillKeywords.some(keyword => lowerLine.includes(keyword))) {
            inSkillSection = true;
            continue;
        }

        // Check if we're leaving skills section
        if (inSkillSection && (
            lowerLine.includes('chứng chỉ') ||
            lowerLine.includes('certificates') ||
            lowerLine.includes('sở thích') ||
            lowerLine.includes('hobbies') ||
            lowerLine.includes('tham khảo') ||
            lowerLine.includes('references')
        )) {
            break;
        }

        if (inSkillSection && line.length > 2) {
            // Split by common delimiters
            const skillItems = line.split(/[,;•\-\|]/);
            skillItems.forEach(skill => {
                const trimmedSkill = skill.trim();
                if (trimmedSkill && trimmedSkill.length > 2 && trimmedSkill.length < 50) {
                    skills.push(trimmedSkill);
                }
            });
        }
    }

    return [...new Set(skills)]; // Remove duplicates
}

/**
 * Main function to parse CV file
 */
export async function parseCV(file: File): Promise<ParsedCV> {
    let text = '';

    // Extract text based on file type
    if (file.type === 'application/pdf') {
        text = await parsePDF(file);
    } else if (file.type === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') {
        text = await parseDOCX(file);
    } else {
        throw new Error('Định dạng file không được hỗ trợ');
    }

    if (!text || text.trim().length === 0) {
        throw new Error('Không thể trích xuất nội dung từ file');
    }

    // Extract different sections
    const personalInfo = extractPersonalInfo(text);
    const experiences = extractExperiences(text);
    const educations = extractEducations(text);
    const skills = extractSkills(text);

    // Generate title from filename
    const title = file.name.replace(/\.(pdf|docx)$/i, '') || 'CV Import';

    return {
        title,
        personalInfo,
        experiences,
        educations,
        skills
    };
}

/**
 * Convert parsed data to ICV format
 */
export function convertToICV(parsedCV: ParsedCV, userId: string): ICV {
    return {
        id: uuidv4(),
        userId,
        title: parsedCV.title,
        personalInfo: parsedCV.personalInfo,
        experiences: parsedCV.experiences,
        educations: parsedCV.educations,
        skills: parsedCV.skills,
        avatar: null,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
    };
}
