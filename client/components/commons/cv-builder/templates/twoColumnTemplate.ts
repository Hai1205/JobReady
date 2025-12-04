/**
 * Two Column CV Template (Hình 4)
 * Two column layout with avatar on the right
 */

/**
 * Generate Two Column Template HTML
 * @param cv - CV data
 * @param primaryColor - Primary theme color (hex)
 * @param fontFamily - Font family to use (default: 'Inter, sans-serif')
 * @returns Complete HTML string with inline CSS
 */
export function generateTwoColumnTemplate(cv: ICV, primaryColor: string, fontFamily: string = 'Inter, sans-serif'): string {
  const escapeHtml = (text: string): string => {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  };

  // Avatar section
  const avatarHtml = (cv.personalInfo?.avatarUrl || cv.personalInfo?.avatarPublicId)
    ? `<img src="${escapeHtml(cv.personalInfo.avatarUrl || cv.personalInfo.avatarPublicId || '')}" 
            alt="Avatar" 
            style="width: 140px; height: 140px; border-radius: 50%; object-fit: cover; 
                   border: 4px solid white; box-shadow: 0 4px 12px rgba(0,0,0,0.1);" />`
    : '';

  // Summary section
  const summaryHtml = cv.personalInfo?.summary
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: ${primaryColor}; margin-bottom: 10px; 
                    margin-top: 0; text-transform: uppercase;">
           Giới thiệu
         </h2>
         <p style="font-size: 13px; line-height: 1.6; color: #333; text-align: justify; margin: 0;">
           ${escapeHtml(cv.personalInfo.summary)}
         </p>
       </div>`
    : '';

  // Education section with certifications
  const educationItems = [...(cv.educations || [])];

  const educationHtml = educationItems.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: ${primaryColor}; margin-bottom: 10px; 
                    margin-top: 0; text-transform: uppercase;">
           Học vấn
         </h2>
         ${educationItems.map((edu: IEducation) => `
           <div style="margin-bottom: 12px;">
             <h3 style="font-weight: bold; color: #000; font-size: 14px; margin-bottom: 3px; margin-top: 0;">
               ${escapeHtml(edu.school)}
             </h3>
             <p style="font-size: 13px; color: #666; margin: 0;">
               ${escapeHtml(edu.degree)}${edu.field ? ` - ${escapeHtml(edu.field)}` : ''}
             </p>
             ${edu.startDate ? `<p style="font-size: 12px; color: #999; margin: 3px 0 0 0;">
               ${escapeHtml(edu.startDate)}${edu.endDate ? ` - ${escapeHtml(edu.endDate)}` : ''}
             </p>` : ''}
           </div>
         `).join('')}
       </div>`
    : '';

  // Skills section - combined with languages
  const allSkills = [...(cv.skills || [])];

  const skillsHtml = allSkills.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: ${primaryColor}; margin-bottom: 10px; 
                    margin-top: 0; text-transform: uppercase;">
           Kỹ năng
         </h2>
         <div style="display: flex; flex-wrap: wrap; gap: 6px;">
           ${allSkills.map((skill: string) => `
             <span style="display: inline-block; padding: 6px 10px; background-color: #f5f5f5; 
                          color: #333; border-radius: 3px; font-size: 12px; font-weight: 500;">
               ${escapeHtml(skill)}
             </span>
           `).join('')}
         </div>
       </div>`
    : '';

  // Experience section
  const experienceHtml = cv.experiences && cv.experiences.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: ${primaryColor}; margin-bottom: 10px; 
                    margin-top: 0; text-transform: uppercase;">
           Kinh nghiệm làm việc
         </h2>
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
       </div>`
    : '';

  return `
    <div style="max-width: 210mm; margin: 0 auto; background-color: white; padding: 0; min-height: 297mm; 
                font-family: ${fontFamily}; font-size: 13px; line-height: 1.5; color: #000;">
      <!-- Header with diagonal background -->
      <div style="background: linear-gradient(135deg, #f5f5f5 0%, #f5f5f5 60%, transparent 60%), 
                  linear-gradient(135deg, transparent 60%, ${primaryColor}20 60%);
                  padding: 40px; position: relative; display: flex; justify-content: space-between; align-items: flex-start;">
        <div style="flex: 1; padding-right: 30px;">
          <h1 style="font-size: 32px; font-weight: bold; color: #000; margin: 0 0 8px 0; text-transform: uppercase;">
            ${escapeHtml(cv.personalInfo?.fullname || 'Họ và tên')}
          </h1>
          <p style="font-size: 14px; color: ${primaryColor}; margin: 0 0 20px 0; text-transform: uppercase; 
                    font-weight: 600; letter-spacing: 1px;">
            ${escapeHtml(cv.title || 'Untitled CV')}
          </p>
        </div>
        ${avatarHtml ? `<div style="flex-shrink: 0;">${avatarHtml}</div>` : ''}
      </div>

      <!-- Two Column Layout -->
      <div style="display: flex; padding: 0 40px 40px 40px; gap: 30px;">
        <!-- Left Column -->
        <div style="width: 35%;">
          <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 25px;">
            <h2 style="font-size: 14px; font-weight: bold; color: ${primaryColor}; margin-bottom: 12px; 
                       margin-top: 0; text-transform: uppercase;">
              Thông tin cá nhân
            </h2>
            <div style="font-size: 12px; line-height: 1.8; color: #333;">
              ${cv.personalInfo?.phone ? `<div style="margin-bottom: 8px;"><strong>📞</strong> ${escapeHtml(cv.personalInfo.phone)}</div>` : ''}
              ${cv.personalInfo?.email ? `<div style="margin-bottom: 8px;"><strong>✉</strong> ${escapeHtml(cv.personalInfo.email)}</div>` : ''}
              ${cv.personalInfo?.location ? `<div style="margin-bottom: 8px;"><strong>📍</strong> ${escapeHtml(cv.personalInfo.location)}</div>` : ''}
              ${cv.personalInfo?.birth ? `<div style="margin-bottom: 8px;"><strong>🎂</strong> ${escapeHtml(cv.personalInfo.birth)}</div>` : ''}
            </div>
          </div>

          ${educationHtml}
          ${skillsHtml}
        </div>

        <!-- Right Column -->
        <div style="flex: 1;">
          ${summaryHtml}
          ${experienceHtml}
        </div>
      </div>
    </div>
  `;
}