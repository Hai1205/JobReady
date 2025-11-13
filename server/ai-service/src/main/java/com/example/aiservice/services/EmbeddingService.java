package com.example.aiservice.services;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    // Simple in-memory vector store
    private final Map<String, float[]> vectorStore = new HashMap<>();
    private final Map<String, String> contentStore = new HashMap<>();
    private final Map<String, Map<String, Object>> metadataStore = new HashMap<>();

    // Simple embedding simulation (in real implementation, use actual embedding
    // model)
    public float[] generateEmbedding(String text) {
        // Simple hash-based embedding for demo (replace with real embedding model)
        float[] embedding = new float[128]; // Smaller dimension for demo
        String hash = String.valueOf(text.hashCode());
        Random random = new Random(hash.hashCode());

        for (int i = 0; i < embedding.length; i++) {
            embedding[i] = (float) random.nextGaussian();
        }

        // Normalize the vector
        float norm = calculateNorm(embedding);
        for (int i = 0; i < embedding.length; i++) {
            embedding[i] /= norm;
        }

        return embedding;
    }

    private float calculateNorm(float[] vector) {
        float sum = 0;
        for (float v : vector) {
            sum += v * v;
        }
        return (float) Math.sqrt(sum);
    }

    private float cosineSimilarity(float[] a, float[] b) {
        float dotProduct = 0;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
        }
        return dotProduct; // Vectors are normalized, so this is cosine similarity
    }

    public void ingestCV(String content, String title, String userId) {
        float[] embedding = generateEmbedding(content);
        String id = "cv-" + UUID.randomUUID().toString();

        vectorStore.put(id, embedding);
        contentStore.put(id, content);
        metadataStore.put(id, Map.of(
                "title", title,
                "userId", userId,
                "type", "cv"));
    }

    public void ingestJobDescription(String content, String title, String jobId) {
        float[] embedding = generateEmbedding(content);
        String id = "jd-" + UUID.randomUUID().toString();

        vectorStore.put(id, embedding);
        contentStore.put(id, content);
        metadataStore.put(id, Map.of(
                "title", title,
                "jobId", jobId,
                "type", "job_description"));
    }

    public List<String> retrieveSimilar(String query, int topK) {
        float[] queryEmbedding = generateEmbedding(query);

        // Calculate similarities
        List<SimilarityResult> results = new ArrayList<>();
        for (Map.Entry<String, float[]> entry : vectorStore.entrySet()) {
            float similarity = cosineSimilarity(queryEmbedding, entry.getValue());
            results.add(new SimilarityResult(entry.getKey(), similarity));
        }

        // Sort by similarity and return top K
        return results.stream()
                .sorted((a, b) -> Float.compare(b.similarity, a.similarity))
                .limit(topK)
                .map(result -> contentStore.get(result.id))
                .collect(Collectors.toList());
    }

    public List<Document> retrieveSimilarDocuments(String query, int topK) {
        float[] queryEmbedding = generateEmbedding(query);

        // Calculate similarities
        List<SimilarityResult> results = new ArrayList<>();
        for (Map.Entry<String, float[]> entry : vectorStore.entrySet()) {
            float similarity = cosineSimilarity(queryEmbedding, entry.getValue());
            results.add(new SimilarityResult(entry.getKey(), similarity));
        }

        // Sort by similarity and return top K documents
        return results.stream()
                .sorted((a, b) -> Float.compare(b.similarity, a.similarity))
                .limit(topK)
                .map(result -> new Document(
                        contentStore.get(result.id),
                        metadataStore.get(result.id)))
                .collect(Collectors.toList());
    }

    // Simple document class
    public static class Document {
        private final String content;
        private final Map<String, Object> metadata;

        public Document(String content, Map<String, Object> metadata) {
            this.content = content;
            this.metadata = metadata;
        }

        public String getContent() {
            return content;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }
    }

    private static class SimilarityResult {
        final String id;
        final float similarity;

        SimilarityResult(String id, float similarity) {
            this.id = id;
            this.similarity = similarity;
        }
    }
}