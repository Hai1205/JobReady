package com.example.cvservice.services.grpcs;

import com.example.cvservice.dtos.AIResponseDto;
import com.example.cvservice.dtos.CVDto;
import com.example.grpc.ai.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AIGrpcClient {

    @GrpcClient("ai-service")
    private AIServiceGrpc.AIServiceBlockingStub aiServiceStub;

    public AIResponseDto analyzeCV(CVDto cvDto) {
        try {
            log.info("Calling AI service via gRPC to analyze CV: {}", cvDto.getTitle());

            CV protoCV = convertDtoToProtoCV(cvDto);
            AnalyzeCVRequest request = AnalyzeCVRequest.newBuilder()
                    .setCv(protoCV)
                    .build();

            AIResponse response = aiServiceStub.analyzeCV(request);

            if (response.getCode() == 200) {
                return convertProtoToAIResponseDto(response.getData());
            }
            log.error("AI service returned error: {}", response.getMessage());
            return null;
        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed: {}", e.getStatus());
            return null;
        }
    }

    public AIResponseDto improveCV(String section, String content) {
        try {
            log.info("Calling AI service via gRPC to improve CV section: {}", section);

            ImproveCVRequest request = ImproveCVRequest.newBuilder()
                    .setSection(section)
                    .setContent(content)
                    .build();

            AIResponse response = aiServiceStub.improveCV(request);

            if (response.getCode() == 200) {
                return convertProtoToAIResponseDto(response.getData());
            }
            log.error("AI service returned error: {}", response.getMessage());
            return null;
        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed: {}", e.getStatus());
            return null;
        }
    }

    public AIResponseDto analyzeCVWithJobDescription(CVDto cvDto, String language, String jdText) {
        try {
            log.info("Calling AI service via gRPC to analyze CV with job description");

            CV protoCV = convertDtoToProtoCV(cvDto);
            AnalyzeCVWithJDRequest request = AnalyzeCVWithJDRequest.newBuilder()
                    .setCv(protoCV)
                    .setLanguage(language)
                    .setJdText(jdText)
                    .build();

            AIResponse response = aiServiceStub.analyzeCVWithJobDescription(request);

            if (response.getCode() == 200) {
                return convertProtoToAIResponseDto(response.getData());
            }
            log.error("AI service returned error: {}", response.getMessage());
            return null;
        } catch (StatusRuntimeException e) {
            log.error("gRPC call failed: {}", e.getStatus());
            return null;
        }
    }

    private CV convertDtoToProtoCV(CVDto cvDto) {
        CV.Builder builder = CV.newBuilder();

        if (cvDto.getId() != null) {
            builder.setId(cvDto.getId().toString());
        }
        if (cvDto.getUserId() != null) {
            builder.setUserId(cvDto.getUserId().toString());
        }
        if (cvDto.getTitle() != null) {
            builder.setTitle(cvDto.getTitle());
        }

        // Note: Add more field mappings as needed based on your CVDto structure

        return builder.build();
    }

    private AIResponseDto convertProtoToAIResponseDto(AIResponseData protoData) {
        AIResponseDto dto = new AIResponseDto();

        if (!protoData.getAnalysis().isEmpty()) {
            dto.setAnalyzeResult(protoData.getAnalysis());
        }
        if (!protoData.getImprovedContent().isEmpty()) {
            dto.setImproved(protoData.getImprovedContent());
        }

        return dto;
    }
}
