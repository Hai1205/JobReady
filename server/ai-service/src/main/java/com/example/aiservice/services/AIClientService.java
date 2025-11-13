package com.example.aiservice.services;

import com.example.aiservice.configs.OpenRouterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AIClientService {

    @Autowired
    private OpenRouterConfig openRouterConfig;

    public String callAI(String prompt) {
        return openRouterConfig.callModel(prompt);
    }

    public String callAIWithSystem(String systemPrompt, String userPrompt) {
        return openRouterConfig.callModelWithSystemPrompt(systemPrompt, userPrompt);
    }
}