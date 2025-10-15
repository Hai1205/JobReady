package com.example.cvservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.cvservice.config.OpenRouterConfig;
import com.example.cvservice.dto.*;
import com.example.cvservice.dto.requests.*;
import com.example.cvservice.dto.responses.*;
import com.example.cvservice.entity.*;
import com.example.cvservice.exception.OurException;
import com.example.cvservice.repository.*;
import com.example.cvservice.mapper.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class CVService {

    private final CVRepository cvRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final PersonalInfoRepository personalInfoRepository;
    private final FileParserService fileParserService;
    private final OpenRouterConfig openRouterConfig;
    private final CVMapper cvMapper;
    private final ObjectMapper objectMapper;

    public CVService(
            CVRepository cvRepository,
            EducationRepository educationRepository,
            ExperienceRepository experienceRepository,
            PersonalInfoRepository personalInfoRepository,
            FileParserService fileParserService,
            OpenRouterConfig openRouterConfig,
            CVMapper cvMapper) {
        this.cvRepository = cvRepository;
        this.educationRepository = educationRepository;
        this.experienceRepository = experienceRepository;
        this.personalInfoRepository = personalInfoRepository;
        this.fileParserService = fileParserService;
        this.openRouterConfig = openRouterConfig;
        this.cvMapper = cvMapper;
        this.objectMapper = new ObjectMapper();
    }

    public CVDto handleGetCVById(UUID cvId) {
        CV cv = cvRepository.findById(cvId).orElseThrow(() -> new OurException("CV not found"));
        return cvMapper.toDto(cv);
    }

    public CVDto handleCreateCV(
            UUID userId,
            String title,
            PersonalInfoDto personalInfoDto,
            List<ExperienceDto> experiencesDto,
            List<EducationDto> educationsDto,
            List<String> skills) {

        CV cv = new CV();
        cv.setUserId(userId);
        cv.setTitle(title);
        cv.setSkills(skills);

        PersonalInfo personalInfo = new PersonalInfo(personalInfoDto.getEmail(), personalInfoDto.getFullname(),
                personalInfoDto.getPhone(), personalInfoDto.getLocation(), personalInfoDto.getSummary());
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
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
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

            response.setStatusCode(200);
            response.setMessage("CVs retrieved successfully");
            response.setData(data);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response getCVById(UUID cvId) {
        Response response = new Response();

        try {
            CVDto cvDto = handleGetCVById(cvId);

            ResponseData data = new ResponseData();
            data.setCv(cvDto);

            response.setStatusCode(200);
            response.setMessage("CV retrieved successfully");
            response.setData(data);
        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage("Invalid CV ID format");
            System.out.println(e.getMessage());
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response analyzeCV(UUID cvId) {
        Response response = new Response();

        try {
            CVDto cvDto = handleGetCVById(cvId);

            String systemPrompt = "You are an expert CV/resume analyzer. Analyze the CV and provide detailed insights on strengths, weaknesses, and suggestions for improvement. Format your response as JSON with the following structure: {\"overallScore\": <number 0-100>, \"strengths\": [<array of strings>], \"weaknesses\": [<array of strings>], \"suggestions\": [{\"id\": \"<uuid>\", \"type\": \"improvement|warning|error\", \"section\": \"<section name>\", \"message\": \"<description>\", \"suggestion\": \"<specific improvement>\", \"applied\": false}]}";

            String cvContent = formatCVForAnalysis(cvDto);
            String prompt = "Analyze this CV:\n\n" + cvContent;

            String result = openRouterConfig.callModelWithSystemPrompt(systemPrompt, prompt);

            // Parse suggestions from result
            List<AISuggestionDto> suggestions = parseSuggestionsFromAIResponse(result);

            ResponseData data = new ResponseData();
            data.setCv(cvDto);
            data.setAnalysis(result);
            data.setSuggestions(suggestions);

            response.setStatusCode(200);
            response.setMessage("CV analyzed successfully");
            response.setData(data);

        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage("Invalid CV ID format");
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    public Response improveCV(UUID cvId, ImproveCVRequest request) {
        Response response = new Response();

        try {
            CVDto cvDto = handleGetCVById(cvId);

            String systemPrompt = "You are an expert resume writer and career coach. Your task is to improve specific sections of a CV to make them more professional, impactful, and effective. Use action verbs, quantify achievements where possible, and ensure clarity and conciseness.";

            String prompt = String.format(
                    "Improve the following %s section of a CV:\n\n%s\n\nProvide only the improved version without explanations.",
                    request.getSection(),
                    request.getContent());

            String improved = openRouterConfig.callModelWithSystemPrompt(systemPrompt, prompt);

            ResponseData data = new ResponseData();
            data.setCv(cvDto);
            data.setImprovedSection(improved);

            response.setStatusCode(200);
            response.setMessage("CV section improved successfully");
            response.setData(data);

        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage("Invalid CV ID format");
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    public List<CVDto> handleGetUserCVs(UUID userId) {
        List<CV> cvs = cvRepository.findAllByUserId(userId);
        return cvs.stream().map(cvMapper::toDto).collect(Collectors.toList());
    }

    public Response getUserCVs(UUID userId) {
        Response response = new Response();

        try {
            List<CVDto> userCVs = handleGetUserCVs(userId);

            ResponseData data = new ResponseData();
            data.setCvs(userCVs);

            response.setStatusCode(200);
            response.setMessage("CV retrieved successfully");
            response.setData(data);
        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage("Invalid CV ID format");
            System.out.println(e.getMessage());
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response importFile(UUID userId, MultipartFile file) {
        Response response = new Response();

        try {
            // Extract text from file
            String extractedText = fileParserService.extractTextFromFile(file);

            // Use AI to parse the CV content into structured data
            String systemPrompt = "You are an expert CV parser. Extract structured information from the CV text and return it as JSON with this exact structure: {\"title\": \"<job title or 'My CV'>\", \"personalInfo\": {\"fullname\": \"<name>\", \"email\": \"<email>\", \"phone\": \"<phone>\", \"location\": \"<location>\", \"summary\": \"<professional summary>\"}, \"experience\": [{\"company\": \"<company>\", \"position\": \"<position>\", \"startDate\": \"<YYYY-MM>\", \"endDate\": \"<YYYY-MM or Present>\", \"description\": \"<description>\"}], \"education\": [{\"school\": \"<school>\", \"degree\": \"<degree>\", \"field\": \"<field>\", \"startDate\": \"<YYYY-MM>\", \"endDate\": \"<YYYY-MM>\"}], \"skills\": [\"<skill1>\", \"<skill2>\"]}";

            String prompt = "Parse this CV text into structured JSON format:\n\n" + extractedText;

            String aiResponse = openRouterConfig.callModelWithSystemPrompt(systemPrompt, prompt);

            // Parse AI response and create CV
            CVDto cvDto = parseAndCreateCVFromAIResponse(userId, aiResponse);

            ResponseData data = new ResponseData();
            data.setCv(cvDto);
            data.setExtractedText(extractedText);

            response.setStatusCode(201);
            response.setMessage("CV imported successfully");
            response.setData(data);

        } catch (OurException e) {
            response.setStatusCode(e.getStatusCode());
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error importing file: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    public Response getCVByTitle(String title) {
        Response response = new Response();

        try {
            Optional<CV> cvOpt = cvRepository.findByTitle(title);

            if (!cvOpt.isPresent()) {
                throw new OurException("CV not found with title: " + title);
            }

            CVDto cvDto = cvMapper.toDto(cvOpt.get());

            ResponseData data = new ResponseData();
            data.setCv(cvDto);

            response.setStatusCode(200);
            response.setMessage("CV retrieved successfully");
            response.setData(data);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
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

            response.setStatusCode(200);
            response.setMessage("CV updated successfully");
            response.setData(data);
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
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

            response.setStatusCode(200);
            response.setMessage("CV deleted successfully");
        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage("Invalid CV ID format");
            System.out.println(e.getMessage());
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response analyzeCVWithJobDescription(UUID cvId, AnalyzeCVWithJDRequest request) {
        Response response = new Response();

        try {
            CVDto cvDto = handleGetCVById(cvId);

            String systemPrompt = "You are an expert ATS (Applicant Tracking System) analyzer and career coach. Analyze how well the CV matches the job description. Provide a match score (0-100), identify missing keywords, suggest improvements, and format your response as JSON: {\"matchScore\": <number>, \"missingKeywords\": [<array>], \"strengths\": [<array>], \"suggestions\": [{\"id\": \"<uuid>\", \"type\": \"improvement\", \"section\": \"<section>\", \"message\": \"<message>\", \"suggestion\": \"<suggestion>\", \"applied\": false}]}";

            String cvContent = formatCVForAnalysis(cvDto);
            String prompt = String.format(
                    "Job Description:\n%s\n\nCV Content:\n%s\n\nAnalyze the match between this CV and job description.",
                    request.getJobDescription(),
                    cvContent);

            String result = openRouterConfig.callModelWithSystemPrompt(systemPrompt, prompt);

            // Parse suggestions from result
            List<AISuggestionDto> suggestions = parseSuggestionsFromAIResponse(result);
            Double matchScore = extractMatchScore(result);

            ResponseData data = new ResponseData();
            data.setCv(cvDto);
            data.setAnalysis(result);
            data.setSuggestions(suggestions);
            data.setMatchScore(matchScore);

            response.setStatusCode(200);
            response.setMessage("CV analyzed with job description successfully");
            response.setData(data);

        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage("Invalid CV ID format");
        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    // Helper methods
    private String formatCVForAnalysis(CVDto cvDto) {
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

    private List<AISuggestionDto> parseSuggestionsFromAIResponse(String aiResponse) {
        List<AISuggestionDto> suggestions = new ArrayList<>();

        try {
            // Try to extract JSON from the response
            String jsonContent = extractJsonFromResponse(aiResponse);
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

    private Double extractMatchScore(String aiResponse) {
        try {
            String jsonContent = extractJsonFromResponse(aiResponse);
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            if (rootNode.has("matchScore")) {
                return rootNode.get("matchScore").asDouble();
            }
        } catch (Exception e) {
            System.err.println("Error extracting match score: " + e.getMessage());
        }

        return null;
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

    private CVDto parseAndCreateCVFromAIResponse(UUID userId, String aiResponse) {
        try {
            String jsonContent = extractJsonFromResponse(aiResponse);
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