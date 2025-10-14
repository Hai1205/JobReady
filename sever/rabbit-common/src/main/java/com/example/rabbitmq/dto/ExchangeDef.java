package com.example.rabbitmq.dto;

import java.util.List;

/**
 * Exchange definition for RabbitMQ
 * Holds information about exchange and associated queues
 */
public class ExchangeDef {
    public final String name;
    public final List<QueueDef> queues;

    public ExchangeDef(String name, List<QueueDef> queues) {
        this.name = name;
        this.queues = queues;
    }
}
