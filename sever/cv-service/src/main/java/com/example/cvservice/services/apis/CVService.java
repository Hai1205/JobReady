package com.example.cvservice.services.apis;

import org.springframework.stereotype.Service;
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

    private final CVRepository cvRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final PersonalInfoRepository personalInfoRepository;
    private final FileParserService fileParserService;
    private final JobDescriptionParserService jobDescriptionParserService;
    private final OpenRouterConfig openRouterConfig;
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
        this.cvMapper = cvMapper;
        this.cloudinaryService = cloudinaryService;
        this.userProducer = userProducer;
        this.objectMapper = new ObjectMapper();
    }

    public CVDto handleGetCVById(UUID cvId) {
        CV cv = cvRepository.findById(cvId).orElseThrow(() -> new OurException("CV not found", 404));
        return cvMapper.toDto(cv);
    }

    public CVDto handleCreateCV(
            UUID userId,
            String title,
            PersonalInfoDto personalInfoDto,
            List<ExperienceDto> experiencesDto,
            List<EducationDto> educationsDto,
            List<String> skills) {

        UserDto user = userProducer.findUserById(userId);
        if (user == null) {
            throw new OurException("User not found", 404);
        }

        CV cv = new CV(userId, title, skills);

        PersonalInfo personalInfo = new PersonalInfo(personalInfoDto.getEmail(), personalInfoDto.getFullname(),
                personalInfoDto.getPhone(), personalInfoDto.getLocation(), personalInfoDto.getSummary());

        if (personalInfoDto.getAvatar() != null && !personalInfoDto.getAvatar().isEmpty()) {
            var uploadResult = cloudinaryService.uploadImage(personalInfoDto.getAvatar());
            if (uploadResult.containsKey("error")) {
                throw new RuntimeException("Failed to upload avatar: " + uploadResult.get("error"));
            }

            personalInfo.setAvatarUrl((String) uploadResult.get("url"));
            personalInfo.setAvatarPublicId((String) uploadResult.get("publicId"));
        }
        personalInfoRepository.save(personalInfo);

        cv.setPersonalInfo(personalInfo);

        List<Experience> experiences = experiencesDto.stream()
                .map(e -> {
                    Experience ex = new Experience(e.getCompany(), e.getPosition(), e.getStartDate(), e.getEndDate(),
                            e.getDescription());
                    experienceRepository.save(ex);
                    return ex;
                })
                .collect(Collectors.toList());

        List<Education> educations = educationsDto.stream()
                .map(e -> {
                    Education ed = new Education(e.getSchool(), e.getDegree(), e.getField(), e.getStartDate(),
                            e.getEndDate());
                    educationRepository.save(ed);
                    return ed;
                })
                .collect(Collectors.toList());

        cv.setExperience(experiences);
        cv.setEducation(educations);

        CV savedCV = cvRepository.save(cv);

        return cvMapper.toDto(savedCV);
    }

    public Response createCV(UUID userId, CreateCVRequest request) {
        Response response = new Response();

        try {
            CVDto cvDto = handleCreateCV(userId, request.getTitle(), request.getPersonalInfo(),
                    request.getExperience(), request.getEducation(), request.getSkills());

            ResponseData data = new ResponseData();
            data.setCv(cvDto);

            response.setStatusCode(201);
            response.setMessage("CV created successfully");
            response.setData(data);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
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

            ResponseData data = new ResponseData();
            data.setCvs(cvDtos);

            response.setMessage("Get all cvs successfully");
            response.setData(data);
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

            ResponseData data = new ResponseData();
            data.setCv(cvDto);

            response.setMessage("Get cv successfully");
            response.setData(data);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response analyzeCV(UUID cvId) {
        Response response = new Response();

        try {
            CVDto cvDto = handleGetCVById(cvId);
            if (cvDto == null) {
                throw new OurException("CV not found", 404);
            }

            String systemPrompt = "You are an expert CV/resume analyzer. Analyze the CV and provide detailed insights on strengths, weaknesses, and suggestions for improvement. Format your response as JSON with the following structure: {\"overallScore\": <number 0-100>, \"strengths\": [<array of strings>], \"weaknesses\": [<array of strings>], \"suggestions\": [{\"id\": \"<uuid>\", \"type\": \"improvement|warning|error\", \"section\": \"<section name>\", \"message\": \"<description>\", \"suggestion\": \"<specific improvement>\", \"applied\": false}]}";

            String cvContent = handleFormatCVForAnalysis(cvDto);
            String prompt = "Analyze this CV:\n\n" + cvContent;

            String result = openRouterConfig.callModelWithSystemPrompt(systemPrompt, prompt);

            // Parse suggestions from result
            List<AISuggestionDto> suggestions = handleParseSuggestionsFromAIResponse(result);

            ResponseData data = new ResponseData();
            data.setAnalyze(result);
            data.setSuggestions(suggestions);

            response.setMessage("CV analyzed successfully");
            response.setData(data);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response improveCV(UUID cvId, ImproveCVRequest request) {
        Response response = new Response();

        try {
            CVDto cvDto = handleGetCVById(cvId);
            if (cvDto == null) {
                throw new OurException("CV not found", 404);
            }

            String systemPrompt = "You are an expert resume writer and career coach. Your task is to improve specific sections of a CV to make them more professional, impactful, and effective. Use action verbs, quantify achievements where possible, and ensure clarity and conciseness.";

            String prompt = String.format(
                    "Improve the following %s section of a CV:\n\n%s\n\nProvide only the improved version without explanations.",
                    request.getSection(),
                    request.getContent());

            String improved = openRouterConfig.callModelWithSystemPrompt(systemPrompt, prompt);

            ResponseData data = new ResponseData();
            data.setImprovedSection(improved);

            response.setMessage("CV section improved successfully");
            response.setData(data);
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

            ResponseData data = new ResponseData();
            data.setCvs(userCVs);

            response.setMessage("Get user's CVs successfully");
            response.setData(data);
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
                throw new OurException("User not found", 404);
            }

            // Extract text from file
            String extractedText = fileParserService.extractTextFromFile(file);

            // Use AI to parse the CV content into structured data
            String systemPrompt = "You are an expert CV parser. Extract structured information from the CV text and return it as JSON with this exact structure: {\"title\": \"<job title or 'My CV'>\", \"personalInfo\": {\"fullname\": \"<name>\", \"email\": \"<email>\", \"phone\": \"<phone>\", \"location\": \"<location>\", \"summary\": \"<professional summary>\"}, \"experience\": [{\"company\": \"<company>\", \"position\": \"<position>\", \"startDate\": \"<YYYY-MM>\", \"endDate\": \"<YYYY-MM or Present>\", \"description\": \"<description>\"}], \"education\": [{\"school\": \"<school>\", \"degree\": \"<degree>\", \"field\": \"<field>\", \"startDate\": \"<YYYY-MM>\", \"endDate\": \"<YYYY-MM>\"}], \"skills\": [\"<skill1>\", \"<skill2>\"]}";

            String prompt = "Parse this CV text into structured JSON format:\n\n" + extractedText;

            String aiResponse = openRouterConfig.callModelWithSystemPrompt(systemPrompt, prompt);

            // Parse AI response and create CV
            CVDto cvDto = handleParseAndCreateCVFromAIResponse(userId, aiResponse);

            ResponseData data = new ResponseData();
            data.setCv(cvDto);
            data.setExtractedText(extractedText);

            response.setStatusCode(201);
            response.setMessage("CV imported successfully");
            response.setData(data);
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

            ResponseData data = new ResponseData();
            data.setCv(cvDto);

            response.setMessage("Get cv successfully");
            response.setData(data);
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
                // Delete old avatar if exists
                String oldAvatarPublicId = pi.getAvatarPublicId();
                if (oldAvatarPublicId != null && !oldAvatarPublicId.isEmpty()) {
                    cloudinaryService.deleteImage(oldAvatarPublicId);
                }

                // Upload new avatar
                var uploadResult = cloudinaryService.uploadImage(personalInfoDto.getAvatar());
                if (uploadResult.containsKey("error")) {
                    throw new RuntimeException("Failed to upload avatar: " + uploadResult.get("error"));
                }

                pi.setAvatarUrl((String) uploadResult.get("url"));
                pi.setAvatarPublicId((String) uploadResult.get("publicId"));
            }

            existing.setPersonalInfo(pi);
        }

        existing.getExperience().clear();
        if (experiencesDto != null) {
            for (ExperienceDto e : experiencesDto) {
                Experience ex = new Experience(e.getCompany(), e.getPosition(), e.getStartDate(), e.getEndDate(),
                        e.getDescription());
                existing.getExperience().add(ex);
            }
        }

        existing.getEducation().clear();
        if (educationsDto != null) {
            for (EducationDto ed : educationsDto) {
                Education e = new Education(ed.getSchool(), ed.getDegree(), ed.getField(), ed.getStartDate(),
                        ed.getEndDate());
                existing.getEducation().add(e);
            }
        }

        existing.getSkills().clear();
        if (skills != null)
            existing.getSkills().addAll(skills);

        existing.setUpdatedAt(Instant.now());

        CV saved = cvRepository.save(existing);
        return cvMapper.toDto(saved);
    }

    public Response updateCV(UUID cvId, UpdateCVRequest request) {
        Response response = new Response();

        try {
            CVDto updatedDto = handleUpdateCV(cvId, request.getTitle(), request.getPersonalInfo(),
                    request.getExperience(), request.getEducation(), request.getSkills());

            ResponseData data = new ResponseData();
            data.setCv(updatedDto);

            response.setMessage("CV updated successfully");
            response.setData(data);
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

    public Response deleteCV(UUID cvId) {
        Response response = new Response();

        try {
            handleDeleteCV(cvId);

            response.setMessage("CV deleted successfully");
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response analyzeCVWithJobDescription(UUID cvId, AnalyzeCVWithJDRequest request) {
        Response response = new Response();

        try {
            CVDto cvDto = handleGetCVById(cvId);

            String jdText = handleExtractJobDescriptionText(request);
            
            String cvContent = handleFormatCVForAnalysis(cvDto);
            String language = Optional.ofNullable(request.getLanguage()).orElse("English");

            String systemPrompt = handleBuildSystemPrompt(language);
            String userPrompt = handleBuildUserPrompt(jdText, cvContent);

            String aiResponse = openRouterConfig.callModelWithSystemPrompt(systemPrompt, userPrompt);
            String jsonContent = handleExtractJsonFromResponse(aiResponse);

            ResponseData data = handleParseAIResponse(jsonContent, aiResponse);

            response.setMessage("CV analyzed with job description successfully");
            response.setData(data);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    private String handleExtractJobDescriptionText(AnalyzeCVWithJDRequest request) {
        if (request.getFile() == null || request.getFile().isEmpty()) {
            return request.getJobDescription();
        }

        try {
            return jobDescriptionParserService.extractTextFromFile(request.getFile());
        } catch (Exception ex) {
            System.err.println("Error extracting JD file: " + ex.getMessage());
            return request.getJobDescription(); // fallback
        }
    }

    private String handleBuildSystemPrompt(String language) {
        return "You are an expert job description parser and ATS analyst. " +
                "Given a Job Description and a CV, return two JSON objects: " +
                "(1) the parsed Job Description with fields " +
                "{\"jobTitle\",\"company\",\"jobLevel\",\"jobType\",\"salary\",\"location\",\"responsibilities\":[],\"requirements\":[],\"requiredSkills\":[],\"preferredSkills\":[],\"benefits\":[]} "
                +
                "and (2) an analysis of how well the CV matches the JD with fields " +
                "{\"matchScore\":<0-100>, \"missingKeywords\":[], \"strengths\":[], \"suggestions\":[] }. " +
                "Return ONLY valid JSON. Language for output: " + language + ".";
    }

    private String handleBuildUserPrompt(String jdText, String cvContent) {
        return String.format(
                "Job Description:\n%s\n\nCV Content:\n%s\n\nReturn the parsed JD JSON and the analysis JSON.",
                jdText, cvContent);
    }

    private ResponseData handleParseAIResponse(String jsonContent, String rawAIResponse) {
        JobDescriptionResult jdResult = handleTryParseJobDescription(jsonContent);
        Double matchScore = null;
        List<String> missingKeywords = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(jsonContent);
            if (root.has("matchScore")) {
                matchScore = root.get("matchScore").asDouble();
            }
            if (root.has("missingKeywords") && root.get("missingKeywords").isArray()) {
                for (JsonNode n : root.get("missingKeywords")) {
                    missingKeywords.add(n.asText());
                }
            }
        } catch (Exception ignored) {
        }

        ResponseData data = new ResponseData();
        data.setParsedJobDescription(jdResult);
        data.setAnalyze(rawAIResponse);
        data.setMatchScore(matchScore);
        data.setMissingKeywords(missingKeywords);
        return data;
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
        if (cvDto.getExperience() != null && !cvDto.getExperience().isEmpty()) {
            sb.append("\nWork Experience:\n");
            for (ExperienceDto exp : cvDto.getExperience()) {
                sb.append("- ").append(exp.getPosition()).append(" at ").append(exp.getCompany())
                        .append(" (").append(exp.getStartDate()).append(" - ").append(exp.getEndDate()).append(")\n");
                sb.append("  ").append(exp.getDescription()).append("\n");
            }
        }

        // Education
        if (cvDto.getEducation() != null && !cvDto.getEducation().isEmpty()) {
            sb.append("\nEducation:\n");
            for (EducationDto edu : cvDto.getEducation()) {
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

    private Double handleExtractMatchScore(String aiResponse) {
        try {
            String jsonContent = handleExtractJsonFromResponse(aiResponse);
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            if (rootNode.has("matchScore")) {
                return rootNode.get("matchScore").asDouble();
            }
        } catch (Exception e) {
            System.err.println("Error extracting match score: " + e.getMessage());
        }

        return null;
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