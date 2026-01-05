package com.example.paymentservice.dtos.requests.vnpay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO cho VNPay payment request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VNPayPaymentRequest {

    private Long amount;
    private String orderInfo;
    private String orderType;
    private String locale; // "vn" hoặc "en"

    // Invoice information
    private UUID userId;
    private UUID planId;
    private String planTitle;
}
