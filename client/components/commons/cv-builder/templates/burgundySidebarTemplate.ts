/**
 * Burgundy Sidebar CV Template (Hình 1)
 * Deep burgundy sidebar with white main content
 */

/**
 * Generate Burgundy Sidebar Template HTML
 * @param cv - CV data
 * @param primaryColor - Primary theme color (hex)
 * @param fontFamily - Font family to use (default: 'Inter, sans-serif')
 * @returns Complete HTML string with inline CSS
 */
export function generateBurgundySidebarTemplate(cv: ICV, primaryColor: string, fontFamily: string = 'Inter, sans-serif'): string {
  const escapeHtml = (text: string): string => {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  };

  // Skills section - two column grid
  const allSkills = [...(cv.skills || [])];

  const skillsHtml = allSkills.length > 0
    ? `<div style="margin-bottom: 30px;">
         <h2 style="font-size: 16px; font-weight: bold; color: white; margin-bottom: 12px; 
                    margin-top: 0; text-transform: uppercase; letter-spacing: 1px;">
           Kỹ năng
         </h2>
         <div style="margin-bottom: 15px;">
           <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 8px;">
             ${allSkills.map((skill: string) => {
      const parts = skill.split('(');
      const name = parts[0].trim();
      const exp = parts[1] ? parts[1].replace(')', '').trim() : '';
      return `
                 <div style="background-color: rgba(255,255,255,0.15); padding: 6px 8px; 
                            border-radius: 3px; font-size: 11px; color: white;">
                   <div style="font-weight: 600;">${escapeHtml(name)}</div>
                   ${exp ? `<div style="font-size: 10px; color: rgba(255,255,255,0.8);">${escapeHtml(exp)}</div>` : ''}
                 </div>
               `;
    }).join('')}
           </div>
         </div>
       </div>`
    : '';

  // Education section with certifications
  const educationItems = [...(cv.educations || [])];

  const educationHtml = educationItems.length > 0
    ? `<div style="margin-bottom: 30px;">
         <h2 style="font-size: 16px; font-weight: bold; color: white; margin-bottom: 12px; 
                    margin-top: 0; text-transform: uppercase; letter-spacing: 1px;">
           Học vấn
         </h2>
         ${educationItems.map((edu: IEducation) => `
           <div style="margin-bottom: 15px;">
             <h3 style="font-weight: bold; color: white; font-size: 12px; margin-bottom: 5px; margin-top: 0;">
               ${escapeHtml(edu.school)}
             </h3>
             <p style="font-size: 11px; color: rgba(255,255,255,0.9); margin: 0;">
               ${escapeHtml(edu.startDate)}${edu.endDate && edu.endDate !== edu.startDate ? ` - ${escapeHtml(edu.endDate)}` : ''}
             </p>
             <p style="font-size: 11px; color: rgba(255,255,255,0.8); margin: 3px 0 0 0; font-style: italic;">
               ${escapeHtml(edu.degree)}${edu.field ? ` - ${escapeHtml(edu.field)}` : ''}
             </p>
           </div>
         `).join('')}
       </div>`
    : '';

  // Experience section
  const experienceHtml = cv.experiences && cv.experiences.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 18px; font-weight: bold; color: #000; margin-bottom: 15px; 
                    margin-top: 0; text-transform: uppercase; letter-spacing: 1px;">
           Kinh nghiệm làm việc
         </h2>
         ${cv.experiences.map((exp: IExperience) => `
           <div style="margin-bottom: 25px;">
             <div style="margin-bottom: 10px;">
               <div style="display: flex; justify-content: space-between; align-items: baseline;">
                 <h3 style="font-weight: bold; color: #000; font-size: 15px; margin: 0;">
                   ${escapeHtml(exp.position)} | ${escapeHtml(exp.company)}
                 </h3>
               </div>
               <p style="font-size: 12px; color: #999; margin: 5px 0 0 0;">
                 ${escapeHtml(exp.startDate)} - ${escapeHtml(exp.endDate || 'Present')}
               </p>
             </div>
             ${exp.description ? `
               <ul style="margin: 0; padding-left: 20px; font-size: 13px; color: #333; line-height: 1.7;">
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
      <div style="width: 35%; background: linear-gradient(180deg, ${primaryColor} 0%, #6d1f3f 100%); 
                  padding: 40px 25px; color: white;">
        <div style="text-align: center; margin-bottom: 30px;">
          <h1 style="font-size: 22px; font-weight: bold; color: white; margin: 0 0 5px 0; text-transform: uppercase;">
            ${escapeHtml(cv.personalInfo?.fullname || 'Họ và tên')}
          </h1>
          <p style="font-size: 13px; color: rgba(255,255,255,0.9); margin: 0; text-transform: uppercase; letter-spacing: 1px;">
            ${escapeHtml(cv.title || 'Untitled CV')}
          </p>
        </div>

        <div style="margin-bottom: 25px; font-size: 11px; line-height: 1.8;">
          ${cv.personalInfo?.phone ? `<div style="margin-bottom: 8px; display: flex; align-items: center; gap: 8px;">
            <span style="color: rgba(255,255,255,0.8);">📞</span>
            <span>${escapeHtml(cv.personalInfo.phone)}</span>
          </div>` : ''}
          ${cv.personalInfo?.email ? `<div style="margin-bottom: 8px; display: flex; align-items: center; gap: 8px;">
            <span style="color: rgba(255,255,255,0.8);">✉</span>
            <span style="word-break: break-all;">${escapeHtml(cv.personalInfo.email)}</span>
          </div>` : ''}
          ${cv.personalInfo?.location ? `<div style="margin-bottom: 8px; display: flex; align-items: center; gap: 8px;">
            <span style="color: rgba(255,255,255,0.8);">📍</span>
            <span>${escapeHtml(cv.personalInfo.location)}</span>
          </div>` : ''}
          ${cv.personalInfo?.birth ? `<div style="display: flex; align-items: center; gap: 8px;">
            <span style="color: rgba(255,255,255,0.8);">🎂</span>
            <span>${escapeHtml(cv.personalInfo.birth)}</span>
          </div>` : ''}
        </div>

        ${educationHtml}
        ${skillsHtml}
      </div>

      <!-- Main Content -->
      <div style="flex: 1; padding: 40px; background-color: white;">
        ${cv.personalInfo?.summary ? `
          <div style="margin-bottom: 30px; padding: 20px; background-color: #e9ecef; border-radius: 8px;">
            <p style="font-size: 13px; line-height: 1.7; color: #333; text-align: justify; margin: 0;">
              ${escapeHtml(cv.personalInfo.summary)}
            </p>
          </div>
        ` : ''}

        ${experienceHtml}
      </div>
    </div>
  `;
}