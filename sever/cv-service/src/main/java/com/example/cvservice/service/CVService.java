package com.example.cvservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cvservice.dto.*;
import com.example.cvservice.dto.responses.*;
import com.example.cvservice.entity.*;
import com.example.cvservice.repository.CVRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CVService {

    @Autowired
    private CVRepository cvRepository;

    public Response createCV(CVDto dto) {
        Response response = new Response();

        try {
            // simple uniqueness check by email
            if (dto.getPersonalInfo() != null && dto.getPersonalInfo().getEmail() != null) {
                if (cvRepository.existsByPersonalInfoEmail(dto.getPersonalInfo().getEmail())) {
                    throw new RuntimeException("Email already exists");
                }
            }

            CV entity = convertToEntity(dto);
            CV saved = cvRepository.save(entity);
            CVDto savedDto = convertToDto(saved);

            ResponseData data = new ResponseData();
            data.setCv(savedDto);

            response.setStatusCode(201);
            response.setMessage("CV created successfully");
            response.setData(data);
        } catch (RuntimeException e) {
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

    public Response getAllCVs() {
        Response response = new Response();

        try {
            List<CVDto> cvDtos = cvRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

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

    public Response getCVById(UUID id) {
        Response response = new Response();

        try {
            CV cv = cvRepository.findById(id).orElseThrow(() -> new RuntimeException("CV not found"));
            CVDto cvDto = convertToDto(cv);

            ResponseData data = new ResponseData();
            data.setCv(cvDto);

            response.setStatusCode(200);
            response.setMessage("CV retrieved successfully");
            response.setData(data);
        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage("Invalid CV ID format");
            System.out.println(e.getMessage());
        } catch (RuntimeException e) {
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

            if (cvOpt.isPresent()) {
                CVDto cvDto = convertToDto(cvOpt.get());

                ResponseData data = new ResponseData();
                data.setCv(cvDto);

                response.setStatusCode(200);
                response.setMessage("CV retrieved successfully");
                response.setData(data);
            } else {
                throw new RuntimeException("CV not found with title: " + title);
            }
        } catch (RuntimeException e) {
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

    public Response updateCV(UUID id, CVDto dto) {
        Response response = new Response();

        try {
            CV existing = cvRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("CV not found"));

            // update fields
            existing.setTitle(dto.getTitle());

            if (dto.getPersonalInfo() != null) {
                PersonalInfo pi = existing.getPersonalInfo();
                if (pi == null)
                    pi = new PersonalInfo();
                pi.setFullname(dto.getPersonalInfo().getFullname());
                pi.setEmail(dto.getPersonalInfo().getEmail());
                pi.setPhone(dto.getPersonalInfo().getPhone());
                pi.setLocation(dto.getPersonalInfo().getLocation());
                pi.setSummary(dto.getPersonalInfo().getSummary());
                existing.setPersonalInfo(pi);
            }

            // replace lists
            existing.getExperience().clear();
            if (dto.getExperience() != null) {
                for (ExperienceDto e : dto.getExperience()) {
                    Experience ex = new Experience(e.getCompany(), e.getPosition(), e.getStartDate(), e.getEndDate(),
                            e.getDescription());
                    existing.getExperience().add(ex);
                }
            }

            existing.getEducation().clear();
            if (dto.getEducation() != null) {
                for (EducationDto ed : dto.getEducation()) {
                    Education e = new Education(ed.getSchool(), ed.getDegree(), ed.getField(), ed.getStartDate(),
                            ed.getEndDate());
                    existing.getEducation().add(e);
                }
            }

            existing.getSkills().clear();
            if (dto.getSkills() != null)
                existing.getSkills().addAll(dto.getSkills());

            existing.setUpdatedAt(Instant.now());

            CV saved = cvRepository.save(existing);
            CVDto updatedDto = convertToDto(saved);

            ResponseData data = new ResponseData();
            data.setCv(updatedDto);

            response.setStatusCode(200);
            response.setMessage("CV updated successfully");
            response.setData(data);
        } catch (RuntimeException e) {
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

    public Response deleteCV(UUID id) {
        Response response = new Response();

        try {
            UUID uuid = id;
            if (!cvRepository.existsById(uuid))
                throw new RuntimeException("CV not found");

            cvRepository.deleteById(uuid);

            response.setStatusCode(200);
            response.setMessage("CV deleted successfully");
        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage("Invalid CV ID format");
            System.out.println(e.getMessage());
        } catch (RuntimeException e) {
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

    // mapping helpers
    private CVDto convertToDto(CV cv) {
        CVDto dto = new CVDto();
        dto.setId(cv.getId() == null ? null : cv.getId());
        dto.setTitle(cv.getTitle());

        if (cv.getPersonalInfo() != null) {
            PersonalInfoDto pid = new PersonalInfoDto();
            pid.setFullname(cv.getPersonalInfo().getFullname());
            pid.setEmail(cv.getPersonalInfo().getEmail());
            pid.setPhone(cv.getPersonalInfo().getPhone());
            pid.setLocation(cv.getPersonalInfo().getLocation());
            pid.setSummary(cv.getPersonalInfo().getSummary());
            dto.setPersonalInfo(pid);
        }

        if (cv.getExperience() != null) {
            dto.setExperience(cv.getExperience().stream().map(e -> {
                ExperienceDto ed = new ExperienceDto();
                ed.setId(e.getId() == null ? null : e.getId());
                ed.setCompany(e.getCompany());
                ed.setPosition(e.getPosition());
                ed.setStartDate(e.getStartDate());
                ed.setEndDate(e.getEndDate());
                ed.setDescription(e.getDescription());
                return ed;
            }).collect(Collectors.toList()));
        }

        if (cv.getEducation() != null) {
            dto.setEducation(cv.getEducation().stream().map(ed -> {
                EducationDto e = new EducationDto();
                e.setId(ed.getId() == null ? null : ed.getId());
                e.setSchool(ed.getSchool());
                e.setDegree(ed.getDegree());
                e.setField(ed.getField());
                e.setStartDate(ed.getStartDate());
                e.setEndDate(ed.getEndDate());
                return e;
            }).collect(Collectors.toList()));
        }

        dto.setSkills(cv.getSkills());
        dto.setCreatedAt(cv.getCreatedAt() == null ? null : cv.getCreatedAt().toString());
        dto.setUpdatedAt(cv.getUpdatedAt() == null ? null : cv.getUpdatedAt().toString());

        return dto;
    }

    private CV convertToEntity(CVDto dto) {
        CV cv = new CV();
        cv.setTitle(dto.getTitle());

        if (dto.getPersonalInfo() != null) {
            PersonalInfo pi = new PersonalInfo();
            pi.setFullname(dto.getPersonalInfo().getFullname());
            pi.setEmail(dto.getPersonalInfo().getEmail());
            pi.setPhone(dto.getPersonalInfo().getPhone());
            pi.setLocation(dto.getPersonalInfo().getLocation());
            pi.setSummary(dto.getPersonalInfo().getSummary());
            cv.setPersonalInfo(pi);
        }

        if (dto.getExperience() != null) {
            for (ExperienceDto e : dto.getExperience()) {
                Experience ex = new Experience(e.getCompany(), e.getPosition(), e.getStartDate(), e.getEndDate(),
                        e.getDescription());
                cv.getExperience().add(ex);
            }
        }

        if (dto.getEducation() != null) {
            for (EducationDto ed : dto.getEducation()) {
                Education e = new Education(ed.getSchool(), ed.getDegree(), ed.getField(), ed.getStartDate(),
                        ed.getEndDate());
                cv.getEducation().add(e);
            }
        }

        if (dto.getSkills() != null)
            cv.getSkills().addAll(dto.getSkills());

        cv.setCreatedAt(dto.getCreatedAt() != null ? Instant.parse(dto.getCreatedAt()) : Instant.now());
        cv.setUpdatedAt(dto.getUpdatedAt() != null ? Instant.parse(dto.getUpdatedAt()) : cv.getCreatedAt());

        return cv;
    }
}