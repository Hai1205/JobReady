# CV Builder with AI Features - Implementation Guide

## Overview

This implementation adds comprehensive AI-powered features to the CV Builder application, including:

- **Import CV files** (PDF, DOCX, TXT)
- **AI CV Analysis** with detailed suggestions
- **AI CV Improvement** for specific sections
- **Job Description Matching** with AI-powered analysis

## Backend Changes (Java Spring Boot)

### 1. Dependencies Added (pom.xml)

```xml
<!-- Apache PDFBox for PDF parsing -->
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>2.0.30</version>
</dependency>

<!-- Apache POI for DOCX parsing -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```

### 2. New Files Created

- `FileParserService.java` - Handles file parsing (PDF, DOCX, TXT)
- `AISuggestionDto.java` - DTO for AI suggestions
- `AnalyzeCVWithJDRequest.java` - Request DTO for job description analysis

### 3. Updated Files

- `OpenRouterConfig.java` - Fixed configuration and added system prompt support
- `CVService.java` - Added AI analysis methods
- `CVController.java` - Added new endpoints
- `ResponseData.java` - Added new fields for AI responses

### 4. New API Endpoints

```
POST /cvs/analyze/{cvId} - Analyze CV with AI
POST /cvs/improve/{cvId} - Improve CV section with AI
POST /cvs/analyze-with-jd/{cvId} - Analyze CV against Job Description
POST /cvs/users/{userId}/import - Import CV from file
```

### 5. Environment Variables Required

Add to your `.env` file:

```properties
# OpenRouter API Configuration
OPENROUTER_API_URL=https://openrouter.ai/api/v1/chat/completions
OPENROUTER_API_KEY=your_api_key_here
OPENROUTER_API_MODEL=meta-llama/llama-3.2-3b-instruct:free
```

## Frontend Changes (Next.js/TypeScript)

### 1. Updated Store (cvStore.ts)

Added new methods:

- `analyzeCVWithJD()` - Analyze CV with job description
- `handleSetJobDescription()` - Store job description
- `handleApplySuggestion()` - Mark suggestion as applied
- `handleClearCVList()` - Clear CV list

### 2. New Components Created

- `JobDescriptionImport.tsx` - Component for uploading/pasting job descriptions
- `AISuggestionsList.tsx` - Component for displaying and managing AI suggestions

### 3. Updated Files

- `axiosInstance.ts` - Now supports JSON body in addition to FormData
- `cvStore.ts` - Added job description state and methods

## How to Use

### Backend Setup

1. Install dependencies:

```bash
cd sever/cv-service
mvn clean install
```

2. Set environment variables in `.env`:

```properties
OPENROUTER_API_KEY=sk-or-v1-xxxxx
```

3. Run the service:

```bash
mvn spring-boot:run
```

### Frontend Setup

1. Install dependencies:

```bash
cd client
npm install
```

2. Run the development server:

```bash
npm run dev
```

### Using the Features

#### 1. Import CV File

```typescript
const { importFile } = useCVStore();

const handleFileImport = async (file: File) => {
  const response = await importFile(userId, file);
  if (response.data?.cv) {
    console.log("CV imported:", response.data.cv);
  }
};
```

#### 2. Analyze CV

```typescript
const { analyzeCV, handleSetAISuggestions } = useCVStore();

const handleAnalyze = async (cvId: string) => {
  const response = await analyzeCV(cvId);
  if (response.data?.suggestions) {
    handleSetAISuggestions(response.data.suggestions);
  }
};
```

#### 3. Analyze with Job Description

```tsx
import { JobDescriptionImport } from "@/components/cv-builder/JobDescriptionImport";

<JobDescriptionImport
  cvId={currentCV.id}
  onAnalysisComplete={(suggestions, matchScore) => {
    console.log("Match Score:", matchScore);
    console.log("Suggestions:", suggestions);
  }}
/>;
```

#### 4. Display AI Suggestions

```tsx
import { AISuggestionsList } from "@/components/cv-builder/AISuggestionsList";

<AISuggestionsList
  onApplySuggestion={(suggestion) => {
    // Apply the suggestion to the CV
    console.log("Applying:", suggestion);
  }}
/>;
```

#### 5. Improve CV Section

```typescript
const { improveCV } = useCVStore();

const handleImprove = async (
  cvId: string,
  section: string,
  content: string
) => {
  const response = await improveCV(cvId, section, content);
  if (response.data?.improvedSection) {
    console.log("Improved:", response.data.improvedSection);
  }
};
```

## API Response Examples

### Analyze CV Response

```json
{
  "statusCode": 200,
  "message": "CV analyzed successfully",
  "data": {
    "cv": { ... },
    "analysis": "Overall analysis text...",
    "suggestions": [
      {
        "id": "uuid",
        "type": "improvement",
        "section": "experience",
        "message": "Add quantifiable achievements",
        "suggestion": "Increased sales by 30% YoY",
        "applied": false
      }
    ]
  }
}
```

### Analyze with JD Response

```json
{
  "statusCode": 200,
  "message": "CV analyzed with job description successfully",
  "data": {
    "cv": { ... },
    "analysis": "Match analysis...",
    "matchScore": 75,
    "suggestions": [ ... ]
  }
}
```

### Import File Response

```json
{
  "statusCode": 201,
  "message": "CV imported successfully",
  "data": {
    "cv": {
      "id": "uuid",
      "title": "Imported CV",
      "personalInfo": { ... },
      "experience": [ ... ],
      "education": [ ... ],
      "skills": [ ... ]
    },
    "extractedText": "Raw extracted text..."
  }
}
```

## Testing

### Test File Import

```bash
curl -X POST http://localhost:8084/cvs/users/{userId}/import \
  -H "Authorization: Bearer {token}" \
  -F "file=@resume.pdf"
```

### Test CV Analysis

```bash
curl -X POST http://localhost:8084/cvs/analyze/{cvId} \
  -H "Authorization: Bearer {token}"
```

### Test JD Analysis

```bash
curl -X POST http://localhost:8084/cvs/analyze-with-jd/{cvId} \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{"jobDescription": "We are looking for..."}'
```

## Notes

- The free OpenRouter model (llama-3.2-3b-instruct) is used by default
- File upload size limit is 10MB
- Supported file formats: PDF, DOCX, TXT
- AI responses are parsed from JSON format
- Suggestions include type, section, message, and improvement text

## Troubleshooting

### Common Issues

1. **OpenRouter API Key Error**: Make sure `OPENROUTER_API_KEY` is set in environment variables
2. **File Parsing Error**: Check file format is supported (PDF, DOCX, TXT)
3. **Large File Error**: Reduce file size below 10MB
4. **JSON Parsing Error**: AI response might not be in expected JSON format

### Debug Tips

- Check backend logs for OpenRouter API errors
- Verify environment variables are loaded correctly
- Test OpenRouter API key separately
- Check file MIME type before upload
