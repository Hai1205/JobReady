package com.example.rabbitmq.configs;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration cho Dead Letter Queue (DLQ) và Retry Mechanism
 * 
 * Kiến trúc:
 * 1. Main Queue → xử lý message bình thường
 * 2. Nếu fail/timeout → message đi vào DLX (Dead Letter Exchange)
 * 3. DLX route message vào DLQ (Dead Letter Queue)
 * 4. DLQ Listener retry message với exponential backoff
 * 5. Sau MAX_RETRY → move vào Poison Queue (manual intervention)
 */
@Configuration
public class DeadLetterQueueConfig {

    // DLX và DLQ names
    public static final String DLX_EXCHANGE = "dlx.exchange";
    public static final String POISON_EXCHANGE = "poison.exchange";

    // User service DLQ
    public static final String USER_CREATE_DLQ = "dlq.user.create.queue";
    public static final String USER_CREATE_DLQ_ROUTING_KEY = "dlq.user.create";

    public static final String USER_ACTIVATE_DLQ = "dlq.user.activate.queue";
    public static final String USER_ACTIVATE_DLQ_ROUTING_KEY = "dlq.user.activate";

    // Poison queue cho messages không thể xử lý
    public static final String POISON_QUEUE = "poison.queue";
    public static final String POISON_ROUTING_KEY = "poison.*";

    /**
     * Dead Letter Exchange - nơi nhận tất cả failed messages
     */
    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(DLX_EXCHANGE, true, false);
    }

    /**
     * Poison Exchange - nơi chứa messages không thể recovery
     */
    @Bean
    public TopicExchange poisonExchange() {
        return new TopicExchange(POISON_EXCHANGE, true, false);
    }

    /**
     * User Create DLQ và Binding
     */
    @Bean
    public Queue userCreateDLQ() {
        return QueueBuilder
                .durable(USER_CREATE_DLQ)
                .withArgument("x-queue-mode", "lazy") // Optimize for large backlogs
                .build();
    }

    @Bean
    public Binding userCreateDLQBinding() {
        return BindingBuilder
                .bind(userCreateDLQ())
                .to(deadLetterExchange())
                .with(USER_CREATE_DLQ_ROUTING_KEY);
    }

    /**
     * User Activate DLQ và Binding
     */
    @Bean
    public Queue userActivateDLQ() {
        return QueueBuilder
                .durable(USER_ACTIVATE_DLQ)
                .withArgument("x-queue-mode", "lazy")
                .build();
    }

    @Bean
    public Binding userActivateDLQBinding() {
        return BindingBuilder
                .bind(userActivateDLQ())
                .to(deadLetterExchange())
                .with(USER_ACTIVATE_DLQ_ROUTING_KEY);
    }

    /**
     * Poison Queue - chứa messages failed vĩnh viễn
     */
    @Bean
    public Queue poisonQueue() {
        return QueueBuilder
                .durable(POISON_QUEUE)
                .withArgument("x-queue-mode", "lazy")
                .withArgument("x-message-ttl", 7 * 24 * 60 * 60 * 1000) // 7 days TTL
                .build();
    }

    @Bean
    public Binding poisonQueueBinding() {
        return BindingBuilder
                .bind(poisonQueue())
                .to(poisonExchange())
                .with(POISON_ROUTING_KEY);
    }

    /**
     * Helper method để tạo queue với DLX config
     * 
     * @param queueName           Main queue name
     * @param dlxRoutingKey       Routing key cho DLX
     * @param messageTTL          Message TTL (milliseconds), null = no TTL
     * @param maxRetries          Max retries before DLQ
     * @return Queue với DLX configured
     */
    public static Queue createQueueWithDLX(
            String queueName,
            String dlxRoutingKey,
            Integer messageTTL,
            Integer maxRetries) {

        QueueBuilder builder = QueueBuilder
                .durable(queueName)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", dlxRoutingKey);

        if (messageTTL != null) {
            builder.withArgument("x-message-ttl", messageTTL);
        }

        if (maxRetries != null) {
            builder.withArgument("x-max-length", maxRetries);
        }

        return builder.build();
    }
}
