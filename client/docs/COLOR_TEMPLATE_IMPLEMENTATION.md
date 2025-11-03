# 🎨 Hướng dẫn áp dụng Color và Template vào PreviewStep

## 📋 Tổng quan

Tài liệu này hướng dẫn cách áp dụng màu sắc (`color`) và template (`template`) đã chọn vào component **PreviewStep** để hiển thị CV với style tùy chỉnh.

---

## 🔧 Các thay đổi cần thiết

### 1️⃣ Cập nhật CVRenderer Component

File: `client/components/comons/cv-builder/CVRenderer.tsx`

#### a) Thêm props color và template

```tsx
interface CVRendererProps {
  cv: ICV;
  color?: string; // Màu chủ đạo
  template?: string; // Template layout
}

export function CVRenderer({ cv, color, template }: CVRendererProps) {
  const primaryColor = color || cv.color || "#3498db";
  const templateType = template || cv.template || "modern";

  // Render dựa trên template được chọn
  // ...
}
```

#### b) Áp dụng màu vào các phần tử

```tsx
// Sử dụng CSS custom properties
<div
  id="cv-preview-content"
  style={
    {
      "--cv-primary-color": primaryColor,
    } as React.CSSProperties
  }
  className="cv-container"
>
  {/* Headers sử dụng màu chủ đạo */}
  <h1 style={{ color: primaryColor }}>{cv.personalInfo.fullname}</h1>

  {/* Section dividers */}
  <div className="section-header" style={{ borderBottomColor: primaryColor }}>
    Experience
  </div>
</div>
```

#### c) Template rendering logic

```tsx
// Render dựa trên template type
const renderByTemplate = () => {
  switch (templateType) {
    case "modern":
      return <ModernTemplate cv={cv} color={primaryColor} />;
    case "classic":
      return <ClassicTemplate cv={cv} color={primaryColor} />;
    case "minimal":
      return <MinimalTemplate cv={cv} color={primaryColor} />;
    default:
      return <ModernTemplate cv={cv} color={primaryColor} />;
  }
};
```

---

### 2️⃣ Cập nhật PreviewStep Component

File: `client/components/comons/cv-builder/steps/PreviewStep.tsx`

#### Truyền color và template vào CVRenderer

```tsx
import { CVRenderer } from "../CVRenderer";
import { useCVStore } from "@/stores/cvStore";

export function PreviewStep() {
  const { currentCVCreate, currentCVUpdate } = useCVStore();
  const currentCV = currentCVUpdate || currentCVCreate;

  if (!currentCV) return <div>No CV data</div>;

  return (
    <div className="preview-container">
      <CVRenderer
        cv={currentCV}
        color={currentCV.color}
        template={currentCV.template}
      />
    </div>
  );
}
```

---

### 3️⃣ Tạo Template Components (Optional - Nâng cao)

Nếu muốn có các layout khác nhau cho mỗi template, tạo các component riêng:

#### ModernTemplate.tsx

```tsx
interface TemplateProps {
  cv: ICV;
  color: string;
}

export function ModernTemplate({ cv, color }: TemplateProps) {
  return (
    <div className="modern-template">
      <header style={{ backgroundColor: color }}>
        <h1>{cv.personalInfo.fullname}</h1>
      </header>
      {/* Layout hiện đại với sidebar */}
      <div className="grid grid-cols-3 gap-4">
        <aside className="col-span-1">{/* Sidebar */}</aside>
        <main className="col-span-2">{/* Main content */}</main>
      </div>
    </div>
  );
}
```

#### ClassicTemplate.tsx

```tsx
export function ClassicTemplate({ cv, color }: TemplateProps) {
  return (
    <div className="classic-template">
      {/* Layout truyền thống single column */}
      <header>
        <h1 style={{ color }}>{cv.personalInfo.fullname}</h1>
      </header>
      <section>{/* Experience */}</section>
      <section>{/* Education */}</section>
    </div>
  );
}
```

---

### 4️⃣ CSS Styling với Custom Properties

#### globals.css hoặc CV-specific CSS

```css
.cv-container {
  --cv-primary-color: #3498db; /* Default */
}

/* Headers sử dụng màu chủ đạo */
.cv-section-header {
  color: var(--cv-primary-color);
  border-bottom: 2px solid var(--cv-primary-color);
  padding-bottom: 0.5rem;
  margin-bottom: 1rem;
}

/* Icon colors */
.cv-icon {
  color: var(--cv-primary-color);
}

/* Links và highlights */
.cv-link {
  color: var(--cv-primary-color);
}

.cv-link:hover {
  opacity: 0.8;
}

/* Bullets và decorations */
.cv-bullet::before {
  background-color: var(--cv-primary-color);
}
```

---

### 5️⃣ PDF Export với Color và Template

#### Cập nhật pdfExportService.ts

Đảm bảo màu và template được giữ nguyên khi export PDF:

```typescript
export class PDFExportService {
  static async exportToPDF(elementId: string, filename: string) {
    const element = document.getElementById(elementId);
    if (!element) throw new Error("Element not found");

    // Capture với full style bao gồm custom properties
    const canvas = await html2canvas(element, {
      scale: 2,
      useCORS: true,
      allowTaint: true,
      backgroundColor: "#ffffff",
      // Đảm bảo CSS custom properties được áp dụng
      onclone: (clonedDoc) => {
        const clonedElement = clonedDoc.getElementById(elementId);
        if (clonedElement) {
          // Force recompute styles
          window
            .getComputedStyle(clonedElement)
            .getPropertyValue("--cv-primary-color");
        }
      },
    });

    // Generate PDF...
  }
}
```

---

### 6️⃣ Backend - Cập nhật CV Service (Java)

#### a) CVService.java - createCV method

```java
public CV createCV(UUID userId, String title, MultipartFile avatar,
                   IPersonalInfo personalInfo, List<IExperience> experiences,
                   List<IEducation> educations, List<String> skills,
                   String privacy, String color, String template) {
    CV cv = new CV();
    cv.setUserId(userId);
    cv.setTitle(title);
    cv.setPrivacy(CVPrivacy.valueOf(privacy));
    cv.setColor(color != null ? color : "#3498db");
    cv.setTemplate(template != null ? template : "modern");
    // ... rest of the logic
    return cvRepository.save(cv);
}
```

#### b) CVController.java - Update endpoints

```java
@PostMapping("/users/{userId}")
public ResponseEntity<Response> createCV(
    @PathVariable UUID userId,
    @RequestPart("data") String dataJson,
    @RequestPart(value = "avatar", required = false) MultipartFile avatar
) {
    CVRequest request = objectMapper.readValue(dataJson, CVRequest.class);

    CV cv = cvService.createCV(
        userId,
        request.getTitle(),
        avatar,
        request.getPersonalInfo(),
        request.getExperiences(),
        request.getEducations(),
        request.getSkills(),
        request.getPrivacy(),
        request.getColor(),      // Thêm color
        request.getTemplate()    // Thêm template
    );

    return ResponseEntity.ok(Response.success("CV created", cv));
}
```

#### c) CVRequest DTO

```java
@Data
public class CVRequest {
    private String title;
    private IPersonalInfo personalInfo;
    private List<IExperience> experiences;
    private List<IEducation> educations;
    private List<String> skills;
    private String privacy;
    private String color;      // New field
    private String template;   // New field
}
```

---

## ✅ Checklist triển khai

- [x] **Interface ICV** - Thêm `color` và `template` vào type definition
- [x] **CV Entity (Java)** - Thêm fields với giá trị mặc định
- [x] **ColorThemeSelector** - Component chọn màu
- [x] **TemplateSelector** - Component chọn template
- [x] **CVBuilderWizard** - Tích hợp UI selectors
- [x] **CVStore** - Cập nhật create/update methods
- [ ] **CVRenderer** - Áp dụng color và template vào rendering
- [ ] **PreviewStep** - Truyền props vào CVRenderer
- [ ] **Template Components** (Optional) - Modern, Classic, Minimal
- [ ] **CSS Styling** - Sử dụng CSS custom properties
- [ ] **PDF Export** - Đảm bảo style được giữ nguyên
- [ ] **Backend Service** - Cập nhật CVService methods
- [ ] **Backend Controller** - Cập nhật endpoints
- [ ] **Backend DTOs** - Thêm color và template vào request/response

---

## 🎨 Color Palette đã định nghĩa

```typescript
const colorThemes = [
  { name: "blue", value: "#3498db", label: "Professional Blue" },
  { name: "emerald", value: "#10b981", label: "Fresh Emerald" },
  { name: "violet", value: "#8b5cf6", label: "Creative Violet" },
  { name: "rose", value: "#f43f5e", label: "Bold Rose" },
  { name: "amber", value: "#f59e0b", label: "Warm Amber" },
  { name: "cyan", value: "#06b6d4", label: "Modern Cyan" },
  { name: "indigo", value: "#6366f1", label: "Deep Indigo" },
  { name: "slate", value: "#64748b", label: "Classic Slate" },
  { name: "teal", value: "#14b8a6", label: "Tech Teal" },
  { name: "fuchsia", value: "#d946ef", label: "Vibrant Fuchsia" },
];
```

---

## 📝 Templates đã định nghĩa

```typescript
const templates = [
  { id: "modern", name: "Modern", description: "Thiết kế hiện đại" },
  { id: "classic", name: "Classic", description: "Thiết kế truyền thống" },
  { id: "minimal", name: "Minimal", description: "Thiết kế tối giản" },
  { id: "creative", name: "Creative", isPremium: true },
  { id: "executive", name: "Executive", isPremium: true },
  { id: "compact", name: "Compact", description: "Thiết kế gọn nhẹ" },
];
```

---

## 🚀 Next Steps

1. **Đọc file PreviewStep.tsx hiện tại** để xem cấu trúc
2. **Đọc file CVRenderer.tsx** (nếu có) để hiểu cách render CV
3. **Áp dụng color vào các element** như headers, dividers, icons
4. **Tạo logic switch template** nếu muốn có nhiều layout khác nhau
5. **Test PDF export** để đảm bảo màu và layout được giữ nguyên
6. **Cập nhật backend** để lưu và trả về color/template

---

## 💡 Tips

- **CSS Variables**: Sử dụng `--cv-primary-color` để dễ dàng thay đổi màu toàn bộ CV
- **Template Fallback**: Luôn có giá trị mặc định nếu template không tồn tại
- **Validation**: Kiểm tra color format (hex) trước khi lưu vào database
- **Preview Real-time**: Color và template thay đổi ngay lập tức khi user chọn
- **PDF Export**: Test kỹ để đảm bảo màu không bị mất khi export

---

## 📚 Tài liệu tham khảo

- **Color Psychology in CVs**: Màu xanh - chuyên nghiệp, màu đỏ - năng động
- **Template Design**: Modern phù hợp IT, Classic phù hợp tài chính/luật
- **CSS Custom Properties**: [MDN Web Docs](https://developer.mozilla.org/en-US/docs/Web/CSS/--*)
