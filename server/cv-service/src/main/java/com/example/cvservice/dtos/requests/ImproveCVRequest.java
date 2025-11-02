package com.example.cvservice.dtos.requests;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImproveCVRequest {
    private String section;
    private String content;
}
