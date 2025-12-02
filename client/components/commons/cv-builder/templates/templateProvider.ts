/**
 * CV Template Registry
 * Central registry for all available CV templates
 */

import { generateModernTemplate } from './modernTemplate';
import { generateClassicTemplate } from './classicTemplate';
import { generateProfessionalTemplate } from './professionalTemplate';
import { generateSidebarBadgeTemplate } from './sidebarBadgeTemplate';
import { generateCornerAccentTemplate } from './cornerAccentTemplate';
import { generateBurgundySidebarTemplate } from './burgundySidebarTemplate';
import { generateSimpleTemplate } from './simpleTemplate';
import { generateTwoColumnTemplate } from './twoColumnTemplate';

export interface TemplateGenerator {
  (cv: ICV, primaryColor: string, fontFamily?: string): string;
}

export interface CVTemplate {
  id: string;
  name: string;
  description: string;
  generator: TemplateGenerator;
  hasAvatar: boolean;
  preview: string;
  isPremium: boolean;
}

export const templates: CVTemplate[] = [
  {
    id: 'modern',
    name: 'Modern',
    description: 'Thiết kế hiện đại với sidebar, phù hợp cho ngành công nghệ',
    generator: generateModernTemplate,
    hasAvatar: true,
    preview: "M",
    isPremium: false,
  },
  {
    id: 'burgundy-sidebar',
    name: 'Burgundy Sidebar',
    description: 'Sidebar màu burgundy đậm, phong cách sang trọng',
    generator: generateBurgundySidebarTemplate,
    hasAvatar: false,
    preview: "BS",
    isPremium: false,
  },
  {
    id: 'simple',
    name: 'Simple',
    description: 'Thiết kế đơn giản với điểm nhấn màu cam',
    generator: generateSimpleTemplate,
    hasAvatar: false,
    preview: "SP",
    isPremium: false,
  },
  {
    id: 'classic',
    name: 'Classic',
    description: 'Thiết kế truyền thống, chuyên nghiệp, single-column',
    generator: generateClassicTemplate,
    hasAvatar: true,
    preview: "C",
    isPremium: false,
  },
  {
    id: 'professional',
    name: 'Professional',
    description: 'Header với avatar, layout sạch sẽ và chuyên nghiệp',
    generator: generateProfessionalTemplate,
    hasAvatar: true,
    preview: "P",
    isPremium: false,
  },
  {
    id: 'sidebar-badge',
    name: 'Sidebar Badge',
    description: 'Sidebar với kỹ năng hiển thị dạng badge',
    generator: generateSidebarBadgeTemplate,
    hasAvatar: true,
    preview: "SB",
    isPremium: false,
  },
  {
    id: 'two-column',
    name: 'Two Column',
    description: 'Layout 2 cột với avatar bên phải',
    generator: generateTwoColumnTemplate,
    hasAvatar: true,
    preview: "TC",
    isPremium: false,
  },
  {
    id: 'corner-accent',
    name: 'Corner Accent',
    description: 'Thiết kế với góc trang trí tam giác độc đáo',
    generator: generateCornerAccentTemplate,
    hasAvatar: false,
    preview: "CA",
    isPremium: false,
  },
  {
    id: 'minimal',
    name: 'Minimal',
    description: 'Thiết kế tối giản (sử dụng Modern template với style tối giản)',
    generator: generateModernTemplate,
    hasAvatar: true,
    preview: "Mi",
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
  const fontFamily = cv.font || 'Inter, sans-serif';
  const template = templateId || cv.template || 'modern';

  const templateInfo = templates.find(t => t.id === template);

  if (!templateInfo) {
    console.warn(`Template "${template}" not found. Using "modern" as fallback.`);
    const modernTemplate = templates.find(t => t.id === 'modern');
    return modernTemplate ? modernTemplate.generator(cv, primaryColor, fontFamily) : generateModernTemplate(cv, primaryColor, fontFamily);
  }

  return templateInfo.generator(cv, primaryColor, fontFamily);
}