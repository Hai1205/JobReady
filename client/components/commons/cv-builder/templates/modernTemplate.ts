/**
 * Modern CV Template
 * Clean, professional design with emphasis on readability
 */

/**
 * Generate Modern Template HTML
 * @param cv - CV data
 * @param primaryColor - Primary theme color (hex)
 * @returns Complete HTML string with inline CSS
 */
export function generateModernTemplate(cv: ICV, primaryColor: string): string {
  // Helper function to escape HTML
  const escapeHtml = (text: string): string => {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  };

  // Avatar section
  const avatarHtml = (cv.personalInfo?.avatarUrl || cv.personalInfo?.avatarPublicId)
    ? `<img src="${escapeHtml(cv.personalInfo.avatarUrl || cv.personalInfo.avatarPublicId || '')}" 
            alt="Avatar" 
            style="width: 120px; height: 120px; border-radius: 50%; object-fit: cover; border: 3px solid ${primaryColor};" />`
    : `<div style="width: 120px; height: 120px; border-radius: 50%; background-color: ${primaryColor}; 
               color: white; display: flex; align-items: center; justify-content: center; 
               font-size: 40px; font-weight: bold; border: 3px solid ${primaryColor};">
         ${cv.personalInfo?.fullname
      ? cv.personalInfo.fullname.split(' ').map((n: string) => n[0]).join('').toUpperCase().slice(0, 2)
      : 'CV'}
       </div>`;

  // Contact info
  const contactInfo = [
    cv.personalInfo?.email && `<div style="margin-bottom: 5px;"><span style="font-weight: 600;">Email:</span> ${escapeHtml(cv.personalInfo.email)}</div>`,
    cv.personalInfo?.phone && `<div style="margin-bottom: 5px;"><span style="font-weight: 600;">Phone:</span> ${escapeHtml(cv.personalInfo.phone)}</div>`,
    cv.personalInfo?.location && `<div style="margin-bottom: 5px;"><span style="font-weight: 600;">Location:</span> ${escapeHtml(cv.personalInfo.location)}</div>`,
  ].filter(Boolean).join('');

  // Summary section
  const summaryHtml = cv.personalInfo?.summary
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: ${primaryColor}; margin-bottom: 10px; 
                    margin-top: 0; text-transform: uppercase; border-bottom: 2px solid ${primaryColor}; padding-bottom: 5px;">
           Giới thiệu
         </h2>
         <p style="font-size: 13px; line-height: 1.6; color: #333; text-align: justify; margin: 0;">
           ${escapeHtml(cv.personalInfo.summary)}
         </p>
       </div>`
    : '';

  // Experience section
  const experienceHtml = cv.experiences && cv.experiences.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: ${primaryColor}; margin-bottom: 10px; 
                    margin-top: 0; text-transform: uppercase; border-bottom: 2px solid ${primaryColor}; padding-bottom: 5px;">
           Kinh nghiệm làm việc
         </h2>
         <div>
           ${cv.experiences.map((exp: IExperience, index: number) => `
             <div style="margin-bottom: ${index < cv.experiences.length - 1 ? '15px' : '0'}; 
                         background-color: #f8f9fa; padding: 12px 15px; border-left: 3px solid ${primaryColor};">
               <h3 style="font-weight: bold; color: #000; font-size: 14px; margin-bottom: 5px; margin-top: 0;">
                 ${escapeHtml(exp.position)}
               </h3>
               <p style="font-size: 13px; color: #666; margin-bottom: 5px; margin-top: 0; font-style: italic;">
                 ${escapeHtml(exp.company)}
               </p>
               <p style="font-size: 12px; color: #999; margin-bottom: 8px; margin-top: 0;">
                 ${escapeHtml(exp.startDate)} - ${escapeHtml(exp.endDate || 'Present')}
               </p>
               ${exp.description ? `<p style="font-size: 13px; color: #333; line-height: 1.6; text-align: justify; margin: 0;">
                 ${escapeHtml(exp.description)}
               </p>` : ''}
             </div>
           `).join('')}
         </div>
       </div>`
    : '';

  // Education section
  const educationHtml = cv.educations && cv.educations.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: ${primaryColor}; margin-bottom: 10px; 
                    margin-top: 0; text-transform: uppercase; border-bottom: 2px solid ${primaryColor}; padding-bottom: 5px;">
           Học vấn
         </h2>
         <div>
           ${cv.educations.map((edu: IEducation, index: number) => `
             <div style="margin-bottom: ${index < cv.educations.length - 1 ? '15px' : '0'}; 
                         background-color: #f8f9fa; padding: 12px 15px; border-left: 3px solid ${primaryColor};">
               <h3 style="font-weight: bold; color: #000; font-size: 14px; margin-bottom: 5px; margin-top: 0;">
                 ${escapeHtml(edu.degree)}
               </h3>
               <p style="font-size: 13px; color: #666; margin-bottom: 5px; margin-top: 0; font-style: italic;">
                 ${escapeHtml(edu.school)}
               </p>
               <p style="font-size: 12px; color: #999; margin: 0;">
                 ${edu.field ? `<span>${escapeHtml(edu.field)} • </span>` : ''}
                 ${escapeHtml(edu.startDate)} - ${escapeHtml(edu.endDate || 'Present')}
               </p>
             </div>
           `).join('')}
         </div>
       </div>`
    : '';

  // Skills section
  const skillsHtml = cv.skills && cv.skills.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: ${primaryColor}; margin-bottom: 10px; 
                    margin-top: 0; text-transform: uppercase; border-bottom: 2px solid ${primaryColor}; padding-bottom: 5px;">
           Kỹ năng
         </h2>
         <div style="display: flex; flex-wrap: wrap; gap: 8px;">
           ${cv.skills.map((skill: string) => `
             <span style="display: inline-block; padding: 6px 12px; background-color: ${primaryColor}15; 
                          color: ${primaryColor}; border-radius: 4px; font-size: 12px; font-weight: 500; 
                          border: 1px solid ${primaryColor}40;">
               ${escapeHtml(skill)}
             </span>
           `).join('')}
         </div>
       </div>`
    : '';

  // Complete HTML document
  return `
    <div style="max-width: 210mm; margin: 0 auto; background-color: white; padding: 40px; min-height: 297mm; 
                font-family: 'Times New Roman', Times, serif; font-size: 13px; line-height: 1.5; color: #000;">
      <!-- Header Section -->
      <div style="display: flex; gap: 30px; align-items: flex-start; margin-bottom: 30px; 
                  padding-bottom: 20px; border-bottom: 2px solid ${primaryColor};">
        <!-- Avatar -->
        <div style="flex-shrink: 0;">
          ${avatarHtml}
        </div>
        
        <!-- Personal Info -->
        <div style="flex: 1;">
          <h1 style="font-size: 32px; font-weight: bold; color: #000; margin-bottom: 8px; margin-top: 0; text-transform: uppercase;">
            ${escapeHtml(cv.personalInfo?.fullname || 'Họ và tên')}
          </h1>
          <p style="font-size: 15px; color: #666; font-style: italic; margin-bottom: 15px; margin-top: 0;">
            ${escapeHtml(cv.title || 'Vị trí ứng tuyển')}
          </p>
          <div style="font-size: 13px; color: #333; line-height: 1.8;">
            ${contactInfo}
          </div>
        </div>
      </div>

      <!-- Content Sections -->
      <div style="margin-top: 0;">
        ${summaryHtml}
        ${experienceHtml}
        ${educationHtml}
        ${skillsHtml}
      </div>
    </div>
  `;
}
