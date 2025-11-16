package com.example.aiservice.services.grpcs.clients;

import com.example.grpc.cv.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CVGrpcClient {

    @GrpcClient("cv-service")
    private CVServiceGrpc.CVServiceBlockingStub cvServiceStub;

    public CreateCVResponse createCV(
            String userId,
            String title,
            PersonalInfo personalInfo,
            List<Experience> experiences,
            List<Education> educations,
            List<String> skills) {
        try {
            CreateCVRequest request = CreateCVRequest.newBuilder()
                    .setUserId(userId)
                    .setTitle(title)
                    .setPersonalInfo(personalInfo)
                    .addAllExperiences(experiences)
                    .addAllEducations(educations)
                    .addAllSkills(skills)
                    .build();
            return cvServiceStub.createCV(request);
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            throw e;
        }
    }
}
