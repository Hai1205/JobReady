package com.example.contactservice.dtos.requests;

import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitContactRequest {
    private String name;
    private String email;
    private String plan;
    private String phone;
    private String message;
}
