package com.example.rabbitmq.configs;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;

import com.example.rabbitmq.dtos.ExchangeDef;
import com.example.rabbitmq.dtos.QueueDef;
import org.springframework.amqp.core.Queue;

import java.util.*;

/**
 * Base RabbitMQ Configuration:
 * - Abstract class to be extended by service-specific RabbitMQ configurations
 * - Provides common beans and utilities for RabbitMQ setup
 */
public abstract class BaseRabbitConfig {

    /**
     * Creates declarables for exchanges, queues, and bindings from provided
     * definitions
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
                Queue requestQueue = QueueBuilder.durable(qDef.requestQueue).build();
                declarables.add(requestQueue);

                Binding binding = BindingBuilder.bind(requestQueue)
                        .to(exchange)
                        .with(qDef.routingKey);
                declarables.add(binding);
            }
        }

        return new Declarables(declarables);
    }

    /**
     * Creates a declarable for a reply queue with proper binding to an exchange
     * 
     * @param queueName    Name of the reply queue
     * @param exchangeName Name of the exchange to bind to
     * @param routingKey   Routing key for binding
     * @return Declarables object with queue and binding
     */
    public Declarables createReplyQueueDeclarable(String queueName, String exchangeName, String routingKey) {
        Queue replyQueue = QueueBuilder.durable(queueName).build();
        TopicExchange exchange = new TopicExchange(exchangeName, true, false);
        Binding binding = BindingBuilder.bind(replyQueue).to(exchange).with(routingKey);

        return new Declarables(replyQueue, binding);
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
        // Set timeout to 30 seconds for RPC calls
        template.setReplyTimeout(30000);
        return template;
    }

    /**
     * Bean for RabbitAdmin
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * Custom ConnectionFactory with 30 seconds timeout
     */
    @Bean
    public ConnectionFactory connectionFactory(
            org.springframework.amqp.rabbit.connection.ConnectionFactory defaultFactory) {
        if (defaultFactory instanceof CachingConnectionFactory) {
            CachingConnectionFactory cachingFactory = (CachingConnectionFactory) defaultFactory;
            // Set connection timeout to 30 seconds
            cachingFactory.setConnectionTimeout(30000);
            // Set handshake timeout to 30 seconds (if available)
            try {
                // Use reflection to set handshake timeout if the method exists
                java.lang.reflect.Method setHandshakeTimeout = cachingFactory.getRabbitConnectionFactory().getClass()
                        .getMethod("setHandshakeTimeout", int.class);
                setHandshakeTimeout.invoke(cachingFactory.getRabbitConnectionFactory(), 30000);
            } catch (Exception e) {
                // Handshake timeout might not be available in all versions
            }
            return cachingFactory;
        }
        return defaultFactory;
    }
}