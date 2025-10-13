package com.example.authservice.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for RabbitMQ RPC client
 */
@Configuration
public class RabbitRpcClientConfig {

    @Bean
    public RabbitRpcClient rabbitRpcClient(RabbitTemplate rabbitTemplate) {
        // Default timeout values: 30 seconds for response, 5 seconds for container
        // shutdown
        return new RabbitRpcClient(rabbitTemplate, 30, 5);
    }
}