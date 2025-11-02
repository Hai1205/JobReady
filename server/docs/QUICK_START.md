# 🚀 Quick Start Guide - AI Features

## ⚡ Bắt Đầu Nhanh

### Bước 1: Đảm bảo Backend đang chạy

```bash
# Di chuyển đến thư mục server
cd server

# Start các services (hoặc dùng Docker)
# Đảm bảo cv-service đang chạy trên port 8083
```

### Bước 2: Cấu hình OpenRouter API Key

```bash
# Trong cv-service/application.properties hoặc environment variables
OPENROUTER_API_KEY=your_api_key_here
```

### Bước 3: Start Frontend

```bash
# Di chuyển đến thư mục client
cd client

# Install dependencies (nếu chưa)
npm install

# Start dev server
npm run dev
```

### Bước 4: Truy cập ứng dụng

```
Open browser: http://localhost:3000
```

---

## 🎯 Sử Dụng AI Features

### 1️⃣ Tạo CV Mới

```
1. Navigate to /cv-builder
2. Điền thông tin các bước 1-4
3. Chuyển sang bước 5: "AI Analysis"
```

### 2️⃣ Quick Analyze

```
1. Click nút "Quick Analyze" (có icon ✨)
2. Đợi 5-10 giây để AI phân tích
3. Xem kết quả trong tab "Gợi Ý AI"
```

### 3️⃣ Apply Suggestions

```
1. Chọn tab "Gợi Ý AI"
2. Review các suggestions
3. Click "Apply" trên suggestion muốn áp dụng
4. Review improved content
5. Click "Apply to CV" để lưu
```

### 4️⃣ Job Match Analysis

```
1. Chọn tab "Job Match Analysis"
2. Paste job description HOẶC upload file
3. Chọn ngôn ngữ (Vietnamese/English)
4. Click "Phân Tích Độ Khớp"
5. Xem match score và missing keywords
```

---

## 📋 Checklist Trước Khi Analyze

- [ ] Personal Info đã điền đầy đủ
- [ ] Có ít nhất 1 Experience
- [ ] Có ít nhất 1 Education
- [ ] Skills list không rỗng
- [ ] CV title đã đặt

---

## 🐛 Common Issues & Solutions

### Issue 1: "CV not found" error

**Giải pháp:**

```typescript
// Đảm bảo currentCV không null
if (!currentCV) {
  toast.error("Please fill in CV information first");
  return;
}
```

### Issue 2: Analyze trả về error

**Giải pháp:**

- Kiểm tra OpenRouter API key
- Xem logs trong backend console
- Đảm bảo data không rỗng

### Issue 3: Apply suggestion không hoạt động

**Giải pháp:**

- Kiểm tra section name (case-insensitive)
- Verify improved content format
- Check console logs

### Issue 4: Match score không hiển thị

**Giải pháp:**

- Đảm bảo job description không rỗng
- Kiểm tra response format từ backend
- Verify parsedJobDescription trong response

---

## 🔧 Debug Mode

### Enable Debug Logs

```typescript
// Trong AIFeaturesTab.tsx
console.log("Response:", response);
console.log("Suggestions:", responseData?.suggestions);
console.log("Analyze text:", responseData?.analyze);
```

### Check Network Requests

```
1. Open DevTools (F12)
2. Go to Network tab
3. Click "Quick Analyze"
4. Check POST /cvs/analyze request/response
```

### Verify Store State

```typescript
// Trong component
const { currentCV, aiSuggestions } = useCVStore();
console.log("Current CV:", currentCV);
console.log("AI Suggestions:", aiSuggestions);
```

---

## 📊 Expected Response Format

### Analyze Response

```json
{
  "statusCode": 200,
  "message": "CV analyzed successfully",
  "data": {
    "analyze": "{...}", // JSON string hoặc plain text
    "suggestions": [
      {
        "id": "uuid",
        "type": "improvement",
        "section": "Summary",
        "message": "Add more impact",
        "suggestion": "Include metrics",
        "applied": false
      }
    ]
  }
}
```

### Improve Response

```json
{
  "statusCode": 200,
  "message": "CV section improved successfully",
  "data": {
    "improvedSection": "Improved content here..."
  }
}
```

### Analyze with JD Response

```json
{
  "statusCode": 200,
  "message": "Analysis complete",
  "data": {
    "analyze": "...",
    "matchScore": 85,
    "parsedJobDescription": {
      "jobTitle": "Senior Developer",
      "company": "ABC Corp",
      "requiredSkills": ["React", "Node.js"],
      ...
    },
    "missingKeywords": ["Docker", "Kubernetes"]
  }
}
```

---

## 🎨 UI Indicators

### Loading States

```tsx
// Analyzing
{
  isAnalyzing && (
    <>
      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
      Analyzing...
    </>
  );
}

// Improving
{
  isImproving && (
    <>
      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
      Applying...
    </>
  );
}
```

### Success Indicators

```tsx
// Applied suggestion
{
  suggestion.applied && <Check className="h-4 w-4 text-green-500" />;
}

// Badge
<Badge variant="outline" className="text-green-600">
  <Check className="h-3 w-3 mr-1" />
  Applied
</Badge>;
```

---

## 🧪 Test với Sample Data

### Sample CV Data

```typescript
const sampleCV = {
  title: "Software Engineer CV",
  personalInfo: {
    fullname: "Nguyễn Văn A",
    email: "nva@example.com",
    phone: "0123456789",
    location: "Hà Nội, Việt Nam",
    summary:
      "Experienced software developer with 5 years of experience in web development.",
  },
  experiences: [
    {
      company: "Tech Company",
      position: "Senior Developer",
      startDate: "2020-01",
      endDate: "2024-01",
      description: "Developed web applications using React and Node.js",
    },
  ],
  educations: [
    {
      school: "VNU University",
      degree: "Bachelor",
      field: "Computer Science",
      startDate: "2016-09",
      endDate: "2020-06",
    },
  ],
  skills: ["JavaScript", "React", "Node.js", "MongoDB", "Git"],
};
```

### Sample Job Description

```
Position: Senior Full-Stack Developer
Company: ABC Technology

Requirements:
- 5+ years of experience in web development
- Strong knowledge of React, Node.js, and MongoDB
- Experience with Docker and Kubernetes
- AWS certification preferred
- Agile/Scrum methodology

Responsibilities:
- Lead development team of 5-7 developers
- Design and implement scalable solutions
- Mentor junior developers
- Collaborate with product team

Benefits:
- Competitive salary
- Health insurance
- Annual bonus
- Remote work options
```

---

## 📚 API Endpoints Reference

### Quick Reference

```
POST /cvs/analyze              - Phân tích CV
POST /cvs/improve              - Cải thiện section
POST /cvs/analyze-with-jd      - Phân tích với JD
```

### Headers Required

```
Content-Type: multipart/form-data
Authorization: Bearer <token>
```

### Rate Limits

- 10 requests per minute per user
- 100 requests per hour per user

---

## 💡 Best Practices

### 1. Error Handling

```typescript
try {
  const response = await analyzeCV(...);
  if (response.data?.suggestions) {
    handleSetAISuggestions(response.data.suggestions);
  }
} catch (error) {
  console.error("Analysis error:", error);
  toast.error("Failed to analyze CV");
}
```

### 2. Loading States

```typescript
const [isLoading, setIsLoading] = useState(false);

const handleAnalyze = async () => {
  setIsLoading(true);
  try {
    await analyzeCV(...);
  } finally {
    setIsLoading(false);
  }
};
```

### 3. User Feedback

```typescript
// Success
toast.success("Analysis complete!");

// Error
toast.error("Failed to analyze CV");

// Info
toast.info("Analyzing your CV...");
```

---

## 🎓 Learning Resources

### Internal Docs

- `/client/components/cv-builder/AI-powered/README.md` - Detailed documentation
- `/AI_INTEGRATION_SUMMARY.md` - Integration summary
- `/AI_DEMO_FLOW.md` - Demo flow visualization

### External Resources

- OpenRouter API: https://openrouter.ai/docs
- React Toastify: https://fkhadra.github.io/react-toastify
- Zustand Store: https://github.com/pmndrs/zustand

---

## 🚀 Next Steps

1. ✅ Test với sample data
2. ✅ Verify tất cả features hoạt động
3. ✅ Deploy to staging environment
4. ✅ User acceptance testing
5. ✅ Production deployment

---

## 📞 Support

Nếu gặp vấn đề:

1. Check console logs (F12 → Console)
2. Check network requests (F12 → Network)
3. Verify backend is running
4. Check API key configuration
5. Review error messages

---

## ✨ Enjoy your AI-powered CV Builder! 🎉
