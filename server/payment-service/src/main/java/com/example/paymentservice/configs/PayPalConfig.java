package com.example.paymentservice.configs;

import com.paypal.base.rest.APIContext;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "paypal")
public class PayPalConfig {

    private String clientId;
    private String clientSecret;
    private String mode;
    private String successUrl;
    private String cancelUrl;

    public APIContext createApiContext() throws Exception {
        if (clientId == null || clientId.startsWith("YOUR_") ||
                clientSecret == null || clientSecret.startsWith("YOUR_")) {
            throw new IllegalStateException(
                    "PayPal credentials chưa được cấu hình. " +
                            "Vui lòng cập nhật paypal.client-id và paypal.client-secret trong application.properties. "
                            +
                            "Lấy credentials tại: https://developer.paypal.com/");
        }

        java.util.Map<String, String> configMap = new java.util.HashMap<>();
        configMap.put("mode", mode != null ? mode : "sandbox");

        com.paypal.base.rest.OAuthTokenCredential credential = new com.paypal.base.rest.OAuthTokenCredential(clientId,
                clientSecret, configMap);

        APIContext context = new APIContext(credential.getAccessToken());
        context.setConfigurationMap(configMap);

        return context;
    }
}
