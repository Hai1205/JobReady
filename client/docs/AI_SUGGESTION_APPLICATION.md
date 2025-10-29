# 🤖 AI Suggestion Application System

## Tổng quan

Hệ thống này xử lý và áp dụng gợi ý AI vào CV một cách thông minh, bằng cách parse phần "After" từ suggestion text và cập nhật state tương ứng.

## 📋 Cấu trúc Suggestion Response

Từ API, mỗi suggestion có format:

```json
{
  "id": "b3a9f1e7-9909-4f52-9c47-c8cb9f6d435d",
  "type": "error",
  "section": "summary",
  "lineNumber": null,
  "message": "Summary chỉ có 1 câu, cần mở rộng hơn.",
  "suggestion": "Before: 'Old text'\nAfter: 'New improved text'",
  "applied": false
}
```

## 🔧 Cách hoạt động

### 1. Parse "After" Content

Function `parseAfterContent()` trích xuất nội dung sau "After:" từ suggestion string:

**Input:**

```
Before: 'Experienced software developer with 5+ years in full-stack development.'
After: 'Software Developer with over 5 years of experience in full-stack development, seeking opportunities to leverage expertise in React, Node.js, and cloud technologies.'
```

**Output:**

```
Software Developer with over 5 years of experience in full-stack development, seeking opportunities to leverage expertise in React, Node.js, and cloud technologies.
```

### 2. Apply to CV State

Dựa trên `section`, hệ thống sẽ cập nhật phần tương ứng:

#### Summary

```typescript
updatedCV.personalInfo.summary = afterContent;
```

#### Experience

- Tìm experience khớp dựa trên company name trong message
- Nếu không tìm thấy, so sánh với "Before" content
- Fallback: áp dụng vào experience đầu tiên

```typescript
updatedCV.experiences[matchingIndex].description = afterContent;
```

#### Skills

Parse và thêm skills mới (loại bỏ duplicate):

**Input:**

```
Technical Skills: JavaScript, React, Node.js. Soft Skills: Leadership, Mentoring, Problem-solving.
```

**Parsed:**

```typescript
[
  "JavaScript",
  "React",
  "Node.js",
  "Leadership",
  "Mentoring",
  "Problem-solving",
];
```

### 3. Update State

Function `handleApplySuggestion()` trong `cvStore`:

1. Tìm suggestion theo ID
2. Parse "After" content
3. Áp dụng vào `currentCVCreate` và/hoặc `currentCVUpdate`
4. Đánh dấu suggestion là `applied: true`
5. Hiển thị toast notification

## 🎯 Ví dụ thực tế

### Case 1: Update Summary

**Suggestion:**

```json
{
  "section": "summary",
  "message": "Summary chỉ có 1 câu, cần mở rộng hơn.",
  "suggestion": "Before: 'Short summary'\nAfter: 'Detailed professional summary with keywords and experience highlights'"
}
```

**Kết quả:**

```typescript
cv.personalInfo.summary =
  "Detailed professional summary with keywords and experience highlights";
```

### Case 2: Update Experience

**Suggestion:**

```json
{
  "section": "experience",
  "message": "Experience at TechCorp Inc. thiếu metrics định lượng.",
  "suggestion": "Before: 'Lead projects'\nAfter: 'Led 5+ projects resulting in 30% efficiency increase'"
}
```

**Kết quả:**

- Tìm experience có company name "TechCorp Inc."
- Update description với metrics cụ thể

### Case 3: Add Skills

**Suggestion:**

```json
{
  "section": "skills",
  "message": "Skills cần phân loại rõ ràng",
  "suggestion": "Technical Skills: React, Node.js. Soft Skills: Leadership"
}
```

**Kết quả:**

```typescript
cv.skills = [...cv.skills, "React", "Node.js", "Leadership"];
// Chỉ thêm những skills chưa tồn tại
```

## 🧪 Debug & Testing

### Trong Browser Console:

```javascript
// Test parsing
runSuggestionTests();

// Test với custom suggestion
testParseSuggestion("Before: 'old'\nAfter: 'new'");
```

### Check Logs:

Khi apply suggestion, console sẽ hiển thị:

```
🔍 Applying suggestion: {
  section: "summary",
  message: "...",
  originalSuggestion: "Before: ... After: ...",
  parsedAfterContent: "..."
}
✅ Applied to experience: TechCorp Inc.
```

## 📁 Files

- `lib/suggestionApplier.ts` - Core logic để parse và apply
- `stores/cvStore.ts` - State management và handleApplySuggestion
- `components/.../AISuggestionCard.tsx` - UI component
- `components/.../AISuggestionsList.tsx` - List wrapper
- `lib/debugSuggestions.ts` - Debug utilities

## ✅ Supported Sections

| Section             | Field Updated             | Note                             |
| ------------------- | ------------------------- | -------------------------------- |
| summary             | personalInfo.summary      | Direct replacement               |
| experience          | experiences[].description | Smart matching by company        |
| education           | educations[].field        | By lineNumber or first item      |
| skills              | skills[]                  | Parse and append unique skills   |
| title               | title                     | Direct replacement               |
| personalInfo fields | personalInfo.\*           | fullname, email, phone, location |

## 🚨 Error Handling

- Không tìm thấy suggestion: Toast error
- Section không được hỗ trợ: Console warning + return null
- Không parse được "After": Sử dụng original suggestion text
- Không tìm thấy matching experience: Apply vào item đầu tiên

## 💡 Tips

1. Luôn có logging trong console để debug
2. Check format của suggestion từ API
3. Test với nhiều loại suggestion khác nhau
4. Verify CV state sau khi apply

## 🔄 Flow Chart

```
User clicks "Áp dụng"
    ↓
handleApplySuggestion(id)
    ↓
Find suggestion by ID
    ↓
parseAfterContent(suggestion.suggestion)
    ↓
Switch by section type
    ↓
applySuggestionToCV(cv, suggestion)
    ↓
Update CV state (currentCVCreate/currentCVUpdate)
    ↓
Mark suggestion as applied
    ↓
Show success toast ✅
```

---

**Last updated:** October 29, 2025
