package com.example.userservice.services.consumers;

import com.example.rabbitmq.config.DeadLetterQueueConfig;
import com.example.rabbitmq.constants.RabbitConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * DLQ Retry Listener với Exponential Backoff
 * 
 * Strategy:
 * 1. Nhận message từ DLQ
 * 2. Check retry count
 * 3. Nếu < MAX_RETRY: chờ exponential backoff rồi retry
 * 4. Nếu >= MAX_RETRY: move to poison queue và alert admin
 * 
 * Exponential Backoff:
 * - Retry 1: 5 seconds
 * - Retry 2: 10 seconds
 * - Retry 3: 20 seconds
 * - Retry 4: 40 seconds
 * - Retry 5: 80 seconds
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DLQRetryListener {

    private final RabbitTemplate rabbitTemplate;

    private static final int MAX_RETRY = 3;
    private static final int BASE_DELAY_SECONDS = 5;

    /**
     * Handle User Create DLQ
     */
    @RabbitListener(queues = DeadLetterQueueConfig.USER_CREATE_DLQ)
    public void handleUserCreateDLQ(Message message) {
        retryMessage(
                message,
                "UserCreate",
                RabbitConstants.USER_EXCHANGE,
                RabbitConstants.USER_CREATE
        );
    }

    /**
     * Handle User Activate DLQ
     */
    @RabbitListener(queues = DeadLetterQueueConfig.USER_ACTIVATE_DLQ)
    public void handleUserActivateDLQ(Message message) {
        retryMessage(
                message,
                "UserActivate",
                RabbitConstants.USER_EXCHANGE,
                RabbitConstants.USER_ACTIVATE
        );
    }

    /**
     * Generic retry logic
     */
    private void retryMessage(
            Message message,
            String operationName,
            String targetExchange,
            String targetRoutingKey) {

        MessageProperties props = message.getMessageProperties();
        String correlationId = props.getCorrelationId();

        try {
            // Get retry count
            Integer retryCount = (Integer) props.getHeader("x-retry-count");
            if (retryCount == null) {
                retryCount = 0;
            }
            final int currentRetryCount = retryCount; // Make final for lambda

            log.warn("📥 [DLQ-{}] Message received - correlationId: {}, retryCount: {}/{}",
                    operationName, correlationId, currentRetryCount, MAX_RETRY);

            // Check if exceeded max retry
            if (currentRetryCount >= MAX_RETRY) {
                log.error("🚨 [DLQ-{}] Max retry exceeded - correlationId: {}", 
                        operationName, correlationId);
                moveToPoisonQueue(message, operationName);
                notifyAdmin(message, operationName, "Max retry exceeded");
                return;
            }

            // Calculate exponential backoff delay
            int delaySeconds = calculateDelay(currentRetryCount);
            log.info("⏳ [DLQ-{}] Scheduling retry in {}s - correlationId: {}, attempt: {}/{}",
                    operationName, delaySeconds, correlationId, currentRetryCount + 1, MAX_RETRY);

            // Increment retry count
            MessageProperties newProps = new MessageProperties();
            newProps.setContentType(props.getContentType());
            newProps.setCorrelationId(correlationId);
            newProps.setReplyTo(props.getReplyTo());

            // Copy custom headers
            props.getHeaders().forEach((key, value) -> {
                if (!key.startsWith("x-")) { // Keep custom headers
                    newProps.setHeader(key, value);
                }
            });

            // Update retry count
            newProps.setHeader("x-retry-count", currentRetryCount + 1);
            newProps.setHeader("x-first-death-time", 
                    props.getHeader("x-first-death-time") != null 
                            ? props.getHeader("x-first-death-time")
                            : System.currentTimeMillis());

            Message retryMessage = MessageBuilder
                    .withBody(message.getBody())
                    .andProperties(newProps)
                    .build();

            // Schedule retry với exponential backoff
            CompletableFuture.delayedExecutor(delaySeconds, TimeUnit.SECONDS).execute(() -> {
                try {
                    rabbitTemplate.send(targetExchange, targetRoutingKey, retryMessage);
                    log.info("🔄 [DLQ-{}] Message resent - correlationId: {}, attempt: {}",
                            operationName, correlationId, currentRetryCount + 1);
                } catch (Exception e) {
                    log.error("❌ [DLQ-{}] Failed to resend message - correlationId: {}",
                            operationName, correlationId, e);
                }
            });

        } catch (Exception e) {
            log.error("❌ [DLQ-{}] Error processing DLQ message - correlationId: {}",
                    operationName, correlationId, e);

            // Move to poison queue on processing error
            moveToPoisonQueue(message, operationName);
        }
    }

    /**
     * Calculate exponential backoff delay
     * Formula: BASE_DELAY * (2 ^ retryCount)
     * 
     * @param retryCount Current retry count
     * @return Delay in seconds
     */
    private int calculateDelay(int retryCount) {
        return (int) (BASE_DELAY_SECONDS * Math.pow(2, retryCount));
    }

    /**
     * Move message to poison queue
     */
    private void moveToPoisonQueue(Message message, String operationName) {
        try {
            MessageProperties props = message.getMessageProperties();
            String correlationId = props.getCorrelationId();

            // Add metadata
            MessageProperties poisonProps = new MessageProperties();
            poisonProps.setContentType(props.getContentType());
            poisonProps.setCorrelationId(correlationId);
            poisonProps.setHeader("x-original-operation", operationName);
            poisonProps.setHeader("x-poison-time", System.currentTimeMillis());
            poisonProps.setHeader("x-original-exchange", props.getHeader("x-original-exchange"));
            poisonProps.setHeader("x-original-routing-key", props.getHeader("x-original-routing-key"));

            // Copy all headers for debugging
            props.getHeaders().forEach(poisonProps::setHeader);

            Message poisonMessage = MessageBuilder
                    .withBody(message.getBody())
                    .andProperties(poisonProps)
                    .build();

            rabbitTemplate.send(
                    DeadLetterQueueConfig.POISON_EXCHANGE,
                    "poison." + operationName.toLowerCase(),
                    poisonMessage
            );

            log.warn("☠️ [DLQ-{}] Message moved to poison queue - correlationId: {}",
                    operationName, correlationId);

        } catch (Exception e) {
            log.error("❌ [DLQ] Failed to move message to poison queue", e);
        }
    }

    /**
     * Notify admin về poison message
     */
    private void notifyAdmin(Message message, String operationName, String reason) {
        MessageProperties props = message.getMessageProperties();
        String correlationId = props.getCorrelationId();
        String body = new String(message.getBody());

        String alert = String.format(
                "🚨 POISON MESSAGE ALERT\n" +
                        "Operation: %s\n" +
                        "Correlation ID: %s\n" +
                        "Reason: %s\n" +
                        "Retry Count: %s\n" +
                        "Message: %s\n",
                operationName,
                correlationId,
                reason,
                props.getHeader("x-retry-count"),
                body.substring(0, Math.min(200, body.length()))
        );

        log.error(alert);

        // - Email to ops team
        // - Slack notification
        // - PagerDuty alert
        // - Metrics increment (poison_message_count)
    }

    /**
     * Listen to poison queue cho monitoring/debugging
     */
    @RabbitListener(queues = DeadLetterQueueConfig.POISON_QUEUE)
    public void monitorPoisonQueue(Message message) {
        MessageProperties props = message.getMessageProperties();
        String correlationId = props.getCorrelationId();
        String operation = (String) props.getHeader("x-original-operation");

        log.error("☠️ [POISON] Message in poison queue - operation: {}, correlationId: {}",
                operation, correlationId);

        // Store to database for manual review
    }
}
