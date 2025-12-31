package com.example.paymentservice.dtos.requests.momo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho IPN callback từ MoMo sau khi thanh toán
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoMoIPNRequest {
    
    private String partnerCode;
    private String orderId;
    private String requestId;
    private Long amount;
    private String orderInfo;
    private String orderType;
    private Long transId;
    private Integer resultCode;
    private String message;
    private String payType;
    private Long responseTime;
    private String extraData;
    private String signature;
}
