package com.example.rabbitmq.consumer;

import com.example.rabbitmq.dto.RabbitHeader;
import com.example.rabbitmq.service.RabbitRPCService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Base consumer class for RabbitMQ message processing
 * Provides common utilities for extracting headers and payloads
 */
public abstract class BaseConsumer {

    @Autowired
    protected RabbitRPCService rpcService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Extract payload from RabbitMQ message
     * 
     * @param message Message from RabbitMQ
     * @param clazz Class of payload
     * @return Payload object
     * @param <T> Type of payload
     */
    // protected <T> T extractPayload(Message message, Class<T> clazz) {
    //     try {
    //         JsonNode root = objectMapper.readTree(message.getBody());
    //         return objectMapper.treeToValue(root.get("payload"), clazz);
    //     } catch (Exception e) {
    //         throw new RuntimeException("❌ [BaseConsumer] Error extracting payload: " + e.getMessage());
    //     }
    // }
    protected <T> T extractPayload(Message message, TypeReference<T> typeRef) {
        try {
            JsonNode root = objectMapper.readTree(message.getBody());
            return objectMapper.readValue(root.get("payload").toString(), typeRef);
        } catch (Exception e) {
            throw new RuntimeException("❌ [BaseConsumer] Error extracting payload: " + e.getMessage());
        }
    }


    /**
     * Extract header from RabbitMQ message
     * 
     * @param message Message from RabbitMQ
     * @return RabbitHeader object
     */
    protected RabbitHeader extractHeader(Message message) {
        try {
            JsonNode root = objectMapper.readTree(message.getBody());
            return objectMapper.treeToValue(root.get("header"), RabbitHeader.class);
        } catch (Exception e) {
            throw new RuntimeException("❌ [BaseConsumer] Error extracting header: " + e.getMessage());
        }
    }
}