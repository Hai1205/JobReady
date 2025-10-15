package com.example.authservice.config;

import java.util.List;

import org.springframework.amqp.core.Declarables;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.rabbitmq.config.BaseRabbitConfig;
import com.example.rabbitmq.constants.RabbitConstants;
import com.example.rabbitmq.dto.ExchangeDef;
import com.example.rabbitmq.dto.QueueDef;

@Configuration
public class AuthRabbitConfig extends BaseRabbitConfig {

    @Bean
    public Declarables userExchangeConfig() {
        // Khai báo các queue và binding cho user.exchange từ auth-service
        return createDeclarables(List.of(
                new ExchangeDef(RabbitConstants.USER_EXCHANGE, List.of(
                        new QueueDef(RabbitConstants.USER_FIND_BY_EMAIL_QUEUE,
                                RabbitConstants.USER_FIND_BY_EMAIL, false)))));
    }

    @Bean
    public Declarables authReplyQueueConfig() {
        // Khai báo reply queue và binding của nó cho auth-service
        return createReplyQueueDeclarable(
                RabbitConstants.AUTH_REPLY_QUEUE,
                RabbitConstants.AUTH_EXCHANGE,
                RabbitConstants.AUTH_REPLY_QUEUE);
    }
}
