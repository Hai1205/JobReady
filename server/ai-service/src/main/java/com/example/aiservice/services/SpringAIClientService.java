package com.example.aiservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service sử dụng Spring AI ChatModel
 * Thay thế cho OpenRouterConfig thủ công
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpringAIClientService {

    private final ChatModel chatModel;

    public String callAI(String prompt) {
        log.debug("Calling AI with prompt: {}", prompt);
        return chatModel.call(prompt);
    }

    public String callAIWithSystem(String systemPrompt, String userPrompt) {
        log.debug("Calling AI with system prompt");

        Message systemMessage = new SystemMessage(systemPrompt);
        Message userMessage = new UserMessage(userPrompt);

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        return chatModel.call(prompt).getResult().getOutput().getContent();
    }
}
