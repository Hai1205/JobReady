package com.example.aiservice.services;

import com.example.aiservice.dtos.CVTemplateDTO;
import com.example.aiservice.entities.CVTemplateEntity;
import com.example.aiservice.repositories.CVTemplateCommandRepository;
import com.example.aiservice.repositories.CVTemplateQueryRepository;
import com.example.aiservice.repositories.SimpleCVTemplateRepository;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CVKnowledgeBaseService {

    private final CVTemplateCommandRepository templateCommandRepository;
    private final CVTemplateQueryRepository templateQueryRepository;
    private final SimpleCVTemplateRepository simpleTemplateRepository;
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
            long templateCount = templateQueryRepository.countAllTemplates();
            if (templateCount == 0) {
                log.info("📝 No templates in DB, creating defaults...");
                createDefaultTemplates();
                templateCount = templateQueryRepository.countAllTemplates();
            }

            // 2. Check vector store status
            boolean vectorStoreEmpty = isVectorStoreEmpty();

            if (vectorStoreEmpty) {
                log.info("Vector store is empty, ingesting {} templates...", templateCount);

                List<CVTemplateEntity> templates = templateQueryRepository
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
            log.error("Knowledge base initialization failed", e);
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
        LocalDateTime now = LocalDateTime.now();
        template.setCreatedAt(now);
        template.setUpdatedAt(now);
        template.setIsActive(true);

        // Generate ID if not set
        if (template.getId() == null) {
            template.setId(UUID.randomUUID().toString());
        }

        // Insert using command repository
        templateCommandRepository.insertTemplate(
                template.getId(),
                template.getCategory(),
                template.getLevel(),
                template.getSection(),
                template.getContent(),
                template.getRating(),
                template.getKeywords() != null ? String.join(",", template.getKeywords()) : null,
                template.getIsActive(),
                template.getCreatedAt(),
                template.getUpdatedAt());

        // Get the saved template
        CVTemplateEntity saved = templateQueryRepository.findTemplateById(template.getId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve saved template"));

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
        CVTemplateEntity existing = templateQueryRepository.findTemplateById(id)
                .orElseThrow(() -> new RuntimeException("Template not found: " + id));

        LocalDateTime now = LocalDateTime.now();

        // Update using command repository
        int updated = templateCommandRepository.updateTemplate(
                id,
                updatedTemplate.getContent(),
                updatedTemplate.getCategory(),
                updatedTemplate.getLevel(),
                updatedTemplate.getSection(),
                updatedTemplate.getRating(),
                updatedTemplate.getKeywords(),
                now);

        if (updated == 0) {
            throw new RuntimeException("Failed to update template: " + id);
        }

        // Get updated template
        CVTemplateEntity saved = templateQueryRepository.findTemplateById(id)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve updated template"));

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
        // Verify template exists
        if (!simpleTemplateRepository.existsById(id)) {
            throw new RuntimeException("Template not found: " + id);
        }

        // Soft delete using command repository
        int deleted = templateCommandRepository.softDeleteTemplate(id, LocalDateTime.now());

        if (deleted == 0) {
            throw new RuntimeException("Failed to delete template: " + id);
        }

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
        LocalDateTime now = LocalDateTime.now();

        for (CVTemplateDTO dto : dtos) {
            String id = UUID.randomUUID().toString();

            templateCommandRepository.insertTemplate(
                    id,
                    dto.getCategory(),
                    dto.getLevel(),
                    dto.getSection(),
                    dto.getContent(),
                    dto.getRating(),
                    dto.getKeywords() != null ? String.join(",", dto.getKeywords()) : null,
                    true,
                    now,
                    now);
        }

        log.info("Created {} default templates", dtos.size());
    }

    public List<CVTemplateEntity> getAllTemplates() {
        return templateQueryRepository.findByIsActiveTrue();
    }
}