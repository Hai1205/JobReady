package com.example.aiservice.dtos;

import java.util.List;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIResponseDto {
    CVDto cv;
    List<AISuggestionDto> suggestions;
    String analyzeResult;
    String improved;
    JobDescriptionResult jdResult;
    Double matchScore;
    List<String> missingKeywords;
    String extractedText;
}
