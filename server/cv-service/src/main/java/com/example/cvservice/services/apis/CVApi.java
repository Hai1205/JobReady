package com.example.cvservice.services.apis;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import com.example.cvservice.dtos.*;
import com.example.cvservice.dtos.requests.*;
import com.example.cvservice.dtos.responses.*;
import com.example.cvservice.entities.*;
import com.example.cvservice.exceptions.OurException;
import com.example.cvservice.mappers.*;
import com.example.cvservice.repositoryies.*;
import com.example.cvservice.services.CloudinaryService;
import com.example.cvservice.services.feigns.UserFeignClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class CVApi extends BaseApi {
    private final CVRepository cvRepository;
    private final CVMapper cvMapper;
    private final ObjectMapper objectMapper;
    private final CloudinaryService cloudinaryService;
    private final UserFeignClient userFeignClient;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final PersonalInfoRepository personalInfoRepository;

    public CVApi(
            CVRepository cvRepository,
            CloudinaryService cloudinaryService,
            CVMapper cvMapper,
            UserFeignClient userFeignClient,
            ExperienceRepository experienceRepository,
            EducationRepository educationRepository,
            PersonalInfoRepository personalInfoRepository) {
        this.cvRepository = cvRepository;
        this.cvMapper = cvMapper;
        this.cloudinaryService = cloudinaryService;
        this.userFeignClient = userFeignClient;
        this.objectMapper = new ObjectMapper();
        this.experienceRepository = experienceRepository;
        this.educationRepository = educationRepository;
        this.personalInfoRepository = personalInfoRepository;
    }

    @Transactional(readOnly = true)
    public CVDto handleGetCVById(UUID cvId) {
        logger.debug("Fetching CV by id={}", cvId);
        CV cv = cvRepository.findById(cvId).orElseThrow(() -> new OurException("CV not found", 404));
        logger.debug("Found CV id={} userId={}", cv.getId(), cv.getUserId());
        return cvMapper.toDto(cv);
    }

    @Transactional
    public CVDto handleCreateCV(
            UUID userId,
            String title,
            PersonalInfoDto personalInfoDto,
            MultipartFile avatar,
            List<ExperienceDto> experiencesDto,
            List<EducationDto> educationsDto,
            List<String> skills,
            Boolean isVisibility,
            String color,
            String template) {

        logger.info("Creating CV for userId={} title='{}' experiencesCount={} educationsCount={}",
                userId, title,
                experiencesDto == null ? 0 : experiencesDto.size(),
                educationsDto == null ? 0 : educationsDto.size());

        validateUser(userId);
        personalInfoDto = ensurePersonalInfoDto(personalInfoDto);

        List<ExperienceDto> safeExperiences = ensureExperienceList(experiencesDto);
        List<EducationDto> safeEducations = ensureEducationList(educationsDto);
        boolean visibility = ensureVisibility(isVisibility);
        String safeColor = (color != null && !color.trim().isEmpty()) ? color : "#3498db";
        String safeTemplate = (template != null && !template.trim().isEmpty()) ? template : "modern";

        CV cv = new CV(userId, title, skills, visibility, safeColor, safeTemplate);

        PersonalInfo personalInfo = buildPersonalInfo(personalInfoDto, avatar);
        cv.setPersonalInfo(personalInfo);

        cv.setExperiences(buildExperiences(safeExperiences));
        cv.setEducations(buildEducations(safeEducations));

        CV saved = cvRepository.save(cv);
        logger.info("Created CV id={} for userId={}", saved.getId(), saved.getUserId());

        return cvMapper.toDto(saved);
    }

    private UserDto validateUser(UUID userId) {
        UserDto user = userFeignClient.getUserById(userId.toString()).getUser();
        if (user == null) {
            logger.warn("User not found when creating CV: userId={}", userId);
            throw new OurException("User not found", 404);
        }
        return user;
    }

    private PersonalInfoDto ensurePersonalInfoDto(PersonalInfoDto dto) {
        return dto != null ? dto : new PersonalInfoDto();
    }

    private List<ExperienceDto> ensureExperienceList(List<ExperienceDto> list) {
        return (list == null || list.isEmpty()) ? new ArrayList<>() : list;
    }

    private List<EducationDto> ensureEducationList(List<EducationDto> list) {
        return (list == null || list.isEmpty()) ? new ArrayList<>() : list;
    }

    private boolean ensureVisibility(Boolean isVisibility) {
        return isVisibility != null ? isVisibility : false;
    }

    private PersonalInfo buildPersonalInfo(PersonalInfoDto dto, MultipartFile avatar) {
        PersonalInfo personalInfo = new PersonalInfo();
        personalInfo.setFullname(dto.getFullname());
        personalInfo.setEmail(dto.getEmail());
        personalInfo.setPhone(dto.getPhone());
        personalInfo.setLocation(dto.getLocation());
        personalInfo.setSummary(dto.getSummary());

        if (avatar != null && !avatar.isEmpty()) {
            var upload = cloudinaryService.uploadImage(avatar);

            if (upload.containsKey("error")) {
                throw new RuntimeException("Failed to upload avatar: " + upload.get("error"));
            }

            personalInfo.setAvatarUrl((String) upload.get("url"));
            personalInfo.setAvatarPublicId((String) upload.get("publicId"));
        } else if (dto.getAvatarUrl() != null) {
            // preserve avatar URL when duplicating
            personalInfo.setAvatarUrl(dto.getAvatarUrl());
        }

        logger.debug("Created personal info for CV (email={})", personalInfo.getEmail());
        return personalInfo;
    }

    private List<Experience> buildExperiences(List<ExperienceDto> dtoList) {
        List<Experience> experiences = dtoList.stream()
                .map(dto -> {
                    Experience exp = new Experience();
                    exp.setCompany(dto.getCompany());
                    exp.setPosition(dto.getPosition());
                    exp.setStartDate(dto.getStartDate());
                    exp.setEndDate(dto.getEndDate());
                    exp.setDescription(dto.getDescription());
                    return exp;
                })
                .collect(Collectors.toList());

        logger.debug("Created {} experiences for CV", experiences.size());
        return experiences;
    }

    private List<Education> buildEducations(List<EducationDto> dtoList) {
        List<Education> educations = dtoList.stream()
                .map(dto -> {
                    Education edu = new Education();
                    edu.setSchool(dto.getSchool());
                    edu.setDegree(dto.getDegree());
                    edu.setField(dto.getField());
                    edu.setStartDate(dto.getStartDate());
                    edu.setEndDate(dto.getEndDate());
                    return edu;
                })
                .collect(Collectors.toList());

        logger.debug("Created {} educations for CV", educations.size());
        return educations;
    }

    public Response createCV(UUID userId) {
        Response response = new Response();

        try {
            UserDto user = userFeignClient.getUserById(userId.toString()).getUser();

            if (user == null) {
                logger.warn("User not found when creating CV: userId={}", userId);
                throw new OurException("User not found", 404);
            }

                // CVCreateRequest request = objectMapper.readValue(dataJson, CVCreateRequest.class);
                // CVDto savedCV = handleCreateCV(userId, request.getTitle(), request.getPersonalInfo(),
                //         null, request.getExperiences(), request.getEducations(), request.getSkills(),
                //         request.getIsVisibility(), request.getColor(), request.getTemplate());

                // response.setStatusCode(201);
                // response.setMessage("CV created successfully");
                // response.setCv(savedCV);
                // logger.debug("createCV response prepared for userId={} cvId={}", userId, savedCV.getId());

                CVDto savedCV = handleCreateCV(userId, "Untitled CV", null, null, null, null, null, null, null, null);

                response.setStatusCode(201);
                response.setMessage("CV created successfully");
                response.setCv(savedCV);
                logger.debug("createCV response prepared for userId={} cvId={}", userId, savedCV.getId());
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, "Error creating CV: " + e.getMessage());
        }
    }

    public Response importCV(UUID userId, String dataJson) {
        Response response = new Response();

        try {
            UserDto user = userFeignClient.getUserById(userId.toString()).getUser();

            if (user == null) {
                logger.warn("User not found when creating CV: userId={}", userId);
                throw new OurException("User not found", 404);
            }

                CVCreateRequest request = objectMapper.readValue(dataJson, CVCreateRequest.class);
                CVDto savedCV = handleCreateCV(userId, request.getTitle(), request.getPersonalInfo(),
                        null, request.getExperiences(), request.getEducations(), request.getSkills(),
                        request.getIsVisibility(), request.getColor(), request.getTemplate());

                response.setStatusCode(201);
                response.setMessage("CV imported successfully");
                response.setCv(savedCV);
                logger.debug("importCV response prepared for userId={} cvId={}", userId, savedCV.getId());
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, "Error creating CV: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<CVDto> handleGetAllCVs() {
        return cvRepository.findAll().stream()
                .map(cvMapper::toDto)
                .collect(Collectors.toList());
    }

    public long handleGetTotalCVs() {
        return cvRepository.count();
    }

    public List<CVDto> handleGetCVsByVisibility(boolean isVisibility) {
        return cvRepository.findAll().stream()
                .filter(cv -> cv.getIsVisibility()
                        .equals(isVisibility))
                .map(cvMapper::toDto)
                .collect(Collectors.toList());
    }

    public long handleGetCVsCountByVisibility(boolean isVisibility) {
        return cvRepository.findAll().stream()
                .filter(cv -> cv.getIsVisibility().equals(isVisibility))
                .count();
    }

    public List<CVDto> handleGetCVsCreatedInRange(Instant startDate, Instant endDate) {
        return cvRepository.findAll().stream()
                .filter(cv -> cv.getCreatedAt() != null &&
                        !cv.getCreatedAt().isBefore(startDate) &&
                        !cv.getCreatedAt().isAfter(endDate))
                .map(cvMapper::toDto)
                .collect(Collectors.toList());
    }

    public long handleGetCVsCountCreatedInRange(Instant startDate, Instant endDate) {
        return cvRepository.findAll().stream()
                .filter(cv -> cv.getCreatedAt() != null &&
                        !cv.getCreatedAt().isBefore(startDate) &&
                        !cv.getCreatedAt().isAfter(endDate))
                .count();
    }

    @Transactional(readOnly = true)
    public List<CVDto> handleGetRecentCVs(int limit) {
        List<CV> cvs = cvRepository.findAll();
        // Force initialization of lazy collections within transaction
        cvs.forEach(cv -> {
            if (cv.getSkills() != null) {
                cv.getSkills().size();
            }
            if (cv.getExperiences() != null) {
                cv.getExperiences().size();
            }
            if (cv.getEducations() != null) {
                cv.getEducations().size();
            }
        });
        
        return cvs.stream()
                .map(cvMapper::toDto)
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Response getAllCVs() {
        Response response = new Response();

        try {
            List<CVDto> cvsDto = handleGetAllCVs();

            response.setMessage("Get all cvs successfully");
            response.setCvs(cvsDto);
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

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<CVDto> handleGetUserCVs(UUID userId) {
        UserDto user = userFeignClient.getUserById(userId.toString()).getUser();
        if (user == null) {
            return new ArrayList<>();
        }

        List<CV> cvs = cvRepository.findAllByUserId(userId);
        if (cvs == null || cvs.isEmpty()) {
            return new ArrayList<>();
        }

        return cvs.stream()
                .map(cvMapper::toDto)
                .sorted((a, b) -> b.getUpdatedAt().compareTo(a.getUpdatedAt()))
                .collect(Collectors.toList());
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

    @Transactional
    public CVDto handleUpdateCV(UUID cvId,
            String title,
            PersonalInfoDto personalInfoDto,
            MultipartFile avatar,
            List<ExperienceDto> experiencesDto,
            List<EducationDto> educationsDto,
            List<String> skills,
            Boolean isVisibility,
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

        // Only update isVisibility if provided
        if (isVisibility != null) {
            existing.setIsVisibility(isVisibility);
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
            Boolean isVisibility = request.getIsVisibility();
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
                    isVisibility,
                    color,
                    template);

            response.setMessage("CV updated successfully");
            response.setCv(cvDto);
            logger.info("Updated CV id={} (userId={})", cvId, cvDto.getUserId());
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    @Transactional
    public boolean handleDeleteCV(UUID cvId) {
        CV cv = cvRepository.findById(cvId).orElseThrow(() -> new OurException("CV not found", 404));

        // Delete avatar from Cloudinary if exists
        if (cv.getPersonalInfo() != null && cv.getPersonalInfo().getAvatarPublicId() != null) {
            cloudinaryService.deleteImage(cv.getPersonalInfo().getAvatarPublicId());
        }

        // Delete related entities
        if (cv.getPersonalInfo() != null) {
            personalInfoRepository.delete(cv.getPersonalInfo());
        }
        experienceRepository.deleteAll(cv.getExperiences());
        educationRepository.deleteAll(cv.getEducations());

        // Delete the CV
        cvRepository.delete(cv);
        return true;
    }

    public CVDto handleDuplicateCV(UUID cvId) {
        logger.info("Duplicating CV id={}", cvId);
        CVDto existingCV = handleGetCVById(cvId);

        CVDto newCV = handleCreateCV(
                existingCV.getUserId(),
                existingCV.getTitle() + " (Copy)",
                existingCV.getPersonalInfo(),
                null,
                existingCV.getExperiences(),
                existingCV.getEducations(),
                new ArrayList<>(existingCV.getSkills()),
                existingCV.getIsVisibility(),
                existingCV.getColor(),
                existingCV.getTemplate());

        logger.info("Duplicated CV id={} created new CV id={}", cvId, newCV.getId());
        return newCV;
    }

    public Response deleteCV(UUID cvId) {
        Response response = new Response();

        try {
            handleDeleteCV(cvId);

            response.setMessage("CV deleted successfully");
            logger.info("Deleted CV id={}", cvId);
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
            logger.info("Duplicated CV id={}", cvId);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response getTotalCVs() {
        try {
            long total = handleGetTotalCVs();
            Response response = new Response(200, "Total CVs retrieved successfully");
            response.setAdditionalData(Map.of("total", total));
            return response;
        } catch (Exception e) {
            logger.error("Error in getTotalCVs: {}", e.getMessage(), e);
            return new Response(500, "Failed to get total CVs");
        }
    }

    public Response getCVsByVisibility(boolean visibility) {
        try {
            long count = handleGetCVsCountByVisibility(visibility);
            Response response = new Response(200, "CVs by visibility retrieved successfully");
            response.setAdditionalData(Map.of("count", count));
            return response;
        } catch (Exception e) {
            logger.error("Error in getCVsByVisibility: {}", e.getMessage(), e);
            return new Response(500, "Failed to get CVs by visibility");
        }
    }

    public Response getCVsCreatedInRange(String startDate, String endDate) {
        try {
            Instant start = Instant.parse(startDate);
            Instant end = Instant.parse(endDate);
            long count = handleGetCVsCountCreatedInRange(start, end);
            Response response = new Response(200, "CVs created in range retrieved successfully");
            response.setAdditionalData(Map.of("count", count));
            return response;
        } catch (Exception e) {
            logger.error("Error in getCVsCreatedInRange: {}", e.getMessage(), e);
            return new Response(500, "Failed to get CVs created in range");
        }
    }

    public Response getRecentCVs(int limit) {
        try {
            List<CVDto> recentCVs = handleGetRecentCVs(limit);
            Response response = new Response(200, "Recent CVs retrieved successfully");
            response.setCvs(recentCVs);
            return response;
        } catch (Exception e) {
            logger.error("Error in getRecentCVs: {}", e.getMessage(), e);
            return new Response(500, "Failed to get recent CVs");
        }
    }
}