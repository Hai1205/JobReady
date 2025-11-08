package com.example.aiservice.services.grpcs;

import com.example.aiservice.dtos.AIResponseDto;
import com.example.aiservice.dtos.CVDto;
import com.example.aiservice.services.apis.AIService;
import com.example.grpc.ai.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@GrpcService
public class AIGrpcService extends AIServiceGrpc.AIServiceImplBase {

    @Autowired
    private AIService aiService;

    @Override
    public void analyzeCV(AnalyzeCVRequest request, StreamObserver<AIResponse> responseObserver) {
        log.info("=== gRPC AI Service: Received analyzeCV request ===");

        try {
            // Convert proto CV to CVDto
            CVDto cvDto = convertProtoCVToDto(request.getCv());

            log.info("Processing CV analysis for: {}", cvDto.getTitle());
            AIResponseDto aiResponse = aiService.analyzeCV(cvDto);

            AIResponse response = AIResponse.newBuilder()
                    .setCode(200)
                    .setMessage("CV analyzed successfully")
                    .setData(convertToProtoAIResponse(aiResponse))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            log.info("CV analyzed successfully via gRPC");
        } catch (Exception e) {
            log.error("Error analyzing CV via gRPC: {}", e.getMessage(), e);
            AIResponse response = AIResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Error analyzing CV: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void improveCV(ImproveCVRequest request, StreamObserver<AIResponse> responseObserver) {
        log.info("=== gRPC AI Service: Received improveCV request ===");

        try {
            String section = request.getSection();
            String content = request.getContent();

            log.info("Improving CV section: {}", section);
            AIResponseDto aiResponse = aiService.improveCV(section, content);

            AIResponse response = AIResponse.newBuilder()
                    .setCode(200)
                    .setMessage("CV improved successfully")
                    .setData(convertToProtoAIResponse(aiResponse))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            log.info("CV improved successfully via gRPC");
        } catch (Exception e) {
            log.error("Error improving CV via gRPC: {}", e.getMessage(), e);
            AIResponse response = AIResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Error improving CV: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void analyzeCVWithJobDescription(AnalyzeCVWithJDRequest request,
            StreamObserver<AIResponse> responseObserver) {
        log.info("=== gRPC AI Service: Received analyzeCVWithJobDescription request ===");

        try {
            CVDto cvDto = convertProtoCVToDto(request.getCv());
            String language = request.getLanguage();
            String jdText = request.getJdText();

            log.info("Analyzing CV with job description, language: {}", language);
            AIResponseDto aiResponse = aiService.analyzeCVWithJobDescription(cvDto, language, jdText);

            AIResponse response = AIResponse.newBuilder()
                    .setCode(200)
                    .setMessage("CV analyzed with job description successfully")
                    .setData(convertToProtoAIResponse(aiResponse))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            log.info("CV analyzed with job description successfully via gRPC");
        } catch (Exception e) {
            log.error("Error analyzing CV with job description via gRPC: {}", e.getMessage(), e);
            AIResponse response = AIResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Error analyzing CV with job description: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    private CVDto convertProtoCVToDto(CV protoCv) {
        try {
            // Simple conversion - can be enhanced based on actual CVDto structure
            CVDto cvDto = new CVDto();
            // Map basic fields - adjust based on your actual CVDto structure
            if (!protoCv.getId().isEmpty()) {
                cvDto.setId(java.util.UUID.fromString(protoCv.getId()));
            }
            if (!protoCv.getUserId().isEmpty()) {
                cvDto.setUserId(java.util.UUID.fromString(protoCv.getUserId()));
            }
            cvDto.setTitle(protoCv.getTitle());

            // Note: You may need to map other nested fields based on your CVDto structure
            return cvDto;
        } catch (Exception e) {
            log.error("Error converting proto CV to DTO: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert CV", e);
        }
    }

    private AIResponseData convertToProtoAIResponse(AIResponseDto aiResponseDto) {
        AIResponseData.Builder builder = AIResponseData.newBuilder();

        if (aiResponseDto.getAnalyzeResult() != null) {
            builder.setAnalysis(aiResponseDto.getAnalyzeResult());
        }
        if (aiResponseDto.getSuggestions() != null && !aiResponseDto.getSuggestions().isEmpty()) {
            // Combine suggestions into a single string
            StringBuilder suggestionText = new StringBuilder();
            for (int i = 0; i < aiResponseDto.getSuggestions().size(); i++) {
                suggestionText.append(aiResponseDto.getSuggestions().get(i).toString());
                if (i < aiResponseDto.getSuggestions().size() - 1) {
                    suggestionText.append("\n");
                }
            }
            builder.setSuggestion(suggestionText.toString());
        }
        if (aiResponseDto.getImproved() != null) {
            builder.setImprovedContent(aiResponseDto.getImproved());
        }

        return builder.build();
    }
}
