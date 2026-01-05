package com.example.paymentservice.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDto {
    private UUID id;
    private UUID userId;
    
    private String planTitle;
    private Integer amount;
    private String currency;
    private String status;
    private String paymentMethod;
    private String transactionId;
    private String billingDate;
    private String periodStart;
    private String periodEnd;
    private String description;
    private String downloadUrl;
}
