package com.example.aiservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnhancedEmbeddingService {

    private final VectorStore vectorStore;

    /**
     * Ingest template vào vector store
     */
    public void ingestTemplate(String id, String content, Map<String, Object> metadata) {
        log.debug("Ingesting template {} into vector store", id);
        
        Document document = new Document(id, content, metadata);
        vectorStore.add(List.of(document));
        
        log.debug("Template {} ingested successfully", id);
    }

    /**
     * Batch ingest nhiều templates
     */
    public void batchIngestTemplates(List<Document> documents) {
        log.info("Batch ingesting {} templates", documents.size());
        vectorStore.add(documents);
        log.info("Batch ingest completed");
    }

    /**
     * Search relevant templates với filters
     */
    public List<Document> searchRelevantTemplates(
            String query, 
            String section,
            String category,
            String level,
            int topK) {
        
        log.debug("Searching templates: section={}, category={}, level={}, topK={}", 
            section, category, level, topK);
        
        // Build filter expression
        StringBuilder filterExpr = new StringBuilder();
        filterExpr.append(String.format("section == '%s'", section));
        
        if (category != null && !category.isEmpty()) {
            filterExpr.append(String.format(" && category == '%s'", category));
        }
        
        if (level != null && !level.isEmpty()) {
            filterExpr.append(String.format(" && level == '%s'", level));
        }
        
        // Only high-quality templates
        filterExpr.append(" && rating >= 4");
        
        SearchRequest searchRequest = SearchRequest.query(query)
                .withTopK(topK)
                .withSimilarityThreshold(0.7)
                .withFilterExpression(filterExpr.toString());
        
        List<Document> results = vectorStore.similaritySearch(searchRequest);
        
        log.debug("Found {} relevant templates", results.size());
        
        return results;
    }

    /**
     * Delete template
     */
    public void deleteTemplate(String id) {
        vectorStore.delete(List.of(id));
        log.info("Deleted template {} from vector store", id);
    }

    /**
     * Update template
     */
    public void updateTemplate(String id, String newContent, Map<String, Object> metadata) {
        deleteTemplate(id);
        ingestTemplate(id, newContent, metadata);
        log.info("Updated template {} in vector store", id);
    }
}
