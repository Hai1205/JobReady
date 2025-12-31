package com.example.paymentservice.dtos.requests.momo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho request tạo thanh toán MoMo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoMoPaymentRequest {
    
    private String partnerCode;
    private String accessKey;
    private String requestId;
    private Long amount;
    private String orderId;
    private String orderInfo;
    private String redirectUrl;
    private String ipnUrl;
    private String requestType;
    private String extraData;
    private String orderType;  // QUAN TRỌNG: Bắt buộc phải có
    private String lang;
    private String signature;
}
