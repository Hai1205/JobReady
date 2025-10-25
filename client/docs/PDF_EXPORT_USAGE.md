# PDF Export Usage Guide

The `handleGeneratePDF` function in `cvStore` now supports multiple export modes, making it flexible for use throughout the application.

## Function Signature

```typescript
handleGeneratePDF: (cv: ICV, htmlContent?: string) => Promise<void>;
```

## Three Export Modes

### Mode 1: Automatic (Recommended)

Just pass the CV object - the function will automatically choose the best method:

```typescript
const { handleGeneratePDF } = useCVStore();

// In your component
const handleExport = async (cv: ICV) => {
  await handleGeneratePDF(cv);
};
```

**How it works:**

- If preview DOM element (`#cv-preview-content`) exists → uses DOM-based export (preserves exact styling)
- If preview DOM doesn't exist → generates HTML from CV data automatically

**Use in:**

- ✅ CV Builder Preview Step (uses DOM)
- ✅ Admin CV Dashboard (generates HTML)
- ✅ My CVs page (generates HTML)
- ✅ Any other location

### Mode 2: Custom HTML (Advanced)

Pass pre-rendered HTML directly if you have a custom template:

```typescript
import { renderCVToHTMLAsync } from "@/components/cv-builder/CVRenderer";

const customHTML = await renderCVToHTMLAsync(cv);
// Or use your own HTML string
const customHTML = `<div>Your custom CV HTML...</div>`;

await handleGeneratePDF(cv, customHTML);
```

**Use when:**

- You need a completely custom CV template
- You want to pre-process the HTML before export
- You're implementing A/B testing with different templates

### Mode 3: DOM-based (Automatic when preview exists)

This happens automatically when the preview element is in the DOM:

```typescript
// In cv-builder preview step
// The #cv-preview-content element exists in DOM
await handleGeneratePDF(currentCV);
// → Will use PDFExportService.exportToPDF("cv-preview-content", filename)
```

**Benefits:**

- Preserves exact styling from preview
- Includes any dynamic content or user customizations
- Faster as it doesn't need to regenerate HTML

## Examples

### Example 1: Admin CV Dashboard

```typescript
// In admin/cv-dashboard/page.tsx
<CVTable
  CVs={filteredCVs}
  isLoading={isLoading}
  onDownload={(cv) => {
    // Automatically generates HTML since preview DOM doesn't exist
    handleGeneratePDF(cv);
  }}
/>
```

### Example 2: CV Builder Preview

```typescript
// In components/cv-builder/steps/PreviewStep.tsx
<Button onClick={() => handleGeneratePDF(currentCV)}>
  <Download className="mr-2 h-4 w-4" />
  Tải xuống PDF
</Button>
// → Uses DOM-based export since #cv-preview-content exists
```

### Example 3: Custom Template

```typescript
import { generateCVHTML } from "@/components/cv-builder/CVRenderer";

// Generate HTML with modifications
const html = generateCVHTML(cv);
const customizedHTML = html.replace(/blue/g, "green"); // Example: change colors

await handleGeneratePDF(cv, customizedHTML);
```

## CV Renderer Utility

The `CVRenderer.tsx` component provides utilities for generating CV HTML:

```typescript
import {
  CVRenderer, // React component
  generateCVHTML, // Sync HTML generation
  renderCVToHTMLAsync, // Async HTML generation
} from "@/components/cv-builder/CVRenderer";

// Synchronous (returns HTML string directly)
const html = generateCVHTML(cv);

// Asynchronous (returns Promise<string>)
const html = await renderCVToHTMLAsync(cv);
```

## Error Handling

The function handles all edge cases gracefully:

- ❌ Server-side execution → Shows error toast
- ❌ PDF generation fails → Shows detailed error message
- ✅ Successful export → Shows success toast

## User Feedback

The function provides clear feedback to users:

- 🔵 "Đang tạo PDF..." - When generating from data
- ✅ "Tải xuống CV thành công!" - On success
- ❌ "Lỗi tạo PDF: [error details]" - On failure

## Technical Notes

### Performance

- DOM-based export: ~1-2 seconds (includes image loading, style inlining)
- HTML generation: ~0.5-1 second (direct HTML generation, no DOM manipulation)

### Styling

The `generateCVHTML` function uses inline styles to ensure consistent rendering in the PDF. Tailwind classes are converted to inline styles by the PDF export service.

### Image Handling

- DOM-based: Images are fetched and converted to base64 automatically
- HTML generation: Uses `avatarUrl` directly (must be accessible URL)

### Browser Compatibility

- ✅ Chrome, Edge, Firefox, Safari (latest versions)
- ✅ Works in both development and production builds
- ❌ Node.js/SSR context (gracefully shows error)

## Troubleshooting

### "Element with ID 'cv-preview-content' not found"

This error no longer occurs! The function now automatically falls back to HTML generation.

### Images not showing in PDF

Ensure avatar URLs are publicly accessible. For local files, they're converted to base64 in DOM-based export.

### Styling looks different

- DOM-based export: Should match preview exactly
- HTML generation: Uses standardized template (may differ slightly from preview)

### PDF generation is slow

If using avatar images:

- Ensure images are optimized (recommended < 500KB)
- Use CDN URLs when possible (faster than converting File objects)
- Consider compressing images before upload

## Future Enhancements

Potential improvements:

- [ ] Add progress indicator for long exports
- [ ] Support multiple CV templates/themes
- [ ] Client-side PDF generation (remove server dependency)
- [ ] Batch PDF export (multiple CVs at once)
- [ ] PDF preview before download
