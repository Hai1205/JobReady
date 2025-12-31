package com.example.paymentservice.dtos.requests.momo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho request query status transaction
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoMoQueryRequest {
    
    private String partnerCode;
    private String accessKey;
    private String requestId;
    private String orderId;
    private String lang;
    private String signature;
}
