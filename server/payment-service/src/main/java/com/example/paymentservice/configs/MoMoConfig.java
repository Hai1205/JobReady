package com.example.paymentservice.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "momo")
public class MoMoConfig {
    
    private String partnerCode;
    private String accessKey;
    private String secretKey;
    private String paymentUrl;
    private String queryUrl;
    private String refundUrl;
    private String redirectUrl;
    private String ipnUrl;
    private String requestType;
}
