package com.example.cvservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Helper class for RabbitMQ RPC (request-reply) pattern.
 * Handles sending requests and receiving responses with correlation IDs.
 */
public class RabbitRpcClient {
    private static final Logger log = LoggerFactory.getLogger(RabbitRpcClient.class);

    private final RabbitTemplate rabbitTemplate;
    private final ConcurrentHashMap<String, CompletableFuture<Object>> futures = new ConcurrentHashMap<>();
    private final int responseTimeoutSeconds;
    private final int containerShutdownTimeoutSeconds;

    public RabbitRpcClient(RabbitTemplate rabbitTemplate, int responseTimeoutSeconds,
            int containerShutdownTimeoutSeconds) {
        this.rabbitTemplate = rabbitTemplate;
        this.responseTimeoutSeconds = responseTimeoutSeconds > 0 ? responseTimeoutSeconds : 30;
        this.containerShutdownTimeoutSeconds = containerShutdownTimeoutSeconds > 0 ? containerShutdownTimeoutSeconds
                : 5;
    }

    /**
     * Send a message to an exchange and wait for a reply.
     *
     * @param exchange     Exchange to send the message to
     * @param routingKey   Routing key for the message
     * @param request      Request object (will have correlation ID set)
     * @param replyQueue   Queue to receive the reply on
     * @param responseType Class of the expected response
     * @return Response object
     * @param <T> Type of the response
     * @throws RabbitRpcException if something goes wrong
     */
    public <T> T sendAndReceive(String exchange, String routingKey, Object request, String replyQueue,
            Class<T> responseType) throws RabbitRpcException {
        String correlationId = UUID.randomUUID().toString();
        log.debug("Starting RPC call with correlationId: {}", correlationId);

        // Set correlation ID if possible
        if (request instanceof CorrelationIdAware) {
            ((CorrelationIdAware) request).setCorrelationId(correlationId);
        }

        // Create future for the response
        CompletableFuture<Object> responseFuture = new CompletableFuture<>();
        futures.put(correlationId, responseFuture);

        // Create a listener for the response
        SimpleMessageListenerContainer container = createResponseContainer(replyQueue, correlationId);

        try {
            // Send the message
            sendRequest(exchange, routingKey, request, correlationId, replyQueue);

            // Wait for the response
            Object response = receiveResponse(correlationId, responseFuture);

            // Handle null response
            if (response == null) {
                throw RabbitRpcException.responseError("Received null response");
            }

            // Check if the response is of the expected type
            if (!responseType.isInstance(response)) {
                throw RabbitRpcException.responseError(
                        String.format("Response type mismatch. Expected: %s, Got: %s",
                                responseType.getName(), response.getClass().getName()));
            }

            // Return the response
            return responseType.cast(response);
        } finally {
            // Clean up
            cleanupResources(container, correlationId);
        }
    }

    private SimpleMessageListenerContainer createResponseContainer(String replyQueue, String correlationId) {
        try {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(
                    rabbitTemplate.getConnectionFactory());
            container.setQueueNames(replyQueue);
            container.setShutdownTimeout(containerShutdownTimeoutSeconds * 1000L);
            container.setMessageListener(message -> {
                String receivedCorrelationId = message.getMessageProperties().getCorrelationId();
                log.debug("Received response with correlationId: {}", receivedCorrelationId);

                if (receivedCorrelationId != null && receivedCorrelationId.equals(correlationId)) {
                    try {
                        // Convert message to object
                        Object responseObject = rabbitTemplate.getMessageConverter().fromMessage(message);

                        // Complete the future
                        CompletableFuture<Object> future = futures.get(correlationId);
                        if (future != null) {
                            future.complete(responseObject);
                        } else {
                            log.warn("No future found for correlationId: {}", correlationId);
                        }
                    } catch (Exception e) {
                        log.error("Error processing response message", e);
                        completeExceptionally(correlationId,
                                RabbitRpcException.serializationError("Error processing response message", e));
                    }
                } else {
                    log.warn("Received message with wrong correlationId. Expected: {}, Got: {}", correlationId,
                            receivedCorrelationId);
                }
            });

            container.start();
            return container;
        } catch (Exception e) {
            throw RabbitRpcException.connectionError("Error creating response container", e);
        }
    }

    private void sendRequest(String exchange, String routingKey, Object request, String correlationId,
            String replyQueue) {
        try {
            // Post processor to set correlation ID and reply-to queue
            MessagePostProcessor messagePostProcessor = message -> {
                MessageProperties props = message.getMessageProperties();
                props.setCorrelationId(correlationId);
                props.setReplyTo(replyQueue);
                return message;
            };

            // Send the message with the post processor
            rabbitTemplate.convertAndSend(exchange, routingKey, request, messagePostProcessor);
            log.debug("Sent request with correlationId: {} to {}/{}", correlationId, exchange, routingKey);
        } catch (Exception e) {
            futures.remove(correlationId);
            throw RabbitRpcException.connectionError("Error sending request", e);
        }
    }

    private Object receiveResponse(String correlationId, CompletableFuture<Object> future) {
        try {
            // Wait for the response
            return future.get(responseTimeoutSeconds, TimeUnit.SECONDS);
        } catch (java.util.concurrent.TimeoutException e) {
            throw RabbitRpcException.timeout("Response timeout after " + responseTimeoutSeconds + " seconds");
        } catch (Exception e) {
            if (e.getCause() instanceof RabbitRpcException) {
                throw (RabbitRpcException) e.getCause();
            }
            throw RabbitRpcException.responseError("Error receiving response: " + e.getMessage());
        }
    }

    private void cleanupResources(SimpleMessageListenerContainer container, String correlationId) {
        // Remove the future
        futures.remove(correlationId);

        // Stop the container if it's not null
        if (container != null) {
            try {
                container.stop();
                log.debug("Stopped message listener container for correlationId: {}", correlationId);
            } catch (Exception e) {
                log.warn("Error stopping message listener container", e);
            }
        }
    }

    private void completeExceptionally(String correlationId, RabbitRpcException exception) {
        CompletableFuture<Object> future = futures.get(correlationId);
        if (future != null) {
            future.completeExceptionally(exception);
        }
    }
}