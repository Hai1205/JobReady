package com.example.rabbitmq.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RabbitHeader {
    private String correlationId;
    private String replyTo;
    private String replyExchange; // Exchange to send reply to
    private String sourceService;
    private String targetService;
    private String status; // SUCCESS | ERROR
    private long timestamp;
}