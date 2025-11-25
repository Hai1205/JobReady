import React from "react";
import { generateCVHTML as generateTemplateHTML } from "./templates/templates";

interface CVRendererProps {
  cv: any;
}

/**
 * CVRenderer component - renders a CV to HTML for PDF export
 * This component is used to generate HTML content that can be exported to PDF
 * without requiring the preview step to be mounted in the DOM
 * Now delegates to the template system for consistent rendering
 */
export const CVRenderer: React.FC<CVRendererProps> = ({ cv }) => {
  const html = generateTemplateHTML(cv, cv.template);

  return <div dangerouslySetInnerHTML={{ __html: html }} />;
};

/**
 * Utility function to generate CV HTML string directly from CV data
 * This is more efficient than rendering React components for PDF export
 * Supports dynamic color theming from CV.color property
 * @deprecated Use generateCVHTML from templates/index.ts instead
 */
export const generateCVHTML = (cv: any): string => {
  // Delegate to the template system
  return generateTemplateHTML(cv, cv.template);
};

/**
 * Async version that wraps generateCVHTML for consistency with the API
 */
export const renderCVToHTMLAsync = async (cv: ICV): Promise<string> => {
  return Promise.resolve(generateCVHTML(cv));
};
