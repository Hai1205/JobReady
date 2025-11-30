package com.example.aiservice.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CVTemplateDTO {
    private String category;
    private String level;
    private String section;
    private String content;
    private Integer rating;
    private List<String> keywords;
}