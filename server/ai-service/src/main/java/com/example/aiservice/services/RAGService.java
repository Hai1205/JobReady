package com.example.aiservice.services;

import com.example.aiservice.configs.RAGConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RAGService {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;
    private final RAGConfig ragConfig;

    /**
     * Thực hiện RAG query cơ bản
     */
    public String query(String question, String userId) {
        log.info("RAG query for user {}: {}", userId, question);

        // 1. Retrieval - Tìm kiếm documents liên quan
        List<Document> relevantDocs = retrieveRelevantDocuments(question, userId);

        if (relevantDocs.isEmpty()) {
            return "Tôi không tìm thấy thông tin liên quan trong tài liệu của bạn để trả lời câu hỏi này.";
        }

        // 2. Augmented - Tạo context từ documents
        String context = buildContext(relevantDocs);

        // 3. Generation - Generate answer dựa trên context
        return generateAnswer(question, context);
    }

    /**
     * RAG với custom system prompt
     */
    public String queryWithSystemPrompt(String question, String userId, String systemPrompt) {
        log.info("RAG query with system prompt for user {}: {}", userId, question);

        // 1. Retrieval
        List<Document> relevantDocs = retrieveRelevantDocuments(question, userId);

        if (relevantDocs.isEmpty()) {
            return "Tôi không tìm thấy thông tin liên quan trong tài liệu của bạn.";
        }

        // 2. Augmented
        String context = buildContext(relevantDocs);

        // 3. Generation với system prompt
        return generateAnswerWithSystemPrompt(question, context, systemPrompt);
    }

    /**
     * RAG với filter metadata
     */
    public String queryWithFilter(String question, String userId, Map<String, Object> filterMetadata) {
        log.info("RAG query with filter for user {}: {}", userId, question);

        // 1. Retrieval với filter
        List<Document> relevantDocs = retrieveRelevantDocumentsWithFilter(question, userId, filterMetadata);

        if (relevantDocs.isEmpty()) {
            return "Không tìm thấy thông tin phù hợp với bộ lọc của bạn.";
        }

        // 2. Augmented
        String context = buildContext(relevantDocs);

        // 3. Generation
        return generateAnswer(question, context);
    }

    /**
     * Retrieve relevant documents từ vector store
     */
    private List<Document> retrieveRelevantDocuments(String query, String userId) {
        SearchRequest searchRequest = SearchRequest.query(query)
                .withTopK(ragConfig.getTopK())
                .withSimilarityThreshold(0.7)
                .withFilterExpression("userId == '" + userId + "'");

        return vectorStore.similaritySearch(searchRequest);
    }

    /**
     * Retrieve với filter metadata bổ sung
     */
    private List<Document> retrieveRelevantDocumentsWithFilter(
            String query,
            String userId,
            Map<String, Object> filterMetadata) {

        // Build filter expression
        StringBuilder filterExpr = new StringBuilder("userId == '" + userId + "'");
        if (filterMetadata != null && !filterMetadata.isEmpty()) {
            for (Map.Entry<String, Object> entry : filterMetadata.entrySet()) {
                filterExpr.append(" && ")
                        .append(entry.getKey())
                        .append(" == '")
                        .append(entry.getValue())
                        .append("'");
            }
        }

        SearchRequest searchRequest = SearchRequest.query(query)
                .withTopK(ragConfig.getTopK())
                .withSimilarityThreshold(0.7)
                .withFilterExpression(filterExpr.toString());

        return vectorStore.similaritySearch(searchRequest);
    }

    /**
     * Build context từ danh sách documents
     */
    private String buildContext(List<Document> documents) {
        return documents.stream()
                .map(doc -> doc.getContent())
                .collect(Collectors.joining("\n\n---\n\n"));
    }

    /**
     * Generate answer sử dụng ChatModel
     */
    private String generateAnswer(String question, String context) {
        String promptTemplate = """
                Dựa trên thông tin sau đây:

                {context}

                Hãy trả lời câu hỏi: {question}

                Nếu thông tin không đủ để trả lời, hãy nói rằng bạn không có đủ thông tin.
                Trả lời bằng tiếng Việt một cách chuyên nghiệp và chi tiết.
                """;

        Map<String, Object> model = new HashMap<>();
        model.put("context", context);
        model.put("question", question);

        PromptTemplate template = new PromptTemplate(promptTemplate);
        Prompt prompt = template.create(model);

        return chatModel.call(prompt).getResult().getOutput().getContent();
    }

    /**
     * Generate answer với custom system prompt
     */
    private String generateAnswerWithSystemPrompt(String question, String context, String systemPrompt) {
        ChatClient chatClient = ChatClient.builder(chatModel).build();

        return chatClient.prompt()
                .system(systemPrompt)
                .user(u -> u.text("""
                        Dựa trên thông tin sau:

                        {context}

                        Câu hỏi: {question}
                        """)
                        .param("context", context)
                        .param("question", question))
                .call()
                .content();
    }

    /**
     * Lấy danh sách documents liên quan (không generate answer)
     */
    public List<Map<String, Object>> getRelevantDocuments(String query, String userId) {
        List<Document> documents = retrieveRelevantDocuments(query, userId);

        return documents.stream()
                .map(doc -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("content", doc.getContent());
                    result.put("metadata", doc.getMetadata());
                    return result;
                })
                .collect(Collectors.toList());
    }
}
