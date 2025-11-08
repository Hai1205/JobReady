package com.example.aiservice.services.apis;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.aiservice.configs.OpenRouterConfig;
import com.example.aiservice.dtos.*;
import com.example.aiservice.exceptions.OurException;
import com.example.aiservice.services.PromptBuilderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    private final OpenRouterConfig openRouterConfig;
    private final PromptBuilderService promptBuilderService;
    private final ObjectMapper objectMapper;

    public AIService(
            OpenRouterConfig openRouterConfig,
            PromptBuilderService promptBuilderService) {
        this.openRouterConfig = openRouterConfig;
        this.promptBuilderService = promptBuilderService;
        this.objectMapper = new ObjectMapper();
    }

    public AIResponseDto analyzeCV(CVDto cv) {
        try {
            String systemPrompt = promptBuilderService.buildCVAnalysisPrompt();
            String cvContent = handleFormatCVForAnalysis(cv);
            String prompt = "Analyze this CV:\n\n" + cvContent;
            String analyzeResult = openRouterConfig.callModelWithSystemPrompt(systemPrompt,
                    prompt);
            List<AISuggestionDto> suggestions = handleParseSuggestionsFromAIResponse(analyzeResult);

            return AIResponseDto.builder()
                    .analyzeResult(analyzeResult)
                    .suggestions(suggestions)
                    .build();
        } catch (OurException e) {
            logger.error("Error in analyzeCV: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in analyzeCV: {}", e.getMessage(), e);
            throw new OurException("Failed to analyze CV", 500);
        }
    }

    public AIResponseDto improveCV(String section, String content) {
        try {
            String systemPrompt = promptBuilderService.buildCVImprovementPrompt(
                    section,
                    "General position",
                    List.of());
            String prompt = String.format(
                    "Improve the following %s section of a CV:\n\n%s\n\nProvide only the improved version without explanations.",
                    section, content);
            String improved = openRouterConfig.callModelWithSystemPrompt(systemPrompt, prompt);

            return AIResponseDto.builder()
                    .improved(improved)
                    .build();
        } catch (OurException e) {
            logger.error("Error in improveCV: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in improveCV: {}", e.getMessage(), e);
            throw new OurException("Failed to improve CV", 500);
        }
    }

    public AIResponseDto analyzeCVWithJobDescription(CVDto cv, String language, String jdText) {
        try {
            String cvContent = handleFormatCVForAnalysis(cv);

            String systemPrompt = promptBuilderService.buildJobMatchPrompt(language != null ? language : "vi");
            String userPrompt = handleBuildUserPrompt(jdText, cvContent);

            String analyzeResult = openRouterConfig.callModelWithSystemPrompt(systemPrompt, userPrompt);
            String jsonContent = handleExtractJsonFromResponse(analyzeResult);
            JobDescriptionResult jdResult = handleTryParseJobDescription(jsonContent);
            JsonNode root = objectMapper.readTree(jsonContent);

            Double matchScore = null;

            // Extract match score
            if (root.has("overallMatchScore")) {
                matchScore = root.get("overallMatchScore").asDouble();
            } else if (root.has("matchScore")) {
                matchScore = root.get("matchScore").asDouble();
            }

            // Extract missing keywords
            List<String> missingKeywords = new ArrayList<>();
            if (root.has("missingKeywords") && root.get("missingKeywords").isArray()) {
                for (JsonNode n : root.get("missingKeywords")) {
                    missingKeywords.add(n.asText());
                }
            }

            List<AISuggestionDto> suggestions = handleParseSuggestionsFromAIResponse(analyzeResult);

            return AIResponseDto.builder()
                    .jdResult(jdResult)
                    .analyzeResult(analyzeResult)
                    .matchScore(matchScore)
                    .missingKeywords(missingKeywords)
                    .suggestions(suggestions)
                    .build();
        } catch (OurException e) {
            logger.error("Error in analyzeCVWithJobDescription: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in analyzeCVWithJobDescription: {}", e.getMessage(), e);
            throw new OurException("Failed to analyze CV with Job Description", 500);
        }
    }

    private String handleBuildUserPrompt(String jdText, String cvContent) {
        return String.format(
                "Job Description:\n%s\n\nCV Content:\n%s\n\nReturn the parsed JD JSON and the analyze JSON.",
                jdText, cvContent);
    }

    private JobDescriptionResult handleTryParseJobDescription(String jsonContent) {
        try {
            return objectMapper.readValue(jsonContent, JobDescriptionResult.class);
        } catch (Exception e) {
            try {
                JsonNode root = objectMapper.readTree(jsonContent);
                if (root.has("jobTitle") || root.has("responsibilities")) {
                    return objectMapper.treeToValue(root, JobDescriptionResult.class);
                } else if (root.has("parsedJobDescription")) {
                    return objectMapper.treeToValue(root.get("parsedJobDescription"), JobDescriptionResult.class);
                }
            } catch (Exception ignored) {
            }
            return null;
        }
    }

    private String handleFormatCVForAnalysis(CVDto cvDto) {
        StringBuilder sb = new StringBuilder();

        // Personal Info
        if (cvDto.getPersonalInfo() != null) {
            PersonalInfoDto pi = cvDto.getPersonalInfo();
            sb.append("Name: ").append(pi.getFullname()).append("\n");
            sb.append("Email: ").append(pi.getEmail()).append("\n");
            sb.append("Phone: ").append(pi.getPhone()).append("\n");
            sb.append("Location: ").append(pi.getLocation()).append("\n");
            if (pi.getSummary() != null && !pi.getSummary().isEmpty()) {
                sb.append("\nProfessional Summary:\n").append(pi.getSummary()).append("\n");
            }
        }

        // Experience
        if (cvDto.getExperiences() != null && !cvDto.getExperiences().isEmpty()) {
            sb.append("\nWork Experience:\n");
            for (ExperienceDto exp : cvDto.getExperiences()) {
                sb.append("- ").append(exp.getPosition()).append(" at ").append(exp.getCompany())
                        .append(" (").append(exp.getStartDate()).append(" - ").append(exp.getEndDate()).append(")\n");
                sb.append("  ").append(exp.getDescription()).append("\n");
            }
        }

        // Education
        if (cvDto.getEducations() != null && !cvDto.getEducations().isEmpty()) {
            sb.append("\nEducation:\n");
            for (EducationDto edu : cvDto.getEducations()) {
                sb.append("- ").append(edu.getDegree()).append(" in ").append(edu.getField())
                        .append(" from ").append(edu.getSchool())
                        .append(" (").append(edu.getStartDate()).append(" - ").append(edu.getEndDate()).append(")\n");
            }
        }

        // Skills
        if (cvDto.getSkills() != null && !cvDto.getSkills().isEmpty()) {
            sb.append("\nSkills:\n");
            sb.append(String.join(", ", cvDto.getSkills())).append("\n");
        }

        return sb.toString();
    }

    private List<AISuggestionDto> handleParseSuggestionsFromAIResponse(String aiResponse) {
        List<AISuggestionDto> suggestions = new ArrayList<>();

        try {
            String jsonContent = handleExtractJsonFromResponse(aiResponse);
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            JsonNode suggestionsNode = rootNode.get("suggestions");
            if (suggestionsNode != null && suggestionsNode.isArray()) {
                for (JsonNode node : suggestionsNode) {
                    AISuggestionDto suggestion = AISuggestionDto.builder()
                            .id(node.has("id") ? node.get("id").asText() : UUID.randomUUID().toString())
                            .type(node.has("type") ? node.get("type").asText() : "improvement")
                            .section(node.has("section") ? node.get("section").asText() : "general")
                            .message(node.has("message") ? node.get("message").asText() : "")
                            .suggestion(node.has("suggestion") ? node.get("suggestion").asText() : "")
                            .applied(false)
                            .build();
                    suggestions.add(suggestion);
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing suggestions: " + e.getMessage());
        }

        return suggestions;
    }

    private String handleExtractJsonFromResponse(String response) {
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
}