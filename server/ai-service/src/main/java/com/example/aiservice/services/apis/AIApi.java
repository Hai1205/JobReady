package com.example.aiservice.services.apis;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ai.document.Document;
import org.springframework.cache.annotation.Cacheable;

import com.example.aiservice.dtos.*;
import com.example.aiservice.dtos.requests.*;
import com.example.aiservice.dtos.responses.AIResponseDto;
import com.example.aiservice.dtos.responses.Response;
import com.example.aiservice.exceptions.OurException;
import com.example.aiservice.services.*;
import com.example.aiservice.services.feigns.CVFeignClient;
import com.example.aiservice.services.feigns.UserFeignClient;
import com.fasterxml.jackson.databind.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AIApi - Sử dụng RAG (Retrieve-Augment-Generate) với Gemini
 * 
 * Architecture:
 * - RETRIEVE: EnhancedEmbeddingService tìm relevant templates từ PGVector
 * - AUGMENT: Build prompt với examples từ knowledge base
 * - GENERATE: ChatClient (Gemini) tạo response dựa trên augmented prompt
 */
@Service
@Slf4j
public class AIApi extends BaseApi {

    // Core RAG Components
    private final ChatClient chatClient; // Gemini Chat Model
    private final EmbeddingService embeddingService; // Vector Search
    // Engineering
    private final CompactPromptBuilder promptBuilder;

    // Supporting Services
    private final FileParserService fileParserService;
    private final CVFeignClient cvFeignClient;
    private final UserFeignClient userFeignClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructor - Inject all dependencies
     */
    public AIApi(
            ChatClient chatClient, // Inject từ RAGConfig
            EmbeddingService embeddingService,
            CompactPromptBuilder promptBuilder,
            FileParserService fileParserService,
            CVFeignClient cvFeignClient,
            UserFeignClient userFeignClient) {

        this.chatClient = chatClient;
        this.embeddingService = embeddingService;
        this.promptBuilder = promptBuilder;
        this.fileParserService = fileParserService;
        this.cvFeignClient = cvFeignClient;
        this.userFeignClient = userFeignClient;
        this.objectMapper = new ObjectMapper();
    }

    // ========================================
    // PUBLIC API METHODS
    // ========================================

    /**
     * API: Improve CV section
     */
    public Response improveCV(String dataJson) {
        Response response = new Response();

        try {
            long startTime = System.currentTimeMillis();
            logger.info("Starting improveCV request at {}", startTime);

            ImproveCVRequest request = objectMapper.readValue(dataJson, ImproveCVRequest.class);
            String section = request.getSection();
            String content = request.getContent();

            String category = detectCategoryFromText(content);
            String level = detectLevelFromText(content);

            logger.info("Improving section: {}, cat={}, lvl={}", section, category, level);

            // OPTIMIZED: Reduced example count from 3 to 1
            long geminiStart = System.currentTimeMillis();
            logger.info("Starting Gemini call for improveCV");
            AIResponseDto aiResponse = handleImproveCVFast(section, content, category, level);
            long geminiEnd = System.currentTimeMillis();
            logger.info("Completed Gemini call for improveCV in {} ms", geminiEnd - geminiStart);
            String improved = aiResponse.getImproved();

            response.setMessage("CV section improved successfully");
            response.setImprovedSection(improved);

            long endTime = System.currentTimeMillis();
            logger.info("Completed improveCV request in {} ms", endTime - startTime);

            return response;

        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Error in improveCV: {}", e.getMessage(), e);
            return buildErrorResponse(500, e.getMessage());
        }
    }

    /**
     * API: Analyze CV
     */
    public Response analyzeCV(String dataJson) {
        Response response = new Response();

        try {
            long startTime = System.currentTimeMillis();
            logger.info("Starting analyzeCV request at {}", startTime);

            AnalyzeCVRequest request = objectMapper.readValue(dataJson, AnalyzeCVRequest.class);

            CVDto cvDto = CVDto.builder()
                    .title(request.getTitle())
                    .personalInfo(request.getPersonalInfo())
                    .experiences(request.getExperiences())
                    .educations(request.getEducations())
                    .skills(request.getSkills())
                    .build();

            String category = detectCategory(cvDto);
            String level = detectLevel(cvDto);

            logger.info("Analyzing CV: title={}, cat={}, lvl={}", request.getTitle(), category, level);

            // OPTIMIZED: Parallel RAG + shorter prompt
            long geminiStart = System.currentTimeMillis();
            logger.info("Starting Gemini call for analyzeCV");

            AIResponseDto aiResponse = handleAnalyzeCVFast(cvDto, category, level);
            long geminiEnd = System.currentTimeMillis();

            logger.info("Completed Gemini call for analyzeCV in {} ms", geminiEnd - geminiStart);
            AnalyzeResultDto analyzeResult = aiResponse.getAnalyzeResult();
            logger.info("analyzeResult: {}", analyzeResult);

            response.setMessage("CV analyzed successfully");
            response.setAnalyze(analyzeResult);
            response.setSuggestions(analyzeResult.getSuggestions());

            long endTime = System.currentTimeMillis();
            logger.info("Completed analyzeCV request in {} ms", endTime - startTime);

            return response;

        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Error in analyzeCV: {}", e.getMessage(), e);
            return buildErrorResponse(500, e.getMessage());
        }
    }

    /**
     * API: Analyze CV with Job Description
     */
    public Response analyzeCVWithJobDescription(String dataJson, MultipartFile jdFile) {
        Response response = new Response();

        try {
            long startTime = System.currentTimeMillis();
            logger.info("Starting analyzeCVWithJobDescription request at {}", startTime);

            if (jdFile != null && !jdFile.isEmpty()) {
                fileParserService.validateDocumentFile(jdFile);
            }

            AnalyzeCVWithJDRequest request = objectMapper.readValue(
                    dataJson, AnalyzeCVWithJDRequest.class);

            String language = request.getLanguage();
            String jobDescription = request.getJobDescription();
            String jdText = handleExtractJobDescriptionText(jdFile, jobDescription);

            CVDto cvDto = CVDto.builder()
                    .title(request.getTitle())
                    .personalInfo(request.getPersonalInfo())
                    .experiences(request.getExperiences())
                    .educations(request.getEducations())
                    .skills(request.getSkills())
                    .build();

            logger.info("Analyzing CV with JD, lang={}", language);

            // OPTIMIZED: Skip RAG for JD matching (too slow)
            long geminiStart = System.currentTimeMillis();
            logger.info("Starting Gemini call for analyzeCVWithJD");
            AIResponseDto aiResponse = handleAnalyzeCVWithJDFast(cvDto, language, jdText);
            long geminiEnd = System.currentTimeMillis();
            logger.info("Completed Gemini call for analyzeCVWithJD in {} ms", geminiEnd - geminiStart);

            logger.info("analyzeResult: {}", aiResponse.getAnalyzeResult());
            response.setMessage("CV analyzed with job description successfully");
            response.setParsedJobDescription(aiResponse.getJdResult());
            response.setAnalyze(aiResponse.getAnalyzeResult());
            response.setMatchScore(aiResponse.getMatchScore());
            response.setMissingKeywords(aiResponse.getMissingKeywords());

            long endTime = System.currentTimeMillis();
            logger.info("Completed analyzeCVWithJobDescription request in {} ms", endTime - startTime);

            return response;

        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Error in analyzeCVWithJobDescription: {}", e.getMessage(), e);
            return buildErrorResponse(500, e.getMessage());
        }
    }

    /**
     * API: Import CV from PDF
     */
    public Response importCV(UUID userId, MultipartFile file) {
        Response response = new Response();

        try {
            CVDto savedCV = handleImportCV(userId, file);

            response.setStatusCode(201);
            response.setMessage("CV created successfully");
            response.setCv(savedCV);

            return response;

        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Error in importCV: {}", e.getMessage(), e);
            return buildErrorResponse(500, "Error creating CV: " + e.getMessage());
        }
    }

    // ========================================
    // CORE RAG METHODS - Implement RAG Flow
    // ========================================

    private CVDto handleImportCV(UUID userId, MultipartFile file) {
        try {
            long startTime = System.currentTimeMillis();
            logger.info("Starting importCV request at {}", startTime);

            UserDto user = userFeignClient.getUserById(userId.toString()).getUser();

            if (user == null) {
                logger.warn("User not found when creating CV: userId={}", userId);
                throw new OurException("User not found", 404);
            }

            fileParserService.validatePDFFile(file);

            String cvText = fileParserService.extractTextFromFile(file);
            long geminiStart = System.currentTimeMillis();
            logger.info("Starting Gemini call for importCV");
            CVDto cvDto = fileParserService.parseCVWithAI(cvText);
            long geminiEnd = System.currentTimeMillis();
            logger.info("Completed Gemini call for importCV in {} ms", geminiEnd - geminiStart);

            // Merge AI parsed personal info with user's real info
            PersonalInfoDto mergedPersonalInfo = cvDto.getPersonalInfo();
            if (mergedPersonalInfo == null) {
                mergedPersonalInfo = new PersonalInfoDto();
            }
            mergedPersonalInfo
                    .setFullname(user.getFullname() != null ? user.getFullname() : mergedPersonalInfo.getFullname());
            mergedPersonalInfo.setEmail(user.getEmail() != null ? user.getEmail() : mergedPersonalInfo.getEmail());
            mergedPersonalInfo.setPhone(user.getPhone() != null ? user.getPhone() : mergedPersonalInfo.getPhone());
            mergedPersonalInfo
                    .setLocation(user.getLocation() != null ? user.getLocation() : mergedPersonalInfo.getLocation());
            mergedPersonalInfo.setBirth(user.getBirth() != null ? user.getBirth() : mergedPersonalInfo.getBirth());
            mergedPersonalInfo.setAvatarUrl(
                    user.getAvatarUrl() != null ? user.getAvatarUrl() : mergedPersonalInfo.getAvatarUrl());

            CVCreateRequest cvCreateRequest = CVCreateRequest.builder()
                    .title(cvDto.getTitle())
                    .personalInfo(mergedPersonalInfo)
                    .experiences(cvDto.getExperiences())
                    .educations(cvDto.getEducations())
                    .skills(cvDto.getSkills())
                    .build();

            String dataJson = objectMapper.writeValueAsString(cvCreateRequest);
            Response createCVResponse = cvFeignClient.importCV(userId.toString(), dataJson);
            CVDto savedCV = createCVResponse.getCv();
            long endTime = System.currentTimeMillis();
            logger.info("Completed importCV request in {} ms", endTime - startTime);
            return savedCV;
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error in handleImportCV: {}", e.getMessage(), e);
            throw new OurException("Failed to import CV: " + e.getMessage(), 500);
        }
    }

    /**
     * RAG Flow 1: Analyze CV
     * 
     * Flow:
     * 1. RETRIEVE: Tìm best practice examples từ vector store
     * 2. AUGMENT: Build prompt với examples
     * 3. GENERATE: Gemini tạo analysis dựa trên augmented prompt
     */
    private AIResponseDto handleAnalyzeCVFast(CVDto cv, String category, String level) {
        try {
            logger.info("Starting FAST RAG analysis...");
            long startTime = System.currentTimeMillis();

            String cvContent = formatCVCompact(cv);

            // OPTIMIZATION 1: Parallel vector search for multiple sections
            Map<String, String> sectionQueries = new HashMap<>();

            if (cv.getPersonalInfo() != null && cv.getPersonalInfo().getSummary() != null) {
                sectionQueries.put("summary", cv.getPersonalInfo().getSummary());
            }

            if (cv.getExperiences() != null && !cv.getExperiences().isEmpty()) {
                sectionQueries.put("experience", formatExperiencesCompact(cv.getExperiences()));
            }

            // Execute parallel search - reduces latency by 50-70%
            Map<String, List<Document>> allExamples = embeddingService
                    .searchMultipleSectionsParallel(sectionQueries, category, level, 1); // Only 1 example per section

            logger.info("RAG retrieve phase: {}ms", System.currentTimeMillis() - startTime);

            // OPTIMIZATION 2: Compact prompt with minimal examples
            String systemPrompt = promptBuilder.buildCompactAnalysisPrompt();
            String userPrompt = buildCompactAnalysisPrompt(cvContent, allExamples);

            logger.info("Prompt built: {} chars", userPrompt.length());

            // OPTIMIZATION 3: Single Gemini call
            String aiResponse = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            AnalyzeResultDto analyzeResult = parseAnalyzeResult(aiResponse);

            long totalTime = System.currentTimeMillis() - startTime;
            logger.info("FAST RAG completed in {}ms", totalTime);

            return AIResponseDto.builder()
                    .analyzeResult(analyzeResult)
                    .suggestions(analyzeResult.getSuggestions())
                    .build();

        } catch (Exception e) {
            logger.error("Fast RAG failed: {}", e.getMessage(), e);
            throw new OurException("Failed to analyze CV", 500);
        }
    }

    /**
     * RAG Flow 2: Improve CV Section
     * 
     * Flow:
     * 1. RETRIEVE: Tìm high-quality examples cho section
     * 2. AUGMENT: Build improvement prompt với examples
     * 3. GENERATE: Gemini rewrite section
     */
    private AIResponseDto handleImproveCVFast(
            String section, String content, String category, String level) {
        try {
            logger.info("Starting FAST improvement for section: {}", section);
            long startTime = System.currentTimeMillis();

            // OPTIMIZATION 1: Only 1 example (down from 3)
            List<Document> examples = embeddingService.searchRelevantTemplates(
                    content, section, category, level, 1);

            logger.info("Retrieved {} example in {}ms",
                    examples.size(), System.currentTimeMillis() - startTime);

            // OPTIMIZATION 2: Ultra-compact prompt
            String systemPrompt = promptBuilder.buildCompactImprovementPrompt(section);
            String userPrompt = buildCompactImprovementPrompt(content, examples);

            // OPTIMIZATION 3: Single Gemini call
            String improved = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            logger.info("Improvement completed in {}ms", System.currentTimeMillis() - startTime);

            return AIResponseDto.builder()
                    .improved(improved)
                    .build();

        } catch (Exception e) {
            logger.error("Fast improvement failed: {}", e.getMessage(), e);
            throw new OurException("Failed to improve CV", 500);
        }
    }

    /**
     * RAG Flow 3: Analyze CV with Job Description
     * 
     * Flow:
     * 1. RETRIEVE: Tìm JD examples + CV examples
     * 2. AUGMENT: Build matching prompt với examples
     * 3. GENERATE: Gemini tạo match analysis
     */
    @Cacheable(value = "jdMatchCache", key = "#cv.personalInfo.fullname + '-' + #jdText.hashCode()")
    private AIResponseDto handleAnalyzeCVWithJDFast(
            CVDto cv, String language, String jdText) {
        try {
            logger.info("Starting FAST JD matching...");
            long startTime = System.currentTimeMillis();

            String cvContent = formatCVCompact(cv);

            String jdCompact = smartTruncate(jdText, 1500, "JD"); // Increased from 1000
            String cvCompact = smartTruncate(cvContent, 2000, "CV"); // Increased from 1500

            logger.info("Input sizes: JD={}chars (from {}), CV={}chars (from {})",
                    jdCompact.length(), jdText.length(),
                    cvCompact.length(), cvContent.length());

            String systemPrompt = promptBuilder.buildCompactJobMatchPrompt(language);
            String userPrompt = String.format(
                    "Job Description:\n%s\n\nCandidate CV:\n%s\n\nAnalyze match and return JSON.",
                    truncate(jdCompact, 2000), // Limit JD length
                    truncate(cvCompact, 3000)); // Limit CV length

            String aiResponse = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            // Parse response
            String jsonContent = extractJsonFromResponse(aiResponse);
            JsonNode root = objectMapper.readTree(jsonContent);

            JobDescriptionResult jdResult = null;
            if (root.has("jobDescription")) {
                jdResult = objectMapper.treeToValue(
                        root.get("jobDescription"), JobDescriptionResult.class);
            }

            Double matchScore = root.has("overallMatchScore")
                    ? root.get("overallMatchScore").asDouble()
                    : null;

            List<String> missingKeywords = new ArrayList<>();
            if (root.has("missingKeywords") && root.get("missingKeywords").isArray()) {
                for (JsonNode n : root.get("missingKeywords")) {
                    missingKeywords.add(n.asText());
                }
            }

            AnalyzeResultDto analyzeResult = parseAnalyzeResultFromNode(root);

            logger.info("JD matching completed in {}ms", System.currentTimeMillis() - startTime);

            return AIResponseDto.builder()
                    .jdResult(jdResult)
                    .analyzeResult(analyzeResult)
                    .matchScore(matchScore)
                    .missingKeywords(missingKeywords)
                    .suggestions(analyzeResult.getSuggestions())
                    .build();

        } catch (Exception e) {
            logger.error("Fast JD matching failed: {}", e.getMessage(), e);
            throw new OurException("Failed to analyze CV with JD", 500);
        }
    }

    // ========================================
    // AUGMENT PHASE - Build Prompts with RAG Context
    // ========================================

    private String smartTruncate(String text, int maxLen, String label) {
        if (text == null || text.length() <= maxLen) {
            return text;
        }

        // Keep first 70% and last 30% to preserve both intro and conclusion
        int keepStart = (int) (maxLen * 0.7);
        int keepEnd = maxLen - keepStart;

        String result = text.substring(0, keepStart)
                + "\n...[truncated]...\n"
                + text.substring(text.length() - keepEnd);

        logger.debug("{} truncated: {} -> {} chars", label, text.length(), result.length());
        return result;
    }

    /**
     * Build augmented prompt for CV analysis
     * Includes best practice examples from knowledge base
     */
    private String buildCompactAnalysisPrompt(
            String cvContent, Map<String, List<Document>> examples) {
        StringBuilder sb = new StringBuilder();

        // Add only 1 best example per section
        if (!examples.isEmpty()) {
            sb.append("Best Practices:\n");
            examples.forEach((section, docs) -> {
                if (!docs.isEmpty()) {
                    sb.append(section).append(": ")
                            .append(truncate(docs.get(0).getText(), 200))
                            .append("\n");
                }
            });
            sb.append("\n");
        }

        sb.append("CV:\n").append(truncate(cvContent, 2000));
        return sb.toString();
    }

    /**
     * Build augmented prompt for CV improvement
     * Includes high-quality examples for the specific section
     */
    private String buildCompactImprovementPrompt(
            String content, List<Document> examples) {
        StringBuilder sb = new StringBuilder();

        if (!examples.isEmpty()) {
            sb.append("Example: ")
                    .append(truncate(examples.get(0).getText(), 300))
                    .append("\n\n");
        }

        sb.append("Improve:\n").append(content);
        return sb.toString();
    }

    /**
     * Build augmented prompt for JD matching
     * Includes both JD examples and CV examples for comparison
     */
    private String formatCVCompact(CVDto cv) {
        StringBuilder sb = new StringBuilder();

        if (cv.getPersonalInfo() != null) {
            PersonalInfoDto pi = cv.getPersonalInfo();
            sb.append(pi.getFullname()).append(" | ")
                    .append(pi.getEmail()).append("\n");
            if (pi.getSummary() != null) {
                sb.append(pi.getSummary()).append("\n\n");
            }
        }

        if (cv.getExperiences() != null && !cv.getExperiences().isEmpty()) {
            sb.append("Experience:\n");
            for (ExperienceDto exp : cv.getExperiences()) {
                sb.append("• ").append(exp.getPosition())
                        .append(" @ ").append(exp.getCompany())
                        .append(" (").append(exp.getStartDate())
                        .append("-").append(exp.getEndDate()).append(")\n");
            }
            sb.append("\n");
        }

        if (cv.getSkills() != null && !cv.getSkills().isEmpty()) {
            sb.append("Skills: ").append(String.join(", ", cv.getSkills()));
        }

        return sb.toString();
    }

    /**
     * Helper: Capitalize first letter
     */
    private String formatExperiencesCompact(List<ExperienceDto> exps) {
        return exps.stream()
                .limit(3) // Only first 3 experiences
                .map(exp -> exp.getPosition() + " @ " + exp.getCompany())
                .collect(Collectors.joining("; "));
    }

    // ========================================
    // HELPER METHODS - Format, Parse, Detect
    // ========================================

    private String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    private AnalyzeResultDto parseAnalyzeResult(String aiResponse) {
        try {
            String json = extractJsonFromResponse(aiResponse);
            JsonNode root = objectMapper.readTree(json);
            return parseAnalyzeResultFromNode(root);
        } catch (Exception e) {
            logger.error("Parse error: {}", e.getMessage());
            return AnalyzeResultDto.builder()
                    .suggestions(new ArrayList<>())
                    .strengths(new ArrayList<>())
                    .weaknesses(new ArrayList<>())
                    .build();
        }
    }

    private AnalyzeResultDto parseAnalyzeResultFromNode(JsonNode node) {
        Integer score = node.has("overallScore") ? node.get("overallScore").asInt() : null;

        List<String> strengths = new ArrayList<>();
        if (node.has("strengths") && node.get("strengths").isArray()) {
            node.get("strengths").forEach(n -> strengths.add(n.asText()));
        }

        List<String> weaknesses = new ArrayList<>();
        if (node.has("weaknesses") && node.get("weaknesses").isArray()) {
            node.get("weaknesses").forEach(n -> weaknesses.add(n.asText()));
        }

        List<AISuggestionDto> suggestions = new ArrayList<>();
        if (node.has("suggestions") && node.get("suggestions").isArray()) {
            node.get("suggestions").forEach(n -> {
                // Parse standard format: {id, type, section, message, suggestion, data,
                // applied}
                // This unified format is used by both analyze and analyze-with-jd APIs
                // The 'data' field contains actual content ready to apply (skills array, text,
                // dates, etc.)
                suggestions.add(AISuggestionDto.builder()
                        .id(n.has("id") ? n.get("id").asText() : UUID.randomUUID().toString())
                        .type(n.has("type") ? n.get("type").asText() : "improvement")
                        .section(n.has("section") ? n.get("section").asText() : "general")
                        .message(n.has("message") ? n.get("message").asText() : "")
                        .suggestion(n.has("suggestion") ? n.get("suggestion").asText() : "")
                        .before(n.has("before") ? n.get("before").asText() : null)
                        .data(n.has("data") ? n.get("data") : null)
                        .applied(n.has("applied") ? n.get("applied").asBoolean() : false)
                        .build());
            });
        }

        return AnalyzeResultDto.builder()
                .overallScore(score)
                .strengths(strengths)
                .weaknesses(weaknesses)
                .suggestions(suggestions)
                .build();
    }

    private String extractJsonFromResponse(String response) {
        String trimmed = response.trim();
        if (trimmed.startsWith("```")) {
            int start = trimmed.indexOf('{');
            int end = trimmed.lastIndexOf('}');
            if (start != -1 && end != -1) {
                return trimmed.substring(start, end + 1);
            }
        }
        return trimmed;
    }

    private String handleExtractJobDescriptionText(MultipartFile file, String text) {
        if (file == null || file.isEmpty()) {
            return text;
        }
        try {
            return fileParserService.extractTextFromFile(file);
        } catch (Exception e) {
            logger.error("Error extracting JD file: {}", e.getMessage());
            return text;
        }
    }

    // Category & Level detection (same as before but kept for compatibility)
    private String detectCategory(CVDto cv) {
        String text = (cv.getTitle() + " " +
                (cv.getSkills() != null ? String.join(" ", cv.getSkills()) : "")).toLowerCase();

        if (text.contains("java") || text.contains("python") || text.contains("developer")) {
            return "tech";
        }
        if (text.contains("marketing") || text.contains("seo")) {
            return "marketing";
        }
        return "general";
    }

    private String detectLevel(CVDto cv) {
        int years = calculateYearsOfExperience(cv.getExperiences());
        if (years >= 5)
            return "senior";
        if (years >= 2)
            return "mid";
        return "junior";
    }

    private int calculateYearsOfExperience(List<ExperienceDto> exps) {
        if (exps == null || exps.isEmpty())
            return 0;

        int totalMonths = 0;
        for (ExperienceDto exp : exps) {
            try {
                String[] start = exp.getStartDate().split("-");
                int startYear = Integer.parseInt(start[0]);

                int endYear;
                if (exp.getEndDate().equalsIgnoreCase("present")) {
                    endYear = java.time.LocalDate.now().getYear();
                } else {
                    endYear = Integer.parseInt(exp.getEndDate().split("-")[0]);
                }

                totalMonths += (endYear - startYear) * 12;
            } catch (Exception e) {
                // Skip invalid dates
            }
        }
        return totalMonths / 12;
    }

    private String detectCategoryFromText(String text) {
        if (text == null)
            return "general";
        String lower = text.toLowerCase();
        if (lower.contains("java") || lower.contains("python") || lower.contains("developer")) {
            return "tech";
        }
        if (lower.contains("marketing"))
            return "marketing";
        return "general";
    }

    private String detectLevelFromText(String text) {
        if (text == null)
            return "mid";
        String lower = text.toLowerCase();
        if (lower.contains("senior") || lower.contains("10+ years"))
            return "senior";
        if (lower.contains("junior") || lower.contains("1 year"))
            return "junior";
        return "mid";
    }
}