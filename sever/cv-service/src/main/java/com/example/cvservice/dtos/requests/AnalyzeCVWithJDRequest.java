package com.example.cvservice.dtos.requests;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeCVWithJDRequest {
    // optional raw text
    private String jobDescription;

    // optional uploaded file (.pdf or .docx)
    private MultipartFile jdFile;

    // desired output language: 'en' or 'vi' (default 'vi')
    private String language = "vi";
}
