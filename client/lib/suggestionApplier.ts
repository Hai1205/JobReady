/**
 * Helper functions to apply AI suggestions to CV data
 */

/**
 * Parse suggestion text to extract "After" content
 * Example: "Before: 'old text'\nAfter: 'new text'" => "new text"
 */
const parseAfterContent = (suggestionText: string): string => {
    if (!suggestionText) return "";

    // Split by newline and find the "After:" line
    const lines = suggestionText.split('\n');
    const afterLineIndex = lines.findIndex(line => line.trim().startsWith('After:'));

    if (afterLineIndex !== -1) {
        const afterLine = lines[afterLineIndex];
        // Remove "After:" and trim quotes if present
        const afterText = afterLine
            .replace(/^After:\s*/i, '')
            .trim()
            .replace(/^['"]|['"]$/g, ''); // Remove leading and trailing quotes

        return afterText;
    }

    // Fallback: try regex match for single line format
    const afterMatch = suggestionText.match(/After:\s*['"](.*?)['"]/);
    if (afterMatch && afterMatch[1]) {
        return afterMatch[1].trim();
    }

    // If still no match, try without quotes
    const afterMatchNoQuotes = suggestionText.match(/After:\s*(.+)/);
    if (afterMatchNoQuotes && afterMatchNoQuotes[1]) {
        return afterMatchNoQuotes[1].trim();
    }

    // If no "After:" pattern found, return the original suggestion
    return suggestionText.trim();
};

/**
 * Extract skills from suggestion text
 * Handles formats like: "Technical Skills: A, B. Soft Skills: C, D."
 */
const parseSkillsSuggestion = (suggestionText: string): string[] => {
    const afterContent = parseAfterContent(suggestionText);

    // Remove category labels and split by common delimiters
    const cleanedText = afterContent
        .replace(/(Technical Skills|Soft Skills|Kỹ năng|Skills):\s*/gi, "")
        .replace(/\.\s*/g, ","); // Replace periods with commas

    // Split by comma and clean up
    const skills = cleanedText
        .split(",")
        .map((skill) => skill.trim())
        .filter((skill) => skill.length > 0);

    return skills;
};

export const applySuggestionToCV = (
    cv: ICV | null,
    suggestion: IAISuggestion
): ICV | null => {
    if (!cv) return null;

    const updatedCV = { ...cv };
    const section = suggestion.section.toLowerCase().trim();

    // Extract the "After" content from suggestion
    const afterContent = parseAfterContent(suggestion.suggestion);

    // Debug logging
    console.log('🔍 Applying suggestion:', {
        section: suggestion.section,
        message: suggestion.message,
        originalSuggestion: suggestion.suggestion,
        parsedAfterContent: afterContent
    });

    switch (section) {
        case "summary":
        case "personal info":
        case "personalinfo":
        case "thông tin cá nhân":
            updatedCV.personalInfo = {
                ...updatedCV.personalInfo,
                summary: afterContent || updatedCV.personalInfo.summary,
            };
            break;

        case "experience":
        case "experiences":
        case "kinh nghiệm":
        case "kinh nghiệm làm việc":
            // If lineNumber is provided, update specific experience
            if (
                suggestion.lineNumber !== undefined &&
                suggestion.lineNumber !== null &&
                updatedCV.experiences[suggestion.lineNumber]
            ) {
                updatedCV.experiences = [...updatedCV.experiences];
                const targetExp = updatedCV.experiences[suggestion.lineNumber];
                updatedCV.experiences[suggestion.lineNumber] = {
                    ...targetExp,
                    description: afterContent || targetExp.description,
                };
            } else if (afterContent) {
                // Try to find matching experience by company name in message
                // Example: "Experience at TechCorp Inc. thiếu metrics..."
                const companyMatch = suggestion.message.match(/(?:at|tại)\s+([^.]+)/i);
                const companyName = companyMatch ? companyMatch[1].trim() : null;

                let matchingIndex = -1;

                if (companyName) {
                    // Try exact or partial company name match
                    matchingIndex = updatedCV.experiences.findIndex(
                        (exp) =>
                            exp.company.toLowerCase().includes(companyName.toLowerCase()) ||
                            companyName.toLowerCase().includes(exp.company.toLowerCase())
                    );
                }

                // If no company match, try matching by description content
                if (matchingIndex === -1) {
                    const beforeContent = suggestion.suggestion.match(/Before:\s*['"](.*?)['"]/)?.[1];
                    if (beforeContent) {
                        matchingIndex = updatedCV.experiences.findIndex(
                            (exp) => exp.description.includes(beforeContent.substring(0, 30))
                        );
                    }
                }

                if (matchingIndex !== -1) {
                    updatedCV.experiences = [...updatedCV.experiences];
                    updatedCV.experiences[matchingIndex] = {
                        ...updatedCV.experiences[matchingIndex],
                        description: afterContent,
                    };
                    console.log('✅ Applied to experience:', updatedCV.experiences[matchingIndex].company);
                } else if (updatedCV.experiences.length > 0) {
                    // Apply to first experience if no match found
                    updatedCV.experiences = [...updatedCV.experiences];
                    updatedCV.experiences[0] = {
                        ...updatedCV.experiences[0],
                        description: afterContent,
                    };
                    console.log('⚠️ No match found, applied to first experience');
                }
            }
            break;

        case "education":
        case "educations":
        case "học vấn":
            // If lineNumber is provided, update specific education
            if (
                suggestion.lineNumber !== undefined &&
                updatedCV.educations[suggestion.lineNumber]
            ) {
                updatedCV.educations = [...updatedCV.educations];
                const targetEdu = updatedCV.educations[suggestion.lineNumber];
                updatedCV.educations[suggestion.lineNumber] = {
                    ...targetEdu,
                    field: afterContent || targetEdu.field,
                };
            }
            break;

        case "skills":
        case "skill":
        case "kỹ năng":
            // Parse and add skills from suggestion
            if (afterContent) {
                const newSkills = parseSkillsSuggestion(suggestion.suggestion);
                const uniqueNewSkills = newSkills.filter(
                    (skill) => !updatedCV.skills.includes(skill)
                );

                if (uniqueNewSkills.length > 0) {
                    updatedCV.skills = [...updatedCV.skills, ...uniqueNewSkills];
                }
            }
            break;

        case "title":
        case "tiêu đề":
            if (afterContent) {
                updatedCV.title = afterContent;
            }
            break;

        case "fullname":
        case "name":
        case "họ tên":
            if (afterContent) {
                updatedCV.personalInfo = {
                    ...updatedCV.personalInfo,
                    fullname: afterContent,
                };
            }
            break;

        case "email":
            if (afterContent) {
                updatedCV.personalInfo = {
                    ...updatedCV.personalInfo,
                    email: afterContent,
                };
            }
            break;

        case "phone":
        case "điện thoại":
        case "số điện thoại":
            if (afterContent) {
                updatedCV.personalInfo = {
                    ...updatedCV.personalInfo,
                    phone: afterContent,
                };
            }
            break;

        case "location":
        case "địa chỉ":
        case "vị trí":
            if (afterContent) {
                updatedCV.personalInfo = {
                    ...updatedCV.personalInfo,
                    location: afterContent,
                };
            }
            break;

        default:
            console.warn(`Unknown section: ${suggestion.section}`);
            return null; // Return null if section is not recognized
    }

    updatedCV.updatedAt = new Date().toISOString();
    return updatedCV;
};

/**
 * Get a human-readable section name
 */
export const getSectionDisplayName = (section: string): string => {
    const sectionMap: Record<string, string> = {
        summary: "Tóm tắt",
        "personal info": "Thông tin cá nhân",
        personalinfo: "Thông tin cá nhân",
        experience: "Kinh nghiệm",
        experiences: "Kinh nghiệm",
        education: "Học vấn",
        educations: "Học vấn",
        skills: "Kỹ năng",
        skill: "Kỹ năng",
        title: "Tiêu đề",
        fullname: "Họ tên",
        name: "Họ tên",
        email: "Email",
        phone: "Số điện thoại",
        location: "Địa chỉ",
    };

    return sectionMap[section.toLowerCase().trim()] || section;
};
