package com.example.aiservice.services;

import com.example.aiservice.configs.RAGConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final VectorStore vectorStore;
    private final RAGConfig ragConfig;

    /**
     * Xử lý và lưu tài liệu vào vector store
     */
    public void processAndStoreDocument(MultipartFile file, String userId, Map<String, Object> metadata) throws IOException {
        log.info("Processing document: {}", file.getOriginalFilename());
        
        // Đọc tài liệu
        List<Document> documents = readDocument(file);
        
        // Thêm metadata
        documents.forEach(doc -> {
            doc.getMetadata().put("userId", userId);
            doc.getMetadata().put("filename", file.getOriginalFilename());
            doc.getMetadata().put("contentType", file.getContentType());
            if (metadata != null) {
                doc.getMetadata().putAll(metadata);
            }
        });
        
        // Chia nhỏ tài liệu thành chunks
        TokenTextSplitter splitter = new TokenTextSplitter(
                ragConfig.getChunkSize(),
                ragConfig.getChunkOverlap(),
                5,
                10000,
                true
        );
        List<Document> chunks = splitter.apply(documents);
        
        log.info("Split document into {} chunks", chunks.size());
        
        // Lưu vào vector store
        vectorStore.add(chunks);
        
        log.info("Successfully stored {} document chunks for user {}", chunks.size(), userId);
    }

    /**
     * Xử lý và lưu text trực tiếp
     */
    public void processAndStoreText(String text, String userId, Map<String, Object> metadata) {
        log.info("Processing text content for user: {}", userId);
        
        // Tạo document từ text
        Document document = new Document(text);
        document.getMetadata().put("userId", userId);
        if (metadata != null) {
            document.getMetadata().putAll(metadata);
        }
        
        // Chia nhỏ text thành chunks
        TokenTextSplitter splitter = new TokenTextSplitter(
                ragConfig.getChunkSize(),
                ragConfig.getChunkOverlap(),
                5,
                10000,
                true
        );
        List<Document> chunks = splitter.apply(List.of(document));
        
        log.info("Split text into {} chunks", chunks.size());
        
        // Lưu vào vector store
        vectorStore.add(chunks);
        
        log.info("Successfully stored {} text chunks for user {}", chunks.size(), userId);
    }

    /**
     * Đọc tài liệu dựa trên loại file
     */
    private List<Document> readDocument(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        byte[] bytes = file.getBytes();
        Resource resource = new ByteArrayResource(bytes);

        if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
            return readPdfDocument(resource);
        } else {
            return readGenericDocument(resource);
        }
    }

    /**
     * Đọc file PDF
     */
    private List<Document> readPdfDocument(Resource resource) {
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
                        .withNumberOfBottomTextLinesToDelete(3)
                        .withNumberOfTopPagesToSkipBeforeDelete(1)
                        .build())
                .withPagesPerDocument(1)
                .build();

        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(resource, config);
        return pdfReader.get();
    }

    /**
     * Đọc các loại file khác (Word, Excel, Text, etc.) bằng Tika
     */
    private List<Document> readGenericDocument(Resource resource) {
        TikaDocumentReader tikaReader = new TikaDocumentReader(resource);
        return tikaReader.get();
    }

    /**
     * Xóa tất cả documents của user
     */
    public void deleteDocumentsByUser(String userId) {
        log.info("Deleting all documents for user: {}", userId);
        // Note: Spring AI VectorStore không có API delete by metadata trực tiếp
        // Bạn cần implement custom logic hoặc sử dụng JdbcTemplate
        log.warn("Delete by user not implemented yet. Requires custom implementation.");
    }
}
