package com.example.aiservice.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rag")
@Data
public class RAGConfig {
    private int chunkSize = 1000;
    private int chunkOverlap = 200;
    private int topK = 5;
}
