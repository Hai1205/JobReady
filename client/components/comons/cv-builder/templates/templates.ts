/**
 * CV Template Registry
 * Central registry for all available CV templates
 */

import { generateModernTemplate } from './modernTemplate';
import { generateClassicTemplate } from './classicTemplate';

export interface TemplateGenerator {
  (cv: ICV, primaryColor: string): string;
}

export interface CVTemplate {
  id: string;
  name: string;
  description: string;
  generator: TemplateGenerator;
  preview: string;
  isPremium: boolean;
}

export const templates: CVTemplate[] = [
  {
    id: 'modern',
    name: 'Modern',
    description: 'Thiết kế hiện đại với sidebar, phù hợp cho ngành công nghệ',
    generator: generateModernTemplate,
    preview: "M",
    isPremium: false,
  },
  {
    id: 'classic',
    name: 'Classic',
    description: 'Thiết kế truyền thống, chuyên nghiệp, single-column',
    generator: generateClassicTemplate,
    preview: "C",
    isPremium: false,
  },
  {
    id: 'minimal',
    name: 'Minimal',
    description: 'Thiết kế tối giản (sử dụng Modern template với style tối giản)',
    generator: generateModernTemplate,
    preview: "Mi",
    isPremium: true,
  },
  {
    id: 'creative',
    name: 'Creative',
    description: 'Thiết kế sáng tạo cho ngành nghệ thuật, thiết kế',
    generator: generateModernTemplate,
    preview: "Cr",
    isPremium: true,
  },
  {
    id: 'executive',
    name: 'Executive',
    description: 'Thiết kế cao cấp cho vị trí quản lý',
    generator: generateClassicTemplate,
    preview: "E",
    isPremium: true,
  },
  {
    id: 'compact',
    name: 'Compact',
    description: 'Thiết kế gọn nhẹ, tối ưu không gian',
    generator: generateModernTemplate,
    preview: "Co",
    isPremium: true,
  },
];

/**
 * Generate CV HTML from template
 * @param cv - CV data object
 * @param templateId - Template identifier (default: 'modern')
 * @returns HTML string
 */
export function generateCVHTML(cv: ICV, templateId?: string): string {
  const primaryColor = cv.color || '#3498db';
  const template = templateId || cv.template || 'modern';

  const templateInfo = templates.find(t => t.id === template);

  if (!templateInfo) {
    console.warn(`Template "${template}" not found. Using "modern" as fallback.`);
    const modernTemplate = templates.find(t => t.id === 'modern');
    return modernTemplate ? modernTemplate.generator(cv, primaryColor) : generateModernTemplate(cv, primaryColor);
  }

  return templateInfo.generator(cv, primaryColor);
}