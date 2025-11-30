package com.example.aiservice.services;

import com.example.aiservice.dtos.CVTemplateDTO;
import com.example.aiservice.entities.CVTemplateEntity;
import com.example.aiservice.repositories.CVTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CVKnowledgeBaseService {

    private final CVTemplateRepository templateRepository;
    private final EmbeddingService embeddingService;
    private final CVTemplateDataProvider templateDataProvider;

    private boolean initialized = false;

    /**
     * Initialize knowledge base khi application startup
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public void initializeKnowledgeBase() {
        if (initialized) {
            log.info("Knowledge base already initialized in this session");
            return;
        }

        log.info("Starting knowledge base initialization...");

        try {
            // 1. Check PostgreSQL templates
            long templateCount = templateRepository.count();
            if (templateCount == 0) {
                log.info("📝 No templates in DB, creating defaults...");
                createDefaultTemplates();
                templateCount = templateRepository.count();
            }

            // 2. Check vector store status
            boolean vectorStoreEmpty = isVectorStoreEmpty();

            if (vectorStoreEmpty) {
                log.info("Vector store is empty, ingesting {} templates...", templateCount);

                List<CVTemplateEntity> templates = templateRepository
                        .findByRatingGreaterThanEqualAndIsActiveTrue(4);

                List<Document> documents = templates.stream()
                        .map(this::convertToDocument)
                        .collect(Collectors.toList());

                if (!documents.isEmpty()) {
                    embeddingService.batchIngestTemplates(documents);
                    log.info("Ingested {} templates into vector store", documents.size());
                }
            } else {
                log.info("Vector store already has data, skipping ingestion");
            }

            initialized = true;
            log.info("Knowledge base initialization completed");

        } catch (Exception e) {
            log.error("❌ Knowledge base initialization failed", e);
            throw new RuntimeException("Knowledge base initialization failed", e);
        }
    }

    /**
     * Check if vector store is empty by doing a test query
     */
    private boolean isVectorStoreEmpty() {
        try {
            // Do a simple search to check if data exists
            List<Document> testResults = embeddingService.searchRelevantTemplates(
                    "test query",
                    "summary",
                    null,
                    null,
                    1);

            boolean isEmpty = testResults.isEmpty();
            log.debug("Vector store empty check: {}", isEmpty);
            return isEmpty;

        } catch (Exception e) {
            // If search fails, assume empty and try to ingest
            log.warn("⚠️ Cannot check vector store status, assuming empty: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Manually reinitialize
     */
    @Transactional
    public void reinitializeKnowledgeBase() {
        log.info("Manual re-initialization triggered");
        initialized = false;
        initializeKnowledgeBase();
    }

    /**
     * Add new template
     */
    @Transactional
    public CVTemplateEntity addTemplate(CVTemplateEntity template) {
        template.setCreatedAt(LocalDateTime.now());
        template.setUpdatedAt(LocalDateTime.now());
        template.setIsActive(true);

        CVTemplateEntity saved = templateRepository.save(template);

        // Ingest if high quality
        if (saved.getRating() >= 4) {
            Document document = convertToDocument(saved);
            embeddingService.ingestTemplate(
                    saved.getId(),
                    document.getText(),
                    document.getMetadata());
            log.info("Template {} added and ingested", saved.getId());
        }

        return saved;
    }

    /**
     * Update template
     */
    @Transactional
    public CVTemplateEntity updateTemplate(String id, CVTemplateEntity updatedTemplate) {
        CVTemplateEntity existing = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found: " + id));

        existing.setContent(updatedTemplate.getContent());
        existing.setCategory(updatedTemplate.getCategory());
        existing.setLevel(updatedTemplate.getLevel());
        existing.setSection(updatedTemplate.getSection());
        existing.setRating(updatedTemplate.getRating());
        existing.setKeywords(updatedTemplate.getKeywords());
        existing.setUpdatedAt(LocalDateTime.now());

        CVTemplateEntity saved = templateRepository.save(existing);

        // Update in vector store
        if (saved.getRating() >= 4 && saved.getIsActive()) {
            Document document = convertToDocument(saved);
            embeddingService.updateTemplate(
                    saved.getId(),
                    document.getText(),
                    document.getMetadata());
        } else {
            embeddingService.deleteTemplate(saved.getId());
        }

        return saved;
    }

    /**
     * Soft delete template
     */
    @Transactional
    public void deleteTemplate(String id) {
        CVTemplateEntity template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found: " + id));

        template.setIsActive(false);
        template.setUpdatedAt(LocalDateTime.now());
        templateRepository.save(template);

        embeddingService.deleteTemplate(id);
        log.info("Template {} soft deleted", id);
    }

    /**
     * Convert entity to Document
     */
    private Document convertToDocument(CVTemplateEntity template) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("category", template.getCategory());
        metadata.put("level", template.getLevel());
        metadata.put("section", template.getSection());
        metadata.put("rating", template.getRating());
        metadata.put("keywords", template.getKeywords() != null ? template.getKeywords() : List.of());

        return new Document(
                template.getId(),
                template.getContent(),
                metadata);
    }

    /**
     * Create default templates
     */
    @Transactional
    protected void createDefaultTemplates() {
        List<CVTemplateDTO> dtos = templateDataProvider.getAllDefaultTemplates();

        List<CVTemplateEntity> entities = dtos.stream()
                .map(dto -> CVTemplateEntity.builder()
                        .category(dto.getCategory())
                        .level(dto.getLevel())
                        .section(dto.getSection())
                        .content(dto.getContent())
                        .rating(dto.getRating())
                        .keywords(dto.getKeywords())
                        .isActive(true)
                        .build())
                .collect(Collectors.toList());

        templateRepository.saveAll(entities);
        log.info("Created {} default templates", entities.size());
    }

    public List<CVTemplateEntity> getAllTemplates() {
        return templateRepository.findByIsActiveTrue();
    }
}