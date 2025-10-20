import { saveAs } from "file-saver";

export class PDFExportService {

    static async exportToPDF(elementId: string, filename: string = "CV.pdf"): Promise<void> {
        try {
            const element = document.getElementById(elementId);
            if (!element) {
                throw new Error(`Element with ID "${elementId}" not found`);
            }

            const clonedElement = element.cloneNode(true) as HTMLElement;

            this.applyInlineStyles(clonedElement);

            this.transformHTMLForPDF(clonedElement);

            const images = clonedElement.querySelectorAll("img");
            for (const img of Array.from(images)) {
                if (img.src && !img.src.startsWith("data:")) {
                    try {
                        const response = await fetch(img.src);
                        const blob = await response.blob();
                        const base64 = await this.blobToBase64(blob);
                        img.src = base64;
                    } catch (e) {
                        console.warn("Could not convert image to base64:", img.src, e);
                    }
                }
            }

            const wrapper = document.createElement("div");
            wrapper.appendChild(clonedElement);

            const styles = this.getComputedStyles();

            const htmlContent = `
        <!DOCTYPE html>
        <html lang="vi">
          <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${filename}</title>
            <style>
              /* Reset và base styles */
              * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
                -webkit-print-color-adjust: exact !important;
                print-color-adjust: exact !important;
                color-adjust: exact !important;
              }
              
              body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
                -webkit-font-smoothing: antialiased;
                -moz-osx-font-smoothing: grayscale;
                background: white;
                padding: 0;
                margin: 0;
              }
              
              /* Container */
              .cv-container {
                max-width: 210mm;
                margin: 0 auto;
                background: white;
                padding: 2rem;
                min-height: 297mm;
              }
              
              /* Header with gray background - CRITICAL */
              .cv-header {
                margin: -2rem -2rem 2rem -2rem;
                background-color: #f3f4f6 !important;
                padding: 1.5rem 2rem;
                border-bottom: 2px solid #e5e7eb !important;
                -webkit-print-color-adjust: exact !important;
                print-color-adjust: exact !important;
              }
              
              .cv-header-content {
                display: flex;
                align-items: flex-start;
                gap: 1.5rem;
              }
              
              /* Avatar */
              .cv-avatar {
                width: 6rem;
                height: 6rem;
                border-radius: 50%;
                border: 4px solid white;
                box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
                object-fit: cover;
                flex-shrink: 0;
              }
              
              .cv-avatar-fallback {
                width: 6rem;
                height: 6rem;
                border-radius: 50%;
                border: 4px solid white;
                box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
                background-color: #1e3a8a;
                color: white;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 1.5rem;
                font-weight: 700;
                flex-shrink: 0;
              }
              
              /* Header info */
              .cv-header-info {
                flex: 1;
              }
              
              .cv-name {
                font-size: 2.25rem;
                font-weight: 700;
                color: #1e3a8a;
                margin-bottom: 0.5rem;
                line-height: 2.5rem;
              }
              
              .cv-title {
                font-size: 1.125rem;
                font-weight: 500;
                color: #4b5563;
                margin-bottom: 1rem;
                line-height: 1.75rem;
              }
              
              .cv-contact-grid {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 0.5rem;
                font-size: 0.875rem;
                color: #374151;
              }
              
              .cv-contact-item {
                display: flex;
                align-items: center;
                gap: 0.5rem;
              }
              
              .cv-contact-label {
                font-weight: 600;
              }
              
              /* Sections */
              .cv-section {
                margin-top: 1.5rem;
              }
              
              .cv-section:first-child {
                margin-top: 0;
              }
              
              .cv-section-title {
                font-size: 1.25rem;
                font-weight: 700;
                color: #1e3a8a;
                padding-bottom: 0.25rem;
                margin-bottom: 0.75rem;
                border-bottom: 2px solid #1e3a8a !important;
                -webkit-print-color-adjust: exact !important;
                print-color-adjust: exact !important;
              }
              
              .cv-section-content {
                font-size: 0.875rem;
                line-height: 1.625;
                color: #374151;
                text-align: justify;
              }
              
              /* Education & Experience items */
              .cv-item {
                margin-top: 1rem;
              }
              
              .cv-item:first-child {
                margin-top: 0;
              }
              
              .cv-item-date {
                font-size: 0.75rem;
                color: #6b7280;
                font-weight: 500;
                margin-bottom: 0.25rem;
              }
              
              .cv-item-title {
                font-size: 1rem;
                font-weight: 700;
                color: #111827;
                margin-bottom: 0.25rem;
              }
              
              .cv-item-subtitle {
                font-size: 0.875rem;
                font-weight: 600;
                color: #374151;
                margin-bottom: 0.5rem;
              }
              
              .cv-item-description {
                font-size: 0.875rem;
                line-height: 1.625;
                color: #374151;
                text-align: justify;
              }
              
              /* Skills list */
              .cv-skills-list {
                list-style-type: disc;
                margin-left: 1.25rem;
                font-size: 0.875rem;
                color: #374151;
              }
              
              .cv-skills-list li {
                margin-top: 0.375rem;
              }
              
              /* Icons (using Unicode) */
              .icon-phone::before {
                content: "📞 ";
              }
              
              .icon-mail::before {
                content: "✉️ ";
              }
            </style>
          </head>
          <body>
            ${wrapper.innerHTML}
          </body>
        </html>
      `;

            const response = await fetch("/api/export-cv", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    html: htmlContent,
                    filename: filename,
                }),
            });

            if (!response.ok) {
                const error = await response.json().catch(() => ({ error: response.statusText }));
                console.error("API Error Details:", error);
                throw new Error(error.error || error.message || `Failed to export PDF (${response.status})`);
            }

            const blob = await response.blob();
            saveAs(blob, filename);

            return Promise.resolve();
        } catch (error) {
            console.error("PDF export error:", error);
            throw error;
        }
    }

    private static getComputedStyles(): string {
        let styles = "";

        const styleSheets = Array.from(document.styleSheets);

        styleSheets.forEach((styleSheet) => {
            try {
                const rules = styleSheet.cssRules || styleSheet.rules;
                if (rules) {
                    Array.from(rules).forEach((rule) => {
                        styles += rule.cssText + "\n";
                    });
                }
            } catch (e) {
                console.warn("Cannot access stylesheet:", styleSheet.href);
            }
        });

        const inlineStyles = Array.from(document.querySelectorAll("style"));
        inlineStyles.forEach((style) => {
            styles += style.textContent + "\n";
        });

        return styles;
    }

    static async exportCustomHTML(html: string, filename: string = "document.pdf"): Promise<void> {
        try {
            const response = await fetch("/api/export-cv", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    html: html,
                    filename: filename,
                }),
            });

            if (!response.ok) {
                const error = await response.json().catch(() => ({ error: response.statusText }));
                console.error("API Error Details (Custom HTML):", error);
                throw new Error(error.error || error.message || `Failed to export PDF (${response.status})`);
            }

            const blob = await response.blob();
            saveAs(blob, filename);
        } catch (error) {
            console.error("PDF export error (Custom HTML):", error);
            throw error;
        }
    }

    private static blobToBase64(blob: Blob): Promise<string> {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onloadend = () => resolve(reader.result as string);
            reader.onerror = reject;
            reader.readAsDataURL(blob);
        });
    }

    private static applyInlineStyles(element: HTMLElement): void {
        const allElements = [element, ...Array.from(element.querySelectorAll("*"))];

        allElements.forEach((el) => {
            if (!(el instanceof HTMLElement)) return;

            const computedStyle = window.getComputedStyle(el);

            // Critical styles to preserve
            const criticalStyles = [
                "background-color",
                "color",
                "border",
                "border-color",
                "border-width",
                "border-style",
                "border-bottom",
                "border-top",
                "border-left",
                "border-right",
                "font-size",
                "font-weight",
                "font-family",
                "line-height",
                "text-align",
                "margin",
                "margin-top",
                "margin-bottom",
                "margin-left",
                "margin-right",
                "padding",
                "padding-top",
                "padding-bottom",
                "padding-left",
                "padding-right",
                "display",
                "flex",
                "flex-direction",
                "align-items",
                "justify-content",
                "gap",
                "width",
                "height",
                "min-height",
                "max-width",
                "box-shadow",
                "border-radius",
            ];

            // Apply computed styles as inline styles
            let styleString = "";
            criticalStyles.forEach((prop) => {
                const value = computedStyle.getPropertyValue(prop);
                if (value && value !== "none" && value !== "normal") {
                    styleString += `${prop}: ${value}; `;
                }
            });

            if (styleString) {
                el.setAttribute("style", styleString + (el.getAttribute("style") || ""));
            }
        });
    }

    /**
     * Transform HTML structure to use PDF-optimized CSS classes
     */
    private static transformHTMLForPDF(element: HTMLElement): void {
        // Find and transform the main container
        const container = element.querySelector('[id="cv-preview-content"]') as HTMLElement;
        if (container) {
            container.className = "cv-container";
        }

        // Transform header section
        const header = element.querySelector('.bg-gray-100') as HTMLElement;
        if (header) {
            header.className = "cv-header";

            const headerContent = header.querySelector('.flex.items-start.gap-6') as HTMLElement;
            if (headerContent) {
                headerContent.className = "cv-header-content";
            }
        }

        // Transform avatar
        const avatarImg = element.querySelector('[class*="Avatar"] img') as HTMLElement;
        if (avatarImg) {
            avatarImg.className = "cv-avatar";
        }

        const avatarFallback = element.querySelector('[class*="AvatarFallback"]') as HTMLElement;
        if (avatarFallback) {
            avatarFallback.className = "cv-avatar-fallback";
        }

        // Transform name and title
        const name = element.querySelector('h1.text-4xl') as HTMLElement;
        if (name) {
            name.className = "cv-name";
        }

        const title = element.querySelector('p.text-lg') as HTMLElement;
        if (title) {
            title.className = "cv-title";
        }

        // Transform contact grid
        const contactGrid = element.querySelector('.grid.grid-cols-1') as HTMLElement;
        if (contactGrid) {
            contactGrid.className = "cv-contact-grid";

            const contactItems = contactGrid.querySelectorAll('.flex.items-center.gap-2');
            contactItems.forEach((item) => {
                (item as HTMLElement).className = "cv-contact-item";

                const label = item.querySelector('.font-semibold') as HTMLElement;
                if (label) {
                    label.className = "cv-contact-label";
                }
            });
        }

        // Transform sections
        const sections = element.querySelectorAll('.space-y-6 > div');
        sections.forEach((section) => {
            (section as HTMLElement).className = "cv-section";

            // Transform section title
            const sectionTitle = section.querySelector('h2.border-b-2') as HTMLElement;
            if (sectionTitle) {
                sectionTitle.className = "cv-section-title";
            }

            // Transform section content
            const content = section.querySelector('p.text-sm') as HTMLElement;
            if (content) {
                content.className = "cv-section-content";
            }

            // Transform items (education/experience)
            const items = section.querySelectorAll('.space-y-4 > div');
            items.forEach((item) => {
                (item as HTMLElement).className = "cv-item";

                const date = item.querySelector('.text-xs') as HTMLElement;
                if (date) {
                    date.className = "cv-item-date";
                }

                const itemTitle = item.querySelector('h3.font-bold') as HTMLElement;
                if (itemTitle) {
                    itemTitle.className = "cv-item-title";
                }

                const subtitle = item.querySelector('p.font-semibold') as HTMLElement;
                if (subtitle) {
                    subtitle.className = "cv-item-subtitle";
                }

                const description = item.querySelector('p.leading-relaxed') as HTMLElement;
                if (description) {
                    description.className = "cv-item-description";
                }
            });

            // Transform skills list
            const skillsList = section.querySelector('ul.list-disc') as HTMLElement;
            if (skillsList) {
                skillsList.className = "cv-skills-list";
            }
        });
    }
}
