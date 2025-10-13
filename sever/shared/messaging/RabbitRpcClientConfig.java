package com.example.shared.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

/**
 * Configuration for RabbitMQ RPC client
 */
@Configuration
public class RabbitRpcClientConfig {

    @Value("${rabbitmq.rpc.reply-timeout:10}")
    private int replyTimeout;

    @Value("${rabbitmq.rpc.container-shutdown-timeout:1}")
    private int containerShutdownTimeout;

    @Bean
    public RabbitRpcClient rabbitRpcClient(RabbitTemplate rabbitTemplate) {
        return new RabbitRpcClient(rabbitTemplate, replyTimeout, containerShutdownTimeout);
    }
}