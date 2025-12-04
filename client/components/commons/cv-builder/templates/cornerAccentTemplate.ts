/**
 * Corner Accent CV Template (Hình 7)
 * Template with decorative corner triangles
 */

/**
 * Generate Corner Accent Template HTML
 * @param cv - CV data
 * @param primaryColor - Primary theme color (hex)
 * @param fontFamily - Font family to use (default: 'Inter, sans-serif')
 * @returns Complete HTML string with inline CSS
 */
export function generateCornerAccentTemplate(cv: ICV, primaryColor: string, fontFamily: string = 'Inter, sans-serif'): string {
  const escapeHtml = (text: string): string => {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  };

  // Education section with certifications
  const educationItems = [...(cv.educations || [])];

  const educationHtml = educationItems.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: #000; margin-bottom: 10px; 
                    margin-top: 0; display: flex; align-items: center; gap: 10px;">
           <span style="width: 6px; height: 6px; background-color: ${primaryColor}; 
                        transform: rotate(45deg); display: inline-block;"></span>
           HỌC VẤN
         </h2>
         ${educationItems.map((edu: IEducation) => `
           <div style="margin-bottom: 12px; padding-left: 16px;">
             <div style="display: flex; justify-content: space-between; align-items: baseline;">
               <h3 style="font-weight: bold; color: #000; font-size: 13px; margin-bottom: 3px; margin-top: 0;">
                 ${escapeHtml(edu.school)}
               </h3>
               ${edu.startDate ? `<span style="font-size: 11px; color: #999; white-space: nowrap; margin-left: 15px;">
                 ${escapeHtml(edu.startDate)}${edu.endDate ? ` - ${escapeHtml(edu.endDate)}` : ''}
               </span>` : ''}
             </div>
             <p style="font-size: 12px; color: #666; margin: 0;">
               ${escapeHtml(edu.degree)}${edu.field ? ` - ${escapeHtml(edu.field)}` : ''}
             </p>
           </div>
         `).join('')}
       </div>`
    : '';

  // Skills section - combined with languages
  const allSkills = [...(cv.skills || [])];

  const skillsHtml = allSkills.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: #000; margin-bottom: 10px; 
                    margin-top: 0; display: flex; align-items: center; gap: 10px;">
           <span style="width: 6px; height: 6px; background-color: ${primaryColor}; 
                        transform: rotate(45deg); display: inline-block;"></span>
           KỸ NĂNG
         </h2>
         <div style="padding-left: 16px; display: flex; flex-wrap: wrap; gap: 6px;">
           ${allSkills.map((skill: string) => `
             <span style="display: inline-block; padding: 5px 10px; background-color: white; 
                          color: #333; border-radius: 3px; font-size: 11px; font-weight: 500;
                          border: 1px solid #ddd;">
               ${escapeHtml(skill)}
             </span>
           `).join('')}
         </div>
       </div>`
    : '';

  // Experience section
  const experienceHtml = cv.experiences && cv.experiences.length > 0
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: #000; margin-bottom: 10px; 
                    margin-top: 0; display: flex; align-items: center; gap: 10px;">
           <span style="width: 6px; height: 6px; background-color: ${primaryColor}; 
                        transform: rotate(45deg); display: inline-block;"></span>
           KINH NGHIỆM LÀM VIỆC
         </h2>
         ${cv.experiences.map((exp: IExperience) => `
           <div style="margin-bottom: 20px; padding-left: 16px;">
             <div style="margin-bottom: 8px;">
               <div style="display: flex; justify-content: space-between; align-items: baseline;">
                 <h3 style="font-weight: bold; color: #000; font-size: 14px; margin: 0;">
                   ${escapeHtml(exp.position)}
                 </h3>
                 <span style="font-size: 11px; color: #999; white-space: nowrap; margin-left: 15px;">
                   ${escapeHtml(exp.startDate)} - ${escapeHtml(exp.endDate || 'Present')}
                 </span>
               </div>
               <p style="font-size: 13px; color: #666; margin: 5px 0 0 0; font-weight: 600;">
                 ${escapeHtml(exp.company)}
               </p>
             </div>
             ${exp.description ? `
               <ul style="margin: 0; padding-left: 20px; font-size: 12px; color: #333; line-height: 1.6;">
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
    <div style="max-width: 210mm; margin: 0 auto; background-color: white; padding: 50px; min-height: 297mm; 
                font-family: ${fontFamily}; font-size: 13px; line-height: 1.5; color: #000; position: relative;">
      <!-- Top Left Corner -->
      <div style="position: absolute; top: 0; left: 0; width: 0; height: 0; 
                  border-top: 120px solid ${primaryColor}; border-right: 120px solid transparent;"></div>
      
      <!-- Bottom Right Corner -->
      <div style="position: absolute; bottom: 0; right: 0; width: 0; height: 0; 
                  border-bottom: 120px solid ${primaryColor}; border-left: 120px solid transparent;"></div>

      <!-- Header Section -->
      <div style="position: relative; z-index: 1; margin-bottom: 30px; padding: 20px 0;">
        <h1 style="font-size: 32px; font-weight: bold; color: ${primaryColor}; margin: 0 0 8px 0; text-transform: uppercase;">
          ${escapeHtml(cv.personalInfo?.fullname || 'Họ và tên')}
        </h1>
        <p style="font-size: 14px; color: #666; margin: 0 0 15px 0; font-weight: 600;">
          ${escapeHtml(cv.title || 'Untitled CV')}
        </p>
        <div style="font-size: 12px; color: #333; line-height: 1.8;">
          ${cv.personalInfo?.phone ? `<span style="margin-right: 15px;">📞 ${escapeHtml(cv.personalInfo.phone)}</span>` : ''}
          ${cv.personalInfo?.email ? `<span style="margin-right: 15px;">✉ ${escapeHtml(cv.personalInfo.email)}</span>` : ''}
          ${cv.personalInfo?.location ? `<span style="margin-right: 15px;">📍 ${escapeHtml(cv.personalInfo.location)}</span>` : ''}
          ${cv.personalInfo?.birth ? `<span>🎂 ${escapeHtml(cv.personalInfo.birth)}</span>` : ''}
        </div>
        ${cv.personalInfo?.summary ? `
          <p style="font-size: 13px; line-height: 1.6; color: #333; text-align: justify; margin: 15px 0 0 0;">
            ${escapeHtml(cv.personalInfo.summary)}
          </p>
        ` : ''}
      </div>

      <!-- Content Section -->
      <div style="position: relative; z-index: 1;">
        ${educationHtml}
        ${experienceHtml}
        ${skillsHtml}
      </div>
    </div>
  `;
}