# AI-Powered CV Features

## Tổng quan

Các tính năng AI được tích hợp vào CV Builder giúp phân tích, cải thiện và tối ưu hóa CV của người dùng.

## Các Component

### 1. AIFeaturesTab

Component chính cho các tính năng AI, bao gồm:

- **Quick Analyze**: Phân tích nhanh CV và đưa ra gợi ý cải thiện
- **Job Match Analysis**: So sánh CV với mô tả công việc
- **AI Suggestions**: Hiển thị và áp dụng các gợi ý từ AI

#### Cách sử dụng:

```tsx
import { AIFeaturesTab } from "./AI-powered/AIFeaturesTab";

// Sử dụng trong wizard hoặc component khác
<AIFeaturesTab />;
```

### 2. AISuggestionsSidebar

Sidebar hiển thị các gợi ý AI ở dạng compact, thích hợp để hiển thị bên cạnh form.

#### Cách sử dụng:

```tsx
import { AISuggestionsSidebar } from "./AI-powered/AISuggestionsSidebar";

<AISuggestionsSidebar />;
```

### 3. AISuggestionsList

Component hiển thị danh sách các gợi ý AI với khả năng áp dụng trực tiếp.

#### Cách sử dụng:

```tsx
import { AISuggestionsList } from "./AI-powered/AISuggestionsList";

<AISuggestionsList
  onApplySuggestion={(suggestion) => console.log(suggestion)}
  isApplying={false}
/>;
```

## API Endpoints

### Analyze CV

```
POST /cvs/analyze
Content-Type: multipart/form-data

Body:
- data: JSON string {
    title: string,
    personalInfo: IPersonalInfo,
    experiences: IExperience[],
    educations: IEducation[],
    skills: string[]
  }

Response:
{
  statusCode: 200,
  message: "CV analyzed successfully",
  data: {
    analyze: string,  // Raw AI analysis text
    suggestions: IAISuggestion[]
  }
}
```

### Improve CV Section

```
POST /cvs/improve
Content-Type: multipart/form-data

Body:
- data: JSON string {
    section: string,  // "summary", "experience", "education", "skills"
    content: string
  }

Response:
{
  statusCode: 200,
  message: "CV section improved successfully",
  data: {
    improvedSection: string
  }
}
```

### Analyze CV with Job Description

```
POST /cvs/analyze-with-jd
Content-Type: multipart/form-data

Body:
- data: JSON string {
    jobDescription: string,
    language: "vi" | "en",
    title: string,
    personalInfo: IPersonalInfo,
    experiences: IExperience[],
    educations: IEducation[],
    skills: string[]
  }
- jdFile: File (optional, PDF/DOCX)

Response:
{
  statusCode: 200,
  message: "Analysis complete",
  data: {
    analyze: string,
    matchScore: number,
    parsedJobDescription: IJobDescriptionResult,
    missingKeywords: string[]
  }
}
```

## Tích hợp vào CV Builder Wizard

Tính năng AI đã được tích hợp vào CV Builder Wizard như một bước (step) riêng:

```tsx
const steps = [
  { id: 0, title: "Personal Info", component: PersonalInfoStep },
  { id: 1, title: "Experience", component: ExperienceStep },
  { id: 2, title: "Education", component: EducationStep },
  { id: 3, title: "Skills", component: SkillsStep },
  { id: 4, title: "AI Analysis", component: AIFeaturesTab }, // ← Bước mới
  { id: 5, title: "Preview & Export", component: PreviewStep },
];
```

## Workflow

1. **Người dùng điền thông tin CV** (Personal Info, Experience, Education, Skills)
2. **Chuyển sang bước AI Analysis**
3. **Click "Quick Analyze"** để phân tích CV
4. **Xem gợi ý** trong tab "Gợi Ý AI"
5. **Click "Apply"** trên từng gợi ý để cải thiện
6. **Xem nội dung đã cải thiện** và quyết định áp dụng hoặc bỏ qua
7. **Chuyển sang Preview** để xem kết quả cuối cùng

## Store Management

### useCVStore

Store quản lý state của CV và các tính năng AI:

```typescript
// Lấy state và actions
const {
  currentCV,
  aiSuggestions,
  analyzeCV,
  improveCV,
  analyzeCVWithJD,
  handleSetAISuggestions,
  handleApplySuggestion,
} = useCVStore();

// Phân tích CV
await analyzeCV(title, personalInfo, experiences, educations, skills);

// Cải thiện section
await improveCV(section, content);

// Phân tích với job description
await analyzeCVWithJD(
  jobDescription,
  jdFile,
  language,
  title,
  personalInfo,
  experiences,
  educations,
  skills
);
```

## Lưu ý

1. **Backend cần OpenRouter API key** để sử dụng tính năng AI
2. **Dữ liệu CV phải đầy đủ** (ít nhất có Personal Info, 1 Experience, 1 Education)
3. **Suggestions có thể là JSON hoặc text** - component xử lý cả hai
4. **Improved content cần review** trước khi áp dụng vào CV

## Troubleshooting

### Lỗi "CV not found"

- Đảm bảo currentCV không null
- Kiểm tra dữ liệu CV đã được điền đầy đủ

### Lỗi parse JSON

- Kiểm tra format của improved content
- Thêm try-catch khi parse JSON

### Không có suggestions

- Kiểm tra response từ backend
- Đảm bảo AI model trả về đúng format JSON

## Future Enhancements

- [ ] Thêm tính năng so sánh nhiều job descriptions
- [ ] AI chatbot để hỏi đáp về CV
- [ ] Tự động apply các suggestions có confidence cao
- [ ] Export báo cáo phân tích CV dạng PDF
- [ ] Hỗ trợ nhiều ngôn ngữ hơn (tiếng Anh, tiếng Trung...)
