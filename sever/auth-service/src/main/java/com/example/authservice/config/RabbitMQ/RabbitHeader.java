package com.example.authservice.config.RabbitMQ;

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
    private String type;
    private long timestamp;
}