package com.example.userservice.config;

import java.util.List;

import org.springframework.amqp.core.Declarables;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.rabbitmq.config.BaseRabbitConfig;
import com.example.rabbitmq.constants.RabbitConstants;
import com.example.rabbitmq.dto.ExchangeDef;
import com.example.rabbitmq.dto.QueueDef;

@Configuration
public class UserRabbitConfig extends BaseRabbitConfig {

    @Bean
    public Declarables userExchangeConfig() {
        return createDeclarables(List.of(
                new ExchangeDef(RabbitConstants.USER_EXCHANGE, List.of(
                        new QueueDef(RabbitConstants.USER_FIND_BY_EMAIL_QUEUE,
                                RabbitConstants.USER_FIND_BY_EMAIL, false),
                        new QueueDef(RabbitConstants.USER_CREATE_QUEUE,
                                RabbitConstants.USER_CREATE, false),
                        new QueueDef(RabbitConstants.USER_CHANGE_PASSWORD_QUEUE,
                                RabbitConstants.USER_CHANGE_PASSWORD, false),
                        new QueueDef(RabbitConstants.USER_AUTHENTICATE_QUEUE,
                                RabbitConstants.USER_AUTHENTICATE, false),
                        new QueueDef(RabbitConstants.USER_RESET_PASSWORD_QUEUE,
                                RabbitConstants.USER_RESET_PASSWORD, false),
                        new QueueDef(RabbitConstants.USER_FORGOT_PASSWORD_QUEUE,
                                RabbitConstants.USER_FORGOT_PASSWORD, false)
                ))));
    }

    @Bean
    public Declarables userReplyQueueConfig() {
        return createReplyQueueDeclarable(
                RabbitConstants.USER_REPLY_QUEUE,
                RabbitConstants.USER_EXCHANGE,
                RabbitConstants.USER_REPLY_QUEUE);
    }
}
