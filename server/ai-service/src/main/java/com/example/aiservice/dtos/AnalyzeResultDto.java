package com.example.aiservice.dtos;

import java.util.List;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyzeResultDto {
    private Integer overallScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<AISuggestionDto> suggestions;
}
