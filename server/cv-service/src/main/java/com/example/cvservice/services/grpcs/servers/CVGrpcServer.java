package com.example.cvservice.services.grpcs.servers;

import com.example.cvservice.dtos.CVDto;
import com.example.cvservice.dtos.PersonalInfoDto;
import com.example.cvservice.dtos.ExperienceDto;
import com.example.cvservice.dtos.EducationDto;
import com.example.cvservice.services.apis.CVApi;
import com.example.grpc.cv.*;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CVGrpcServer extends CVServiceGrpc.CVServiceImplBase {

    private final CVApi cvApi;

    @Override
    public void getTotalCVs(GetTotalCVsRequest request, StreamObserver<GetTotalCVsResponse> responseObserver) {
        try {
            long total = cvApi.handleGetTotalCVs();

            GetTotalCVsResponse response = GetTotalCVsResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Success")
                    .setTotal(total)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("getTotalCVs: {}", total);
        } catch (Exception e) {
            log.error("Error getting total CVs", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getCVsByVisibility(GetCVsByVisibilityRequest request,
            StreamObserver<GetCVsByVisibilityResponse> responseObserver) {
        try {
            long count = cvApi.handleGetCVsCountByVisibility(request.getIsVisibility());

            GetCVsByVisibilityResponse response = GetCVsByVisibilityResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Success")
                    .setCount(count)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("getCVsByVisibility: visibility={}, count={}", request.getIsVisibility(), count);
        } catch (Exception e) {
            log.error("Error getting CVs by visibility", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getCVsCreatedInRange(GetCVsCreatedInRangeRequest request,
            StreamObserver<GetCVsCreatedInRangeResponse> responseObserver) {
        try {
            Instant startDate = Instant.parse(request.getStartDate());
            Instant endDate = Instant.parse(request.getEndDate());

            long count = cvApi.handleGetCVsCountCreatedInRange(startDate, endDate);

            GetCVsCreatedInRangeResponse response = GetCVsCreatedInRangeResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Success")
                    .setCount(count)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("getCVsCreatedInRange: start={}, end={}, count={}",
                    request.getStartDate(), request.getEndDate(), count);
        } catch (Exception e) {
            log.error("Error getting CVs created in range", e);
            responseObserver.onError(e);
        }
    }

    private CVInfo toCVInfo(CVDto cv) {
        CVInfo.Builder builder = CVInfo.newBuilder()
                .setId(cv.getId().toString())
                .setTitle(cv.getTitle())
                .setUserId(cv.getUserId().toString())
                .setCreatedAt(cv.getCreatedAt())
                .setIsVisibility(cv.getIsVisibility());

        // Add PersonalInfo if present
        if (cv.getPersonalInfo() != null) {
            PersonalInfoDto piDto = cv.getPersonalInfo();
            com.example.grpc.cv.PersonalInfo.Builder piBuilder = com.example.grpc.cv.PersonalInfo.newBuilder()
                    .setFullname(piDto.getFullname() != null ? piDto.getFullname() : "")
                    .setEmail(piDto.getEmail() != null ? piDto.getEmail() : "")
                    .setPhone(piDto.getPhone() != null ? piDto.getPhone() : "")
                    .setLocation(piDto.getLocation() != null ? piDto.getLocation() : "")
                    .setSummary(piDto.getSummary() != null ? piDto.getSummary() : "")
                    .setAvatarUrl(piDto.getAvatarUrl() != null ? piDto.getAvatarUrl() : "")
                    .setAvatarPublicId(piDto.getAvatarPublicId() != null ? piDto.getAvatarPublicId() : "");
            
            if (piDto.getId() != null) {
                piBuilder.setId(piDto.getId().toString());
            }
            
            builder.setPersonalInfo(piBuilder.build());
        }

        // Add Experiences if present
        if (cv.getExperiences() != null && !cv.getExperiences().isEmpty()) {
            List<com.example.grpc.cv.Experience> experiences = cv.getExperiences().stream()
                    .map(exp -> {
                        com.example.grpc.cv.Experience.Builder expBuilder = com.example.grpc.cv.Experience.newBuilder()
                                .setCompany(exp.getCompany() != null ? exp.getCompany() : "")
                                .setPosition(exp.getPosition() != null ? exp.getPosition() : "")
                                .setStartDate(exp.getStartDate() != null ? exp.getStartDate() : "")
                                .setEndDate(exp.getEndDate() != null ? exp.getEndDate() : "")
                                .setDescription(exp.getDescription() != null ? exp.getDescription() : "");
                        
                        if (exp.getId() != null) {
                            expBuilder.setId(exp.getId().toString());
                        }
                        
                        return expBuilder.build();
                    })
                    .collect(Collectors.toList());
            builder.addAllExperiences(experiences);
        }

        // Add Educations if present
        if (cv.getEducations() != null && !cv.getEducations().isEmpty()) {
            List<com.example.grpc.cv.Education> educations = cv.getEducations().stream()
                    .map(edu -> {
                        com.example.grpc.cv.Education.Builder eduBuilder = com.example.grpc.cv.Education.newBuilder()
                                .setSchool(edu.getSchool() != null ? edu.getSchool() : "")
                                .setDegree(edu.getDegree() != null ? edu.getDegree() : "")
                                .setField(edu.getField() != null ? edu.getField() : "")
                                .setStartDate(edu.getStartDate() != null ? edu.getStartDate() : "")
                                .setEndDate(edu.getEndDate() != null ? edu.getEndDate() : "");
                        
                        if (edu.getId() != null) {
                            eduBuilder.setId(edu.getId().toString());
                        }
                        
                        return eduBuilder.build();
                    })
                    .collect(Collectors.toList());
            builder.addAllEducations(educations);
        }

        // Add Skills if present
        if (cv.getSkills() != null && !cv.getSkills().isEmpty()) {
            builder.addAllSkills(cv.getSkills());
        }

        return builder.build();
    }

    @Override
    public void getRecentCVs(GetRecentCVsRequest request,
            StreamObserver<GetRecentCVsResponse> responseObserver) {
        try {
            List<CVDto> recentCVDtos = cvApi.handleGetRecentCVs(request.getLimit());

            List<CVInfo> recentCVs = recentCVDtos.stream()
                    .map(this::toCVInfo)
                    .toList();

            GetRecentCVsResponse response = GetRecentCVsResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Success")
                    .addAllCvs(recentCVs)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("getRecentCVs: limit={}, found={}", request.getLimit(), recentCVs.size());
        } catch (Exception e) {
            log.error("Error getting recent CVs", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void createCV(CreateCVRequest request,
            StreamObserver<CreateCVResponse> responseObserver) {
        try {
            // CVDto cvDto = CVDto.builder()
            //         .userId(UUID.fromString(request.getUserId()))
            //         .title(request.getTitle())
            //         .personalInfo(convertPersonalInfo(request.getPersonalInfo()))
            //         .experiences(request.getExperiencesList().stream().map(this::convertExperience)
            //                 .collect(Collectors.toList()))
            //         .educations(request.getEducationsList().stream().map(this::convertEducation)
            //                 .collect(Collectors.toList()))
            //         .skills(new ArrayList<>(request.getSkillsList()))
            //         .build();
            
            UUID userId = UUID.fromString(request.getUserId());
            String title = request.getTitle();
            PersonalInfoDto personalInfo = convertPersonalInfo(request.getPersonalInfo());
            List<ExperienceDto> experiences = request.getExperiencesList().stream()
                    .map(this::convertExperience)
                    .collect(Collectors.toList());
            List<EducationDto> educations = request.getEducationsList().stream()
                    .map(this::convertEducation)
                    .collect(Collectors.toList());
            List<String> skills = new ArrayList<>(request.getSkillsList());

            CVDto createdCV = cvApi.handleCreateCV(userId, title, personalInfo, null, experiences, educations, skills, false, "#3498db", "modern");

            CVInfo createdCVInfo = toCVInfo(createdCV);

            CreateCVResponse response = CreateCVResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Success")
                    .setCv(createdCVInfo)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("createCV: userId={}, cvId={}", userId, createdCV.getId());
        } catch (Exception e) {
            log.error("Error creating CV", e);
            responseObserver.onError(e);
        }
    }

    private PersonalInfoDto convertPersonalInfo(com.example.grpc.cv.PersonalInfo pi) {
        return new PersonalInfoDto(
                pi.getId().isEmpty() ? null : UUID.fromString(pi.getId()),
                pi.getFullname(),
                pi.getEmail(),
                pi.getPhone(),
                pi.getLocation(),
                pi.getSummary(),
                pi.getAvatarUrl(),
                pi.getAvatarPublicId());
    }

    private ExperienceDto convertExperience(com.example.grpc.cv.Experience exp) {
        return new ExperienceDto(
                exp.getId().isEmpty() ? null : UUID.fromString(exp.getId()),
                exp.getCompany(),
                exp.getPosition(),
                exp.getStartDate(),
                exp.getEndDate(),
                exp.getDescription());
    }

    private EducationDto convertEducation(com.example.grpc.cv.Education edu) {
        return new EducationDto(
                edu.getId().isEmpty() ? null : UUID.fromString(edu.getId()),
                edu.getSchool(),
                edu.getDegree(),
                edu.getField(),
                edu.getStartDate(),
                edu.getEndDate());
    }
}
