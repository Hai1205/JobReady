package com.example.cvservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RabbitHeader {
    private String correlationId;
    private String replyTo;
    private String sourceService;
    private String targetService;
    private String status; // SUCCESS | ERROR
    private long timestamp;
}
