/**
 * Helper functions to apply AI suggestions to CV data
 * Updated to use suggestion.data directly instead of parsing text
 */

export const applySuggestionToCV = (
    cv: ICV | null,
    suggestion: IAISuggestion
): ICV | null => {
    if (!cv) return null;

    const updatedCV = { ...cv };
    const section = suggestion.section.toLowerCase().trim();

    // Use suggestion.data directly - AI provides actual data to apply
    const data = suggestion.data;

    // Debug logging
    console.log('🔧 Applying suggestion:', {
        section: suggestion.section,
        message: suggestion.message,
        data: data
    });

    if (!data) {
        console.warn('⚠️ No data provided in suggestion, cannot apply');
        return null;
    }

    switch (section) {
        case "summary":
        case "personal info":
        case "personalinfo":
        case "thông tin cá nhân":
            if (data.text) {
                updatedCV.personalInfo = {
                    ...updatedCV.personalInfo,
                    summary: data.text,
                };
                console.log('✅ Applied summary:', data.text.substring(0, 50) + '...');
            }
            break;

        case "experience":
        case "experiences":
        case "kinh nghiệm":
        case "kinh nghiệm làm việc":
            if (data.description) {
                // Clean description: remove bullet points (•, -, *, etc.) from the beginning of each line
                const cleanedDescription = data.description
                    .split('\n')
                    .map(line => line.trim().replace(/^[•\-*]\s*/, ''))
                    .join('\n');

                // If lineNumber is provided, update specific experience
                if (
                    suggestion.lineNumber !== undefined &&
                    suggestion.lineNumber !== null &&
                    updatedCV.experiences[suggestion.lineNumber]
                ) {
                    updatedCV.experiences = [...updatedCV.experiences];
                    updatedCV.experiences[suggestion.lineNumber] = {
                        ...updatedCV.experiences[suggestion.lineNumber],
                        description: cleanedDescription,
                    };
                    console.log('✅ Applied experience at index:', suggestion.lineNumber);
                } else if (updatedCV.experiences.length > 0) {
                    // Apply to first/most recent experience
                    updatedCV.experiences = [...updatedCV.experiences];
                    updatedCV.experiences[0] = {
                        ...updatedCV.experiences[0],
                        description: cleanedDescription,
                    };
                    console.log('✅ Applied experience to most recent position');
                }
            }

            // Handle date updates
            if (data.startDate || data.endDate) {
                const targetIndex = suggestion.lineNumber ?? 0;
                if (updatedCV.experiences[targetIndex]) {
                    updatedCV.experiences = [...updatedCV.experiences];
                    updatedCV.experiences[targetIndex] = {
                        ...updatedCV.experiences[targetIndex],
                        ...(data.startDate && { startDate: data.startDate }),
                        ...(data.endDate && { endDate: data.endDate }),
                    };
                    console.log('✅ Applied dates:', data.startDate, '->', data.endDate);
                }
            }
            break;

        case "education":
        case "educations":
        case "học vấn":
            const eduIndex = suggestion.lineNumber ?? 0;
            if (updatedCV.educations[eduIndex]) {
                updatedCV.educations = [...updatedCV.educations];
                updatedCV.educations[eduIndex] = {
                    ...updatedCV.educations[eduIndex],
                    ...(data.field && { field: data.field }),
                    ...(data.degree && { degree: data.degree }),
                    ...(data.startDate && { startDate: data.startDate }),
                    ...(data.endDate && { endDate: data.endDate }),
                };
                console.log('✅ Applied education updates');
            }
            break;

        case "skills":
        case "skill":
        case "kỹ năng":
            if (data.skills && Array.isArray(data.skills)) {
                console.log('📋 Current skills:', updatedCV.skills);
                console.log('➕ Adding skills:', data.skills);

                // Filter out skills that already exist (case insensitive)
                const uniqueNewSkills = data.skills.filter(
                    (skill) => !updatedCV.skills.some(
                        (existingSkill) => existingSkill.toLowerCase() === skill.toLowerCase()
                    )
                );

                if (uniqueNewSkills.length > 0) {
                    updatedCV.skills = [...updatedCV.skills, ...uniqueNewSkills];
                    console.log('✅ Added new skills:', uniqueNewSkills);
                    console.log('📊 Updated skills list:', updatedCV.skills);
                } else {
                    console.log('ℹ️ All skills already exist, no new skills added');
                }
            }
            break;

        case "title":
        case "tiêu đề":
            if (data.text) {
                updatedCV.title = data.text;
                console.log('✅ Applied title:', data.text);
            }
            break;

        case "fullname":
        case "name":
        case "họ tên":
            if (data.text) {
                updatedCV.personalInfo = {
                    ...updatedCV.personalInfo,
                    fullname: data.text,
                };
                console.log('✅ Applied fullname:', data.text);
            }
            break;

        case "email":
            if (data.text) {
                updatedCV.personalInfo = {
                    ...updatedCV.personalInfo,
                    email: data.text,
                };
                console.log('✅ Applied email:', data.text);
            }
            break;

        case "phone":
        case "điện thoại":
        case "số điện thoại":
            if (data.text) {
                updatedCV.personalInfo = {
                    ...updatedCV.personalInfo,
                    phone: data.text,
                };
                console.log('✅ Applied phone:', data.text);
            }
            break;

        case "location":
        case "địa chỉ":
        case "vị trí":
            if (data.text) {
                updatedCV.personalInfo = {
                    ...updatedCV.personalInfo,
                    location: data.text,
                };
                console.log('✅ Applied location:', data.text);
            }
            break;

        default:
            console.warn(`⚠️ Unknown section: ${suggestion.section}`);
            return null;
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
