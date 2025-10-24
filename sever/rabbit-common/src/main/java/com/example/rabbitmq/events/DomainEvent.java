// package com.example.rabbitmq.events;

// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// import java.time.LocalDateTime;
// import java.util.Map;

// /**
//  * Base Event class cho tất cả domain events
//  */
// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// public abstract class DomainEvent {
//     private String eventId;
//     private String eventType;
//     private String aggregateId;
//     private String aggregateType;
//     private LocalDateTime occurredAt;
//     private String sourceService;
//     private Map<String, Object> metadata;
// }
