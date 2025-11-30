package com.example.aiservice.services;

import com.example.aiservice.dtos.CVTemplateDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CVTemplateDataProvider {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    private final List<CVTemplateDTO> allTemplates = new ArrayList<>();

    @PostConstruct
    public void loadAllTemplates() {
        try {
            Resource[] resources = ResourcePatternUtils
                    .getResourcePatternResolver(resourceLoader)
                    .getResources("classpath:data/templates/*.json");

            for (Resource resource : resources) {
                log.info("Loading templates from: {}", resource.getFilename());
                List<CVTemplateDTO> templates = objectMapper.readValue(
                        resource.getInputStream(),
                        new TypeReference<List<CVTemplateDTO>>() {}
                );
                allTemplates.addAll(templates);
            }

            log.info("Successfully loaded {} default templates from JSON files", allTemplates.size());

        } catch (IOException e) {
            log.error("Failed to load default CV templates from JSON", e);
            throw new RuntimeException("Cannot load default templates", e);
        }
    }

    public List<CVTemplateDTO> getAllDefaultTemplates() {
        return List.copyOf(allTemplates);
    }
}

