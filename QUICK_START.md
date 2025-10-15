# Quick Start Guide - AI CV Builder Features

## 🚀 Quick Setup (5 minutes)

### 1. Get OpenRouter API Key

1. Visit https://openrouter.ai
2. Sign up or log in
3. Go to https://openrouter.ai/keys
4. Create a new API key
5. Copy the key (starts with `sk-or-v1-...`)

### 2. Backend Setup

```bash
# Navigate to cv-service
cd sever/cv-service

# Create .env file
cp .env.example .env

# Edit .env and add your API key
OPENROUTER_API_KEY=sk-or-v1-your-actual-key-here

# Install dependencies
mvn clean install

# Run the service
mvn spring-boot:run
```

### 3. Frontend Setup

```bash
# Navigate to client
cd client

# Install dependencies (if not already done)
npm install

# Run development server
npm run dev
```

### 4. Test the Features

Open http://localhost:3000 and test:

- ✅ Import a CV file (PDF/DOCX/TXT)
- ✅ Click "Analyze CV" button
- ✅ Paste a job description
- ✅ View AI suggestions

## 📝 Usage Examples

### Example 1: Import CV

```typescript
// In your React component
import { useCVStore } from "@/stores/cvStore";

const { importFile } = useCVStore();

const handleFileUpload = async (file: File) => {
  const response = await importFile(userId, file);
  if (response.data?.cv) {
    console.log("CV imported successfully!");
  }
};

// In JSX
<input
  type="file"
  accept=".pdf,.docx,.txt"
  onChange={(e) => handleFileUpload(e.target.files[0])}
/>;
```

### Example 2: Quick CV Analysis

```typescript
import { useCVStore } from "@/stores/cvStore";

const { analyzeCV, aiSuggestions } = useCVStore();

const handleAnalyze = async () => {
  await analyzeCV(cvId);
  // Suggestions will be available in aiSuggestions state
  console.log("Got", aiSuggestions.length, "suggestions");
};
```

### Example 3: Job Match Analysis

```tsx
import { JobDescriptionImport } from "@/components/cv-builder/JobDescriptionImport";

<JobDescriptionImport
  cvId={currentCV.id}
  onAnalysisComplete={(suggestions, matchScore) => {
    alert(`Match Score: ${matchScore}%`);
    console.log("Suggestions:", suggestions);
  }}
/>;
```

### Example 4: Display Suggestions

```tsx
import { AISuggestionsList } from "@/components/cv-builder/AISuggestionsList";

<AISuggestionsList
  onApplySuggestion={(suggestion) => {
    // Handle applying the suggestion
    console.log("Applying:", suggestion.suggestion);
  }}
/>;
```

### Example 5: All-in-One Component

```tsx
import { AIFeaturesTab } from "@/components/cv-builder/AIFeaturesTab";

// In your CV builder page
<AIFeaturesTab cvId={currentCV.id} />;
```

## 🧪 Testing with cURL

### Test Import

```bash
curl -X POST http://localhost:8084/cvs/users/YOUR_USER_ID/import \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@path/to/resume.pdf"
```

### Test Analyze

```bash
curl -X POST http://localhost:8084/cvs/analyze/YOUR_CV_ID \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Test Job Description Analysis

```bash
curl -X POST http://localhost:8084/cvs/analyze-with-jd/YOUR_CV_ID \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "jobDescription": "We are seeking a Senior Java Developer with 5+ years experience in Spring Boot, microservices, and cloud technologies..."
  }'
```

### Test Improve Section

```bash
curl -X POST http://localhost:8084/cvs/improve/YOUR_CV_ID \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "section": "summary",
    "content": "I am a Java developer with experience in Spring Boot"
  }'
```

## 💡 Pro Tips

### 1. Best Practices for Job Description

- Include full job posting text
- Keep requirements and responsibilities
- More detail = better analysis

### 2. Optimizing AI Responses

- Use specific section names: "summary", "experience", "education", "skills"
- Provide complete context in content
- Review suggestions before applying

### 3. File Upload Tips

- PDF files: Best quality, maintains structure
- DOCX files: Good, but may lose some formatting
- TXT files: Fastest, but no formatting
- Keep files under 5MB for faster processing

### 4. API Rate Limits

- Free tier: Limited requests per minute
- Consider caching results
- Show loading states to users

## 🐛 Troubleshooting

### Issue: "OpenRouter API Error"

**Solution**:

- Check your API key in .env file
- Verify key is valid on https://openrouter.ai/keys
- Check API rate limits

### Issue: "File parsing failed"

**Solution**:

- Verify file format (PDF, DOCX, TXT only)
- Check file size (< 10MB)
- Try converting file to TXT first

### Issue: "No suggestions returned"

**Solution**:

- CV might be already well-optimized
- Try analyzing with a job description
- Check backend logs for AI response format

### Issue: "Slow AI responses"

**Solution**:

- Free models can be slow (5-30 seconds)
- Consider upgrading to paid model
- Implement loading states in UI

## 📚 Next Steps

1. **Customize AI Prompts**: Edit prompts in `CVService.java` for better results
2. **Add More Models**: Change `OPENROUTER_API_MODEL` to try different AI models
3. **Enhance UI**: Customize components in `client/components/cv-builder/`
4. **Add Features**:
   - Save analysis history
   - Compare before/after
   - Export improved CV
   - Multiple language support

## 🎯 Key Files to Know

### Backend

- `CVService.java` - Main logic
- `OpenRouterConfig.java` - AI API calls
- `FileParserService.java` - File parsing
- `CVController.java` - API endpoints

### Frontend

- `cvStore.ts` - State management
- `AIFeaturesTab.tsx` - Main UI component
- `JobDescriptionImport.tsx` - JD upload
- `AISuggestionsList.tsx` - Show suggestions

## 📞 Support

If you encounter issues:

1. Check backend logs: `sever/cv-service/logs/`
2. Check browser console for frontend errors
3. Verify all environment variables are set
4. Test API endpoints with cURL
5. Review IMPLEMENTATION_SUMMARY.md for detailed info

## ✨ Features Summary

- ✅ Import CV from PDF, DOCX, TXT files
- ✅ AI-powered CV analysis
- ✅ Section-by-section improvements
- ✅ Job description matching with score
- ✅ Actionable suggestions
- ✅ Apply/dismiss suggestions
- ✅ Professional UI components
- ✅ Real-time feedback

Happy building! 🚀
