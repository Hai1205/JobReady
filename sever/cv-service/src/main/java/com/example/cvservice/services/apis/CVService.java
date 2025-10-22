package com.example.cvservice.services.apis;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.example.cvservice.configs.OpenRouterConfig;
import com.example.cvservice.dtos.*;
import com.example.cvservice.dtos.requests.*;
import com.example.cvservice.dtos.responses.*;
import com.example.cvservice.entities.*;
import com.example.cvservice.exceptions.OurException;
import com.example.cvservice.mappers.*;
import com.example.cvservice.repositoryies.*;
import com.example.cvservice.services.CloudinaryService;
import com.example.cvservice.services.FileParserService;
import com.example.cvservice.services.JobDescriptionParserService;
import com.example.cvservice.services.producers.UserProducer;
import com.example.cvservice.services.utils.PromptBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class CVService extends BaseService {

    private static final Logger log = LoggerFactory.getLogger(CVService.class);

    private final CVRepository cvRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final PersonalInfoRepository personalInfoRepository;
    private final FileParserService fileParserService;
    private final JobDescriptionParserService jobDescriptionParserService;
    private final OpenRouterConfig openRouterConfig;
    private final PromptBuilder promptBuilder;
    private final CVMapper cvMapper;
    private final ObjectMapper objectMapper;
    private final CloudinaryService cloudinaryService;
    private final UserProducer userProducer;

    public CVService(
            CVRepository cvRepository,
            EducationRepository educationRepository,
            ExperienceRepository experienceRepository,
            PersonalInfoRepository personalInfoRepository,
            FileParserService fileParserService,
            JobDescriptionParserService jobDescriptionParserService,
            OpenRouterConfig openRouterConfig,
            PromptBuilder promptBuilder,
            CloudinaryService cloudinaryService,
            CVMapper cvMapper,
            UserProducer userProducer) {
        this.cvRepository = cvRepository;
        this.educationRepository = educationRepository;
        this.experienceRepository = experienceRepository;
        this.personalInfoRepository = personalInfoRepository;
        this.fileParserService = fileParserService;
        this.jobDescriptionParserService = jobDescriptionParserService;
        this.openRouterConfig = openRouterConfig;
        this.promptBuilder = promptBuilder;
        this.cvMapper = cvMapper;
        this.cloudinaryService = cloudinaryService;
        this.userProducer = userProducer;
        this.objectMapper = new ObjectMapper();
    }

    public CVDto handleGetCVById(UUID cvId) {
        log.debug("Fetching CV by id={}", cvId);
        CV cv = cvRepository.findById(cvId).orElseThrow(() -> new OurException("CV not found", 404));
        log.debug("Found CV id={} userId={}", cv.getId(), cv.getUserId());
        return cvMapper.toDto(cv);
    }

    public CVDto handleCreateCV(
            UUID userId,
            String title,
            PersonalInfoDto personalInfoDto,
            List<ExperienceDto> experiencesDto,
            List<EducationDto> educationsDto,
            List<String> skills) {
        log.info("Creating CV for userId={} title='{}' experiencesCount={} educationsCount={}", userId, title,
                experiencesDto == null ? 0 : experiencesDto.size(), educationsDto == null ? 0 : educationsDto.size());
        UserDto user = userProducer.findUserById(userId);
        if (user == null) {
            log.warn("User not found when creating CV: userId={}", userId);
            throw new OurException("User not found", 404);
        }

        if (personalInfoDto == null) {
            throw new OurException("Personal info is required", 400);
        }

        if (experiencesDto == null || experiencesDto.isEmpty()) {
            throw new OurException("At least one experience is required", 400);
        }

        if (educationsDto == null || educationsDto.isEmpty()) {
            throw new OurException("At least one education is required", 400);
        }

        CV cv = new CV(userId, title, skills);

        PersonalInfo personalInfo = new PersonalInfo(personalInfoDto);
        if (personalInfoDto.getAvatar() != null && !personalInfoDto.getAvatar().isEmpty()) {
            var uploadResult = cloudinaryService.uploadImage(personalInfoDto.getAvatar());
            if (uploadResult.containsKey("error")) {
                throw new RuntimeException("Failed to upload avatar: " + uploadResult.get("error"));
            }

            personalInfo.setAvatarUrl((String) uploadResult.get("url"));
            personalInfo.setAvatarPublicId((String) uploadResult.get("publicId"));
        }
        personalInfoRepository.save(personalInfo);
        log.debug("Saved personal info for CV (email={})", personalInfo.getEmail());
        cv.setPersonalInfo(personalInfo);

        List<Experience> experiences = experiencesDto.stream()
                .map(Experience::new)
                .peek(experienceRepository::save)
                .collect(Collectors.toList());

        List<Education> educations = educationsDto.stream()
                .map(Education::new)
                .peek(educationRepository::save)
                .collect(Collectors.toList());

        cv.setExperiences(experiences);
        cv.setEducations(educations);

        CV savedCV = cvRepository.save(cv);
        log.info("Created CV id={} for userId={}", savedCV.getId(), savedCV.getUserId());
        return cvMapper.toDto(savedCV);
    }

    public Response createCV(UUID userId, String dataJson, MultipartFile avatar) {
        Response response = new Response();

        try {
            CreateCVRequest request = objectMapper.readValue(dataJson, CreateCVRequest.class);
            String title = request.getTitle();
            PersonalInfoDto personalInfo = request.getPersonalInfo();
            personalInfo.setAvatar(avatar);
            List<ExperienceDto> experiences = request.getExperiences();
            List<EducationDto> educations = request.getEducations();
            List<String> skills = request.getSkills();

            log.info("Received createCV request for userId={}", userId);
            CVDto cvDto = handleCreateCV(
                    userId,
                    title,
                    personalInfo,
                    experiences,
                    educations,
                    skills);

            response.setStatusCode(201);
            response.setMessage("CV created successfully");
            response.setCv(cvDto);
            log.debug("createCV response prepared for userId={} cvId={}", userId, cvDto.getId());
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, "Error creating CV: " + e.getMessage());
        }
    }

    public List<CVDto> handleGetAllCVs() {
        return cvRepository.findAll().stream()
                .map(cvMapper::toDto)
                .collect(Collectors.toList());
    }

    public Response getAllCVs() {
        Response response = new Response();

        try {
            List<CVDto> cvDtos = handleGetAllCVs();

            response.setMessage("Get all cvs successfully");
            response.setCvs(cvDtos);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response getCVById(UUID cvId) {
        Response response = new Response();

        try {
            CVDto cvDto = handleGetCVById(cvId);

            response.setMessage("Get cv successfully");
            response.setCv(cvDto);
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
            CreateCVRequest request = objectMapper.readValue(dataJson, CreateCVRequest.class);
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

            log.info("Analyzing CV with title={}", title);

            // Use optimized GPT-5 prompt from PromptBuilder
            String systemPrompt = promptBuilder.buildCVAnalysisPrompt();

            String cvContent = handleFormatCVForAnalysis(cvDto);
            String prompt = "Analyze this CV:\n\n" + cvContent;
            String result = openRouterConfig.callModelWithSystemPrompt(systemPrompt, prompt);
            List<AISuggestionDto> suggestions = handleParseSuggestionsFromAIResponse(result);

            response.setMessage("CV analyzed successfully");
            response.setAnalyze(result);
            response.setSuggestions(suggestions);
            log.debug("Analysis completed for CV title={} suggestionsCount={}", title,
                    suggestions == null ? 0 : suggestions.size());
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response improveCV(String dataJson) {
        Response response = new Response();

        try {
            ImproveCVRequest request = objectMapper.readValue(dataJson, ImproveCVRequest.class);
            String section = request.getSection();
            String content = request.getContent();

            // Use optimized GPT-5 prompt from PromptBuilder
            // For now, we use "General position" as default. Future: extract from request
            String systemPrompt = promptBuilder.buildCVImprovementPrompt(
                    section,
                    "General position",
                    List.of() // Empty requirements - can be enhanced later
            );

            String prompt = String.format(
                    "Improve the following %s section of a CV:\n\n%s\n\nProvide only the improved version without explanations.",
                    section, content);
            String improved = openRouterConfig.callModelWithSystemPrompt(systemPrompt, prompt);

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

    public List<CVDto> handleGetUserCVs(UUID userId) {
        UserDto user = userProducer.findUserById(userId);
        if (user == null) {
            throw new OurException("User not found", 404);
        }

        List<CV> cvs = cvRepository.findAllByUserId(userId);
        return cvs.stream().map(cvMapper::toDto).collect(Collectors.toList());
    }

    public Response getUserCVs(UUID userId) {
        Response response = new Response();

        try {
            List<CVDto> userCVs = handleGetUserCVs(userId);

            response.setMessage("Get user's CVs successfully");
            response.setCvs(userCVs);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response importFile(UUID userId, MultipartFile file) {
        Response response = new Response();

        try {
            UserDto user = userProducer.findUserById(userId);
            if (user == null) {
                log.warn("User not found while importing file: userId={}", userId);
                throw new OurException("User not found", 404);
            }

            // Extract text from file
            log.info("Importing CV file for userId={} filename={}", userId,
                    file == null ? "<null>" : file.getOriginalFilename());
            String extractedText = fileParserService.extractTextFromFile(file);

            // Use AI to parse the CV content into structured data
            String systemPrompt = "You are an expert CV parser. Extract structured information from the CV text and return it as JSON with this exact structure: {\"title\": \"<job title or 'My CV'>\", \"personalInfo\": {\"fullname\": \"<name>\", \"email\": \"<email>\", \"phone\": \"<phone>\", \"location\": \"<location>\", \"summary\": \"<professional summary>\"}, \"experience\": [{\"company\": \"<company>\", \"position\": \"<position>\", \"startDate\": \"<YYYY-MM>\", \"endDate\": \"<YYYY-MM or Present>\", \"description\": \"<description>\"}], \"education\": [{\"school\": \"<school>\", \"degree\": \"<degree>\", \"field\": \"<field>\", \"startDate\": \"<YYYY-MM>\", \"endDate\": \"<YYYY-MM>\"}], \"skills\": [\"<skill1>\", \"<skill2>\"]}";
            String prompt = "Parse this CV text into structured JSON format:\n\n" + extractedText;
            String aiResponse = openRouterConfig.callModelWithSystemPrompt(systemPrompt, prompt);
            // Parse AI response and create CV
            CVDto cvDto = handleParseAndCreateCVFromAIResponse(userId, aiResponse);

            response.setStatusCode(201);
            response.setMessage("CV imported successfully");
            response.setCv(cvDto);
            response.setExtractedText(extractedText);
            log.info("Imported CV for userId={} created cvId={}", userId, cvDto.getId());
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response getCVByTitle(String title) {
        Response response = new Response();

        try {
            Optional<CV> cvOpt = cvRepository.findByTitle(title);

            if (!cvOpt.isPresent()) {
                throw new OurException("CV not found with title: " + title, 404);
            }

            CV cv = cvOpt.get();
            CVDto cvDto = cvMapper.toDto(cv);

            response.setMessage("Get cv successfully");
            response.setCv(cvDto);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public CVDto handleUpdateCV(UUID cvId,
            String title,
            PersonalInfoDto personalInfoDto,
            List<ExperienceDto> experiencesDto,
            List<EducationDto> educationsDto,
            List<String> skills) {
        CV existing = cvRepository.findById(cvId)
                .orElseThrow(() -> new OurException("CV not found", 404));

        existing.setTitle(title);

        if (personalInfoDto != null) {
            PersonalInfo pi = existing.getPersonalInfo();

            if (pi == null)
                pi = new PersonalInfo();

            pi.setFullname(personalInfoDto.getFullname());
            pi.setEmail(personalInfoDto.getEmail());
            pi.setPhone(personalInfoDto.getPhone());
            pi.setLocation(personalInfoDto.getLocation());
            pi.setSummary(personalInfoDto.getSummary());

            if (personalInfoDto.getAvatar() != null && !personalInfoDto.getAvatar().isEmpty()) {
                String oldAvatarPublicId = pi.getAvatarPublicId();
                if (oldAvatarPublicId != null && !oldAvatarPublicId.isEmpty()) {
                    cloudinaryService.deleteImage(oldAvatarPublicId);
                }

                var uploadResult = cloudinaryService.uploadImage(personalInfoDto.getAvatar());
                if (uploadResult.containsKey("error")) {
                    throw new RuntimeException("Failed to upload avatar: " + uploadResult.get("error"));
                }

                pi.setAvatarUrl((String) uploadResult.get("url"));
                pi.setAvatarPublicId((String) uploadResult.get("publicId"));
            }

            existing.setPersonalInfo(pi);
        }

        existing.getExperiences().clear();
        if (experiencesDto != null) {
            for (ExperienceDto e : experiencesDto) {
                Experience ex = new Experience(e);
                existing.getExperiences().add(ex);
            }
        }

        existing.getEducations().clear();
        if (educationsDto != null) {
            for (EducationDto ed : educationsDto) {
                Education e = new Education(ed);
                existing.getEducations().add(e);
            }
        }

        existing.getSkills().clear();
        if (skills != null)
            existing.getSkills().addAll(skills);

        existing.setUpdatedAt(Instant.now());

        CV saved = cvRepository.save(existing);
        return cvMapper.toDto(saved);
    }

    public Response updateCV(UUID cvId, String dataJson, MultipartFile avatar) {
        Response response = new Response();

        try {
            UpdateCVRequest request = objectMapper.readValue(dataJson, UpdateCVRequest.class);
            String title = request.getTitle();
            PersonalInfoDto personalInfo = request.getPersonalInfo();
            personalInfo.setAvatar(avatar);
            List<ExperienceDto> experiences = request.getExperiences();
            List<EducationDto> educations = request.getEducations();
            List<String> skills = request.getSkills();

            CVDto cvDto = handleUpdateCV(
                    cvId,
                    title,
                    personalInfo,
                    experiences,
                    educations,
                    skills);

            response.setMessage("CV updated successfully");
            response.setCv(cvDto);
            log.info("Updated CV id={} (userId={})", cvId, cvDto.getUserId());
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public boolean handleDeleteCV(UUID cvId) {
        handleGetCVById(cvId);

        cvRepository.deleteById(cvId);
        return true;
    }

    public CVDto handleDuplicateCV(UUID cvId) {
        log.info("Duplicating CV id={}", cvId);
        CVDto existingCV = handleGetCVById(cvId);

        CVDto newCV = handleCreateCV(
                existingCV.getUserId(),
                existingCV.getTitle() + " (Copy)",
                existingCV.getPersonalInfo(),
                existingCV.getExperiences(),
                existingCV.getEducations(),
                existingCV.getSkills());

        log.info("Duplicated CV id={} created new CV id={}", cvId, newCV.getId());
        return newCV;
    }

    public Response deleteCV(UUID cvId) {
        Response response = new Response();

        try {
            handleDeleteCV(cvId);

            response.setMessage("CV deleted successfully");
            log.info("Deleted CV id={}", cvId);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response duplicateCV(UUID cvId) {
        Response response = new Response();

        try {
            CVDto duplicatedCV = handleDuplicateCV(cvId);

            response.setMessage("CV duplicated successfully");
            response.setCv(duplicatedCV);
            log.info("Duplicated CV id={}", cvId);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response analyzeCVWithJobDescription(String dataJson) {
        Response response = new Response();

        try {
            AnalyzeCVWithJDRequest request = objectMapper.readValue(dataJson, AnalyzeCVWithJDRequest.class);
            String language = request.getLanguage();
            MultipartFile jdFile = request.getJdFile();
            String jobDescription = request.getJobDescription();

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

            String jdText = handleExtractJobDescriptionText(jdFile, jobDescription);

            String cvContent = handleFormatCVForAnalysis(cvDto);

            // Use optimized GPT-5 prompt from PromptBuilder
            String systemPrompt = promptBuilder.buildJobMatchPrompt(language != null ? language : "vi");
            String userPrompt = handleBuildUserPrompt(jdText, cvContent);

            String aiResponse = openRouterConfig.callModelWithSystemPrompt(systemPrompt, userPrompt);
            String jsonContent = handleExtractJsonFromResponse(aiResponse);

            JobDescriptionResult jdResult = handleTryParseJobDescription(jsonContent);
            Double matchScore = null;
            List<String> missingKeywords = new ArrayList<>();

            JsonNode root = objectMapper.readTree(jsonContent);
            if (root.has("matchScore")) {
                matchScore = root.get("matchScore").asDouble();
            }
            if (root.has("missingKeywords") && root.get("missingKeywords").isArray()) {
                for (JsonNode n : root.get("missingKeywords")) {
                    missingKeywords.add(n.asText());
                }
            }

            response.setMessage("CV analyzed with job description successfully");
            response.setParsedJobDescription(jdResult);
            response.setAnalyze(aiResponse);
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
            // Try to extract JSON from the response
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
            // Return empty list if parsing fails
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

    private CVDto handleParseAndCreateCVFromAIResponse(UUID userId, String aiResponse) {
        try {
            String jsonContent = handleExtractJsonFromResponse(aiResponse);
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            // Extract data from JSON
            String title = rootNode.has("title") ? rootNode.get("title").asText() : "Imported CV";

            // Personal Info
            PersonalInfoDto personalInfo = new PersonalInfoDto();
            if (rootNode.has("personalInfo")) {
                JsonNode piNode = rootNode.get("personalInfo");
                personalInfo.setFullname(piNode.has("fullname") ? piNode.get("fullname").asText() : "");
                personalInfo.setEmail(piNode.has("email") ? piNode.get("email").asText() : "");
                personalInfo.setPhone(piNode.has("phone") ? piNode.get("phone").asText() : "");
                personalInfo.setLocation(piNode.has("location") ? piNode.get("location").asText() : "");
                personalInfo.setSummary(piNode.has("summary") ? piNode.get("summary").asText() : "");
            }

            // Experience
            List<ExperienceDto> experiences = new ArrayList<>();
            if (rootNode.has("experience")) {
                JsonNode expNode = rootNode.get("experience");
                if (expNode.isArray()) {
                    for (JsonNode node : expNode) {
                        ExperienceDto exp = new ExperienceDto();
                        exp.setCompany(node.has("company") ? node.get("company").asText() : "");
                        exp.setPosition(node.has("position") ? node.get("position").asText() : "");
                        exp.setStartDate(node.has("startDate") ? node.get("startDate").asText() : "");
                        exp.setEndDate(node.has("endDate") ? node.get("endDate").asText() : "");
                        exp.setDescription(node.has("description") ? node.get("description").asText() : "");
                        experiences.add(exp);
                    }
                }
            }

            // Education
            List<EducationDto> educations = new ArrayList<>();
            if (rootNode.has("education")) {
                JsonNode eduNode = rootNode.get("education");
                if (eduNode.isArray()) {
                    for (JsonNode node : eduNode) {
                        EducationDto edu = new EducationDto();
                        edu.setSchool(node.has("school") ? node.get("school").asText() : "");
                        edu.setDegree(node.has("degree") ? node.get("degree").asText() : "");
                        edu.setField(node.has("field") ? node.get("field").asText() : "");
                        edu.setStartDate(node.has("startDate") ? node.get("startDate").asText() : "");
                        edu.setEndDate(node.has("endDate") ? node.get("endDate").asText() : "");
                        educations.add(edu);
                    }
                }
            }

            // Skills
            List<String> skills = new ArrayList<>();
            if (rootNode.has("skills")) {
                JsonNode skillsNode = rootNode.get("skills");
                if (skillsNode.isArray()) {
                    for (JsonNode node : skillsNode) {
                        skills.add(node.asText());
                    }
                }
            }

            // Create CV
            return handleCreateCV(userId, title, personalInfo, experiences, educations, skills);

        } catch (Exception e) {
            throw new OurException("Error parsing AI response: " + e.getMessage(), 500);
        }
    }
}