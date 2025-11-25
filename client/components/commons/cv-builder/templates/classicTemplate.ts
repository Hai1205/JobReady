/**
 * Classic CV Template
 * Traditional single-column layout, professional and formal
 */

/**
 * Generate Classic Template HTML
 * @param cv - CV data
 * @param primaryColor - Primary theme color (hex)
 * @returns Complete HTML string with inline CSS
 */
export function generateClassicTemplate(cv: ICV, primaryColor: string): string {
  // Helper function to escape HTML
  const escapeHtml = (text: string): string => {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  };

  // Avatar section - centered in classic layout
  const avatarHtml = (cv.personalInfo?.avatarUrl || cv.personalInfo?.avatarPublicId)
    ? `<img src="${escapeHtml(cv.personalInfo.avatarUrl || cv.personalInfo.avatarPublicId || '')}" 
            alt="Avatar" 
            style="width: 100px; height: 100px; border-radius: 50%; object-fit: cover; 
                   border: 3px solid ${primaryColor}; margin: 0 auto 20px; display: block;" />`
    : `<div style="width: 100px; height: 100px; border-radius: 50%; background-color: ${primaryColor}; 
               color: white; display: flex; align-items: center; justify-content: center; 
               font-size: 36px; font-weight: bold; border: 3px solid ${primaryColor}; 
               margin: 0 auto 20px;">
         ${cv.personalInfo?.fullname
      ? cv.personalInfo.fullname.split(' ').map((n: string) => n[0]).join('').toUpperCase().slice(0, 2)
      : 'CV'}
       </div>`;

  // Contact info - formatted as inline list
  const contactItems = [
    cv.personalInfo?.email && escapeHtml(cv.personalInfo.email),
    cv.personalInfo?.phone && escapeHtml(cv.personalInfo.phone),
    cv.personalInfo?.location && escapeHtml(cv.personalInfo.location),
  ].filter(Boolean);

  const contactInfo = contactItems.length > 0
    ? `<p style="text-align: center; font-size: 13px; color: #666; margin: 10px 0 0 0; line-height: 1.8;">
         ${contactItems.join(' • ')}
       </p>`
    : '';

  // Summary section
  const summaryHtml = cv.personalInfo?.summary
    ? `<div style="margin-bottom: 30px; padding-top: 20px; border-top: 2px solid ${primaryColor};">
         <h2 style="font-size: 18px; font-weight: bold; color: ${primaryColor}; margin-bottom: 12px; 
                    margin-top: 0; text-transform: uppercase; text-align: center; letter-spacing: 1px;">
           Mục tiêu nghề nghiệp
         </h2>
         <p style="font-size: 13px; line-height: 1.8; color: #333; text-align: justify; margin: 0; 
                   padding: 0 20px;">
           ${escapeHtml(cv.personalInfo.summary)}
         </p>
       </div>`
    : '';

  // Experience section
  const experienceHtml = cv.experiences && cv.experiences.length > 0
    ? `<div style="margin-bottom: 30px;">
         <h2 style="font-size: 18px; font-weight: bold; color: ${primaryColor}; margin-bottom: 15px; 
                    margin-top: 0; text-transform: uppercase; text-align: center; letter-spacing: 1px; 
                    padding-bottom: 8px; border-bottom: 2px solid ${primaryColor};">
           Kinh nghiệm làm việc
         </h2>
         <div style="padding: 0 20px;">
           ${cv.experiences.map((exp: IExperience, index: number) => `
             <div style="margin-bottom: ${index < cv.experiences.length - 1 ? '20px' : '0'}; 
                         page-break-inside: avoid;">
               <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 8px;">
                 <h3 style="font-weight: bold; color: #000; font-size: 15px; margin: 0; flex: 1;">
                   ${escapeHtml(exp.position)}
                 </h3>
                 <span style="font-size: 12px; color: #999; white-space: nowrap; margin-left: 15px;">
                   ${escapeHtml(exp.startDate)} - ${escapeHtml(exp.endDate || 'Present')}
                 </span>
               </div>
               <p style="font-size: 13px; color: ${primaryColor}; margin: 0 0 8px 0; font-weight: 600;">
                 ${escapeHtml(exp.company)}
               </p>
               ${exp.description ? `<p style="font-size: 13px; color: #555; line-height: 1.7; text-align: justify; margin: 0;">
                 ${escapeHtml(exp.description)}
               </p>` : ''}
             </div>
           `).join('')}
         </div>
       </div>`
    : '';

  // Education section
  const educationHtml = cv.educations && cv.educations.length > 0
    ? `<div style="margin-bottom: 30px;">
         <h2 style="font-size: 18px; font-weight: bold; color: ${primaryColor}; margin-bottom: 15px; 
                    margin-top: 0; text-transform: uppercase; text-align: center; letter-spacing: 1px; 
                    padding-bottom: 8px; border-bottom: 2px solid ${primaryColor};">
           Học vấn
         </h2>
         <div style="padding: 0 20px;">
           ${cv.educations.map((edu: IEducation, index: number) => `
             <div style="margin-bottom: ${index < cv.educations.length - 1 ? '20px' : '0'}; 
                         page-break-inside: avoid;">
               <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 8px;">
                 <h3 style="font-weight: bold; color: #000; font-size: 15px; margin: 0; flex: 1;">
                   ${escapeHtml(edu.degree)}
                 </h3>
                 <span style="font-size: 12px; color: #999; white-space: nowrap; margin-left: 15px;">
                   ${escapeHtml(edu.startDate)} - ${escapeHtml(edu.endDate || 'Present')}
                 </span>
               </div>
               <p style="font-size: 13px; color: ${primaryColor}; margin: 0; font-weight: 600;">
                 ${escapeHtml(edu.school)}${edu.field ? ` - <span style="font-style: italic; font-weight: normal;">${escapeHtml(edu.field)}</span>` : ''}
               </p>
             </div>
           `).join('')}
         </div>
       </div>`
    : '';

  // Skills section - displayed as columns
  const skillsHtml = cv.skills && cv.skills.length > 0
    ? `<div style="margin-bottom: 30px;">
         <h2 style="font-size: 18px; font-weight: bold; color: ${primaryColor}; margin-bottom: 15px; 
                    margin-top: 0; text-transform: uppercase; text-align: center; letter-spacing: 1px; 
                    padding-bottom: 8px; border-bottom: 2px solid ${primaryColor};">
           Kỹ năng chuyên môn
         </h2>
         <div style="padding: 0 20px;">
           <ul style="column-count: 2; column-gap: 30px; margin: 0; padding-left: 20px; list-style-type: square;">
             ${cv.skills.map((skill: string) => `
               <li style="font-size: 13px; color: #333; margin-bottom: 8px; padding-left: 5px;">
                 <span style="color: ${primaryColor}; font-weight: 500;">${escapeHtml(skill)}</span>
               </li>
             `).join('')}
           </ul>
         </div>
       </div>`
    : '';

  // Complete HTML document
  return `
    <div style="max-width: 210mm; margin: 0 auto; background-color: white; padding: 40px 50px; min-height: 297mm; 
                font-family: 'Georgia', 'Times New Roman', Times, serif; font-size: 13px; line-height: 1.6; color: #000;">
      <!-- Header Section - Centered -->
      <div style="text-align: center; margin-bottom: 30px; padding-bottom: 20px;">
        ${avatarHtml}
        
        <h1 style="font-size: 36px; font-weight: bold; color: #000; margin: 0 0 5px 0; 
                   text-transform: uppercase; letter-spacing: 2px;">
          ${escapeHtml(cv.personalInfo?.fullname || 'Họ và tên')}
        </h1>
        
        <p style="font-size: 16px; color: #666; margin: 5px 0; font-style: italic; font-weight: 500;">
          ${escapeHtml(cv.title || 'Vị trí ứng tuyển')}
        </p>
        
        ${contactInfo}
      </div>

      <!-- Content Sections -->
      ${summaryHtml}
      ${experienceHtml}
      ${educationHtml}
      ${skillsHtml}
    </div>
  `;
}
