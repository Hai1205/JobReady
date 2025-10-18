package com.example.authservice.configs;

import java.util.List;

import org.springframework.amqp.core.Declarables;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.rabbitmq.configs.BaseRabbitConfig;
import com.example.rabbitmq.constants.RabbitConstants;
import com.example.rabbitmq.dtos.ExchangeDef;

@Configuration
public class AuthRabbitConfig extends BaseRabbitConfig {

    @Bean
    public Declarables authExchangeConfig() {
        return createDeclarables(List.of(
                new ExchangeDef(RabbitConstants.AUTH_EXCHANGE, List.of())));
    }

    @Bean
    public Declarables authReplyQueueConfig() {
        return createReplyQueueDeclarable(
                RabbitConstants.AUTH_REPLY_QUEUE,
                RabbitConstants.AUTH_EXCHANGE,
                RabbitConstants.AUTH_REPLY_QUEUE);
    }
}
