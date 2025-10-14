package com.example.rabbitmq.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RabbitResponse<T> {
    private int code;
    private String message;
    private T data;
}