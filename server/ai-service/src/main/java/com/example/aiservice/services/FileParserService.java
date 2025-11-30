package com.example.aiservice.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.aiservice.dtos.CVDto;
import com.example.aiservice.dtos.PersonalInfoDto;
import com.example.aiservice.dtos.ExperienceDto;
import com.example.aiservice.dtos.EducationDto;
import com.example.aiservice.exceptions.OurException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * FileParserService - Using Gemini ChatClient for CV parsing
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileParserService {

    private final ChatClient chatClient; // Use Gemini instead of OpenRouter
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Validate PDF file for Import CV
     */
    public void validatePDFFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new OurException("File is required", 400);
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".pdf")) {
            throw new OurException("Only PDF files are supported for CV import", 400);
        }

        // Check file size (max 10MB)
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new OurException("File size must not exceed 10MB", 400);
        }
    }

    /**
     * Validate document file for JD analysis
     */
    public void validateDocumentFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return; // Optional file
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new OurException("Invalid filename", 400);
        }

        String lowerFilename = filename.toLowerCase();
        boolean isValidFormat = lowerFilename.endsWith(".pdf") ||
                lowerFilename.endsWith(".docx") ||
                lowerFilename.endsWith(".doc");

        if (!isValidFormat) {
            throw new OurException(
                    "Only PDF and Word (.docx, .doc) files are supported", 400);
        }

        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new OurException("File size must not exceed 10MB", 400);
        }
    }

    /**
     * Extract text from various file formats
     */
    public String extractTextFromFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            return null;
        }

        String lower = filename.toLowerCase();

        if (lower.endsWith(".pdf")) {
            return extractTextFromPdf(file.getInputStream());
        } else if (lower.endsWith(".docx")) {
            return extractTextFromDocx(file.getInputStream());
        } else {
            return readAsText(file.getInputStream());
        }
    }

    /**
     * Parse CV using Gemini ChatClient
     */
    public CVDto parseCVWithAI(String cvText) {
        if (cvText == null || cvText.trim().isEmpty()) {
            throw new OurException("CV text is required", 400);
        }

        try {
            log.info("Parsing CV with Gemini ({} chars)", cvText.length());

            String systemPrompt = buildCVParsingPrompt();

            // Call Gemini via ChatClient
            String aiResponse = chatClient.prompt()
                    .system(systemPrompt)
                    .user(cvText)
                    .call()
                    .content();

            log.debug("Gemini response received ({} chars)", aiResponse.length());

            // Extract JSON from response
            String jsonContent = extractJsonFromResponse(aiResponse);

            // Parse JSON to CVDto
            CVDto cvDto = parseJsonToCVDto(jsonContent);

            // Validate
            validateCVDto(cvDto);

            log.info("CV parsed successfully: {}", cvDto.getTitle());

            return cvDto;

        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse CV: {}", e.getMessage(), e);
            throw new OurException("Failed to parse CV with AI: " + e.getMessage(), 500);
        }
    }

    /**
     * Build system prompt for CV parsing
     */
    private String buildCVParsingPrompt() {
        return """
                You are an expert CV parser. Parse the following CV text and extract information into JSON.

                Required JSON structure:
                {
                  "title": "string",
                  "personalInfo": {
                    "fullname": "string",
                    "email": "string",
                    "phone": "string",
                    "location": "string",
                    "summary": "string"
                  },
                  "experiences": [
                    {
                      "company": "string",
                      "position": "string",
                      "startDate": "YYYY-MM",
                      "endDate": "YYYY-MM or Present",
                      "description": "string"
                    }
                  ],
                  "educations": [
                    {
                      "school": "string",
                      "degree": "string",
                      "field": "string",
                      "startDate": "YYYY-MM",
                      "endDate": "YYYY-MM"
                    }
                  ],
                  "skills": ["string"]
                }

                Common CV sections (English/Vietnamese):
                - Personal: Name, Email, Phone, Location, Address, Contact
                - Summary: Summary, Profile, About, Objective, GIỚI THIỆU, TÓM TẮT
                - Experience: Experience, Work Experience, Employment, KINH NGHIỆM
                - Education: Education, Academic Background, HỌC VẤN
                - Skills: Skills, Technical Skills, KỸ NĂNG

                Instructions:
                - Extract full name from the CV
                - Title can be job title or "CV" if not specified
                - Parse each job with company, position, dates, description
                - For education: degree, field (major), school, dates
                - Skills should be individual items
                - Dates in format "YYYY-MM" or "Month YYYY"
                - Use empty array if section is missing

                Return ONLY the JSON object, no markdown, no extra text.
                """;
    }

    /**
     * Extract JSON from AI response
     */
    private String extractJsonFromResponse(String response) {
        String trimmed = response.trim();

        // Remove markdown code blocks
        if (trimmed.startsWith("```json") || trimmed.startsWith("```")) {
            int start = trimmed.indexOf('{');
            int end = trimmed.lastIndexOf('}');
            if (start != -1 && end != -1 && end > start) {
                return trimmed.substring(start, end + 1);
            }
        }

        // Find JSON object
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return trimmed.substring(start, end + 1);
        }

        return trimmed;
    }

    /**
     * Parse JSON to CVDto
     */
    private CVDto parseJsonToCVDto(String jsonContent) throws Exception {
        JsonNode root = objectMapper.readTree(jsonContent);

        String title = root.has("title") && !root.get("title").isNull()
                ? root.get("title").asText()
                : "Imported CV";

        // Parse personal info
        PersonalInfoDto personalInfo = null;
        if (root.has("personalInfo") && root.get("personalInfo").isObject()) {
            JsonNode piNode = root.get("personalInfo");
            personalInfo = new PersonalInfoDto(
                    null,
                    piNode.has("fullname") && !piNode.get("fullname").isNull()
                            ? piNode.get("fullname").asText()
                            : "",
                    piNode.has("email") && !piNode.get("email").isNull()
                            ? piNode.get("email").asText()
                            : "",
                    piNode.has("phone") && !piNode.get("phone").isNull()
                            ? piNode.get("phone").asText()
                            : "",
                    piNode.has("location") && !piNode.get("location").isNull()
                            ? piNode.get("location").asText()
                            : "",
                    piNode.has("summary") && !piNode.get("summary").isNull()
                            ? piNode.get("summary").asText()
                            : "",
                    null,
                    null);
        }

        // Parse experiences
        List<ExperienceDto> experiences = new ArrayList<>();
        if (root.has("experiences") && root.get("experiences").isArray()) {
            for (JsonNode expNode : root.get("experiences")) {
                if (expNode.isObject()) {
                    ExperienceDto exp = new ExperienceDto(
                            null,
                            expNode.has("company") && !expNode.get("company").isNull()
                                    ? expNode.get("company").asText()
                                    : "",
                            expNode.has("position") && !expNode.get("position").isNull()
                                    ? expNode.get("position").asText()
                                    : "",
                            expNode.has("startDate") && !expNode.get("startDate").isNull()
                                    ? expNode.get("startDate").asText()
                                    : "",
                            expNode.has("endDate") && !expNode.get("endDate").isNull()
                                    ? expNode.get("endDate").asText()
                                    : "",
                            expNode.has("description") && !expNode.get("description").isNull()
                                    ? expNode.get("description").asText()
                                    : "");
                    experiences.add(exp);
                }
            }
        }

        // Parse educations
        List<EducationDto> educations = new ArrayList<>();
        if (root.has("educations") && root.get("educations").isArray()) {
            for (JsonNode eduNode : root.get("educations")) {
                if (eduNode.isObject()) {
                    EducationDto edu = new EducationDto(
                            null,
                            eduNode.has("school") && !eduNode.get("school").isNull()
                                    ? eduNode.get("school").asText()
                                    : "",
                            eduNode.has("degree") && !eduNode.get("degree").isNull()
                                    ? eduNode.get("degree").asText()
                                    : "",
                            eduNode.has("field") && !eduNode.get("field").isNull()
                                    ? eduNode.get("field").asText()
                                    : "",
                            eduNode.has("startDate") && !eduNode.get("startDate").isNull()
                                    ? eduNode.get("startDate").asText()
                                    : "",
                            eduNode.has("endDate") && !eduNode.get("endDate").isNull()
                                    ? eduNode.get("endDate").asText()
                                    : "");
                    educations.add(edu);
                }
            }
        }

        // Parse skills
        List<String> skills = new ArrayList<>();
        if (root.has("skills") && root.get("skills").isArray()) {
            for (JsonNode skillNode : root.get("skills")) {
                if (!skillNode.isNull()) {
                    skills.add(skillNode.asText());
                }
            }
        }

        return CVDto.builder()
                .title(title)
                .personalInfo(personalInfo)
                .experiences(experiences)
                .educations(educations)
                .skills(skills)
                .build();
    }

    /**
     * Validate parsed CV
     */
    private void validateCVDto(CVDto cvDto) {
        if (cvDto == null) {
            throw new OurException("Parsed CV is null", 400);
        }

        if (cvDto.getPersonalInfo() == null) {
            throw new OurException("Personal info is required", 400);
        }

        if (cvDto.getPersonalInfo().getFullname() == null ||
                cvDto.getPersonalInfo().getFullname().isEmpty()) {
            throw new OurException("Full name is required", 400);
        }

        if (cvDto.getExperiences() == null) {
            cvDto.setExperiences(new ArrayList<>());
        }

        if (cvDto.getEducations() == null) {
            cvDto.setEducations(new ArrayList<>());
        }

        if (cvDto.getSkills() == null) {
            cvDto.setSkills(new ArrayList<>());
        }
    }

    // ========================================
    // Text Extraction Methods
    // ========================================

    private String extractTextFromPdf(InputStream in) throws Exception {
        try (PDDocument document = Loader.loadPDF(in.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            return cleanText(text);
        }
    }

    private String extractTextFromDocx(InputStream in) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(in)) {
            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph p : doc.getParagraphs()) {
                sb.append(p.getText()).append("\n");
            }
            return cleanText(sb.toString());
        }
    }

    private String readAsText(InputStream in) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        return cleanText(sb.toString());
    }

    private String cleanText(String input) {
        if (input == null) {
            return null;
        }

        // Collapse multiple blank lines
        String cleaned = input.replaceAll("\r", "\n");
        cleaned = cleaned.replaceAll("\n{2,}", "\n\n");

        // Trim each line
        StringBuilder out = new StringBuilder();
        for (String line : cleaned.split("\n")) {
            String t = line.trim();
            if (!t.isEmpty()) {
                out.append(t).append('\n');
            }
        }
        return out.toString().trim();
    }
}