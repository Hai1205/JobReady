package com.example.userservice.config.rabbitmq;

import com.example.userservice.dto.RabbitHeader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseConsumer {

    @Autowired
    protected RabbitRPCService rpcService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected <T> T extractPayload(Message message, Class<T> clazz) {
        try {
            JsonNode root = objectMapper.readTree(message.getBody());
            return objectMapper.treeToValue(root.get("payload"), clazz);
        } catch (Exception e) {
            throw new RuntimeException("❌ [BaseConsumer] Error extracting payload: " + e.getMessage());
        }
    }

    protected RabbitHeader extractHeader(Message message) {
        try {
            JsonNode root = objectMapper.readTree(message.getBody());
            return objectMapper.treeToValue(root.get("header"), RabbitHeader.class);
        } catch (Exception e) {
            throw new RuntimeException("❌ [BaseConsumer] Error extracting header: " + e.getMessage());
        }
    }
}
