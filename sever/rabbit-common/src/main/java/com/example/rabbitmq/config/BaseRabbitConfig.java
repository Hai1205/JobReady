package com.example.rabbitmq.config;

import com.example.rabbitmq.dto.ExchangeDef;
import com.example.rabbitmq.dto.QueueDef;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.*;
import org.springframework.context.annotation.Bean;

import java.util.*;

/**
 * Base RabbitMQ Configuration:
 * - Abstract class to be extended by service-specific RabbitMQ configurations
 * - Provides common beans and utilities for RabbitMQ setup
 */
public abstract class BaseRabbitConfig {

    /**
     * Creates declarables for exchanges, queues, and bindings from provided definitions
     * 
     * @param exchangeDefs List of exchange definitions
     * @return Declarables object with all exchanges, queues, and bindings
     */
    protected Declarables createDeclarables(List<ExchangeDef> exchangeDefs) {
        List<Declarable> declarables = new ArrayList<>();
        
        for (ExchangeDef exDef : exchangeDefs) {
            TopicExchange exchange = new TopicExchange(exDef.name, true, false);
            declarables.add(exchange);

            for (QueueDef qDef : exDef.queues) {
                org.springframework.amqp.core.Queue requestQueue = QueueBuilder.durable(qDef.requestQueue).build();
                declarables.add(requestQueue);

                Binding binding = BindingBuilder.bind(requestQueue)
                        .to(exchange)
                        .with(qDef.routingKey);
                declarables.add(binding);

                if (qDef.hasReplyQueue) {
                    org.springframework.amqp.core.Queue replyQueue = QueueBuilder.durable(qDef.replyQueue()).build();
                    declarables.add(replyQueue);
                }
            }
        }

        return new Declarables(declarables);
    }

    /**
     * Bean for JSON converter
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }

    /**
     * Bean for RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(converter);
        return template;
    }

    /**
     * Bean for RabbitAdmin
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}