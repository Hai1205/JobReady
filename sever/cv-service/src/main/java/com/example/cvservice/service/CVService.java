package com.example.cvservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cvservice.dto.*;
import com.example.cvservice.entity.CV;
import com.example.cvservice.entity.Education;
import com.example.cvservice.entity.Experience;
import com.example.cvservice.entity.PersonalInfo;
import com.example.cvservice.repository.CVRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CVService {

    @Autowired
    private CVRepository cvRepository;

    public CVDto createCV(CVDto dto) {
        // simple uniqueness check by email
        if (dto.getPersonalInfo() != null && dto.getPersonalInfo().getEmail() != null) {
            if (cvRepository.existsByPersonalInfoEmail(dto.getPersonalInfo().getEmail())) {
                throw new RuntimeException("Email already exists");
            }
        }

        CV entity = convertToEntity(dto);
        CV saved = cvRepository.save(entity);
        return convertToDto(saved);
    }

    public List<CVDto> getAllCVs() {
        return cvRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public CVDto getCVById(Long id) {
        CV cv = cvRepository.findById(id).orElseThrow(() -> new RuntimeException("CV not found"));
        return convertToDto(cv);
    }

    public CVDto getCVByTitle(String title) {
        Optional<CV> cv = cvRepository.findByTitle(title);
        return cv.map(this::convertToDto).orElse(null);
    }

    public CVDto updateCV(Long id, CVDto dto) {
        CV existing = cvRepository.findById(id).orElseThrow(() -> new RuntimeException("CV not found"));

        // update fields
        existing.setTitle(dto.getTitle());

        if (dto.getPersonalInfo() != null) {
            PersonalInfo pi = existing.getPersonalInfo();
            if (pi == null)
                pi = new PersonalInfo();
            pi.setFullName(dto.getPersonalInfo().getFullName());
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
        return convertToDto(saved);
    }

    public void deleteCV(Long id) {
        if (!cvRepository.existsById(id))
            throw new RuntimeException("CV not found");
        cvRepository.deleteById(id);
    }

    // mapping helpers
    private CVDto convertToDto(CV cv) {
        CVDto dto = new CVDto();
        dto.setId(cv.getId() == null ? null : String.valueOf(cv.getId()));
        dto.setTitle(cv.getTitle());

        if (cv.getPersonalInfo() != null) {
            PersonalInfoDto pid = new PersonalInfoDto();
            pid.setFullName(cv.getPersonalInfo().getFullName());
            pid.setEmail(cv.getPersonalInfo().getEmail());
            pid.setPhone(cv.getPersonalInfo().getPhone());
            pid.setLocation(cv.getPersonalInfo().getLocation());
            pid.setSummary(cv.getPersonalInfo().getSummary());
            dto.setPersonalInfo(pid);
        }

        if (cv.getExperience() != null) {
            dto.setExperience(cv.getExperience().stream().map(e -> {
                ExperienceDto ed = new ExperienceDto();
                ed.setId(e.getId() == null ? null : String.valueOf(e.getId()));
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
                e.setId(ed.getId() == null ? null : String.valueOf(ed.getId()));
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
            pi.setFullName(dto.getPersonalInfo().getFullName());
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