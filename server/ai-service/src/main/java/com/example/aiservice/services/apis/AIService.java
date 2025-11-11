package com.example.aiservice.services.apis;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.aiservice.configs.OpenRouterConfig;
import com.example.aiservice.dtos.*;
import com.example.aiservice.dtos.requests.*;
import com.example.aiservice.dtos.responses.AIResponseDto;
import com.example.aiservice.dtos.responses.Response;
import com.example.aiservice.exceptions.OurException;
import com.example.aiservice.services.*;
import com.fasterxml.jackson.databind.*;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Service
public class AIService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    private final OpenRouterConfig openRouterConfig;
    private final PromptBuilderService promptBuilderService;
    private final ObjectMapper objectMapper;
    private final JobDescriptionParserService jobDescriptionParserService;

    public AIService(
            OpenRouterConfig openRouterConfig,
            PromptBuilderService promptBuilderService,
            JobDescriptionParserService jobDescriptionParserService) {
        this.openRouterConfig = openRouterConfig;
        this.promptBuilderService = promptBuilderService;
        this.objectMapper = new ObjectMapper();
        this.jobDescriptionParserService = jobDescriptionParserService;
    }

    public Response improveCV(String dataJson) {
        Response response = new Response();

        try {
            ImproveCVRequest request = objectMapper.readValue(dataJson, ImproveCVRequest.class);
            String section = request.getSection();
            String content = request.getContent();

            AIResponseDto aiResponse = handleImproveCV(section, content);
            String improved = aiResponse.getImproved();

            response.setMessage("CV section improved successfully");
            response.setImprovedSection(improved);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response analyzeCV(String dataJson) {
        Response response = new Response();

        try {
            AnalyzeCVRequest request = objectMapper.readValue(dataJson, AnalyzeCVRequest.class);
            String title = request.getTitle();
            PersonalInfoDto personalInfo = request.getPersonalInfo();
            List<ExperienceDto> experiences = request.getExperiences();
            List<EducationDto> educations = request.getEducations();
            List<String> skills = request.getSkills();

            CVDto cvDto = CVDto.builder()
                    .title(title)
                    .personalInfo(personalInfo)
                    .experiences(experiences)
                    .educations(educations)
                    .skills(skills)
                    .build();

            logger.info("Analyzing CV with title={}", title);

            AIResponseDto aiResponse = handleAnalyzeCV(cvDto);
            AnalyzeResultDto analyzeResult = aiResponse.getAnalyzeResult();
            List<AISuggestionDto> suggestions = analyzeResult != null && analyzeResult.getSuggestions() != null
                    ? analyzeResult.getSuggestions()
                    : aiResponse.getSuggestions();

            response.setMessage("CV analyzed successfully");
            response.setAnalyze(analyzeResult);
            response.setSuggestions(suggestions);
            logger.debug("Analysis completed for CV title={} suggestionsCount={}", title,
                    suggestions == null ? 0 : suggestions.size());
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response analyzeCVWithJobDescription(String dataJson, MultipartFile jdFile) {
        Response response = new Response();

        try {
            AnalyzeCVWithJDRequest request = objectMapper.readValue(dataJson, AnalyzeCVWithJDRequest.class);
            String language = request.getLanguage();

            String jobDescription = request.getJobDescription();
            String jdText = handleExtractJobDescriptionText(jdFile, jobDescription);

            String title = request.getTitle();
            PersonalInfoDto personalInfo = request.getPersonalInfo();
            List<ExperienceDto> experiences = request.getExperiences();
            List<EducationDto> educations = request.getEducations();
            List<String> skills = request.getSkills();

            CVDto cvDto = CVDto.builder()
                    .title(title)
                    .personalInfo(personalInfo)
                    .experiences(experiences)
                    .educations(educations)
                    .skills(skills)
                    .build();

            AIResponseDto aiResponse = handleAnalyzeCVWithJobDescription(cvDto, language, jdText);
            JobDescriptionResult jdResult = aiResponse.getJdResult();
            AnalyzeResultDto analyzeResult = aiResponse.getAnalyzeResult();
            Double matchScore = aiResponse.getMatchScore();
            List<String> missingKeywords = aiResponse.getMissingKeywords();

            response.setMessage("CV analyzed with job description successfully");
            response.setParsedJobDescription(jdResult);
            response.setAnalyze(analyzeResult);
            response.setMatchScore(matchScore);
            response.setMissingKeywords(missingKeywords);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    private String handleExtractJobDescriptionText(MultipartFile jdFile, String jobDescription) {
        if (jdFile == null || jdFile.isEmpty()) {
            return jobDescription;
        }

        try {
            return jobDescriptionParserService.extractTextFromFile(jdFile);
        } catch (Exception ex) {
            System.err.println("Error extracting JD file: " + ex.getMessage());
            return jobDescription;
        }
    }

    public AIResponseDto handleAnalyzeCV(CVDto cv) {
        try {
            String systemPrompt = promptBuilderService.buildCVAnalysisPrompt();
            String cvContent = handleFormatCVForAnalysis(cv);
            String prompt = "Analyze this CV:\n\n" + cvContent;
            String aiResponseText = openRouterConfig.callModelWithSystemPrompt(systemPrompt,
                    prompt);

            // Parse the analyze result as object
            AnalyzeResultDto analyzeResult = handleParseAnalyzeResult(aiResponseText);
            List<AISuggestionDto> suggestions = analyzeResult.getSuggestions() != null
                    ? analyzeResult.getSuggestions()
                    : new ArrayList<>();

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

    public AIResponseDto handleImproveCV(String section, String content) {
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

    public AIResponseDto handleAnalyzeCVWithJobDescription(CVDto cv, String language, String jdText) {
        try {
            String cvContent = handleFormatCVForAnalysis(cv);

            String systemPrompt = promptBuilderService.buildJobMatchPrompt(language != null ? language : "vi");
            logger.info("jdText", jdText);
            String userPrompt = handleBuildUserPrompt(jdText, cvContent);

            String aiResponseText = openRouterConfig.callModelWithSystemPrompt(systemPrompt, userPrompt);
            logger.info("========== AI RAW RESPONSE ==========");
            logger.info("Response length: {}", aiResponseText.length());
            logger.info("First 500 chars: {}", aiResponseText.substring(0, Math.min(500, aiResponseText.length())));
            logger.info("=====================================");

            String jsonContent = handleExtractJsonFromResponse(aiResponseText);
            logger.info("========== EXTRACTED JSON ==========");
            logger.info("JSON length: {}", jsonContent.length());
            logger.info("JSON content: {}", jsonContent);
            logger.info("====================================");

            JsonNode root = objectMapper.readTree(jsonContent);

            // Check if data is nested in "analysis" field
            JsonNode analysisNode = root.has("analysis") ? root.get("analysis") : root;
            JsonNode jdNode = root.has("jobDescription") ? root.get("jobDescription") : root;

            // Parse job description
            JobDescriptionResult jdResult = null;
            if (jdNode != null && jdNode.isObject()) {
                try {
                    jdResult = objectMapper.treeToValue(jdNode, JobDescriptionResult.class);
                } catch (Exception e) {
                    logger.warn("Failed to parse job description: {}", e.getMessage());
                }
            }

            Double matchScore = null;

            // Extract match score from analysis node
            if (analysisNode.has("overallMatchScore")) {
                matchScore = analysisNode.get("overallMatchScore").asDouble();
            } else if (analysisNode.has("matchScore")) {
                matchScore = analysisNode.get("matchScore").asDouble();
            }

            // Extract missing keywords from analysis node
            List<String> missingKeywords = new ArrayList<>();
            if (analysisNode.has("missingKeywords") && analysisNode.get("missingKeywords").isArray()) {
                for (JsonNode n : analysisNode.get("missingKeywords")) {
                    missingKeywords.add(n.asText());
                }
            }

            // Parse the analyze result from analysis node
            AnalyzeResultDto analyzeResult = handleParseAnalyzeResultFromNode(analysisNode);
            List<AISuggestionDto> suggestions = analyzeResult.getSuggestions() != null
                    ? analyzeResult.getSuggestions()
                    : new ArrayList<>();

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

    private AnalyzeResultDto handleParseAnalyzeResult(String aiResponse) {
        try {
            String jsonContent = handleExtractJsonFromResponse(aiResponse);
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            // Check if data is nested in "analysis" field
            JsonNode analysisNode = rootNode.has("analysis") ? rootNode.get("analysis") : rootNode;

            return handleParseAnalyzeResultFromNode(analysisNode);
        } catch (Exception e) {
            logger.error("Error parsing analyze result: {}", e.getMessage(), e);
            // Return empty result on error
            return AnalyzeResultDto.builder()
                    .suggestions(new ArrayList<>())
                    .strengths(new ArrayList<>())
                    .weaknesses(new ArrayList<>())
                    .build();
        }
    }

    private AnalyzeResultDto handleParseAnalyzeResultFromNode(JsonNode rootNode) {
        try {
            // Parse overallScore
            Integer overallScore = null;
            if (rootNode.has("overallScore")) {
                overallScore = rootNode.get("overallScore").asInt();
            } else if (rootNode.has("overallMatchScore")) {
                overallScore = rootNode.get("overallMatchScore").asInt();
            }

            // Parse strengths
            List<String> strengths = new ArrayList<>();
            if (rootNode.has("strengths") && rootNode.get("strengths").isArray()) {
                for (JsonNode node : rootNode.get("strengths")) {
                    strengths.add(node.asText());
                }
            }

            // Parse weaknesses
            List<String> weaknesses = new ArrayList<>();
            if (rootNode.has("weaknesses") && rootNode.get("weaknesses").isArray()) {
                for (JsonNode node : rootNode.get("weaknesses")) {
                    weaknesses.add(node.asText());
                }
            }

            // Parse suggestions
            List<AISuggestionDto> suggestions = new ArrayList<>();
            if (rootNode.has("suggestions") && rootNode.get("suggestions").isArray()) {
                for (JsonNode node : rootNode.get("suggestions")) {
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

            return AnalyzeResultDto.builder()
                    .overallScore(overallScore)
                    .strengths(strengths)
                    .weaknesses(weaknesses)
                    .suggestions(suggestions)
                    .build();
        } catch (Exception e) {
            logger.error("Error parsing analyze result from node: {}", e.getMessage(), e);
            // Return empty result on error
            return AnalyzeResultDto.builder()
                    .suggestions(new ArrayList<>())
                    .strengths(new ArrayList<>())
                    .weaknesses(new ArrayList<>())
                    .build();
        }
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