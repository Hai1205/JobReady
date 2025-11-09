package com.example.cvservice.services.apis;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.example.cvservice.dtos.*;
import com.example.cvservice.dtos.requests.*;
import com.example.cvservice.dtos.responses.*;
import com.example.cvservice.entities.*;
import com.example.cvservice.entities.CV.CVPrivacy;
import com.example.cvservice.exceptions.OurException;
import com.example.cvservice.mappers.*;
import com.example.cvservice.repositoryies.*;
import com.example.cvservice.services.CloudinaryService;
import com.example.cvservice.services.JobDescriptionParserService;
import com.example.cvservice.services.grpcs.AIGrpcClient;
import com.example.cvservice.services.grpcs.UserGrpcClient;
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
    private final JobDescriptionParserService jobDescriptionParserService;
    private final CVMapper cvMapper;
    private final ObjectMapper objectMapper;
    private final CloudinaryService cloudinaryService;
    private final UserGrpcClient userGrpcClient;
    private final AIGrpcClient aiGrpcClient;

    public CVService(
            CVRepository cvRepository,
            EducationRepository educationRepository,
            ExperienceRepository experienceRepository,
            PersonalInfoRepository personalInfoRepository,
            JobDescriptionParserService jobDescriptionParserService,
            CloudinaryService cloudinaryService,
            CVMapper cvMapper,
            UserGrpcClient userGrpcClient,
            AIGrpcClient aiGrpcClient) {
        this.cvRepository = cvRepository;
        this.educationRepository = educationRepository;
        this.experienceRepository = experienceRepository;
        this.personalInfoRepository = personalInfoRepository;
        this.jobDescriptionParserService = jobDescriptionParserService;
        this.cvMapper = cvMapper;
        this.cloudinaryService = cloudinaryService;
        this.userGrpcClient = userGrpcClient;
        this.aiGrpcClient = aiGrpcClient;
        this.objectMapper = new ObjectMapper();
    }

    public CVDto handleGetCVById(UUID cvId) {
        log.debug("Fetching CV by id={}", cvId);
        CV cv = cvRepository.findById(cvId).orElseThrow(() -> new OurException("CV not found", 404));
        log.debug("Found CV id={} userId={}", cv.getId(), cv.getUserId());
        return cvMapper.toDto(cv);
    }

    public CVDto handleDuplicateCV(
            UUID userId,
            String title,
            PersonalInfoDto personalInfoDto,
            MultipartFile avatar,
            List<ExperienceDto> experiencesDto,
            List<EducationDto> educationsDto,
            List<String> skills,
            String privacy,
            String color,
            String template) {
        log.info("Creating CV for userId={} title='{}' experiencesCount={} educationsCount={}", userId, title,
                experiencesDto == null ? 0 : experiencesDto.size(), educationsDto == null ? 0 : educationsDto.size());

        UserDto user = userGrpcClient.findUserById(userId);

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

        if (privacy == null || privacy.isEmpty()) {
            privacy = "PRIVATE";
        }

        CV cv = new CV(userId, title, skills, privacy, color, template);

        PersonalInfo personalInfo = new PersonalInfo(personalInfoDto);
        if (avatar != null && !avatar.isEmpty()) {
            var uploadResult = cloudinaryService.uploadImage(avatar);
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

    public Response createCV(UUID userId) {
        Response response = new Response();

        try {
            UserDto user = userGrpcClient.findUserById(userId);

            if (user == null) {
                log.warn("User not found when creating CV: userId={}", userId);
                throw new OurException("User not found", 404);
            }

            CV savedCV = cvRepository.save(new CV(userId, "Untitled CV"));

            response.setStatusCode(201);
            response.setMessage("CV created successfully");
            response.setCv(cvMapper.toDto(savedCV));
            log.debug("createCV response prepared for userId={} cvId={}", userId, savedCV.getId());
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

            AIResponseDto aiResponse = aiGrpcClient.analyzeCV(cvDto);
            List<AISuggestionDto> suggestions = aiResponse.getSuggestions();
            String analyzeResult = aiResponse.getAnalyzeResult();

            response.setMessage("CV analyzed successfully");
            response.setAnalyze(analyzeResult);
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

            AIResponseDto aiResponse = aiGrpcClient.improveCV(section, content);
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

    public List<CVDto> handleGetUserCVs(UUID userId) {
        UserDto user = userGrpcClient.findUserById(userId);
        if (user == null) {
            return new ArrayList<>();
        }

        List<CV> cvs = cvRepository.findAllByUserId(userId);
        if (cvs == null || cvs.isEmpty()) {
            return new ArrayList<>();
        }

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
            MultipartFile avatar,
            List<ExperienceDto> experiencesDto,
            List<EducationDto> educationsDto,
            List<String> skills,
            String privacy,
            String color,
            String template) {
        CV existing = cvRepository.findById(cvId)
                .orElseThrow(() -> new OurException("CV not found", 404));

        // Only update if not null
        if (title != null && !title.trim().isEmpty()) {
            existing.setTitle(title);
        }
        if (color != null && !color.trim().isEmpty()) {
            existing.setColor(color);
        }
        if (template != null && !template.trim().isEmpty()) {
            existing.setTemplate(template);
        }

        if (personalInfoDto != null) {
            PersonalInfo pi = existing.getPersonalInfo();

            if (pi == null)
                pi = new PersonalInfo();

            // Only update non-null fields
            if (personalInfoDto.getFullname() != null && !personalInfoDto.getFullname().trim().isEmpty()) {
                pi.setFullname(personalInfoDto.getFullname());
            }
            if (personalInfoDto.getEmail() != null && !personalInfoDto.getEmail().trim().isEmpty()) {
                pi.setEmail(personalInfoDto.getEmail());
            }
            if (personalInfoDto.getPhone() != null) {
                pi.setPhone(personalInfoDto.getPhone());
            }
            if (personalInfoDto.getLocation() != null) {
                pi.setLocation(personalInfoDto.getLocation());
            }
            if (personalInfoDto.getSummary() != null) {
                pi.setSummary(personalInfoDto.getSummary());
            }

            if (avatar != null && !avatar.isEmpty()) {
                String oldAvatarPublicId = pi.getAvatarPublicId();
                if (oldAvatarPublicId != null && !oldAvatarPublicId.isEmpty()) {
                    cloudinaryService.deleteImage(oldAvatarPublicId);
                }

                var uploadResult = cloudinaryService.uploadImage(avatar);
                if (uploadResult.containsKey("error")) {
                    throw new RuntimeException("Failed to upload avatar: " + uploadResult.get("error"));
                }

                pi.setAvatarUrl((String) uploadResult.get("url"));
                pi.setAvatarPublicId((String) uploadResult.get("publicId"));
            }

            existing.setPersonalInfo(pi);
        }

        // Only update experiences if provided
        if (experiencesDto != null) {
            existing.getExperiences().clear();
            for (ExperienceDto e : experiencesDto) {
                Experience ex = new Experience(e);
                existing.getExperiences().add(ex);
            }
        }

        // Only update educations if provided
        if (educationsDto != null) {
            existing.getEducations().clear();
            for (EducationDto ed : educationsDto) {
                Education e = new Education(ed);
                existing.getEducations().add(e);
            }
        }

        // Only update skills if provided
        if (skills != null) {
            existing.getSkills().clear();
            existing.getSkills().addAll(skills);
        }

        // Only update privacy if provided and valid
        if (privacy != null && !privacy.trim().isEmpty()) {
            try {
                // Convert to uppercase to handle case-insensitive input
                existing.setPrivacy(CVPrivacy.valueOf(privacy.toUpperCase()));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid privacy value: {}. Keeping existing value.", privacy);
                // Keep existing privacy value if invalid
            }
        }

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
            List<ExperienceDto> experiences = request.getExperiences();
            List<EducationDto> educations = request.getEducations();
            List<String> skills = request.getSkills();
            String privacy = request.getPrivacy();
            String color = request.getColor();
            String template = request.getTemplate();

            CVDto cvDto = handleUpdateCV(
                    cvId,
                    title,
                    personalInfo,
                    avatar,
                    experiences,
                    educations,
                    skills,
                    privacy,
                    color,
                    template);

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

        CVDto newCV = handleDuplicateCV(
                existingCV.getUserId(),
                existingCV.getTitle() + " (Copy)",
                existingCV.getPersonalInfo(),
                null,
                existingCV.getExperiences(),
                existingCV.getEducations(),
                new ArrayList<>(existingCV.getSkills()),
                existingCV.getPrivacy(),
                existingCV.getColor(),
                existingCV.getTemplate());

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

            AIResponseDto aiResponse = aiGrpcClient.analyzeCVWithJobDescription(cvDto, language, jdText);
            JobDescriptionResult jdResult = aiResponse.getJdResult();
            String analyzeResult = aiResponse.getAnalyzeResult();
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
}