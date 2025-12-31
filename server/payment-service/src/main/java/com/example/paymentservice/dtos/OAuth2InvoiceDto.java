package com.example.paymentservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth2InvoiceDto {
    private UUID id;
    private UUID userId;
    private String email;
    private String provider;
    private String providerId;
}
