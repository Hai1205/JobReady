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

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CVService {

    private final CVRepository cvRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final PersonalInfoRepository personalInfoRepository;

    private OpenRouterConfig openRouterConfig;
    private final CVMapper cvMapper;

    public CVService(
            CVRepository cvRepository,
            EducationRepository educationRepository,
            ExperienceRepository experienceRepository,
            PersonalInfoRepository personalInfoRepository,
            CVMapper cvMapper) {
        this.cvRepository = cvRepository;
        this.educationRepository = educationRepository;
        this.experienceRepository = experienceRepository;
        this.personalInfoRepository = personalInfoRepository;
        this.cvMapper = cvMapper;
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
                .map(e -> new Experience(e.getCompany(), e.getPosition(), e.getStartDate(), e.getEndDate(),
                        e.getDescription()))
                .collect(Collectors.toList());

        List<Education> educations = educationsDto.stream()
                .map(ed -> new Education(ed.getSchool(), ed.getDegree(), ed.getField(), ed.getStartDate(),
                        ed.getEndDate()))
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

            String prompt = "Analyze the following CV and provide insights:\n\n" + cvDto.toString();
            String result = openRouterConfig.callModel(prompt);

            ResponseData data = new ResponseData();
            data.setCv(cvDto);
            data.setAnalysis(result);

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
        }

        return response;
    }

    public Response improveCV(UUID cvId, ImproveCVRequest request) {
        Response response = new Response();

        try {
            CVDto cvDto = handleGetCVById(cvId);

            String prompt = String.format(
                    "You are an expert resume writer. Improve the following %s section to make it professional, concise, and effective:\n\n%s",
                    request.getSection(),
                    request.getContent());

            String improved = openRouterConfig.callModel(prompt);

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

    public Response importFile(UUID cvId, MultipartFile file) {
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
}