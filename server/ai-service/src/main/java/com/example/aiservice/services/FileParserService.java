package com.example.aiservice.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.aiservice.dtos.CVDto;
import com.example.aiservice.dtos.PersonalInfoDto;
import com.example.aiservice.dtos.ExperienceDto;
import com.example.aiservice.dtos.EducationDto;
import com.example.aiservice.exceptions.OurException;

import com.example.aiservice.configs.OpenRouterConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.pdfbox.Loader;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

@Service
public class FileParserService {

    @Autowired
    private OpenRouterConfig openRouterConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Validate file for Import CV - Only PDF is supported
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
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new OurException("File size must not exceed 10MB", 400);
        }
    }

    /**
     * Validate file for Analyze with JD - Both PDF and Word (.docx) are supported
     */
    public void validateDocumentFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new OurException("File is required", 400);
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
            throw new OurException("Only PDF and Word (.docx, .doc) files are supported for job description analysis",
                    400);
        }

        // Check file size (max 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new OurException("File size must not exceed 10MB", 400);
        }
    }

    public String extractTextFromFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty())
            return null;

        String filename = file.getOriginalFilename();
        if (filename == null)
            return null;

        String lower = filename.toLowerCase();

        if (lower.endsWith(".pdf")) {
            return extractTextFromPdf(file.getInputStream());
        } else if (lower.endsWith(".docx")) {
            return extractTextFromDocx(file.getInputStream());
        } else {
            return readAsText(file.getInputStream());
        }
    }

    public CVDto parseCVWithAI(String cvText) {
        if (cvText == null || cvText.trim().isEmpty()) {
            throw new OurException("CV text is required", 400);
        }

        try {
            // Build prompt for AI to parse CV
            String systemPrompt = buildCVParsingPrompt();
            String userPrompt = cvText;

            // Call AI model
            String aiResponse = openRouterConfig.callModelWithSystemPrompt(systemPrompt, userPrompt);

            // Extract JSON from response
            String jsonContent = extractJsonFromResponse(aiResponse);

            // Parse JSON to CVDto
            CVDto cvDto = parseJsonToCVDto(jsonContent);

            // Validate the parsed CV
            validateCVDto(cvDto);

            return cvDto;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            throw new OurException("Failed to parse CV with AI: " + e.getMessage(), 500);
        }
    }

    private String buildCVParsingPrompt() {
        return "You are an expert CV parser. Parse the following CV text and extract the information into a JSON object with the following structure:\n"
                +
                "{\n" +
                "  \"title\": \"string\",\n" +
                "  \"personalInfo\": {\n" +
                "    \"fullname\": \"string\",\n" +
                "    \"email\": \"string\",\n" +
                "    \"phone\": \"string\",\n" +
                "    \"location\": \"string\",\n" +
                "    \"summary\": \"string\"\n" +
                "  },\n" +
                "  \"experiences\": [\n" +
                "    {\n" +
                "      \"company\": \"string\",\n" +
                "      \"position\": \"string\",\n" +
                "      \"startDate\": \"string\",\n" +
                "      \"endDate\": \"string\",\n" +
                "      \"description\": \"string\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"educations\": [\n" +
                "    {\n" +
                "      \"school\": \"string\",\n" +
                "      \"degree\": \"string\",\n" +
                "      \"field\": \"string\",\n" +
                "      \"startDate\": \"string\",\n" +
                "      \"endDate\": \"string\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"skills\": [\"string\"]\n" +
                "}\n\n" +
                "Common section headers in CVs (English and Vietnamese):\n" +
                "- Personal Info: Name, Email, Phone, Location, Address, Contact\n" +
                "- Summary/Introduction: Summary, Profile, About, Objective, GIỚI THIỆU, TÓM TẮT\n" +
                "- Work Experience: Experience, Work Experience, Employment, Professional Experience, KINH NGHIỆM, KINH NGHIỆM LÀM VIỆC\n"
                +
                "- Education: Education, Academic Background, HỌC VẤN, HỌC VẤN VÀ BẰNG CẤP\n" +
                "- Skills: Skills, Technical Skills, Competencies, KỸ NĂNG, KỸ NĂNG CHUYÊN MÔN\n\n" +
                "Instructions:\n" +
                "- Extract the full name from the beginning or name field.\n" +
                "- Title can be the job title or 'CV' if not specified.\n" +
                "- For experiences, parse each job with company, position, dates, and description.\n" +
                "- For education, parse degree, field (major), school, dates.\n" +
                "- Skills should be an array of individual skills, split by spaces or commas.\n" +
                "- Dates should be in format like 'YYYY-MM' or 'Month YYYY'.\n" +
                "- If a section is missing, use empty array or null appropriately.\n\n" +
                "Return only the JSON object, no additional text or explanations.";
    }

    private String extractJsonFromResponse(String response) {
        // Try to extract JSON from markdown code blocks or plain text
        String trimmed = response.trim();

        // Check if response is wrapped in markdown code block
        if (trimmed.startsWith("```json") || trimmed.startsWith("```")) {
            int start = trimmed.indexOf('{');
            int end = trimmed.lastIndexOf('}');
            if (start != -1 && end != -1 && end > start) {
                return trimmed.substring(start, end + 1);
            }
        }

        // Try to find JSON object in the response
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return trimmed.substring(start, end + 1);
        }

        return trimmed;
    }

    private CVDto parseJsonToCVDto(String jsonContent) throws Exception {
        JsonNode root = objectMapper.readTree(jsonContent);

        // Parse title
        String title = root.has("title") && !root.get("title").isNull() ? root.get("title").asText() : "Imported CV";

        // Parse personalInfo
        PersonalInfoDto personalInfo = null;
        if (root.has("personalInfo") && root.get("personalInfo").isObject()) {
            JsonNode piNode = root.get("personalInfo");
            personalInfo = new PersonalInfoDto(
                    null, // id
                    piNode.has("fullname") && !piNode.get("fullname").isNull() ? piNode.get("fullname").asText() : "",
                    piNode.has("email") && !piNode.get("email").isNull() ? piNode.get("email").asText() : "",
                    piNode.has("phone") && !piNode.get("phone").isNull() ? piNode.get("phone").asText() : "",
                    piNode.has("location") && !piNode.get("location").isNull() ? piNode.get("location").asText() : "",
                    piNode.has("summary") && !piNode.get("summary").isNull() ? piNode.get("summary").asText() : "",
                    null, // avatarUrl
                    null // avatarPublicId
            );
        }

        // Parse experiences
        List<ExperienceDto> experiences = new ArrayList<>();
        if (root.has("experiences") && root.get("experiences").isArray()) {
            for (JsonNode expNode : root.get("experiences")) {
                if (expNode.isObject()) {
                    ExperienceDto exp = new ExperienceDto(
                            null, // id
                            expNode.has("company") && !expNode.get("company").isNull() ? expNode.get("company").asText()
                                    : "",
                            expNode.has("position") && !expNode.get("position").isNull()
                                    ? expNode.get("position").asText()
                                    : "",
                            expNode.has("startDate") && !expNode.get("startDate").isNull()
                                    ? expNode.get("startDate").asText()
                                    : "",
                            expNode.has("endDate") && !expNode.get("endDate").isNull() ? expNode.get("endDate").asText()
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
                            null, // id
                            eduNode.has("school") && !eduNode.get("school").isNull() ? eduNode.get("school").asText()
                                    : "",
                            eduNode.has("degree") && !eduNode.get("degree").isNull() ? eduNode.get("degree").asText()
                                    : "",
                            eduNode.has("field") && !eduNode.get("field").isNull() ? eduNode.get("field").asText() : "",
                            eduNode.has("startDate") && !eduNode.get("startDate").isNull()
                                    ? eduNode.get("startDate").asText()
                                    : "",
                            eduNode.has("endDate") && !eduNode.get("endDate").isNull() ? eduNode.get("endDate").asText()
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

    private void validateCVDto(CVDto cvDto) {
        if (cvDto == null) {
            throw new OurException("Parsed CV is null", 400);
        }

        if (cvDto.getPersonalInfo() == null) {
            throw new OurException("Personal info is required", 400);
        }

        if (cvDto.getPersonalInfo().getFullname() == null || cvDto.getPersonalInfo().getFullname().isEmpty()) {
            throw new OurException("Full name is required", 400);
        }

        if (cvDto.getExperiences() == null) {
            cvDto.setExperiences(new java.util.ArrayList<>());
        }

        if (cvDto.getEducations() == null) {
            cvDto.setEducations(new java.util.ArrayList<>());
        }

        if (cvDto.getSkills() == null) {
            cvDto.setSkills(new java.util.ArrayList<>());
        }
    }

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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        return cleanText(sb.toString());
    }

    // simple cleaning: remove excessive whitespace and repeated headers/footers
    // heuristics
    private String cleanText(String input) {
        if (input == null)
            return null;
        // collapse multiple blank lines
        String cleaned = input.replaceAll("\r", "\n");
        cleaned = cleaned.replaceAll("\n{2,}", "\n\n");
        // trim each line
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
