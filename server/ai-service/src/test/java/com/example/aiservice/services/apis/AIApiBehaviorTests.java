package com.example.aiservice.services.apis;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;

import com.example.aiservice.dtos.ExperienceDto;
import com.example.aiservice.services.feigns.UserFeignClient;

/**
 * Safer unit tests that exercise AIApi internal helpers via reflection.
 * These tests avoid calling external services and are stable for CI.
 */
public class AIApiBehaviorTests {

    private AIApi aiApi;

    @BeforeEach
    void setup() {
        ChatClient chatClient = Mockito.mock(ChatClient.class);
        UserFeignClient userFeignClient = Mockito.mock(UserFeignClient.class);
        // other dependencies not used in these helper tests can be null/mocks
        aiApi = new AIApi(chatClient, null, null, null, null, userFeignClient);
    }

    @Test
    void smartTruncate_keeps_head_and_tail() throws Exception {
        Method m = AIApi.class.getDeclaredMethod("smartTruncate", String.class, int.class, String.class);
        m.setAccessible(true);

        String longText = "A".repeat(3000);
        String out = (String) m.invoke(aiApi, longText, 1000, "TEST");
        assertNotNull(out);
        assertTrue(out.contains("...[truncated]..."));
        assertTrue(out.length() <= 1050);
    }

    @Test
    void truncate_short_and_long_variants() throws Exception {
        Method m = AIApi.class.getDeclaredMethod("truncate", String.class, int.class);
        m.setAccessible(true);

        String shortText = "hello";
        String r1 = (String) m.invoke(aiApi, shortText, 10);
        assertEquals(shortText, r1);

        String longText = "x".repeat(50);
        String r2 = (String) m.invoke(aiApi, longText, 20);
        assertTrue(r2.endsWith("..."));
    }

    @Test
    void formatExperiencesCompact_lists_positions() throws Exception {
        Method m = AIApi.class.getDeclaredMethod("formatExperiencesCompact", java.util.List.class);
        m.setAccessible(true);

        ExperienceDto e = new ExperienceDto(null, "Comp", "Engineer", "2019-01", "2020-01", null);
        String out = (String) m.invoke(aiApi, Arrays.asList(e));
        assertTrue(out.contains("Engineer"));
        assertTrue(out.contains("Comp"));
    }

    @Test
    void detectCategory_and_level_from_text_and_cv() throws Exception {
        Method detectText = AIApi.class.getDeclaredMethod("detectCategoryFromText", String.class);
        detectText.setAccessible(true);
        assertEquals("tech", detectText.invoke(aiApi, "Java developer with Spring"));
        assertEquals("marketing", detectText.invoke(aiApi, "SEO marketing specialist"));

        Method detectLevelText = AIApi.class.getDeclaredMethod("detectLevelFromText", String.class);
        detectLevelText.setAccessible(true);
        assertEquals("senior", detectLevelText.invoke(aiApi, "10+ years Senior developer"));
        assertEquals("junior", detectLevelText.invoke(aiApi, "1 year, junior"));
    }

    @Test
    void calculateYearsOfExperience_counts_years() throws Exception {
        Method m = AIApi.class.getDeclaredMethod("calculateYearsOfExperience", java.util.List.class);
        m.setAccessible(true);

        ExperienceDto a = new ExperienceDto(null, "C1", "P", "2019-01", "2021-01", null);
        ExperienceDto b = new ExperienceDto(null, "C2", "P2", "2021-01", "present", null);
        Object years = m.invoke(aiApi, Arrays.asList(a, b));
        assertTrue(((Integer) years) >= 4);
    }
}
