package com.example.rabbitmq.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * CV Created Event
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVCreatedEvent {
    private String eventId;
    private String cvId;
    private String userId;
    private String cvTitle;
    private LocalDateTime createdAt;
    private Map<String, Object> metadata;
}
