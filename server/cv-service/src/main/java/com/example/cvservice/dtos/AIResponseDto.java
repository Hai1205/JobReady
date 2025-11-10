package com.example.cvservice.dtos;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIResponseDto {
    CVDto cv;
    List<AISuggestionDto> suggestions;
    AnalyzeResultDto analyzeResult; // Changed from String to AnalyzeResultDto
    String improved;
    JobDescriptionResult jdResult;
    Double matchScore;
    List<String> missingKeywords;
    String extractedText;
}
