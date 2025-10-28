import React from "react";

interface CVRendererProps {
  cv: ICV;
}

/**
 * CVRenderer component - renders a CV to HTML for PDF export
 * This component is used to generate HTML content that can be exported to PDF
 * without requiring the preview step to be mounted in the DOM
 */
export const CVRenderer: React.FC<CVRendererProps> = ({ cv }) => {
  return (
    <div
      className="max-w-4xl mx-auto bg-white p-8"
      style={{ width: "210mm", minHeight: "297mm" }}
    >
      {/* Header with Avatar and Personal Info */}
      <div className="flex items-start gap-6 mb-6 pb-6 border-b-2 border-gray-200">
        {cv.personalInfo?.avatarUrl && (
          <div className="flex-shrink-0">
            <img
              src={cv.personalInfo.avatarUrl}
              alt={cv.personalInfo?.fullname || "Avatar"}
              className="w-32 h-32 rounded-full object-cover border-4 border-blue-500"
            />
          </div>
        )}
        <div className="flex-1">
          <h1 className="text-4xl font-bold text-gray-800 mb-2">
            {cv.personalInfo?.fullname || "Không có tên"}
          </h1>
          <p className="text-xl text-gray-600 mb-4">{cv.title}</p>
          <div className="grid grid-cols-1 gap-2 text-sm text-gray-700">
            {cv.personalInfo?.email && (
              <div className="flex items-center gap-2">
                <span className="font-semibold">Email:</span>
                <span>{cv.personalInfo.email}</span>
              </div>
            )}
            {cv.personalInfo?.phone && (
              <div className="flex items-center gap-2">
                <span className="font-semibold">Phone:</span>
                <span>{cv.personalInfo.phone}</span>
              </div>
            )}
            {cv.personalInfo?.location && (
              <div className="flex items-center gap-2">
                <span className="font-semibold">Location:</span>
                <span>{cv.personalInfo.location}</span>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Summary */}
      {cv.personalInfo?.summary && (
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-gray-800 mb-3 pb-2 border-b border-gray-300">
            Giới thiệu
          </h2>
          <p className="text-gray-700 leading-relaxed">
            {cv.personalInfo.summary}
          </p>
        </div>
      )}

      {/* Experience */}
      {cv.experiences && cv.experiences.length > 0 && (
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-gray-800 mb-3 pb-2 border-b border-gray-300">
            Kinh nghiệm làm việc
          </h2>
          <div className="space-y-4">
            {cv.experiences.map((exp, index) => (
              <div key={index} className="pl-4 border-l-2 border-blue-500">
                <h3 className="text-lg font-semibold text-gray-800">
                  {exp.position}
                </h3>
                <p className="text-md font-medium text-gray-700">
                  {exp.company}
                </p>
                <p className="text-sm text-gray-600 mb-2">
                  {exp.startDate} - {exp.endDate || "Hiện tại"}
                </p>
                {exp.description && (
                  <p className="text-gray-700 leading-relaxed whitespace-pre-line">
                    {exp.description}
                  </p>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Education */}
      {cv.educations && cv.educations.length > 0 && (
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-gray-800 mb-3 pb-2 border-b border-gray-300">
            Học vấn
          </h2>
          <div className="space-y-4">
            {cv.educations.map((edu, index) => (
              <div key={index} className="pl-4 border-l-2 border-blue-500">
                <h3 className="text-lg font-semibold text-gray-800">
                  {edu.degree}
                </h3>
                <p className="text-md font-medium text-gray-700">
                  {edu.school}
                </p>
                <p className="text-sm text-gray-600 mb-2">
                  {edu.field && <span className="italic">{edu.field} • </span>}
                  {edu.startDate} - {edu.endDate || "Hiện tại"}
                </p>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Skills */}
      {cv.skills && cv.skills.length > 0 && (
        <div className="mb-6">
          <h2 className="text-2xl font-bold text-gray-800 mb-3 pb-2 border-b border-gray-300">
            Kỹ năng
          </h2>
          <div className="flex flex-wrap gap-2">
            {cv.skills.map((skill, index) => (
              <span
                key={index}
                className="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm font-medium"
              >
                {skill}
              </span>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

/**
 * Utility function to generate CV HTML string directly from CV data
 * This is more efficient than rendering React components for PDF export
 */
export const generateCVHTML = (cv: ICV): string => {
  const avatarSection = cv.personalInfo?.avatarUrl
    ? `<div style="flex-shrink: 0;">
         <img
           src="${cv.personalInfo.avatarUrl}"
           alt="${cv.personalInfo?.fullname || "Avatar"}"
           style="width: 120px; height: 120px; border-radius: 50%; object-fit: cover; border: 3px solid #4A90E2;"
         />
       </div>`
    : `<div style="flex-shrink: 0;">
         <div style="width: 120px; height: 120px; border-radius: 50%; background-color: #4A90E2; color: white; display: flex; align-items: center; justify-content: center; font-size: 40px; font-weight: bold; border: 3px solid #4A90E2;">
           ${
             cv.personalInfo?.fullname
               ? cv.personalInfo.fullname
                   .split(" ")
                   .map((n) => n[0])
                   .join("")
                   .toUpperCase()
                   .slice(0, 2)
               : "CV"
           }
         </div>
       </div>`;

  const contactInfo = `
    ${
      cv.personalInfo?.email
        ? `<div style="margin-bottom: 5px;">
      <span style="font-weight: 600;">Email: </span>
      <span>${cv.personalInfo.email}</span>
    </div>`
        : ""
    }
    ${
      cv.personalInfo?.phone
        ? `<div style="margin-bottom: 5px;">
      <span style="font-weight: 600;">Phone: </span>
      <span>${cv.personalInfo.phone}</span>
    </div>`
        : ""
    }
    ${
      cv.personalInfo?.location
        ? `<div style="margin-bottom: 5px;">
      <span style="font-weight: 600;">Location: </span>
      <span>${cv.personalInfo.location}</span>
    </div>`
        : ""
    }
  `;

  const summarySection = cv.personalInfo?.summary
    ? `<div style="margin-bottom: 25px;">
         <h2 style="font-size: 16px; font-weight: bold; color: #000; margin-bottom: 10px; margin-top: 0; text-transform: uppercase; border-bottom: 2px solid #4A90E2; padding-bottom: 5px;">
           Giới thiệu
         </h2>
         <p style="font-size: 13px; line-height: 1.6; color: #333; text-align: justify; margin: 0;">${cv.personalInfo.summary}</p>
       </div>`
    : "";

  const experiencesSection =
    cv.experiences && cv.experiences.length > 0
      ? `<div style="margin-bottom: 25px;">
           <h2 style="font-size: 16px; font-weight: bold; color: #000; margin-bottom: 10px; margin-top: 0; text-transform: uppercase; border-bottom: 2px solid #4A90E2; padding-bottom: 5px;">
             Kinh nghiệm làm việc
           </h2>
           <div>
             ${cv.experiences
               .map(
                 (exp, index) => `
               <div style="margin-bottom: ${
                 index < cv.experiences.length - 1 ? "15px" : "0"
               }; background-color: #f8f9fa; padding: 12px 15px; border-left: 3px solid #4A90E2;">
                 <h3 style="font-weight: bold; color: #000; font-size: 14px; margin-bottom: 5px; margin-top: 0;">${
                   exp.position
                 }</h3>
                 <p style="font-size: 13px; color: #666; margin-bottom: 5px; margin-top: 0; font-style: italic;">${
                   exp.company
                 }</p>
                 <p style="font-size: 12px; color: #999; margin-bottom: 8px; margin-top: 0;">
                   ${exp.startDate} - ${exp.endDate || "Present"}
                 </p>
                 ${
                   exp.description
                     ? `<p style="font-size: 13px; color: #333; line-height: 1.6; text-align: justify; margin: 0;">
                       ${exp.description}
                     </p>`
                     : ""
                 }
               </div>
             `
               )
               .join("")}
           </div>
         </div>`
      : "";

  const educationsSection =
    cv.educations && cv.educations.length > 0
      ? `<div style="margin-bottom: 25px;">
           <h2 style="font-size: 16px; font-weight: bold; color: #000; margin-bottom: 10px; margin-top: 0; text-transform: uppercase; border-bottom: 2px solid #4A90E2; padding-bottom: 5px;">
             Học vấn
           </h2>
           <div>
             ${cv.educations
               .map(
                 (edu, index) => `
               <div style="margin-bottom: ${
                 index < cv.educations.length - 1 ? "15px" : "0"
               }; background-color: #f8f9fa; padding: 12px 15px; border-left: 3px solid #4A90E2;">
                 <h3 style="font-weight: bold; color: #000; font-size: 14px; margin-bottom: 5px; margin-top: 0;">${
                   edu.degree
                 }</h3>
                 <p style="font-size: 13px; color: #666; margin-bottom: 5px; margin-top: 0; font-style: italic;">${
                   edu.school
                 }</p>
                 <p style="font-size: 12px; color: #999; margin: 0;">
                   ${edu.field ? `<span>${edu.field} • </span>` : ""}
                   ${edu.startDate} - ${edu.endDate || "Present"}
                 </p>
               </div>
             `
               )
               .join("")}
           </div>
         </div>`
      : "";

  const skillsSection =
    cv.skills && cv.skills.length > 0
      ? `<div style="margin-bottom: 25px;">
           <h2 style="font-size: 16px; font-weight: bold; color: #000; margin-bottom: 10px; margin-top: 0; text-transform: uppercase; border-bottom: 2px solid #4A90E2; padding-bottom: 5px;">
             Kỹ năng
           </h2>
           <div style="display: flex; flex-wrap: wrap; gap: 8px;">
             ${cv.skills
               .map(
                 (skill) =>
                   `<span style="display: inline-block; padding: 6px 12px; background-color: #E3F2FD; color: #1976D2; border-radius: 4px; font-size: 12px; font-weight: 500;">
                     ${skill}
                   </span>`
               )
               .join("")}
           </div>
         </div>`
      : "";

  return `
    <div style="max-width: 210mm; margin: 0 auto; background-color: white; padding: 40px; min-height: 297mm; font-family: 'Times New Roman', Times, serif; font-size: 13px; line-height: 1.5; color: #000;">
      <!-- Header Section -->
      <div style="display: flex; gap: 30px; align-items: flex-start; margin-bottom: 30px; padding-bottom: 20px; border-bottom: 1px solid #ddd;">
        ${avatarSection}
        <div style="flex: 1;">
          <h1 style="font-size: 32px; font-weight: bold; color: #000; margin-bottom: 8px; margin-top: 0; text-transform: uppercase;">
            ${cv.personalInfo?.fullname || "Họ và tên"}
          </h1>
          <p style="font-size: 15px; color: #666; font-style: italic; margin-bottom: 15px; margin-top: 0;">${
            cv.title || "Vị trí ứng tuyển"
          }</p>
          <div style="font-size: 13px; color: #333; line-height: 1.8;">
            ${contactInfo}
          </div>
        </div>
      </div>

      ${summarySection}
      ${experiencesSection}
      ${educationsSection}
      ${skillsSection}
    </div>
  `;
};

/**
 * Async version that wraps generateCVHTML for consistency with the API
 */
export const renderCVToHTMLAsync = async (cv: ICV): Promise<string> => {
  return Promise.resolve(generateCVHTML(cv));
};
