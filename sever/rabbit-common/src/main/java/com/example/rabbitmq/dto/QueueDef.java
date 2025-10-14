package com.example.rabbitmq.dto;

/**
 * Queue definition for RabbitMQ
 * Holds information about queue and routing key
 */
public class QueueDef {
    public final String requestQueue;
    public final String routingKey;
    public final boolean hasReplyQueue;

    public QueueDef(String requestQueue, String routingKey, boolean hasReplyQueue) {
        this.requestQueue = requestQueue;
        this.routingKey = routingKey;
        this.hasReplyQueue = hasReplyQueue;
    }

    public String replyQueue() {
        return requestQueue + ".reply";
    }
}