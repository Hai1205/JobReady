package com.example.cvservice.configs;

import java.util.List;

import org.springframework.amqp.core.Declarables;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.rabbitmq.configs.BaseRabbitConfig;
import com.example.rabbitmq.constants.RabbitConstants;
import com.example.rabbitmq.dtos.ExchangeDef;

@Configuration
public class CVRabbitConfig extends BaseRabbitConfig {

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
