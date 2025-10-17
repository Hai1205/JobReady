package com.example.cvservice.config;

import java.util.List;

import org.springframework.amqp.core.Declarables;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.rabbitmq.config.BaseRabbitConfig;
import com.example.rabbitmq.constants.RabbitConstants;
import com.example.rabbitmq.dto.ExchangeDef;

@Configuration
public class AuthRabbitConfig extends BaseRabbitConfig {

    @Bean
    public Declarables cvExchangeConfig() {
        return createDeclarables(List.of(
                new ExchangeDef(RabbitConstants.CV_EXCHANGE, List.of())));
    }

    @Bean
    public Declarables cvReplyQueueConfig() {
        return createReplyQueueDeclarable(
                RabbitConstants.CV_REPLY_QUEUE,
                RabbitConstants.CV_EXCHANGE,
                RabbitConstants.CV_REPLY_QUEUE);
    }
}
