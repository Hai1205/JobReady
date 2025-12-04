package com.example.aiservice.services.apis;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.example.aiservice.dtos.ExperienceDto;
import com.example.aiservice.services.EmbeddingService;
import com.example.aiservice.services.CompactPromptBuilder;
import com.example.aiservice.services.FileParserService;
import com.example.aiservice.services.feigns.CVFeignClient;
import com.example.aiservice.services.feigns.UserFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;

public class AIApiPrivateMethodsTest {

    private AIApi aiApi;

    @BeforeEach
    void setup() {
        ChatClient chatClient = Mockito.mock(ChatClient.class);
        EmbeddingService embeddingService = Mockito.mock(EmbeddingService.class);
        CompactPromptBuilder promptBuilder = Mockito.mock(CompactPromptBuilder.class);
        FileParserService fileParser = Mockito.mock(FileParserService.class);
        CVFeignClient cvFeign = Mockito.mock(CVFeignClient.class);
        UserFeignClient userFeign = Mockito.mock(UserFeignClient.class);

        aiApi = new AIApi(chatClient, embeddingService, promptBuilder, fileParser, cvFeign, userFeign);
    }

    @Test
    void detectCategoryFromText_shouldDetectTechMarketingAndGeneral() throws Exception {
        Method m = AIApi.class.getDeclaredMethod("detectCategoryFromText", String.class);
        m.setAccessible(true);

        assertEquals("tech", m.invoke(aiApi, "Experienced Java Developer"));
        assertEquals("marketing", m.invoke(aiApi, "Marketing and SEO specialist"));
        assertEquals("general", m.invoke(aiApi, "Some random content without keywords"));
    }

    @Test
    void detectLevelFromText_shouldDetectLevels() throws Exception {
        Method m = AIApi.class.getDeclaredMethod("detectLevelFromText", String.class);
        m.setAccessible(true);

        assertEquals("senior", m.invoke(aiApi, "Senior Java engineer with 10+ years"));
        assertEquals("junior", m.invoke(aiApi, "1 year experience, junior"));
        assertEquals("mid", m.invoke(aiApi, "Some middle level description"));
    }

    @Test
    void smartTruncate_and_truncate_behaviour() throws Exception {
        Method smart = AIApi.class.getDeclaredMethod("smartTruncate", String.class, int.class, String.class);
        smart.setAccessible(true);

        String longText = "A".repeat(5000);
        String res = (String) smart.invoke(aiApi, longText, 100, "TEST");
        assertTrue(res.contains("...[truncated]..."));
        assertTrue(res.length() <= 100 + 20); // some slack for truncation markers

        Method trunc = AIApi.class.getDeclaredMethod("truncate", String.class, int.class);
        trunc.setAccessible(true);
        String t = (String) trunc.invoke(aiApi, "HelloWorld", 50);
        assertEquals("HelloWorld", t);
        String t2 = (String) trunc.invoke(aiApi, "X".repeat(60), 10);
        assertTrue(t2.endsWith("..."));
    }

    @Test
    void calculateYearsOfExperience_countsYears() throws Exception {
        Method m = AIApi.class.getDeclaredMethod("calculateYearsOfExperience", List.class);
        m.setAccessible(true);

        List<ExperienceDto> exps = new ArrayList<>();
        exps.add(new ExperienceDto(null, "Acme", "Dev", "2018-01", "2020-01", null));
        exps.add(new ExperienceDto(null, "Acme2", "Dev2", "2020-02", "2022-02", null));

        Integer years = (Integer) m.invoke(aiApi, exps);
        assertEquals(4, years.intValue());
    }

    @Test
    void formatExperiencesCompact_buildsSummary() throws Exception {
        Method m = AIApi.class.getDeclaredMethod("formatExperiencesCompact", List.class);
        m.setAccessible(true);

        List<ExperienceDto> exps = new ArrayList<>();
        exps.add(new ExperienceDto(null, "CompanyA", "Engineer", "2019-01", "2020-01", null));
        exps.add(new ExperienceDto(null, "CompanyB", "Lead", "2020-02", "present", null));

        String out = (String) m.invoke(aiApi, exps);
        assertTrue(out.contains("Engineer @ CompanyA") || out.contains("CompanyA"));
    }
}
