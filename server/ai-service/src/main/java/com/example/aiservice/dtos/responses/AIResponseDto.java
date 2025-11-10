package com.example.aiservice.dtos.responses;

import java.util.List;

import com.example.aiservice.dtos.AISuggestionDto;
import com.example.aiservice.dtos.AnalyzeResultDto;
import com.example.aiservice.dtos.CVDto;
import com.example.aiservice.dtos.JobDescriptionResult;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIResponseDto {
    CVDto cv;
    List<AISuggestionDto> suggestions;
    AnalyzeResultDto analyzeResult;
    String improved;
    JobDescriptionResult jdResult;
    Double matchScore;
    List<String> missingKeywords;
    String extractedText;
}
