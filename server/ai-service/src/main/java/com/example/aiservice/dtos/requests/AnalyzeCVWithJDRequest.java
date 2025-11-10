package com.example.aiservice.dtos.requests;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

// @Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeCVWithJDRequest extends AnalyzeCVRequest {
    // optional raw text
    private String jobDescription;

    // optional uploaded file (.pdf or .docx)
    private MultipartFile jdFile;

    // desired output language: 'en' or 'vi' (default 'vi')
    private String language = "vi";

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public MultipartFile getJdFile() {
        return jdFile;
    }

    public void setJdFile(MultipartFile jdFile) {
        this.jdFile = jdFile;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
