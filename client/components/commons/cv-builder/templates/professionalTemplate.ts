/**
 * Professional CV Template (Hình 2)
 * Header with avatar on top, clean layout
 */

/**
 * Generate Professional Template HTML
 * @param cv - CV data
 * @param primaryColor - Primary theme color (hex)
 * @param fontFamily - Font family to use (default: 'Inter, sans-serif')
 * @returns Complete HTML string with inline CSS
 */
export function generateProfessionalTemplate(cv: ICV, primaryColor: string, fontFamily: string = 'Inter, sans-serif'): string {
  const escapeHtml = (text: string): string => {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  };

  // Avatar section
  const avatarHtml = (cv.personalInfo?.avatarUrl || cv.personalInfo?.avatarPublicId)
    ? `<img src="${escapeHtml(cv.personalInfo.avatarUrl || cv.personalInfo.avatarPublicId || '')}" 
            alt="Avatar" 
            style="width: 120px; height: 120px; border-radius: 8px; object-fit: cover;" />`
    : '';

  // Summary section
  const summaryHtml = cv.personalInfo?.summary
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: #000; margin-bottom: 10px; 
                    margin-top: 0;">
           Giới thiệu
         </h2>
         <p style="font-size: 13px; line-height: 1.6; color: #333; text-align: justify; margin: 0;">
           ${escapeHtml(cv.personalInfo.summary)}
         </p>
       </div>`
    : '';

  // Education section
  const educationHtml = cv.educations && cv.educations.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: #000; margin-bottom: 10px; 
                    margin-top: 0;">
           Học vấn
         </h2>
         <div>
           ${cv.educations.map((edu: IEducation) => `
             <div style="margin-bottom: 15px;">
               <h3 style="font-weight: bold; color: #000; font-size: 14px; margin-bottom: 5px; margin-top: 0;">
                 ${escapeHtml(edu.school)}
               </h3>
               <p style="font-size: 13px; color: #666; margin: 0;">
                 ${escapeHtml(edu.startDate)} - ${escapeHtml(edu.endDate || 'Present')} | 
                 ${escapeHtml(edu.degree)}${edu.field ? ` - ${escapeHtml(edu.field)}` : ''}
               </p>
             </div>
           `).join('')}
         </div>
       </div>`
    : '';

  // Skills section - combined with languages
  const allSkills = [...(cv.skills || [])];

  const skillsHtml = allSkills.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: #000; margin-bottom: 10px; 
                    margin-top: 0;">
           Kỹ năng
         </h2>
         <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 8px;">
           ${allSkills.map((skill: string) => `
             <div style="font-size: 13px; color: #333;">
               <span style="font-weight: 600;">${escapeHtml(skill.split(' (')[0])}</span>${skill.includes('(') ? `<span style="color: #666; font-size: 12px;"> (${skill.split('(')[1]}</span>` : ''}
             </div>
           `).join('')}
         </div>
       </div>`
    : '';

  // Experience section
  const experienceHtml = cv.experiences && cv.experiences.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: #000; margin-bottom: 10px; 
                    margin-top: 0;">
           Kinh nghiệm làm việc
         </h2>
         <div>
           ${cv.experiences.map((exp: IExperience) => `
             <div style="margin-bottom: 20px;">
               <div style="margin-bottom: 8px;">
                 <h3 style="font-weight: bold; color: #000; font-size: 14px; margin: 0; text-transform: uppercase;">
                   ${escapeHtml(exp.position)}
                 </h3>
                 <p style="font-size: 13px; color: #666; margin: 5px 0; font-weight: 600;">
                   ${escapeHtml(exp.company)}
                 </p>
               </div>
               <p style="font-size: 12px; color: #999; margin-bottom: 8px;">
                 ${escapeHtml(exp.startDate)} - ${escapeHtml(exp.endDate || 'Present')}
               </p>
               ${exp.description ? `
                 <ul style="margin: 0; padding-left: 20px; font-size: 13px; color: #333; line-height: 1.6;">
                   ${exp.description.split('\n').map(line => line.trim()).filter(Boolean).map(line =>
      `<li style="margin-bottom: 4px;">${escapeHtml(line.replace(/^[•\-]\s*/, ''))}</li>`
    ).join('')}
                 </ul>
               ` : ''}
             </div>
           `).join('')}
         </div>
       </div>`
    : '';

  return `
    <div style="max-width: 210mm; margin: 0 auto; background-color: white; padding: 40px; min-height: 297mm; 
                font-family: ${fontFamily}; font-size: 13px; line-height: 1.5; color: #000;">
      <!-- Header Section -->
      <div style="background-color: ${primaryColor}; padding: 30px; margin: -40px -40px 30px -40px; 
                  display: flex; gap: 25px; align-items: center;">
        ${avatarHtml ? `<div style="flex-shrink: 0;">${avatarHtml}</div>` : ''}
        
        <div style="flex: 1; color: white;">
          <h1 style="font-size: 28px; font-weight: bold; margin: 0 0 8px 0; text-transform: uppercase;">
            ${escapeHtml(cv.personalInfo?.fullname || 'Họ và tên')}
          </h1>
          <p style="font-size: 14px; margin: 0 0 15px 0; text-transform: uppercase; letter-spacing: 1px;">
            ${escapeHtml(cv.title || 'Untitled CV')}
          </p>
          <div style="font-size: 12px; line-height: 1.8;">
            ${cv.personalInfo?.phone ? `<div>📞 ${escapeHtml(cv.personalInfo.phone)}</div>` : ''}
            ${cv.personalInfo?.email ? `<div>✉ ${escapeHtml(cv.personalInfo.email)}</div>` : ''}
            ${cv.personalInfo?.location ? `<div>📍 ${escapeHtml(cv.personalInfo.location)}</div>` : ''}
          </div>
        </div>
      </div>

      <!-- Content Sections -->
      ${summaryHtml}
      ${educationHtml}
      ${skillsHtml}
      ${experienceHtml}
    </div>
  `;
}