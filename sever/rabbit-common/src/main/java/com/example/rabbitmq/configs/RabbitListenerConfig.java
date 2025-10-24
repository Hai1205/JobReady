package com.example.rabbitmq.configs;

import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ Listener Configuration
 * 
 * Key Config:
 * 1. Enable Direct Reply-to pattern
 * 2. Configure Direct Reply-to consumer with NO-ACK mode
 * 3. Container factories for different listener types
 * 
 * Note: RabbitTemplate bean is defined in BaseRabbitConfig
 */
@Slf4j
@Configuration
public class RabbitListenerConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Enable Direct Reply-to container on existing RabbitTemplate
     * 
     * CRITICAL: This must be called after RabbitTemplate bean is created
     */
    @PostConstruct
    public void configureRabbitTemplate() {
        // ✅ Enable Direct Reply-to container
        rabbitTemplate.setUseDirectReplyToContainer(true);
        log.info("✅ RabbitTemplate configured with Direct Reply-to container enabled");
    }

    /**
     * Container factory for regular message listeners
     * Uses AUTO acknowledgment mode
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jackson2JsonMessageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);

        // Regular listeners use AUTO-ACK
        factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.AUTO);
        factory.setPrefetchCount(10);
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);

        return factory;
    }

    /**
     * Container factory for Direct Reply-to listener
     * MUST use NONE acknowledgment mode
     * 
     * This factory is specifically for the @RabbitListener on
     * "amq.rabbitmq.reply-to"
     */
    @Bean("directReplyToContainerFactory")
    public DirectRabbitListenerContainerFactory directReplyToContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter jackson2JsonMessageConverter) {

        log.info("🏗️ Creating DirectRabbitListenerContainerFactory for Direct Reply-to");

        DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);

        // ✅ CRITICAL: Direct Reply-to REQUIRES NO-ACK mode
        factory.setAcknowledgeMode(org.springframework.amqp.core.AcknowledgeMode.NONE);

        // Optimize for low latency RPC
        factory.setConsumersPerQueue(2);
        factory.setPrefetchCount(20);

        log.info("✅ DirectRabbitListenerContainerFactory created with NONE ack mode");
        return factory;
    }
}
