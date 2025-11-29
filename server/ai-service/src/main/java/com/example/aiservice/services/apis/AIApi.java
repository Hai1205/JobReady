package com.example.aiservice.services.apis;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.aiservice.configs.OpenRouterConfig;
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

@Service
public class AIApi extends BaseApi {
    private final OpenRouterConfig openRouterConfig;
    private final EnhancedEmbeddingService embeddingService;
    private final PromptBuilderService promptBuilderService;
    private final ObjectMapper objectMapper;
    private final FileParserService fileParserService;
    private final CVFeignClient cvFeignClient;

    public AIApi(
            OpenRouterConfig openRouterConfig,
            EnhancedEmbeddingService embeddingService,
            PromptBuilderService promptBuilderService,
            FileParserService fileParserService,
            CVFeignClient cvFeignClient) {
        this.openRouterConfig = openRouterConfig;
        this.embeddingService = embeddingService;
        this.promptBuilderService = promptBuilderService;
        this.objectMapper = new ObjectMapper();
        this.fileParserService = fileParserService;
        this.cvFeignClient = cvFeignClient;
    }

    public Response improveCV(String dataJson) {
        Response response = new Response();

        try {
            ImproveCVRequest request = objectMapper.readValue(dataJson, ImproveCVRequest.class);
            String section = request.getSection();
            String content = request.getContent();

             String category = detectCategoryFromText(content);
            String level = detectLevelFromText(content);

            logger.info("Improving CV section: {}, auto-detected: category={}, level={}", 
                section, category, level);

            AIResponseDto aiResponse = handleImproveCV(section, content, category, level);
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

                    String category = detectCategory(cvDto);
            String level = detectLevel(cvDto);

            logger.info("Analyzing CV with title={}, category={}, level={}", title, category, level);

            AIResponseDto aiResponse = handleAnalyzeCV(cvDto, category, level);
            AnalyzeResultDto analyzeResult = aiResponse.getAnalyzeResult();
            List<AISuggestionDto> suggestions = analyzeResult.getSuggestions();

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
            if (jdFile != null && !jdFile.isEmpty()) {
                fileParserService.validateDocumentFile(jdFile);
            }

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

    public Response importCV(UUID userId, MultipartFile file) {
        Response response = new Response();

        try {
            fileParserService.validatePDFFile(file);

            String cvText = fileParserService.extractTextFromFile(file);
            logger.debug("Extracted CV Text: " + cvText);

            CVDto cvDto = fileParserService.parseCVWithAI(cvText);
            logger.debug("Extract CV" + cvDto);

            CVCreateRequest cvCreateRequest = CVCreateRequest.builder()
                    .title(cvDto.getTitle())
                    .personalInfo(cvDto.getPersonalInfo())
                    .experiences(cvDto.getExperiences())
                    .educations(cvDto.getEducations())
                    .skills(cvDto.getSkills())
                    .build();

            String dataJson = objectMapper.writeValueAsString(cvCreateRequest);

            Response createCVResponse = cvFeignClient.importCV(
                    userId.toString(),
                    dataJson);

            CVDto savedCV = createCVResponse.getCv();
            logger.debug("Saved CV" + savedCV);

            response.setStatusCode(201);
            response.setMessage("CV created successfully");
            response.setCv(savedCV);
            logger.debug("createCV response prepared for userId={} cvId={}", userId,
                    savedCV.getId());
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, "Error creating CV: " + e.getMessage());
        }
    }

    private String handleExtractJobDescriptionText(MultipartFile jdFile, String jobDescription) {
        if (jdFile == null || jdFile.isEmpty()) {
            return jobDescription;
        }

        try {
            return fileParserService.extractTextFromFile(jdFile);
        } catch (Exception ex) {
            System.err.println("Error extracting JD file: " + ex.getMessage());
            return jobDescription;
        }
    }

    public AIResponseDto handleAnalyzeCV(CVDto cv, String category, String level) {
        try {
            String systemPrompt = promptBuilderService.buildCVAnalysisPrompt();
            String cvContent = handleFormatCVForAnalysis(cv);

            // RETRIEVE relevant examples từ knowledge base
            List<org.springframework.ai.document.Document> summaryExamples = embeddingService.searchRelevantTemplates(
                    cv.getPersonalInfo() != null ? cv.getPersonalInfo().getSummary() : "",
                    "summary",
                    category,
                    level,
                    2);

            List<org.springframework.ai.document.Document> experienceExamples = embeddingService
                    .searchRelevantTemplates(
                            formatExperiences(cv.getExperiences()),
                            "experience",
                            category,
                            level,
                            3);

            // AUGMENT prompt
            String augmentedPrompt = buildAugmentedAnalysisPrompt(
                    cvContent,
                    summaryExamples,
                    experienceExamples);

            logger.debug("Augmented prompt length: {} chars", augmentedPrompt.length());

            // GENERATE
            String aiResponseText = openRouterConfig.callModelWithSystemPrompt(
                    systemPrompt,
                    augmentedPrompt);

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

    public AIResponseDto handleImproveCV(String section, String content, String category, String level) {
        try {
            String systemPrompt = promptBuilderService.buildCVImprovementPrompt(
                    section, "General position", List.of());

            // RETRIEVE best examples
            List<org.springframework.ai.document.Document> examples = embeddingService.searchRelevantTemplates(
                    content,
                    section,
                    category,
                    level,
                    3);

            // AUGMENT prompt
            String augmentedPrompt = buildAugmentedImprovementPrompt(
                    section,
                    content,
                    examples);

            // GENERATE
            String improved = openRouterConfig.callModelWithSystemPrompt(
                    systemPrompt,
                    augmentedPrompt);

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
            
            // AI TỰ ĐỘNG PHÁN ĐOÁN category và level
            String category = detectCategory(cv);
            String level = detectLevel(cv);
            
            logger.info("Analyzing CV with JD, auto-detected: category={}, level={}", category, level);

            // RETRIEVE relevant JD examples từ knowledge base
            // Tìm các JD templates tương tự để AI có context về format JD chuẩn
            List<org.springframework.ai.document.Document> jdExamples = 
                embeddingService.searchRelevantTemplates(
                    jdText,
                    "job_description", // Section mới cho JD templates
                    category,
                    level,
                    2 // Top 2 JD examples
                );
            
            // RETRIEVE CV templates để so sánh
            List<org.springframework.ai.document.Document> cvExamples = 
                embeddingService.searchRelevantTemplates(
                    cvContent,
                    "summary", // Lấy summary examples
                    category,
                    level,
                    2
                );

            String systemPrompt = promptBuilderService.buildJobMatchPrompt(language != null ? language : "vi");
            
            // AUGMENT prompt với examples
            String augmentedPrompt = buildAugmentedJDMatchPrompt(
                jdText,
                cvContent,
                jdExamples,
                cvExamples
            );
            
            logger.debug("Augmented JD match prompt length: {} chars", augmentedPrompt.length());

            String aiResponseText = openRouterConfig.callModelWithSystemPrompt(systemPrompt, augmentedPrompt);

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

    /**
     * Build augmented prompt cho JD matching
     */
    private String buildAugmentedJDMatchPrompt(
            String jdText,
            String cvContent,
            List<org.springframework.ai.document.Document> jdExamples,
            List<org.springframework.ai.document.Document> cvExamples) {
        
        StringBuilder prompt = new StringBuilder();
        
        // Add JD examples nếu có
        if (!jdExamples.isEmpty()) {
            prompt.append("=== EXAMPLE JOB DESCRIPTIONS (for reference) ===\n\n");
            for (int i = 0; i < jdExamples.size(); i++) {
                org.springframework.ai.document.Document doc = jdExamples.get(i);
                prompt.append(String.format("JD Example %d:\n%s\n\n",
                    i + 1,
                    doc.getContent()
                ));
            }
        }
        
        // Add CV examples để so sánh
        if (!cvExamples.isEmpty()) {
            prompt.append("=== HIGH-QUALITY CV EXAMPLES (for comparison) ===\n\n");
            for (int i = 0; i < cvExamples.size(); i++) {
                org.springframework.ai.document.Document doc = cvExamples.get(i);
                prompt.append(String.format("CV Example %d (Rating: %s):\n%s\n\n",
                    i + 1,
                    doc.getMetadata().get("rating"),
                    doc.getContent()
                ));
            }
        }
        
        // Add actual JD and CV
        prompt.append("=== JOB DESCRIPTION TO ANALYZE ===\n\n");
        prompt.append(jdText);
        prompt.append("\n\n=== CANDIDATE'S CV ===\n\n");
        prompt.append(cvContent);
        prompt.append("\n\nBased on the examples above and the JD requirements, analyze how well this CV matches the job. ");
        prompt.append("Provide detailed match scores, identify missing keywords, and suggest specific improvements.");
        
        return prompt.toString();
    }

    private String handleFormatCVForAnalysis(CVDto cvDto) {
        StringBuilder sb = new StringBuilder();

        // Personal Info
        PersonalInfoDto pi = cvDto.getPersonalInfo();
        if (pi != null) {
            sb.append("Name: ").append(pi.getFullname()).append("\n");
            sb.append("Email: ").append(pi.getEmail()).append("\n");
            sb.append("Phone: ").append(pi.getPhone()).append("\n");
            sb.append("Location: ").append(pi.getLocation()).append("\n");
            sb.append("\nProfessional Summary:\n").append(pi.getSummary()).append("\n");
        }

        // Experience
        List<ExperienceDto> exps = cvDto.getExperiences();
        if (exps != null && !exps.isEmpty()) {
            sb.append("\nWork Experience:\n");
            for (ExperienceDto exp : exps) {
                sb.append("- ").append(exp.getPosition()).append(" at ").append(exp.getCompany())
                        .append(" (").append(exp.getStartDate()).append(" - ").append(exp.getEndDate()).append(")\n");
                sb.append("  ").append(exp.getDescription()).append("\n");
            }
        }

        // Education
        List<EducationDto> edus = cvDto.getEducations();
        if (edus != null && !edus.isEmpty()) {
            sb.append("\nEducation:\n");
            for (EducationDto edu : edus) {
                sb.append("- ").append(edu.getDegree()).append(" in ").append(edu.getField())
                        .append(" from ").append(edu.getSchool())
                        .append(" (").append(edu.getStartDate()).append(" - ").append(edu.getEndDate()).append(")\n");
            }
        }

        // Skills
        List<String> skills = cvDto.getSkills();
        if (skills != null && !skills.isEmpty()) {
            sb.append("\nSkills:\n");
            sb.append(String.join(", ", skills)).append("\n");
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

    private String buildAugmentedAnalysisPrompt(
            String cvContent,
            List<org.springframework.ai.document.Document> summaryExamples,
            List<org.springframework.ai.document.Document> experienceExamples) {
        
        StringBuilder prompt = new StringBuilder();
        
        if (!summaryExamples.isEmpty()) {
            prompt.append("=== BEST PRACTICE EXAMPLES FOR SUMMARY ===\n\n");
            for (int i = 0; i < summaryExamples.size(); i++) {
                org.springframework.ai.document.Document doc = summaryExamples.get(i);
                prompt.append(String.format("Example %d (Rating: %s):\n%s\n\n",
i + 1,
doc.getMetadata().get("rating"),
doc.getContent()
));
}
}
if (!experienceExamples.isEmpty()) {
        prompt.append("=== BEST PRACTICE EXAMPLES FOR EXPERIENCE ===\n\n");
        for (int i = 0; i < experienceExamples.size(); i++) {
            org.springframework.ai.document.Document doc = experienceExamples.get(i);
            prompt.append(String.format("Example %d (Rating: %s):\n%s\n\n",
                i + 1,
                doc.getMetadata().get("rating"),
                doc.getContent()
            ));
        }
    }
    
    prompt.append("=== CV TO ANALYZE ===\n\n");
    prompt.append(cvContent);
    prompt.append("\n\nBased on the best practice examples above, analyze this CV and provide detailed feedback.");
    
    return prompt.toString();
}

private String buildAugmentedImprovementPrompt(
        String section,
        String content,
        List<org.springframework.ai.document.Document> examples) {
    
    StringBuilder prompt = new StringBuilder();
    
    prompt.append(String.format("=== BEST PRACTICE EXAMPLES FOR %s ===\n\n", section.toUpperCase()));
    
    for (int i = 0; i < examples.size(); i++) {
        org.springframework.ai.document.Document doc = examples.get(i);
        prompt.append(String.format("Example %d (Quality Rating: %s):\n%s\n\n",
            i + 1,
            doc.getMetadata().get("rating"),
            doc.getContent()
        ));
    }
    
    prompt.append(String.format("=== CURRENT %s TO IMPROVE ===\n\n", section.toUpperCase()));
    prompt.append(content);
    prompt.append("\n\nUsing the best practice examples above as reference, rewrite this section to be more impactful. Focus on:\n");
    prompt.append("- Strong action verbs\n");
    prompt.append("- Quantifiable metrics\n");
    prompt.append("- Clear and concise language\n");
    prompt.append("- Industry-standard formatting\n");
    
    return prompt.toString();
}

private String formatExperiences(List<ExperienceDto> experiences) {
    if (experiences == null || experiences.isEmpty()) {
        return "";
    }
    return experiences.stream()
        .map(exp -> String.format("%s at %s: %s", 
            exp.getPosition(), 
            exp.getCompany(), 
            exp.getDescription()))
        .collect(Collectors.joining("\n"));
}

private String detectCategory(CVDto cv) {
        StringBuilder allText = new StringBuilder();
        
        // Collect all text
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
                if (exp.getPosition() != null) allText.append(exp.getPosition()).append(" ");
                if (exp.getDescription() != null) allText.append(exp.getDescription()).append(" ");
            }
        }
        
        String text = allText.toString().toLowerCase();
        
        // Tech keywords
        if (text.contains("java") || text.contains("python") || text.contains("javascript") ||
            text.contains("developer") || text.contains("engineer") || text.contains("programmer") ||
            text.contains("software") || text.contains("web") || text.contains("mobile") ||
            text.contains("frontend") || text.contains("backend") || text.contains("fullstack") ||
            text.contains("react") || text.contains("spring") || text.contains("node") ||
            text.contains("database") || text.contains("api") || text.contains("cloud")) {
            return "tech";
        }
        
        // Marketing keywords
        if (text.contains("marketing") || text.contains("seo") || text.contains("social media") ||
            text.contains("campaign") || text.contains("brand") || text.contains("content") ||
            text.contains("advertising") || text.contains("digital marketing") ||
            text.contains("analytics") || text.contains("conversion")) {
            return "marketing";
        }
        
        // Finance keywords
        if (text.contains("finance") || text.contains("accounting") || text.contains("auditor") ||
            text.contains("financial") || text.contains("investment") || text.contains("banking") ||
            text.contains("cpa") || text.contains("cfa") || text.contains("analyst") ||
            text.contains("budget") || text.contains("tax")) {
            return "finance";
        }
        
        // Sales keywords
        if (text.contains("sales") || text.contains("business development") || text.contains("account manager") ||
            text.contains("revenue") || text.contains("client relationship") || text.contains("negotiation")) {
            return "sales";
        }
        
        // HR keywords
        if (text.contains("human resource") || text.contains("recruitment") || text.contains("talent acquisition") ||
            text.contains("hr") || text.contains("recruiter") || text.contains("people operations")) {
            return "hr";
        }
        
        // Default
        return "general";
    }

     private String detectLevel(CVDto cv) {
        int yearsOfExperience = calculateYearsOfExperience(cv.getExperiences());
        
        // Phân tích title và summary
        String title = cv.getTitle() != null ? cv.getTitle().toLowerCase() : "";
        String summary = cv.getPersonalInfo() != null && cv.getPersonalInfo().getSummary() != null
                ? cv.getPersonalInfo().getSummary().toLowerCase()
                : "";
        
        // Check for explicit level indicators
        if (title.contains("senior") || title.contains("lead") || title.contains("principal") ||
            title.contains("architect") || title.contains("manager") || title.contains("director") ||
            summary.contains("senior") || summary.contains("lead") || summary.contains("10+ years") ||
            summary.contains("8+ years")) {
            return "senior";
        }
        
        if (title.contains("junior") || title.contains("intern") || title.contains("entry") ||
            summary.contains("junior") || summary.contains("entry level") || summary.contains("1 year")) {
            return "junior";
        }
        
        // Dựa vào số năm kinh nghiệm
        if (yearsOfExperience >= 5) {
            return "senior";
        } else if (yearsOfExperience >= 2) {
            return "mid";
        } else {
            return "junior";
        }
    }

    private int calculateYearsOfExperience(List<ExperienceDto> experiences) {
        if (experiences == null || experiences.isEmpty()) {
            return 0;
        }
        
        int totalMonths = 0;
        for (ExperienceDto exp : experiences) {
            try {
                String startDate = exp.getStartDate(); // Format: "YYYY-MM"
                String endDate = exp.getEndDate(); // Format: "YYYY-MM" or "Present"
                
                if (startDate != null) {
                    String[] startParts = startDate.split("-");
                    int startYear = Integer.parseInt(startParts[0]);
                    int startMonth = startParts.length > 1 ? Integer.parseInt(startParts[1]) : 1;
                    
                    int endYear, endMonth;
                    if (endDate == null || endDate.equalsIgnoreCase("present") || endDate.equalsIgnoreCase("current")) {
                        java.time.LocalDate now = java.time.LocalDate.now();
                        endYear = now.getYear();
                        endMonth = now.getMonthValue();
                    } else {
                        String[] endParts = endDate.split("-");
                        endYear = Integer.parseInt(endParts[0]);
                        endMonth = endParts.length > 1 ? Integer.parseInt(endParts[1]) : 12;
                    }
                    
                    int months = (endYear - startYear) * 12 + (endMonth - startMonth);
                    totalMonths += Math.max(0, months);
                }
            } catch (Exception e) {
                logger.warn("Error calculating experience duration: {}", e.getMessage());
            }
        }
        
        return totalMonths / 12;
    }

    private String detectCategoryFromText(String content) {
        if (content == null) return "general";
        
        String text = content.toLowerCase();
        
        if (text.contains("java") || text.contains("python") || text.contains("javascript") ||
            text.contains("developer") || text.contains("software") || text.contains("api") ||
            text.contains("database") || text.contains("frontend") || text.contains("backend")) {
            return "tech";
        }
        
        if (text.contains("marketing") || text.contains("campaign") || text.contains("brand") ||
            text.contains("seo") || text.contains("social media")) {
            return "marketing";
        }
        
        if (text.contains("finance") || text.contains("accounting") || text.contains("investment") ||
            text.contains("financial") || text.contains("audit")) {
            return "finance";
        }
        
        return "general";
    }

    private String detectLevelFromText(String content) {
        if (content == null) return "mid";
        
        String text = content.toLowerCase();
        
        if (text.contains("senior") || text.contains("lead") || text.contains("10+ years") ||
            text.contains("8+ years") || text.contains("architect") || text.contains("principal")) {
            return "senior";
        }
        
        if (text.contains("junior") || text.contains("entry") || text.contains("1 year") ||
            text.contains("intern") || text.contains("fresher")) {
            return "junior";
        }
        
        // Check for years pattern
        if (text.matches(".*\\b([5-9]|\\d{2})\\+?\\s*years?\\b.*")) {
            return "senior";
        }
        
        if (text.matches(".*\\b[2-4]\\+?\\s*years?\\b.*")) {
            return "mid";
        }
        
        return "mid"; // Default
    }
}