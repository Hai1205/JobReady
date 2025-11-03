/**
 * CV Template Registry
 * Central registry for all available CV templates
 */

import { generateModernTemplate } from './modernTemplate';
import { generateClassicTemplate } from './classicTemplate';

export interface TemplateGenerator {
  (cv: ICV, primaryColor: string): string;
}

export interface CVTemplateInfo {
  id: string;
  name: string;
  description: string;
  generator: TemplateGenerator;
}

export const CV_TEMPLATES: Record<string, CVTemplateInfo> = {
  modern: {
    id: 'modern',
    name: 'Modern',
    description: 'Thiết kế hiện đại với sidebar, phù hợp cho ngành công nghệ',
    generator: generateModernTemplate,
  },
  classic: {
    id: 'classic',
    name: 'Classic',
    description: 'Thiết kế truyền thống, chuyên nghiệp, single-column',
    generator: generateClassicTemplate,
  },
  minimal: {
    id: 'minimal',
    name: 'Minimal',
    description: 'Thiết kế tối giản (sử dụng Modern template với style tối giản)',
    generator: generateModernTemplate, // Có thể tạo riêng sau
  },
};

/**
 * Generate CV HTML from template
 * @param cv - CV data object
 * @param templateId - Template identifier (default: 'modern')
 * @returns HTML string
 */
export function generateCVHTML(cv: ICV, templateId?: string): string {
  const primaryColor = cv.color || '#3498db';
  const template = templateId || cv.template || 'modern';

  const templateInfo = CV_TEMPLATES[template];

  if (!templateInfo) {
    console.warn(`Template "${template}" not found. Using "modern" as fallback.`);
    return CV_TEMPLATES.modern.generator(cv, primaryColor);
  }

  return templateInfo.generator(cv, primaryColor);
}

/**
 * Get list of available templates
 */
export function getAvailableTemplates(): CVTemplateInfo[] {
  return Object.values(CV_TEMPLATES);
}
