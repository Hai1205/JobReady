package com.example.aiservice.configs;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class VectorStoreConfig {

    @Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        // Spring AI M3 API uses constructor instead of builder pattern
        PgVectorStore vectorStore = new PgVectorStore(jdbcTemplate, embeddingModel);
        
        // Initialize schema if needed
        try {
            vectorStore.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize vector store", e);
        }
        
        return vectorStore;
    }
}
