package com.example.authservice.config.RabbitMQ;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/**
 * RabbitMQ configuration tidy-up:
 * - Declare exchange once
 * - Declare request queues + optional reply queues by configuration list
 * - Auto-create bindings between exchange and request routing keys
 * - Provide Jackson2JsonMessageConverter and RabbitTemplate
 */
@Configuration
public class RabbitConfig {

    public static final String USER_EXCHANGE = "user.exchange";

    /* --- I. List of Queues (Core) --- */
    private static final List<QueueDef> QUEUES = List.of(
            new QueueDef("user.login.queue", "user.login", true)
    // new QueueDef("user.register.queue", "user.register", true),
    // new QueueDef("user.authenticate.queue", "user.authenticate", true),

    // OAuth2
    // new QueueDef("user.oauth2.check.queue", "user.oauth2.check", true),
    // new QueueDef("user.oauth2.create.queue", "user.oauth2.create", true),
    // new QueueDef("user.oauth2.update.queue", "user.oauth2.update", true),

    // other commands
    // new QueueDef("user.find.by.email.queue", "user.find.by.email", true),
    // new QueueDef("user.reset.password.queue", "user.reset.password", true),
    // new QueueDef("user.change.password.queue", "user.change.password", true),
    // new QueueDef("user.change.status.queue", "user.change.status", true),
    // new QueueDef("user.forgot.password.queue", "user.forgot.password", true),
    // new QueueDef("user.process.oauth2.queue", "user.process.oauth2", true)
    );

    /* --- II. Exchange (Core) --- */
    @Bean
    public TopicExchange userExchange() {
        return ExchangeBuilder.topicExchange(USER_EXCHANGE).durable(true).build();
    }

    /* --- III. Binding (Core) --- */
    @Bean
    public Declarables userQueuesAndBindings(TopicExchange userExchange) {
        List<Declarable> declarables = new ArrayList<>();

        declarables.add(userExchange); // 1. Ensure exchange is declared first

        for (QueueDef def : QUEUES) {
            Queue requestQueue = QueueBuilder.durable(def.requestQueue).build(); // 2. Create request queue
            declarables.add(requestQueue);

            Binding binding = BindingBuilder.bind(requestQueue).to(userExchange).with(def.routingKey); // 3. Binding
                                                                                                       // request queue
            declarables.add(binding);

            // 4. Optional reply queue
            if (def.hasReplyQueue) {
                String replyQueueName = def.replyQueue();
                Queue replyQueue = QueueBuilder.durable(replyQueueName).build();
                declarables.add(replyQueue);
                // Note: reply queue doesn't need binding if producer listens directly on queue.
                // If you want replies to come via exchange, add binding here (example below
                // commented)
                // Binding replyBinding =
                // BindingBuilder.bind(replyQueue).to(userExchange).with(replyQueueName);
                // declarables.add(replyBinding);
            }
        }

        return new Declarables(declarables);
    }

    /* --- IV. Message converter & template (Core) --- */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        return converter;
    }

    /* --- V. Rabbit Template (Core) --- */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory,
            Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(converter);
        return template;
    }

    /* --- VI. RabbitAdmin bean to declare queues at runtime (Optional) --- */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /* --- Helper record to keep queue definitions tidy (Optional) --- */
    private static class QueueDef {
        final String requestQueue;
        final String routingKey;
        final boolean hasReplyQueue;

        QueueDef(String requestQueue, String routingKey, boolean hasReplyQueue) {
            this.requestQueue = requestQueue;
            this.routingKey = routingKey;
            this.hasReplyQueue = hasReplyQueue;
        }

        String replyQueue() {
            return requestQueue + ".reply";
        }
    }
}
