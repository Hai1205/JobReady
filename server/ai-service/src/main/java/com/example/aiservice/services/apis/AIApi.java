package com.example.aiservice.services.apis;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ai.document.Document;

import com.example.aiservice.dtos.*;
import com.example.aiservice.dtos.requests.*;
import com.example.aiservice.dtos.responses.AIResponseDto;
import com.example.aiservice.dtos.responses.Response;
import com.example.aiservice.exceptions.OurException;
import com.example.aiservice.services.*;
import com.example.aiservice.services.feigns.CVFeignClient;
import com.fasterxml.jackson.databind.*;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
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
    private final PromptBuilderService promptBuilderService; // Prompt Engineering

    // Supporting Services
    private final FileParserService fileParserService;
    private final CVFeignClient cvFeignClient;
    private final ObjectMapper objectMapper;

    /**
     * Constructor - Inject all dependencies
     */
    public AIApi(
            ChatClient chatClient, // Inject từ RAGConfig
            EmbeddingService embeddingService,
            PromptBuilderService promptBuilderService,
            FileParserService fileParserService,
            CVFeignClient cvFeignClient) {

        this.chatClient = chatClient;
        this.embeddingService = embeddingService;
        this.promptBuilderService = promptBuilderService;
        this.fileParserService = fileParserService;
        this.cvFeignClient = cvFeignClient;
        this.objectMapper = new ObjectMapper();

        log.info("AIApi initialized with RAG + Gemini");
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
            ImproveCVRequest request = objectMapper.readValue(dataJson, ImproveCVRequest.class);
            String section = request.getSection();
            String content = request.getContent();

            // Auto-detect category & level
            String category = detectCategoryFromText(content);
            String level = detectLevelFromText(content);

            log.info("Improving CV section: {}, category={}, level={}",
                    section, category, level);

            // Execute RAG flow
            AIResponseDto aiResponse = handleImproveCV(section, content, category, level);
            String improved = aiResponse.getImproved();

            response.setMessage("CV section improved successfully");
            response.setImprovedSection(improved);
            return response;

        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Error in improveCV: {}", e.getMessage(), e);
            return buildErrorResponse(500, e.getMessage());
        }
    }

    /**
     * API: Analyze CV
     */
    public Response analyzeCV(String dataJson) {
        Response response = new Response();

        try {
            AnalyzeCVRequest request = objectMapper.readValue(dataJson, AnalyzeCVRequest.class);

            CVDto cvDto = CVDto.builder()
                    .title(request.getTitle())
                    .personalInfo(request.getPersonalInfo())
                    .experiences(request.getExperiences())
                    .educations(request.getEducations())
                    .skills(request.getSkills())
                    .build();

            // Auto-detect category & level
            String category = detectCategory(cvDto);
            String level = detectLevel(cvDto);

            log.info("Analyzing CV: title={}, category={}, level={}",
                    request.getTitle(), category, level);

            // Execute RAG flow
            AIResponseDto aiResponse = handleAnalyzeCV(cvDto, category, level);
            AnalyzeResultDto analyzeResult = aiResponse.getAnalyzeResult();

            response.setMessage("CV analyzed successfully");
            response.setAnalyze(analyzeResult);
            response.setSuggestions(analyzeResult.getSuggestions());

            log.debug("Analysis completed: {} suggestions",
                    analyzeResult.getSuggestions() != null ? analyzeResult.getSuggestions().size() : 0);

            return response;

        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Error in analyzeCV: {}", e.getMessage(), e);
            return buildErrorResponse(500, e.getMessage());
        }
    }

    /**
     * API: Analyze CV with Job Description
     */
    public Response analyzeCVWithJobDescription(String dataJson, MultipartFile jdFile) {
        Response response = new Response();

        try {
            // Validate JD file if provided
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

            log.info("Analyzing CV with JD, language={}", language);

            // Execute RAG flow
            AIResponseDto aiResponse = handleAnalyzeCVWithJobDescription(
                    cvDto, language, jdText);

            response.setMessage("CV analyzed with job description successfully");
            response.setParsedJobDescription(aiResponse.getJdResult());
            response.setAnalyze(aiResponse.getAnalyzeResult());
            response.setMatchScore(aiResponse.getMatchScore());
            response.setMissingKeywords(aiResponse.getMissingKeywords());

            return response;

        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Error in analyzeCVWithJobDescription: {}", e.getMessage(), e);
            return buildErrorResponse(500, e.getMessage());
        }
    }

    /**
     * API: Import CV from PDF
     */
    public Response importCV(UUID userId, MultipartFile file) {
        Response response = new Response();

        try {
            fileParserService.validatePDFFile(file);

            String cvText = fileParserService.extractTextFromFile(file);
            log.debug("Extracted CV text ({} chars)", cvText.length());

            CVDto cvDto = fileParserService.parseCVWithAI(cvText);
            log.debug("Parsed CV: {}", cvDto.getTitle());

            CVCreateRequest cvCreateRequest = CVCreateRequest.builder()
                    .title(cvDto.getTitle())
                    .personalInfo(cvDto.getPersonalInfo())
                    .experiences(cvDto.getExperiences())
                    .educations(cvDto.getEducations())
                    .skills(cvDto.getSkills())
                    .build();

            String dataJson = objectMapper.writeValueAsString(cvCreateRequest);

            Response createCVResponse = cvFeignClient.importCV(
                    userId.toString(), dataJson);

            CVDto savedCV = createCVResponse.getCv();

            response.setStatusCode(201);
            response.setMessage("CV created successfully");
            response.setCv(savedCV);

            log.debug("CV imported for userId={}, cvId={}", userId, savedCV.getId());

            return response;

        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Error in importCV: {}", e.getMessage(), e);
            return buildErrorResponse(500, "Error creating CV: " + e.getMessage());
        }
    }

    // ========================================
    // CORE RAG METHODS - Implement RAG Flow
    // ========================================

    /**
     * RAG Flow 1: Analyze CV
     * 
     * Flow:
     * 1. RETRIEVE: Tìm best practice examples từ vector store
     * 2. AUGMENT: Build prompt với examples
     * 3. GENERATE: Gemini tạo analysis dựa trên augmented prompt
     */
    private AIResponseDto handleAnalyzeCV(CVDto cv, String category, String level) {
        try {
            log.info("Starting RAG flow for CV analysis...");

            // Step 1: RETRIEVE - Tìm relevant examples
            log.debug("RETRIEVE phase: Searching for relevant examples...");

            String cvContent = handleFormatCVForAnalysis(cv);

            List<Document> summaryExamples = embeddingService.searchRelevantTemplates(
                    cv.getPersonalInfo() != null ? cv.getPersonalInfo().getSummary() : "",
                    "summary",
                    category,
                    level,
                    2 // Top 2 examples
            );

            List<Document> experienceExamples = embeddingService
                    .searchRelevantTemplates(
                            formatExperiences(cv.getExperiences()),
                            "experience",
                            category,
                            level,
                            3 // Top 3 examples
                    );

            log.info("Retrieved {} examples (summary: {}, experience: {})",
                    summaryExamples.size() + experienceExamples.size(),
                    summaryExamples.size(),
                    experienceExamples.size());

            // Step 2: AUGMENT - Build augmented prompt
            log.debug("AUGMENT phase: Building context with examples...");

            String systemPrompt = promptBuilderService.buildCVAnalysisPrompt();
            String augmentedPrompt = buildAugmentedAnalysisPrompt(
                    cvContent,
                    summaryExamples,
                    experienceExamples);

            log.debug("Augmented prompt built ({} chars)", augmentedPrompt.length());

            // Step 3: GENERATE - Call Gemini với ChatClient
            log.debug("GENERATE phase: Calling Gemini...");

            String aiResponseText = chatClient.prompt()
                    .system(systemPrompt)
                    .user(augmentedPrompt)
                    .call()
                    .content();

            log.info("Gemini response received ({} chars)", aiResponseText.length());

            // Parse response
            AnalyzeResultDto analyzeResult = handleParseAnalyzeResult(aiResponseText);
            List<AISuggestionDto> suggestions = analyzeResult.getSuggestions() != null
                    ? analyzeResult.getSuggestions()
                    : new ArrayList<>();

            log.info("RAG flow completed: {} suggestions generated", suggestions.size());

            return AIResponseDto.builder()
                    .analyzeResult(analyzeResult)
                    .suggestions(suggestions)
                    .build();

        } catch (Exception e) {
            log.error("RAG flow failed in analyzeCV: {}", e.getMessage(), e);
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
    private AIResponseDto handleImproveCV(
            String section,
            String content,
            String category,
            String level) {
        try {
            log.info("Starting RAG flow for CV improvement (section: {})...", section);

            // Step 1: RETRIEVE
            log.debug("RETRIEVE phase: Searching for best examples...");

            List<Document> examples = embeddingService.searchRelevantTemplates(
                    content,
                    section,
                    category,
                    level,
                    3 // Top 3 best examples
            );

            log.info("Retrieved {} high-quality examples (rating >= 4)", examples.size());

            // Step 2: AUGMENT
            log.debug("AUGMENT phase: Building improvement context...");

            String systemPrompt = promptBuilderService.buildCVImprovementPrompt(
                    section, "General position", List.of());

            String augmentedPrompt = buildAugmentedImprovementPrompt(
                    section,
                    content,
                    examples);

            log.debug("Augmented prompt built ({} chars)", augmentedPrompt.length());

            // Step 3: GENERATE
            log.debug("GENERATE phase: Calling Gemini for rewrite...");

            String improved = chatClient.prompt()
                    .system(systemPrompt)
                    .user(augmentedPrompt)
                    .call()
                    .content();

            log.info("RAG flow completed: Content improved ({} chars)", improved.length());

            return AIResponseDto.builder()
                    .improved(improved)
                    .build();

        } catch (Exception e) {
            log.error("RAG flow failed in improveCV: {}", e.getMessage(), e);
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
    private AIResponseDto handleAnalyzeCVWithJobDescription(
            CVDto cv,
            String language,
            String jdText) {
        try {
            log.info("Starting RAG flow for CV-JD matching...");

            String cvContent = handleFormatCVForAnalysis(cv);

            // Auto-detect category & level
            String category = detectCategory(cv);
            String level = detectLevel(cv);

            log.info("Auto-detected: category={}, level={}", category, level);

            // Step 1: RETRIEVE - Tìm JD examples và CV examples
            log.debug("RETRIEVE phase: Searching JD & CV examples...");

            List<Document> jdExamples = embeddingService.searchRelevantTemplates(
                    jdText,
                    "job_description",
                    category,
                    level,
                    2 // Top 2 JD examples
            );

            List<Document> cvExamples = embeddingService.searchRelevantTemplates(
                    cvContent,
                    "summary",
                    category,
                    level,
                    2 // Top 2 CV examples
            );

            log.info("Retrieved {} examples (JD: {}, CV: {})",
                    jdExamples.size() + cvExamples.size(),
                    jdExamples.size(),
                    cvExamples.size());

            // Step 2: AUGMENT
            log.debug("AUGMENT phase: Building match analysis context...");

            String systemPrompt = promptBuilderService.buildJobMatchPrompt(
                    language != null ? language : "vi");

            String augmentedPrompt = buildAugmentedJDMatchPrompt(
                    jdText,
                    cvContent,
                    jdExamples,
                    cvExamples);

            log.debug("Augmented prompt built ({} chars)", augmentedPrompt.length());

            // Step 3: GENERATE
            log.debug("GENERATE phase: Calling Gemini for matching...");

            String aiResponseText = chatClient.prompt()
                    .system(systemPrompt)
                    .user(augmentedPrompt)
                    .call()
                    .content();

            log.info("Gemini response received ({} chars)", aiResponseText.length());

            // Parse complex response
            String jsonContent = handleExtractJsonFromResponse(aiResponseText);
            JsonNode root = objectMapper.readTree(jsonContent);

            // Parse different sections
            JsonNode analysisNode = root.has("analysis") ? root.get("analysis") : root;
            JsonNode jdNode = root.has("jobDescription") ? root.get("jobDescription") : root;

            // Parse job description
            JobDescriptionResult jdResult = null;
            if (jdNode != null && jdNode.isObject()) {
                try {
                    jdResult = objectMapper.treeToValue(jdNode, JobDescriptionResult.class);
                } catch (Exception e) {
                    log.warn("Failed to parse job description: {}", e.getMessage());
                }
            }

            // Parse match score
            Double matchScore = null;
            if (analysisNode.has("overallMatchScore")) {
                matchScore = analysisNode.get("overallMatchScore").asDouble();
            } else if (analysisNode.has("matchScore")) {
                matchScore = analysisNode.get("matchScore").asDouble();
            }

            // Parse missing keywords
            List<String> missingKeywords = new ArrayList<>();
            if (analysisNode.has("missingKeywords") &&
                    analysisNode.get("missingKeywords").isArray()) {
                for (JsonNode n : analysisNode.get("missingKeywords")) {
                    missingKeywords.add(n.asText());
                }
            }

            // Parse analyze result
            AnalyzeResultDto analyzeResult = handleParseAnalyzeResultFromNode(analysisNode);

            log.info("RAG flow completed: matchScore={}, {} keywords missing",
                    matchScore, missingKeywords.size());

            return AIResponseDto.builder()
                    .jdResult(jdResult)
                    .analyzeResult(analyzeResult)
                    .matchScore(matchScore)
                    .missingKeywords(missingKeywords)
                    .suggestions(analyzeResult.getSuggestions())
                    .build();

        } catch (Exception e) {
            log.error("RAG flow failed in analyzeCVWithJobDescription: {}",
                    e.getMessage(), e);
            throw new OurException("Failed to analyze CV with Job Description", 500);
        }
    }

    // ========================================
    // AUGMENT PHASE - Build Prompts with RAG Context
    // ========================================

    /**
     * Build augmented prompt for CV analysis
     * Includes best practice examples from knowledge base
     */
    private String buildAugmentedAnalysisPrompt(
            String cvContent,
            List<Document> summaryExamples,
            List<Document> experienceExamples) {

        StringBuilder prompt = new StringBuilder();

        // Add summary examples if available
        if (!summaryExamples.isEmpty()) {
            prompt.append("=== BEST PRACTICE EXAMPLES FOR SUMMARY ===\n\n");
            for (int i = 0; i < summaryExamples.size(); i++) {
                Document doc = summaryExamples.get(i);
                prompt.append(String.format(
                        "Example %d (Category: %s, Level: %s, Rating: %s):\n%s\n\n",
                        i + 1,
                        doc.getMetadata().get("category"),
                        doc.getMetadata().get("level"),
                        doc.getMetadata().get("rating"),
                        doc.getText()));
            }
        }

        // Add experience examples if available
        if (!experienceExamples.isEmpty()) {
            prompt.append("=== BEST PRACTICE EXAMPLES FOR EXPERIENCE ===\n\n");
            for (int i = 0; i < experienceExamples.size(); i++) {
                Document doc = experienceExamples.get(i);
                prompt.append(String.format(
                        "Example %d (Category: %s, Level: %s, Rating: %s):\n%s\n\n",
                        i + 1,
                        doc.getMetadata().get("category"),
                        doc.getMetadata().get("level"),
                        doc.getMetadata().get("rating"),
                        doc.getText()));
            }
        }

        // Add the actual CV to analyze
        prompt.append("=== CV TO ANALYZE ===\n\n");
        prompt.append(cvContent);
        prompt.append("\n\n");

        // Add instruction
        prompt.append("Based on the best practice examples above, ");
        prompt.append("analyze this CV and provide detailed feedback. ");
        prompt.append("Compare the CV sections with the examples and identify gaps.\n");

        return prompt.toString();
    }

    /**
     * Build augmented prompt for CV improvement
     * Includes high-quality examples for the specific section
     */
    private String buildAugmentedImprovementPrompt(
            String section,
            String content,
            List<Document> examples) {

        StringBuilder prompt = new StringBuilder();

        prompt.append(String.format(
                "=== BEST PRACTICE EXAMPLES FOR %s ===\n\n",
                section.toUpperCase()));

        if (examples.isEmpty()) {
            prompt.append("(No specific examples found, use general best practices)\n\n");
            logger.warn("No examples found for section: {}", section);
        } else {
            for (int i = 0; i < examples.size(); i++) {
                Document doc = examples.get(i);

                // Extract metadata
                String category = (String) doc.getMetadata().get("category");
                String level = (String) doc.getMetadata().get("level");
                Integer rating = (Integer) doc.getMetadata().get("rating");

                prompt.append(String.format(
                        "Example %d - %s %s (Quality Rating: %d/5):\n%s\n\n",
                        i + 1,
                        capitalizeFirst(level),
                        capitalizeFirst(category),
                        rating,
                        doc.getText()));
            }
        }

        // Add current content to improve
        prompt.append(String.format(
                "=== CURRENT %s TO IMPROVE ===\n\n",
                section.toUpperCase()));
        prompt.append(content);
        prompt.append("\n\n");

        // Add improvement instructions
        prompt.append("Using the best practice examples above as reference, ");
        prompt.append("rewrite this section to be more impactful and professional. ");
        prompt.append("Focus on:\n");
        prompt.append("• Strong action verbs and clear achievements\n");
        prompt.append("• Quantifiable metrics and specific numbers\n");
        prompt.append("• Concise and impactful language\n");
        prompt.append("• Industry-standard formatting and structure\n");

        return prompt.toString();
    }

    /**
     * Build augmented prompt for JD matching
     * Includes both JD examples and CV examples for comparison
     */
    private String buildAugmentedJDMatchPrompt(
            String jdText,
            String cvContent,
            List<Document> jdExamples,
            List<Document> cvExamples) {

        StringBuilder prompt = new StringBuilder();

        // Add JD examples if available
        if (!jdExamples.isEmpty()) {
            prompt.append("=== EXAMPLE JOB DESCRIPTIONS (for reference) ===\n\n");
            for (int i = 0; i < jdExamples.size(); i++) {
                Document doc = jdExamples.get(i);

                String category = (String) doc.getMetadata().get("category");
                String level = (String) doc.getMetadata().get("level");

                prompt.append(String.format(
                        "JD Example %d - %s %s Position:\n%s\n\n",
                        i + 1,
                        capitalizeFirst(level),
                        capitalizeFirst(category),
                        doc.getText()));
            }
        }

        // Add CV examples for comparison
        if (!cvExamples.isEmpty()) {
            prompt.append("=== HIGH-QUALITY CV EXAMPLES (for comparison) ===\n\n");
            for (int i = 0; i < cvExamples.size(); i++) {
                Document doc = cvExamples.get(i);

                String category = (String) doc.getMetadata().get("category");
                String level = (String) doc.getMetadata().get("level");
                Integer rating = (Integer) doc.getMetadata().get("rating");

                prompt.append(String.format(
                        "CV Example %d - %s %s (Rating: %d/5):\n%s\n\n",
                        i + 1,
                        capitalizeFirst(level),
                        capitalizeFirst(category),
                        rating,
                        doc.getText()));
            }
        }

        // Add actual JD and CV to analyze
        prompt.append("=== JOB DESCRIPTION TO ANALYZE ===\n\n");
        prompt.append(jdText);
        prompt.append("\n\n");

        prompt.append("=== CANDIDATE'S CV ===\n\n");
        prompt.append(cvContent);
        prompt.append("\n\n");

        // Add analysis instructions
        prompt.append("Based on the examples above and the JD requirements, ");
        prompt.append("analyze how well this CV matches the job. Provide:\n");
        prompt.append("1. Detailed match scores for each criteria\n");
        prompt.append("2. Identify missing keywords from the JD\n");
        prompt.append("3. Suggest specific improvements to increase match score\n");

        return prompt.toString();
    }

    /**
     * Helper: Capitalize first letter
     */
    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    // ========================================
    // HELPER METHODS - Format, Parse, Detect
    // ========================================

    /**
     * Format CV into readable text for analysis
     */
    private String handleFormatCVForAnalysis(CVDto cvDto) {
        StringBuilder sb = new StringBuilder();

        // Personal Info
        PersonalInfoDto pi = cvDto.getPersonalInfo();
        if (pi != null) {
            sb.append("=== PERSONAL INFORMATION ===\n");
            sb.append("Name: ").append(pi.getFullname()).append("\n");
            sb.append("Email: ").append(pi.getEmail()).append("\n");
            sb.append("Phone: ").append(pi.getPhone()).append("\n");
            sb.append("Location: ").append(pi.getLocation()).append("\n");
            sb.append("\n");

            if (pi.getSummary() != null && !pi.getSummary().isEmpty()) {
                sb.append("=== PROFESSIONAL SUMMARY ===\n");
                sb.append(pi.getSummary()).append("\n\n");
            }
        }

        // Work Experience
        List<ExperienceDto> exps = cvDto.getExperiences();
        if (exps != null && !exps.isEmpty()) {
            sb.append("=== WORK EXPERIENCE ===\n");
            for (ExperienceDto exp : exps) {
                sb.append("• ").append(exp.getPosition())
                        .append(" at ").append(exp.getCompany())
                        .append("\n  ")
                        .append(exp.getStartDate()).append(" - ")
                        .append(exp.getEndDate()).append("\n");
                if (exp.getDescription() != null) {
                    sb.append("  ").append(exp.getDescription()).append("\n");
                }
                sb.append("\n");
            }
        }

        // Education
        List<EducationDto> edus = cvDto.getEducations();
        if (edus != null && !edus.isEmpty()) {
            sb.append("=== EDUCATION ===\n");
            for (EducationDto edu : edus) {
                sb.append("• ").append(edu.getDegree());
                if (edu.getField() != null) {
                    sb.append(" in ").append(edu.getField());
                }
                sb.append("\n  ").append(edu.getSchool())
                        .append(" (").append(edu.getStartDate())
                        .append(" - ").append(edu.getEndDate()).append(")\n\n");
            }
        }

        // Skills
        List<String> skills = cvDto.getSkills();
        if (skills != null && !skills.isEmpty()) {
            sb.append("=== SKILLS ===\n");
            sb.append(String.join(", ", skills)).append("\n");
        }

        return sb.toString();
    }

    /**
     * Format experiences list into text
     */
    private String formatExperiences(List<ExperienceDto> experiences) {
        if (experiences == null || experiences.isEmpty()) {
            return "";
        }
        return experiences.stream()
                .map(exp -> String.format("%s at %s: %s",
                        exp.getPosition(),
                        exp.getCompany(),
                        exp.getDescription() != null ? exp.getDescription() : ""))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Extract JD text from file or use provided text
     */
    private String handleExtractJobDescriptionText(
            MultipartFile jdFile,
            String jobDescription) {

        if (jdFile == null || jdFile.isEmpty()) {
            return jobDescription;
        }

        try {
            return fileParserService.extractTextFromFile(jdFile);
        } catch (Exception ex) {
            log.error("Error extracting JD file: {}", ex.getMessage());
            return jobDescription;
        }
    }

    /**
     * Parse AI response into AnalyzeResultDto
     */
    private AnalyzeResultDto handleParseAnalyzeResult(String aiResponse) {
        try {
            String jsonContent = handleExtractJsonFromResponse(aiResponse);
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            JsonNode analysisNode = rootNode.has("analysis")
                    ? rootNode.get("analysis")
                    : rootNode;

            return handleParseAnalyzeResultFromNode(analysisNode);

        } catch (Exception e) {
            log.error("Error parsing analyze result: {}", e.getMessage(), e);
            // Return empty result on error
            return AnalyzeResultDto.builder()
                    .suggestions(new ArrayList<>())
                    .strengths(new ArrayList<>())
                    .weaknesses(new ArrayList<>())
                    .build();
        }
    }

    /**
     * Parse analyze result from JsonNode
     */
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
                            .id(node.has("id")
                                    ? node.get("id").asText()
                                    : UUID.randomUUID().toString())
                            .type(node.has("type")
                                    ? node.get("type").asText()
                                    : "improvement")
                            .section(node.has("section")
                                    ? node.get("section").asText()
                                    : "general")
                            .message(node.has("message")
                                    ? node.get("message").asText()
                                    : "")
                            .suggestion(node.has("suggestion")
                                    ? node.get("suggestion").asText()
                                    : "")
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
            log.error("Error parsing analyze result from node: {}", e.getMessage(), e);
            return AnalyzeResultDto.builder()
                    .suggestions(new ArrayList<>())
                    .strengths(new ArrayList<>())
                    .weaknesses(new ArrayList<>())
                    .build();
        }
    }

    /**
     * Extract JSON from AI response (remove markdown, etc.)
     */
    private String handleExtractJsonFromResponse(String response) {
        String trimmed = response.trim();

        // Check if wrapped in markdown code block
        if (trimmed.startsWith("```json") || trimmed.startsWith("```")) {
            int start = trimmed.indexOf('{');
            int end = trimmed.lastIndexOf('}');
            if (start != -1 && end != -1 && end > start) {
                return trimmed.substring(start, end + 1);
            }
        }

        // Try to find JSON object
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return trimmed.substring(start, end + 1);
        }

        return trimmed;
    }

    // ========================================
    // DETECTION METHODS - Auto-detect Category & Level
    // ========================================

    /**
     * Auto-detect CV category from full CV content
     */
    private String detectCategory(CVDto cv) {
        StringBuilder allText = new StringBuilder();

        // Collect all CV text
        if (cv.getTitle() != null) {
            allText.append(cv.getTitle()).append(" ");
        }
        if (cv.getPersonalInfo() != null && cv.getPersonalInfo().getSummary() != null) {
            allText.append(cv.getPersonalInfo().getSummary()).append(" ");
        }
        if (cv.getSkills() != null) {
            allText.append(String.join(" ", cv.getSkills())).append(" ");
        }
        if (cv.getExperiences() != null) {
            for (ExperienceDto exp : cv.getExperiences()) {
                if (exp.getPosition() != null) {
                    allText.append(exp.getPosition()).append(" ");
                }
                if (exp.getDescription() != null) {
                    allText.append(exp.getDescription()).append(" ");
                }
            }
        }

        String text = allText.toString().toLowerCase();

        // Tech keywords
        if (text.contains("java") || text.contains("python") || text.contains("javascript") ||
                text.contains("developer") || text.contains("engineer") || text.contains("programmer") ||
                text.contains("software") || text.contains("web") || text.contains("mobile") ||
                text.contains("frontend") || text.contains("backend") || text.contains("fullstack") ||
                text.contains("react") || text.contains("spring") || text.contains("node") ||
                text.contains("database") || text.contains("api") || text.contains("cloud") ||
                text.contains("devops") || text.contains("aws") || text.contains("docker")) {
            return "tech";
        }

        // Marketing keywords
        if (text.contains("marketing") || text.contains("seo") || text.contains("social media") ||
                text.contains("campaign") || text.contains("brand") || text.contains("content") ||
                text.contains("advertising") || text.contains("digital marketing") ||
                text.contains("analytics") || text.contains("conversion") ||
                text.contains("growth") || text.contains("engagement")) {
            return "marketing";
        }

        // Finance keywords
        if (text.contains("finance") || text.contains("accounting") || text.contains("auditor") ||
                text.contains("financial") || text.contains("investment") || text.contains("banking") ||
                text.contains("cpa") || text.contains("cfa") || text.contains("analyst") ||
                text.contains("budget") || text.contains("tax") || text.contains("treasury")) {
            return "finance";
        }

        // Sales keywords
        if (text.contains("sales") || text.contains("business development") ||
                text.contains("account manager") || text.contains("revenue") ||
                text.contains("client relationship") || text.contains("negotiation") ||
                text.contains("quota") || text.contains("pipeline")) {
            return "sales";
        }

        // HR keywords
        if (text.contains("human resource") || text.contains("recruitment") ||
                text.contains("talent acquisition") || text.contains("hr") ||
                text.contains("recruiter") || text.contains("people operations") ||
                text.contains("onboarding") || text.contains("employee relations")) {
            return "hr";
        }

        // Default fallback
        log.debug("No specific category detected, using 'general'");
        return "general";
    }

    /**
     * Auto-detect seniority level from CV content
     */
    private String detectLevel(CVDto cv) {
        int yearsOfExperience = calculateYearsOfExperience(cv.getExperiences());

        // Analyze title and summary
        String title = cv.getTitle() != null ? cv.getTitle().toLowerCase() : "";
        String summary = cv.getPersonalInfo() != null &&
                cv.getPersonalInfo().getSummary() != null
                        ? cv.getPersonalInfo().getSummary().toLowerCase()
                        : "";

        // Check for explicit senior indicators
        if (title.contains("senior") || title.contains("lead") ||
                title.contains("principal") || title.contains("architect") ||
                title.contains("manager") || title.contains("director") ||
                summary.contains("senior") || summary.contains("lead") ||
                summary.contains("10+ years") || summary.contains("8+ years")) {
            return "senior";
        }

        // Check for junior indicators
        if (title.contains("junior") || title.contains("intern") ||
                title.contains("entry") || title.contains("fresher") ||
                summary.contains("junior") || summary.contains("entry level") ||
                summary.contains("1 year") || summary.contains("recent graduate")) {
            return "junior";
        }

        // Determine by years of experience
        if (yearsOfExperience >= 5) {
            log.debug("Detected senior level ({} years experience)", yearsOfExperience);
            return "senior";
        } else if (yearsOfExperience >= 2) {
            log.debug("Detected mid level ({} years experience)", yearsOfExperience);
            return "mid";
        } else {
            log.debug("Detected junior level ({} years experience)", yearsOfExperience);
            return "junior";
        }
    }

    /**
     * Calculate total years of experience from experience list
     */
    private int calculateYearsOfExperience(List<ExperienceDto> experiences) {
        if (experiences == null || experiences.isEmpty()) {
            return 0;
        }

        int totalMonths = 0;
        for (ExperienceDto exp : experiences) {
            try {
                String startDate = exp.getStartDate(); // Format: "YYYY-MM"
                String endDate = exp.getEndDate(); // Format: "YYYY-MM" or "Present"

                if (startDate != null && !startDate.isEmpty()) {
                    String[] startParts = startDate.split("-");
                    int startYear = Integer.parseInt(startParts[0]);
                    int startMonth = startParts.length > 1
                            ? Integer.parseInt(startParts[1])
                            : 1;

                    int endYear, endMonth;
                    if (endDate == null || endDate.isEmpty() ||
                            endDate.equalsIgnoreCase("present") ||
                            endDate.equalsIgnoreCase("current")) {
                        // Current position
                        java.time.LocalDate now = java.time.LocalDate.now();
                        endYear = now.getYear();
                        endMonth = now.getMonthValue();
                    } else {
                        String[] endParts = endDate.split("-");
                        endYear = Integer.parseInt(endParts[0]);
                        endMonth = endParts.length > 1
                                ? Integer.parseInt(endParts[1])
                                : 12;
                    }

                    int months = (endYear - startYear) * 12 + (endMonth - startMonth);
                    totalMonths += Math.max(0, months);
                }
            } catch (Exception e) {
                log.warn("Error calculating experience duration: {}", e.getMessage());
            }
        }

        return totalMonths / 12;
    }

    /**
     * Detect category from text snippet (for improve CV)
     */
    private String detectCategoryFromText(String content) {
        if (content == null || content.isEmpty()) {
            return "general";
        }

        String text = content.toLowerCase();

        // Tech
        if (text.contains("java") || text.contains("python") ||
                text.contains("javascript") || text.contains("developer") ||
                text.contains("software") || text.contains("api") ||
                text.contains("database") || text.contains("frontend") ||
                text.contains("backend")) {
            return "tech";
        }

        // Marketing
        if (text.contains("marketing") || text.contains("campaign") ||
                text.contains("brand") || text.contains("seo") ||
                text.contains("social media")) {
            return "marketing";
        }

        // Finance
        if (text.contains("finance") || text.contains("accounting") ||
                text.contains("investment") || text.contains("financial") ||
                text.contains("audit")) {
            return "finance";
        }

        return "general";
    }

    /**
     * Detect level from text snippet (for improve CV)
     */
    private String detectLevelFromText(String content) {
        if (content == null || content.isEmpty()) {
            return "mid";
        }

        String text = content.toLowerCase();

        // Senior indicators
        if (text.contains("senior") || text.contains("lead") ||
                text.contains("10+ years") || text.contains("8+ years") ||
                text.contains("architect") || text.contains("principal") ||
                text.contains("manager") || text.contains("director")) {
            return "senior";
        }

        // Junior indicators
        if (text.contains("junior") || text.contains("entry") ||
                text.contains("1 year") || text.contains("intern") ||
                text.contains("fresher") || text.contains("recent graduate")) {
            return "junior";
        }

        // Check for years pattern using regex
        if (text.matches(".*\\b([5-9]|\\d{2})\\+?\\s*years?\\b.*")) {
            return "senior";
        }

        if (text.matches(".*\\b[2-4]\\+?\\s*years?\\b.*")) {
            return "mid";
        }

        // Default to mid
        return "mid";
    }
}