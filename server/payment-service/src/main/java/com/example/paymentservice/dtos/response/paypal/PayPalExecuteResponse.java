package com.example.paymentservice.dtos.response.paypal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response trả về sau khi thực hiện thanh toán (execute payment)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayPalExecuteResponse {

    private String paymentId;
    private String payerId;
    private String state;
    private String amount;
    private String currency;
    private String description;
    private String message;
}
