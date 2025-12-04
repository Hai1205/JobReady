package com.example.aiservice.services.apis;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.*;

import com.example.aiservice.dtos.AnalyzeResultDto;
import com.example.aiservice.dtos.CVDto;
import com.example.aiservice.dtos.ExperienceDto;
import com.example.aiservice.dtos.PersonalInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.document.Document;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.multipart.MultipartFile;

import com.example.aiservice.services.CompactPromptBuilder;
import com.example.aiservice.services.EmbeddingService;
import com.example.aiservice.services.FileParserService;
import com.example.aiservice.services.feigns.CVFeignClient;
import com.example.aiservice.services.feigns.UserFeignClient;

import java.io.InputStream;

public class AIApiAdditionalTests {

    private AIApi aiApi;
    private FileParserService fileParser;

    @BeforeEach
    void setup() {
        ChatClient chatClient = Mockito.mock(ChatClient.class);
        EmbeddingService embeddingService = Mockito.mock(EmbeddingService.class);
        CompactPromptBuilder promptBuilder = Mockito.mock(CompactPromptBuilder.class);
        fileParser = Mockito.mock(FileParserService.class);
        CVFeignClient cvFeign = Mockito.mock(CVFeignClient.class);
        UserFeignClient userFeign = Mockito.mock(UserFeignClient.class);

        aiApi = new AIApi(chatClient, embeddingService, promptBuilder, fileParser, cvFeign, userFeign);
    }

    @Test
    void extractJsonFromResponse_variants() throws Exception {
        Method m = AIApi.class.getDeclaredMethod("extractJsonFromResponse", String.class);
        m.setAccessible(true);

        String wrapped = "```json\n{ \"k\": 1 }\n```";
        Object res1 = m.invoke(aiApi, wrapped);
        assertTrue(res1 instanceof String);
        assertTrue(((String) res1).trim().startsWith("{"));

        String plain = "{\"a\":true}";
        Object res2 = m.invoke(aiApi, plain);
        assertEquals(plain, res2);
    }

    @Test
    void parseAnalyzeResult_valid_and_invalid() throws Exception {
        Method m = AIApi.class.getDeclaredMethod("parseAnalyzeResult", String.class);
        m.setAccessible(true);

        String good = "```json\n{ \"overallScore\": 88, \"suggestions\": [{\"id\":\"s1\",\"message\":\"ok\"}] }\n```";
        AnalyzeResultDto out = (AnalyzeResultDto) m.invoke(aiApi, good);
        assertNotNull(out);
        assertEquals(88, out.getOverallScore().intValue());
        assertNotNull(out.getSuggestions());

        String bad = "not a json";
        AnalyzeResultDto out2 = (AnalyzeResultDto) m.invoke(aiApi, bad);
        assertNotNull(out2);
        assertNotNull(out2.getSuggestions());
        assertEquals(0, out2.getSuggestions().size());
    }

    @Test
    void buildCompactPrompts_and_formatCVCompact() throws Exception {
        // buildCompactAnalysisPrompt(String, Map<String, List<Document>>)
        Method buildAnalysis = AIApi.class.getDeclaredMethod("buildCompactAnalysisPrompt", String.class, Map.class);
        buildAnalysis.setAccessible(true);

        Document doc = Mockito.mock(Document.class);
        Mockito.when(doc.getText()).thenReturn("Example best practice text for CV.");

        Map<String, List<Document>> examples = new HashMap<>();
        examples.put("summary", Collections.singletonList(doc));

        String analysisPrompt = (String) buildAnalysis.invoke(aiApi, "Candidate CV content here", examples);
        assertTrue(analysisPrompt.contains("Best Practices") || analysisPrompt.contains("CV:"));

        // buildCompactImprovementPrompt(String, List<Document>)
        Method buildImprove = AIApi.class.getDeclaredMethod("buildCompactImprovementPrompt", String.class, List.class);
        buildImprove.setAccessible(true);

        String improvePrompt = (String) buildImprove.invoke(aiApi, "Improve this sentence.",
                Collections.singletonList(doc));
        assertTrue(improvePrompt.contains("Improve") || improvePrompt.contains("Example"));

        // formatCVCompact
        Method format = AIApi.class.getDeclaredMethod("formatCVCompact",
                Class.forName("com.example.aiservice.dtos.CVDto"));
        format.setAccessible(true);

        CVDto cv = CVDto.builder()
                .title("Title")
                .personalInfo(
                        new PersonalInfoDto(null, "John Doe", "john@doe", null, null, "A summary", null, null, null))
                .experiences(Arrays.asList(new ExperienceDto(null, "Comp", "Engineer", "2019-01", "2020-01", null)))
                .skills(Arrays.asList("java", "spring"))
                .build();

        String compact = (String) format.invoke(aiApi, cv);
        assertTrue(compact.contains("John Doe") || compact.contains("Experience:"));
    }

    @Test
    void handleExtractJobDescriptionText_usesFileParser_whenPresent() throws Exception {
        Method m = AIApi.class.getDeclaredMethod("handleExtractJobDescriptionText",
                Class.forName("org.springframework.web.multipart.MultipartFile"), String.class);
        m.setAccessible(true);

        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.isEmpty()).thenReturn(false);
        Mockito.when(fileParser.extractTextFromFile(file)).thenReturn("Extracted JD text from file");

        Object res = m.invoke(aiApi, file, "original text");
        assertEquals("Extracted JD text from file", res);

        // when file is null
        Object res2 = m.invoke(aiApi, (Object) null, "orig");
        assertEquals("orig", res2);
    }
}
