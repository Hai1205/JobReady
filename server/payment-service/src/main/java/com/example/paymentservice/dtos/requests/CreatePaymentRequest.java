package com.example.paymentservice.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO cho request tạo thanh toán từ client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    
    private Long amount;
    private String orderInfo;
    private String extraData;
    
    // Invoice information
    private UUID userId;
    private UUID planId;
    private String planName;
}
