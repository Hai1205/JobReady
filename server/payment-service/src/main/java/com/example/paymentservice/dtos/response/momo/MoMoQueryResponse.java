package com.example.paymentservice.dtos.response.momo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho response query status transaction
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoMoQueryResponse {
    
    private String partnerCode;
    private String accessKey;
    private String requestId;
    private String orderId;
    private Integer errorCode;
    private Long transId;
    private Long amount;
    private String message;
    private String localMessage;
    private String requestType;
    private String payType;
    private String extraData;
    private String signature;
    private Long responseTime;
    private Integer resultCode;
}
