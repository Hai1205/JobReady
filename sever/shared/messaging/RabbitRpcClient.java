package com.example.shared.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Client for performing RabbitMQ RPC (Request-Reply) communication
 * This class handles the boilerplate code for RabbitMQ RPC pattern
 */
public class RabbitRpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RabbitRpcClient.class);

    private final RabbitTemplate rabbitTemplate;
    private final int replyTimeout;
    private final int containerShutdownTimeout;

    /**
     * Constructs a RabbitRpcClient with default timeout values
     * 
     * @param rabbitTemplate The RabbitTemplate to use for sending messages
     */
    public RabbitRpcClient(RabbitTemplate rabbitTemplate) {
        this(rabbitTemplate, 10, 1);
    }

    /**
     * Constructs a RabbitRpcClient with custom timeout values
     * 
     * @param rabbitTemplate The RabbitTemplate to use for sending messages
     * @param replyTimeoutSeconds Timeout in seconds for waiting for a reply
     * @param containerShutdownTimeoutSeconds Timeout in seconds for shutting down the container
     */
    public RabbitRpcClient(RabbitTemplate rabbitTemplate, int replyTimeoutSeconds, int containerShutdownTimeoutSeconds) {
        this.rabbitTemplate = rabbitTemplate;
        this.replyTimeout = replyTimeoutSeconds;
        this.containerShutdownTimeout = containerShutdownTimeoutSeconds;
    }

    /**
     * Send a request to RabbitMQ and receive a response
     *
     * @param exchange The exchange to send the request to
     * @param routingKey The routing key to use
     * @param request The request object to send
     * @param replyQueue The queue to receive the reply on
     * @param responseType The expected response type class
     * @param <TRequest> The type of the request
     * @param <TResponse> The type of the expected response
     * @return The response from the RPC call
     * @throws RabbitRpcException If an error occurs during the RPC call
     */
    public <TRequest, TResponse> TResponse sendAndReceive(
            String exchange,
            String routingKey,
            TRequest request,
            String replyQueue,
            Class<TResponse> responseType) throws RabbitRpcException {
        
        // Generate a unique correlation ID for this request
        String correlationId = UUID.randomUUID().toString();

        // If request implements CorrelationIdAware interface, set the correlation ID
        if (request instanceof CorrelationIdAware) {
            ((CorrelationIdAware) request).setCorrelationId(correlationId);
            logger.debug("Set correlationId {} on request", correlationId);
        }

        // Create a blocking queue to receive the response
        BlockingQueue<Object> responseQueue = new ArrayBlockingQueue<>(1);

        // Create a container to listen for the response
        SimpleMessageListenerContainer container = createResponseContainer(replyQueue, correlationId, responseQueue);

        try {
            container.start();
            logger.debug("Started response container for correlationId {}", correlationId);

            // Send the request
            sendRequest(exchange, routingKey, request, replyQueue, correlationId);

            // Wait for the response with timeout
            return receiveResponse(responseQueue, correlationId, responseType);
        } finally {
            // Always clean up the container
            cleanupContainer(container, correlationId);
        }
    }

    /**
     * Creates a container for listening to responses
     */
    private SimpleMessageListenerContainer createResponseContainer(
            String replyQueue,
            String correlationId,
            BlockingQueue<Object> responseQueue) {

        // Create a message listener that filters messages by correlation ID
        MessageListener messageListener = message -> {
            String receivedCorrelationId = message.getMessageProperties().getCorrelationId();
            if (correlationId.equals(receivedCorrelationId)) {
                try {
                    // Convert the message to the expected response type
                    Object response = rabbitTemplate.getMessageConverter().fromMessage(message);
                    responseQueue.offer(response);
                    logger.debug("Received response for correlationId {}", correlationId);
                } catch (Exception e) {
                    logger.error("Error processing response for correlationId {}", correlationId, e);
                    // Offer an exception to unblock the waiting thread
                    responseQueue.offer(new RabbitRpcException(
                            "Error deserializing response: " + e.getMessage(),
                            e,
                            RabbitRpcException.ErrorType.SERIALIZATION_ERROR));
                }
            }
        };

        // Create and configure the container
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(
                rabbitTemplate.getConnectionFactory());
        container.setQueueNames(replyQueue);
        container.setMessageListener(messageListener);
        return container;
    }

    /**
     * Sends the request to RabbitMQ
     */
    private <TRequest> void sendRequest(
            String exchange,
            String routingKey,
            TRequest request,
            String replyQueue,
            String correlationId) throws RabbitRpcException {

        try {
            logger.info("Sending request with correlationId {} to exchange={}, routingKey={}",
                    correlationId, exchange, routingKey);

            rabbitTemplate.convertAndSend(
                    exchange,
                    routingKey,
                    request,
                    message -> {
                        message.getMessageProperties().setCorrelationId(correlationId);
                        message.getMessageProperties().setReplyTo(replyQueue);
                        return message;
                    });

            logger.debug("Request sent with correlationId {}", correlationId);
        } catch (Exception e) {
            logger.error("Error sending request with correlationId {}", correlationId, e);
            throw new RabbitRpcException(
                    "Error sending request: " + e.getMessage(),
                    e,
                    RabbitRpcException.ErrorType.CONNECTION_ERROR);
        }
    }

    /**
     * Waits for and processes the response
     */
    @SuppressWarnings("unchecked")
    private <TResponse> TResponse receiveResponse(
            BlockingQueue<Object> responseQueue,
            String correlationId,
            Class<TResponse> responseType) throws RabbitRpcException {

        try {
            // Wait for the response with timeout
            Object response = responseQueue.poll(replyTimeout, TimeUnit.SECONDS);

            if (response == null) {
                logger.error("Timeout waiting for response with correlationId {}", correlationId);
                throw new RabbitRpcException(
                        "Timeout waiting for response",
                        RabbitRpcException.ErrorType.TIMEOUT);
            }

            // If the response is an exception, throw it
            if (response instanceof Exception) {
                throw (Exception) response;
            }

            // Check if response is of expected type
            if (responseType.isInstance(response)) {
                logger.info("Successfully received response of type {} for correlationId {}",
                        responseType.getSimpleName(), correlationId);
                return (TResponse) response;
            } else {
                logger.error("Received unexpected response type for correlationId {}. Expected: {}, Got: {}",
                        correlationId, responseType.getSimpleName(),
                        response != null ? response.getClass().getSimpleName() : "null");
                throw new RabbitRpcException(
                        "Received unexpected response type. Expected: " + responseType.getSimpleName() +
                                ", Got: " + (response != null ? response.getClass().getSimpleName() : "null"),
                        RabbitRpcException.ErrorType.INVALID_RESPONSE);
            }
        } catch (RabbitRpcException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrupted while waiting for response with correlationId {}", correlationId, e);
            throw new RabbitRpcException(
                    "Thread interrupted while waiting for response",
                    e,
                    RabbitRpcException.ErrorType.UNKNOWN);
        } catch (Exception e) {
            logger.error("Error processing response with correlationId {}", correlationId, e);
            throw new RabbitRpcException(
                    "Error processing response: " + e.getMessage(),
                    e,
                    RabbitRpcException.ErrorType.UNKNOWN);
        }
    }

    /**
     * Cleans up the response container
     */
    private void cleanupContainer(SimpleMessageListenerContainer container, String correlationId) {
        try {
            logger.debug("Stopping response container for correlationId {}", correlationId);
            container.stop();
            // Give it a short time to shut down gracefully
            Thread.sleep(containerShutdownTimeout * 1000L);
        } catch (Exception e) {
            logger.warn("Error stopping response container for correlationId {}", correlationId, e);
        }
    }
}