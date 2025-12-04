package com.example.aiservice.services.apis;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.aiservice.dtos.AnalyzeResultDto;
import com.example.aiservice.services.EmbeddingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import com.example.aiservice.services.FileParserService;
import com.example.aiservice.services.CompactPromptBuilder;
import com.example.aiservice.services.feigns.CVFeignClient;
import com.example.aiservice.services.feigns.UserFeignClient;

public class AIApiPublicMethodsTest {

    private AIApi aiApi;
    private ChatClient chatClient;
    private EmbeddingService embeddingService;
    private CompactPromptBuilder promptBuilder;
    private FileParserService fileParser;
    private CVFeignClient cvFeign;
    private UserFeignClient userFeign;

    @BeforeEach
    void setup() {
        chatClient = mock(ChatClient.class);
        embeddingService = mock(EmbeddingService.class);
        promptBuilder = mock(CompactPromptBuilder.class);
        fileParser = mock(FileParserService.class);
        cvFeign = mock(CVFeignClient.class);
        userFeign = mock(UserFeignClient.class);

        aiApi = new AIApi(chatClient, embeddingService, promptBuilder, fileParser, cvFeign, userFeign);
    }

    @Test
    void extractJsonFromResponse_returns_plain_json_when_wrapped_and_plain() throws Exception {
        String wrapped = "```json\n{ \"ok\": true }\n```";
        String plain = "{\"ok\":true}";

        // call public-facing helper via reflection to reuse existing private method
        var m = AIApi.class.getDeclaredMethod("extractJsonFromResponse", String.class);
        m.setAccessible(true);

        Object r1 = m.invoke(aiApi, wrapped);
        assertTrue(r1 instanceof String);
        assertEquals(plain, ((String) r1).replaceAll("\\s+", ""));

        Object r2 = m.invoke(aiApi, plain);
        assertEquals(plain, r2);
    }

    @Test
    void parseAnalyzeResult_handles_good_and_bad_json() throws Exception {
        var m = AIApi.class.getDeclaredMethod("parseAnalyzeResult", String.class);
        m.setAccessible(true);

        String good = "{\"overallScore\":90,\"suggestions\":[]}";
        AnalyzeResultDto dto = (AnalyzeResultDto) m.invoke(aiApi, good);
        assertNotNull(dto);
        assertEquals(90, dto.getOverallScore().intValue());

        String bad = "not json";
        AnalyzeResultDto dto2 = (AnalyzeResultDto) m.invoke(aiApi, bad);
        assertNotNull(dto2);
        assertNotNull(dto2.getSuggestions());
    }
}
