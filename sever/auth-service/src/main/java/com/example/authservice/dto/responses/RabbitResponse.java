package com.example.authservice.dto.responses;

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

