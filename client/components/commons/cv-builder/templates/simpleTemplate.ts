/**
 * Simple Orange CV Template (Hình 5)
 * Clean text-based design with orange accents
 */

/**
 * Generate Simple Template HTML
 * @param cv - CV data
 * @param primaryColor - Primary theme color (hex)
 * @param fontFamily - Font family to use (default: 'Inter, sans-serif')
 * @returns Complete HTML string with inline CSS
 */
export function generateSimpleTemplate(cv: ICV, primaryColor: string, fontFamily: string = 'Inter, sans-serif'): string {
  const escapeHtml = (text: string): string => {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  };

  // Skills section - inline display
  const allSkills = [...(cv.skills || [])];

  const skillsHtml = allSkills.length > 0
    ? `<div style="margin-bottom: 30px;">
         <h2 style="font-size: 16px; font-weight: bold; color: ${primaryColor}; margin-bottom: 12px; 
                    margin-top: 0; text-transform: uppercase; letter-spacing: 1px; 
                    border-bottom: 2px solid ${primaryColor}; padding-bottom: 5px;">
           Kỹ năng
         </h2>
         <div style="margin-bottom: 12px;">
           <span style="font-size: 13px; color: #333;">
             ${allSkills.map(s => escapeHtml(s)).join(' • ')}
           </span>
         </div>
       </div>`
    : '';

  // Education section with certifications
  const educationItems = [...(cv.educations || [])];

  const educationHtml = educationItems.length > 0
    ? `<div style="margin-bottom: 30px;">
         <h2 style="font-size: 16px; font-weight: bold; color: ${primaryColor}; margin-bottom: 12px; 
                    margin-top: 0; text-transform: uppercase; letter-spacing: 1px; 
                    border-bottom: 2px solid ${primaryColor}; padding-bottom: 5px;">
           Học vấn
         </h2>
         ${educationItems.map((edu: IEducation) => `
           <div style="margin-bottom: 15px;">
             <h3 style="font-weight: bold; color: #000; font-size: 14px; margin-bottom: 3px; margin-top: 0;">
               ${escapeHtml(edu.school)}
             </h3>
             <p style="font-size: 13px; color: #666; margin: 0;">
               ${escapeHtml(edu.degree)}${edu.field ? ` - ${escapeHtml(edu.field)}` : ''}
             </p>
             ${edu.startDate ? `<p style="font-size: 12px; color: #999; margin: 3px 0 0 0;">
               ${escapeHtml(edu.startDate)}${edu.endDate && edu.endDate !== edu.startDate ? ` - ${escapeHtml(edu.endDate)}` : ''}
             </p>` : ''}
           </div>
         `).join('')}
       </div>`
    : '';

  // Experience section
  const experienceHtml = cv.experiences && cv.experiences.length > 0
    ? `<div style="margin-bottom: 30px;">
         <h2 style="font-size: 16px; font-weight: bold; color: ${primaryColor}; margin-bottom: 12px; 
                    margin-top: 0; text-transform: uppercase; letter-spacing: 1px; 
                    border-bottom: 2px solid ${primaryColor}; padding-bottom: 5px;">
           Kinh nghiệm làm việc
         </h2>
         ${cv.experiences.map((exp: IExperience) => `
           <div style="margin-bottom: 25px;">
             <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 8px;">
               <h3 style="font-weight: bold; color: ${primaryColor}; font-size: 15px; margin: 0;">
                 ${escapeHtml(exp.position)}
               </h3>
               <span style="font-size: 12px; color: #999; white-space: nowrap; margin-left: 15px;">
                 ${escapeHtml(exp.startDate)} - ${escapeHtml(exp.endDate || 'Present')}
               </span>
             </div>
             <p style="font-size: 13px; color: #000; margin: 0 0 8px 0; font-weight: 600;">
               ${escapeHtml(exp.company)}
             </p>
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
    <div style="max-width: 210mm; margin: 0 auto; background-color: white; padding: 50px 60px; min-height: 297mm; 
                font-family: ${fontFamily}; font-size: 13px; line-height: 1.6; color: #000;">
      <!-- Header Section -->
      <div style="text-align: center; margin-bottom: 35px; padding-bottom: 25px; border-bottom: 3px solid ${primaryColor};">
        <h1 style="font-size: 36px; font-weight: bold; color: ${primaryColor}; margin: 0 0 8px 0; text-transform: uppercase; letter-spacing: 2px;">
          ${escapeHtml(cv.personalInfo?.fullname || 'Họ và tên')}
        </h1>
        <p style="font-size: 15px; color: #666; margin: 0; text-transform: uppercase; letter-spacing: 1px;">
          ${escapeHtml(cv.title || 'Untitled CV')}
        </p>
      </div>

      <!-- Contact Info -->
      <div style="text-align: center; font-size: 12px; color: #333; margin-bottom: 30px; line-height: 1.8;">
        ${cv.personalInfo?.phone ? `<span style="margin: 0 12px;">📞 ${escapeHtml(cv.personalInfo.phone)}</span>` : ''}
        ${cv.personalInfo?.email ? `<span style="margin: 0 12px;">✉ ${escapeHtml(cv.personalInfo.email)}</span>` : ''}
        ${cv.personalInfo?.location ? `<span style="margin: 0 12px;">📍 ${escapeHtml(cv.personalInfo.location)}</span>` : ''}
        ${cv.personalInfo?.birth ? `<span style="margin: 0 12px;">🎂 ${escapeHtml(cv.personalInfo.birth)}</span>` : ''}
      </div>

      <!-- Summary Section -->
      ${cv.personalInfo?.summary ? `
        <div style="margin-bottom: 30px;">
          <h2 style="font-size: 16px; font-weight: bold; color: ${primaryColor}; margin-bottom: 12px; 
                     margin-top: 0; text-transform: uppercase; letter-spacing: 1px; 
                     border-bottom: 2px solid ${primaryColor}; padding-bottom: 5px;">
            Giới thiệu
          </h2>
          <p style="font-size: 13px; line-height: 1.7; color: #333; text-align: justify; margin: 0;">
            ${escapeHtml(cv.personalInfo.summary)}
          </p>
        </div>
      ` : ''}

      <!-- Content Sections -->
      ${experienceHtml}
      ${educationHtml}
      ${skillsHtml}
    </div>
  `;
}