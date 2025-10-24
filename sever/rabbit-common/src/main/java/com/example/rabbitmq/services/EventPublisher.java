package com.example.rabbitmq.services;

import com.example.rabbitmq.events.UserCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Event Publisher Service
 * Publish domain events to RabbitMQ Event Exchange
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private static final String EVENT_EXCHANGE = "events.exchange";

    /**
     * Publish UserCreatedEvent
     */
    public void publishUserCreatedEvent(UserCreatedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            rabbitTemplate.convertAndSend(
                    EVENT_EXCHANGE,
                    "user.created", // routing key
                    json
            );

            log.info("📣 Published UserCreatedEvent - userId: {}, eventId: {}",
                    event.getUserId(), event.getEventId());

        } catch (Exception e) {
            log.error("❌ Failed to publish UserCreatedEvent", e);
            // TODO: Store in Outbox table for retry
            throw new RuntimeException("Failed to publish event", e);
        }
    }

    /**
     * Generic publish method
     */
    public void publishEvent(String routingKey, Object event) {
        try {
            String json = objectMapper.writeValueAsString(event);

            rabbitTemplate.convertAndSend(EVENT_EXCHANGE, routingKey, json);

            log.info("📣 Published event - routing: {}, eventType: {}",
                    routingKey, event.getClass().getSimpleName());

        } catch (Exception e) {
            log.error("❌ Failed to publish event - routing: {}", routingKey, e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}
