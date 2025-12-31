package com.example.paymentservice.dtos.requests.paypal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request để tạo thanh toán PayPal
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayPalPaymentRequest {

    private Double amount;
    private String currency; // USD, VND, EUR, ...
    private String method; // paypal
    private String intent; // sale, authorize, order
    private String description;
    private String cancelUrl;
    private String successUrl;
    
    // Invoice information
    private UUID userId;
    private UUID planId;
    private String planName;
}
