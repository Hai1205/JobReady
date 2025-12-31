package com.example.cvservice.services.apis;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;

import com.example.cvservice.dtos.*;
import com.example.cvservice.dtos.requests.*;
import com.example.cvservice.dtos.responses.*;
import com.example.cvservice.entities.*;
import com.example.cvservice.exceptions.OurException;
import com.example.cvservice.mappers.*;
import com.example.cvservice.repositories.cvRepositories.SimpleCVRepository;
import com.example.cvservice.repositories.cvRepositories.CVQueryRepository;
import com.example.cvservice.repositories.cvRepositories.CVCommandRepository;
import com.example.cvservice.repositories.experienceRepositories.SimpleExperienceRepository;
import com.example.cvservice.repositories.experienceRepositories.ExperienceQueryRepository;
import com.example.cvservice.repositories.experienceRepositories.ExperienceCommandRepository;
import com.example.cvservice.repositories.educationRepositories.SimpleEducationRepository;
import com.example.cvservice.repositories.educationRepositories.EducationQueryRepository;
import com.example.cvservice.repositories.educationRepositories.EducationCommandRepository;
import com.example.cvservice.repositories.personalInfoRepositories.SimplePersonalInfoRepository;
import com.example.cvservice.repositories.personalInfoRepositories.PersonalInfoQueryRepository;
import com.example.cvservice.repositories.personalInfoRepositories.PersonalInfoCommandRepository;
import com.example.cloudinarycommon.CloudinaryService;
import com.example.cvservice.services.feigns.UserFeignClient;
import com.example.cvservice.services.CVQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CVApi extends BaseApi {
    private final SimpleCVRepository simpleCVRepository;
    private final CVQueryRepository cvQueryRepository;
    private final CVCommandRepository cvCommandRepository;
    
    private final ExperienceQueryRepository experienceQueryRepository;
    private final ExperienceCommandRepository experienceCommandRepository;
    
    private final EducationQueryRepository educationQueryRepository;
    private final EducationCommandRepository educationCommandRepository;
    
    private final PersonalInfoQueryRepository personalInfoQueryRepository;
    private final PersonalInfoCommandRepository personalInfoCommandRepository;
    
    private final CVMapper cvMapper;
    private final CVQueryService cvQueryService;
    private final ObjectMapper objectMapper;
    private final CloudinaryService cloudinaryService;
    private final UserFeignClient userFeignClient;

    public CVApi(
            SimpleCVRepository simpleCVRepository,
            CVQueryRepository cvQueryRepository,
            CVCommandRepository cvCommandRepository,
            ExperienceQueryRepository experienceQueryRepository,
            ExperienceCommandRepository experienceCommandRepository,
            EducationQueryRepository educationQueryRepository,
            EducationCommandRepository educationCommandRepository,
            PersonalInfoQueryRepository personalInfoQueryRepository,
            PersonalInfoCommandRepository personalInfoCommandRepository,
            CloudinaryService cloudinaryService,
            CVMapper cvMapper,
            CVQueryService cvQueryService,
            UserFeignClient userFeignClient) {
        this.simpleCVRepository = simpleCVRepository;
        this.cvQueryRepository = cvQueryRepository;
        this.cvCommandRepository = cvCommandRepository;
        this.experienceQueryRepository = experienceQueryRepository;
        this.experienceCommandRepository = experienceCommandRepository;
        this.educationQueryRepository = educationQueryRepository;
        this.educationCommandRepository = educationCommandRepository;
        this.personalInfoQueryRepository = personalInfoQueryRepository;
        this.personalInfoCommandRepository = personalInfoCommandRepository;
        this.cvMapper = cvMapper;
        this.cvQueryService = cvQueryService;
        this.cloudinaryService = cloudinaryService;
        this.userFeignClient = userFeignClient;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional(readOnly = true)
    public CVDto handleGetCVById(UUID cvId) {
        logger.debug("Fetching CV by id={}", cvId);
        
        // Sử dụng CVQueryService để fetch CV với đầy đủ children
        CVDto cvDto = cvQueryService.findByIdWithChildren(cvId)
                .orElseThrow(() -> new OurException("CV not found", 404));
        
        logger.debug("Found CV id={} userId={}", cvDto.getId(), cvDto.getUserId());
        return cvDto;
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
            String template,
            String font
        ) {

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
        String safeFont = (font != null && !font.trim().isEmpty()) ? font : "Inter";

        // Save CV first to get ID
        CV cv = new CV(userId, title, skills, visibility, safeColor, safeTemplate, safeFont);
        UUID cvId = UUID.randomUUID();
        Instant now = Instant.now();
        
        // Convert skills list to comma-separated string for native query
        String skillsStr = skills != null && !skills.isEmpty() ? String.join(",", skills) : "";
        
        cvCommandRepository.insertCV(
            cvId,
            userId,
            title,
            skillsStr,
            visibility,
            safeColor,
            safeTemplate,
            safeFont,
            now,
            now
        );
        
        logger.info("Created CV id={} for userId={}", cvId, userId);

        // Save PersonalInfo separately with CV ID
        PersonalInfo personalInfo = buildPersonalInfo(personalInfoDto, avatar);
        UUID personalInfoId = UUID.randomUUID();
        personalInfoCommandRepository.insertPersonalInfo(
            personalInfoId,
            cvId,
            personalInfo.getFullname(),
            personalInfo.getEmail(),
            personalInfo.getPhone(),
            personalInfo.getLocation(),
            personalInfo.getBirth(),
            personalInfo.getSummary(),
            personalInfo.getAvatarUrl(),
            personalInfo.getAvatarPublicId()
        );

        // Save Experiences separately with CV ID
        List<Experience> experiences = buildExperiences(safeExperiences);
        for (Experience exp : experiences) {
            UUID expId = UUID.randomUUID();
            experienceCommandRepository.insertExperience(
                expId,
                cvId,
                exp.getCompany(),
                exp.getPosition(),
                exp.getStartDate(),
                exp.getEndDate(),
                exp.getDescription()
            );
        }

        // Save Educations separately with CV ID
        List<Education> educations = buildEducations(safeEducations);
        for (Education edu : educations) {
            UUID eduId = UUID.randomUUID();
            educationCommandRepository.insertEducation(
                eduId,
                cvId,
                edu.getSchool(),
                edu.getDegree(),
                edu.getField(),
                edu.getStartDate(),
                edu.getEndDate()
            );
        }

        // Fetch the created CV to return
        CV saved = simpleCVRepository.findById(cvId)
                .orElseThrow(() -> new OurException("Failed to create CV", 500));
        return cvMapper.toDto(saved);
    }

    private UserDto validateUser(UUID userId) {
        UserDto user = userFeignClient.getUserById(userId).getUser();
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
        personalInfo.setBirth(dto.getBirth());
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
            CVDto savedCV = handleCreateNew(userId);

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

    public CVDto handleCreateNew(UUID userId) {
        UserDto user = userFeignClient.getUserById(userId).getUser();

        if (user == null) {
            logger.warn("User not found when creating CV: userId={}", userId);
            throw new OurException("User not found", 404);
        }

        PersonalInfoDto personalInfoDto = handleSetUserPersonalInfo(user);

        return handleCreateCV(userId, "Untitled CV", personalInfoDto, null, null, null, null, null, null, null, null);
    }

    private PersonalInfoDto handleSetUserPersonalInfo(UserDto user){
        // Create PersonalInfoDto with user's real information
        PersonalInfoDto personalInfoDto = new PersonalInfoDto();
        personalInfoDto.setFullname(user.getFullname() != null ? user.getFullname() : "");
        personalInfoDto.setEmail(user.getEmail() != null ? user.getEmail() : "");
        personalInfoDto.setPhone(user.getPhone() != null ? user.getPhone() : "");
        personalInfoDto.setLocation(user.getLocation() != null ? user.getLocation() : "");
        personalInfoDto.setBirth(user.getBirth() != null ? user.getBirth() : "");
        personalInfoDto.setSummary(user.getSummary() != null ? user.getSummary() : "");
        personalInfoDto.setAvatarUrl(user.getAvatarUrl() != null ? user.getAvatarUrl() : "");
        return personalInfoDto;
    }

    public Response importCV(UUID userId, String dataJson) {
        Response response = new Response();

        try {
            UserDto user = userFeignClient.getUserById(userId).getUser();

            if (user == null) {
                logger.warn("User not found when creating CV: userId={}", userId);
                throw new OurException("User not found", 404);
            }

                CVCreateRequest request = objectMapper.readValue(dataJson, CVCreateRequest.class);

                PersonalInfoDto personalInfoDto = handleSetUserPersonalInfo(user);
                
                CVDto savedCV = handleCreateCV(userId, request.getTitle(), personalInfoDto,
                    null, request.getExperiences(), request.getEducations(), request.getSkills(),
                    request.getIsVisibility(), request.getColor(), request.getTemplate(), request.getFont());

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
        // Sử dụng CVQueryService để fetch tất cả CV với children
        return cvQueryService.findAllWithChildren(org.springframework.data.domain.Pageable.unpaged()).getContent();
    }

    public long handleGetTotalCVs() {
        return cvQueryRepository.countTotalCVs();
    }

    public List<CVDto> handleGetCVsByVisibility(boolean isVisibility) {
        // Sử dụng CVQueryService để fetch CV theo visibility với children
        return cvQueryService.findByVisibilityWithChildren(isVisibility, org.springframework.data.domain.Pageable.unpaged()).getContent();
    }

    public long handleGetCVsCountByVisibility(boolean isVisibility) {
        return cvQueryRepository.countByVisibility(isVisibility);
    }

    public List<CVDto> handleGetCVsCreatedInRange(Instant startDate, Instant endDate) {
        return cvQueryRepository.findCVsCreatedBetween(startDate, endDate, org.springframework.data.domain.Pageable.unpaged()).stream()
                .map(cvMapper::toDto)
                .collect(Collectors.toList());
    }

    public long handleGetCVsCountCreatedInRange(Instant startDate, Instant endDate) {
        return cvQueryRepository.countCVsCreatedBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<CVDto> handleGetRecentCVs(int limit) {
        // Sử dụng CVQueryService để fetch recent CVs với children
        return cvQueryService.findRecentCVsWithChildren(org.springframework.data.domain.Pageable.unpaged()).stream()
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
        UserDto user = userFeignClient.getUserById(userId).getUser();
        if (user == null) {
            return new ArrayList<>();
        }

        // Sử dụng CVQueryService để fetch user CVs với children
        List<CVDto> cvs = cvQueryService.findAllByUserIdWithChildren(userId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        if (cvs == null || cvs.isEmpty()) {
            return new ArrayList<>();
        }

        return cvs.stream()
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
            Optional<CV> cvOpt = cvQueryRepository.findByTitle(title);

            if (!cvOpt.isPresent()) {
                throw new OurException("CV not found with title: " + title, 404);
            }

            CV cv = cvOpt.get();
            // Fetch CV with children using query service
            CVDto cvDto = cvQueryService.findByIdWithChildren(cv.getId())
                    .orElseThrow(() -> new OurException("CV not found", 404));

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
    @Async
    public void handleUpdateCV(UUID cvId,
            String title,
            PersonalInfoDto personalInfoDto,
            MultipartFile avatar,
            List<ExperienceDto> experiencesDto,
            List<EducationDto> educationsDto,
            List<String> skills,
            Boolean isVisibility,
            String color,
            String template,
            String font) {
        try {
            CV existing = simpleCVRepository.findById(cvId)
                    .orElseThrow(() -> new OurException("CV not found", 404));

            Instant now = Instant.now();
            boolean hasUpdates = false;

            // Only update if not null
            if (title != null && !title.trim().isEmpty()) {
                cvCommandRepository.updateCVTitle(cvId, title, now);
                hasUpdates = true;
            }
            if (color != null && !color.trim().isEmpty()) {
                cvCommandRepository.updateCVColor(cvId, color, now);
                hasUpdates = true;
            }
            if (template != null && !template.trim().isEmpty()) {
                cvCommandRepository.updateCVTemplate(cvId, template, now);
                hasUpdates = true;
            }
            if (font != null && !font.trim().isEmpty()) {
                cvCommandRepository.updateCVFont(cvId, font, now);
                hasUpdates = true;
            }

            // Update PersonalInfo separately
            if (personalInfoDto != null) {
                Optional<PersonalInfo> optionalPi = personalInfoQueryRepository.findByCvId(cvId);
                
                if (optionalPi.isPresent()) {
                    PersonalInfo pi = optionalPi.get();
                    
                    // Build updated values (preserve existing if null)
                    String fullname = personalInfoDto.getFullname() != null && !personalInfoDto.getFullname().trim().isEmpty() 
                        ? personalInfoDto.getFullname() : pi.getFullname();
                    String email = personalInfoDto.getEmail() != null && !personalInfoDto.getEmail().trim().isEmpty()
                        ? personalInfoDto.getEmail() : pi.getEmail();
                    String phone = personalInfoDto.getPhone() != null ? personalInfoDto.getPhone() : pi.getPhone();
                    String location = personalInfoDto.getLocation() != null ? personalInfoDto.getLocation() : pi.getLocation();
                    String summary = personalInfoDto.getSummary() != null ? personalInfoDto.getSummary() : pi.getSummary();
                    String birth = pi.getBirth();

                    if (avatar != null && !avatar.isEmpty()) {
                        String oldAvatarPublicId = pi.getAvatarPublicId();
                        if (oldAvatarPublicId != null && !oldAvatarPublicId.isEmpty()) {
                            cloudinaryService.deleteImage(oldAvatarPublicId);
                        }

                        var uploadResult = cloudinaryService.uploadImage(avatar);
                        if (uploadResult.containsKey("error")) {
                            throw new RuntimeException("Failed to upload avatar: " + uploadResult.get("error"));
                        }

                        personalInfoCommandRepository.updatePersonalInfoAvatar(
                            pi.getId(),
                            (String) uploadResult.get("url"),
                            (String) uploadResult.get("publicId")
                        );
                    }

                    personalInfoCommandRepository.updatePersonalInfoBasic(
                        pi.getId(),
                        fullname,
                        email,
                        phone,
                        location,
                        birth,
                        summary
                    );
                } else {
                    // Create new PersonalInfo if not exists
                    PersonalInfo pi = buildPersonalInfo(personalInfoDto, avatar);
                    UUID personalInfoId = UUID.randomUUID();
                    personalInfoCommandRepository.insertPersonalInfo(
                        personalInfoId,
                        cvId,
                        pi.getFullname(),
                        pi.getEmail(),
                        pi.getPhone(),
                        pi.getLocation(),
                        pi.getBirth(),
                        pi.getSummary(),
                        pi.getAvatarUrl(),
                        pi.getAvatarPublicId()
                    );
                }
                hasUpdates = true;
            }

            // Update experiences separately
            if (experiencesDto != null) {
                // Delete old experiences
                experienceCommandRepository.deleteAllByCvId(cvId);
                
                // Add new experiences
                for (ExperienceDto e : experiencesDto) {
                    Experience ex = new Experience(e);
                    UUID expId = UUID.randomUUID();
                    experienceCommandRepository.insertExperience(
                        expId,
                        cvId,
                        ex.getCompany(),
                        ex.getPosition(),
                        ex.getStartDate(),
                        ex.getEndDate(),
                        ex.getDescription()
                    );
                }
                hasUpdates = true;
            }

            // Update educations separately
            if (educationsDto != null) {
                // Delete old educations
                educationCommandRepository.deleteAllByCvId(cvId);
                
                // Add new educations
                for (EducationDto ed : educationsDto) {
                    Education e = new Education(ed);
                    UUID eduId = UUID.randomUUID();
                    educationCommandRepository.insertEducation(
                        eduId,
                        cvId,
                        e.getSchool(),
                        e.getDegree(),
                        e.getField(),
                        e.getStartDate(),
                        e.getEndDate()
                    );
                }
                hasUpdates = true;
            }

            // Only update skills if provided
            if (skills != null) {
                cvCommandRepository.updateCVSkills(cvId, skills, now);
                hasUpdates = true;
            }

            // Only update isVisibility if provided
            if (isVisibility != null) {
                cvCommandRepository.updateCVVisibility(cvId, isVisibility, now);
                hasUpdates = true;
            }

            if (hasUpdates) {
                cvCommandRepository.updateCVUpdatedAt(cvId, now);
            }

            logger.info("Async update completed for CV id={} (userId={})", cvId, existing.getUserId());
        } catch (OurException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Async update failed for CV id={}: {}", cvId, e.getMessage(), e);
        }
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
            String font = request.getFont();

            handleUpdateCV(cvId, title, personalInfo, avatar, experiences, educations, skills, isVisibility, color, template, font);
            
            logger.info("CV update initiated for cvId={} - processing asynchronously", cvId);
           
            response.setStatusCode(200);
            response.setMessage("CV update initiated successfully - processing in background");
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
        CV cv = simpleCVRepository.findById(cvId).orElseThrow(() -> new OurException("CV not found", 404));

        // Delete avatar from Cloudinary if exists
        Optional<PersonalInfo> personalInfo = personalInfoQueryRepository.findByCvId(cvId);
        if (personalInfo.isPresent() && personalInfo.get().getAvatarPublicId() != null) {
            cloudinaryService.deleteImage(personalInfo.get().getAvatarPublicId());
            personalInfoCommandRepository.deletePersonalInfoById(personalInfo.get().getId());
        }

        // Delete related entities using command repositories
        experienceCommandRepository.deleteAllByCvId(cvId);
        educationCommandRepository.deleteAllByCvId(cvId);

        // Delete the CV
        cvCommandRepository.deleteCVById(cvId);
        return true;
    }

    public CVDto handleDuplicateCV(UUID cvId, UUID userId) {
        logger.info("Duplicating CV id={} for userId={}", cvId, userId);
        CVDto existingCV = handleGetCVById(cvId);
        
                UserDto user = userFeignClient.getUserById(userId).getUser();
        
                if (user == null) {
                    logger.warn("User not found when duplicating CV: userId={}", userId);
                    throw new OurException("User not found", 404);
                }

        if (existingCV == null) {
            return handleCreateNew(userId);
        }

        PersonalInfoDto personalInfoDto = handleSetUserPersonalInfo(user);

        CVDto newCV = handleCreateCV(
                userId,
                existingCV.getTitle() + " (Copy)",
                personalInfoDto,
                null,
                existingCV.getExperiences(),
                existingCV.getEducations(),
                new ArrayList<>(existingCV.getSkills()),
                existingCV.getIsVisibility(),
                existingCV.getColor(),
                existingCV.getTemplate(),
                existingCV.getFont()
            );

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

    public Response duplicateCV(UUID cvId, UUID userId) {
        Response response = new Response();

        try {
            CVDto duplicatedCV = handleDuplicateCV(cvId, userId);

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