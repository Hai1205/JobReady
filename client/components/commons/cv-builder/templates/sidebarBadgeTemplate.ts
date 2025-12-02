/**
 * Sidebar Badge CV Template (Hình 3)
 * Sidebar layout with skills displayed as badges
 */

/**
 * Generate Sidebar Badge Template HTML
 * @param cv - CV data
 * @param primaryColor - Primary theme color (hex)
 * @param fontFamily - Font family to use (default: 'Inter, sans-serif')
 * @returns Complete HTML string with inline CSS
 */
export function generateSidebarBadgeTemplate(cv: ICV, primaryColor: string, fontFamily: string = 'Inter, sans-serif'): string {
  const escapeHtml = (text: string): string => {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  };

  // Avatar section
  const avatarHtml = (cv.personalInfo?.avatarUrl || cv.personalInfo?.avatarPublicId)
    ? `<img src="${escapeHtml(cv.personalInfo.avatarUrl || cv.personalInfo.avatarPublicId || '')}" 
            alt="Avatar" 
            style="width: 150px; height: 150px; border-radius: 8px; object-fit: cover; margin-bottom: 20px;" />`
    : '';

  // Skills section - displayed as badges
  const allSkills = [...(cv.skills || [])];

  const skillsHtml = allSkills.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: white; margin-bottom: 12px; 
                    margin-top: 0; text-transform: uppercase;">
           Kỹ năng
         </h2>
         <div style="margin-bottom: 15px;">
           <h3 style="font-size: 14px; font-weight: 600; color: white; margin: 0 0 10px 0;">
             Kỹ năng chính
           </h3>
           <div style="display: flex; flex-direction: column; gap: 8px;">
             ${allSkills.map((skill: string) => `
               <div style="background-color: rgba(255,255,255,0.2); padding: 8px 12px; 
                          border-radius: 4px; font-size: 12px; color: white; font-weight: 500;
                          border: 1px solid rgba(255,255,255,0.3);">
                 ${escapeHtml(skill)}
               </div>
             `).join('')}
           </div>
         </div>
       </div>`
    : '';

  // Education section with certifications
  const educationItems = [...(cv.educations || [])];

  const educationHtml = educationItems.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: white; margin-bottom: 12px; 
                    margin-top: 0; text-transform: uppercase;">
           Học vấn
         </h2>
         ${educationItems.map((edu: IEducation) => `
           <div style="margin-bottom: 15px;">
             <h3 style="font-weight: bold; color: white; font-size: 13px; margin-bottom: 5px; margin-top: 0;">
               ${escapeHtml(edu.school)}
             </h3>
             <p style="font-size: 12px; color: rgba(255,255,255,0.9); margin: 0;">
               ${escapeHtml(edu.degree)}${edu.field ? ` - ${escapeHtml(edu.field)}` : ''}
             </p>
             ${edu.startDate ? `<p style="font-size: 11px; color: rgba(255,255,255,0.7); margin: 3px 0 0 0;">
               ${escapeHtml(edu.startDate)}${edu.endDate && edu.endDate !== edu.startDate ? ` - ${escapeHtml(edu.endDate)}` : ''}
             </p>` : ''}
           </div>
         `).join('')}
       </div>`
    : '';

  // Experience section
  const experienceHtml = cv.experiences && cv.experiences.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 18px; font-weight: bold; color: #000; margin-bottom: 15px; 
                    margin-top: 0; text-transform: uppercase; padding-bottom: 8px; 
                    border-bottom: 2px solid ${primaryColor};">
           Kinh nghiệm làm việc
         </h2>
         ${cv.experiences.map((exp: IExperience) => `
           <div style="margin-bottom: 25px; border-left: 3px solid ${primaryColor}; padding-left: 15px;">
             <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 8px;">
               <h3 style="font-weight: bold; color: #000; font-size: 15px; margin: 0;">
                 ${escapeHtml(exp.position)}
               </h3>
               <span style="font-size: 12px; color: #999; white-space: nowrap; margin-left: 15px;">
                 ${escapeHtml(exp.startDate)} - ${escapeHtml(exp.endDate || 'Present')}
               </span>
             </div>
             <p style="font-size: 13px; color: ${primaryColor}; margin: 0 0 8px 0; font-weight: 600; text-transform: uppercase;">
               ${escapeHtml(exp.company)}
             </p>
             ${exp.description ? `
               <ul style="margin: 0; padding-left: 20px; font-size: 13px; color: #555; line-height: 1.7;">
                 ${exp.description.split('\n').map(line => line.trim()).filter(Boolean).map(line =>
      `<li style="margin-bottom: 6px;">${escapeHtml(line.replace(/^[•\-]\s*/, ''))}</li>`
    ).join('')}
               </ul>
             ` : ''}
           </div>
         `).join('')}
       </div>`
    : '';

  return `
    <div style="max-width: 210mm; margin: 0 auto; background-color: white; min-height: 297mm; 
                font-family: ${fontFamily}; font-size: 13px; line-height: 1.5; color: #000; display: flex;">
      <!-- Sidebar -->
      <div style="width: 35%; background: linear-gradient(180deg, ${primaryColor} 0%, ${primaryColor}dd 100%); 
                  padding: 40px 25px; color: white;">
        ${avatarHtml}
        
        <div style="margin-bottom: 25px;">
          <div style="font-size: 12px; line-height: 1.8; color: white;">
            ${cv.personalInfo?.phone ? `<div style="margin-bottom: 8px;">📞 ${escapeHtml(cv.personalInfo.phone)}</div>` : ''}
            ${cv.personalInfo?.email ? `<div style="margin-bottom: 8px;">✉ ${escapeHtml(cv.personalInfo.email)}</div>` : ''}
            ${cv.personalInfo?.location ? `<div style="margin-bottom: 8px;">📍 ${escapeHtml(cv.personalInfo.location)}</div>` : ''}
          </div>
        </div>

        ${skillsHtml}
        ${educationHtml}
      </div>

      <!-- Main Content -->
      <div style="flex: 1; padding: 40px;">
        <div style="margin-bottom: 30px;">
          <h1 style="font-size: 32px; font-weight: bold; color: #000; margin: 0 0 8px 0; text-transform: uppercase;">
            ${escapeHtml(cv.personalInfo?.fullname || 'Họ và tên')}
          </h1>
          <p style="font-size: 16px; color: ${primaryColor}; margin: 0 0 20px 0; text-transform: uppercase; 
                    font-weight: 600; letter-spacing: 1px;">
            ${escapeHtml(cv.title || 'Untitled CV')}
          </p>
          ${cv.personalInfo?.summary ? `
            <p style="font-size: 13px; line-height: 1.7; color: #333; text-align: justify; margin: 0;">
              ${escapeHtml(cv.personalInfo.summary)}
            </p>
          ` : ''}
        </div>

        ${experienceHtml}
      </div>
    </div>
  `;
}