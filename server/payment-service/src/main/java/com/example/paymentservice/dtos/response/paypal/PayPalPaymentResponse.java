package com.example.paymentservice.dtos.response.paypal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response trả về sau khi tạo thanh toán PayPal thành công
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayPalPaymentResponse {

    private String paymentId;
    private String status;
    private String approvalUrl; // URL để redirect user đến PayPal
    private String message;
}
