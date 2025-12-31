package com.example.paymentservice.dtos.response.momo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho response trả về cho MoMo khi nhận IPN
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoMoIPNResponse {
    
    private String partnerCode;
    private String requestId;
    private String orderId;
    private Integer resultCode;
    private String message;
    private Long responseTime;
    private String extraData;
    private String signature;
}
