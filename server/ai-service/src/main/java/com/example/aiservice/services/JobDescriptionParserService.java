package com.example.aiservice.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.Loader;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

@Service
public class JobDescriptionParserService {

    public String extractTextFromFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty())
            return null;
        String filename = file.getOriginalFilename();
        if (filename == null)
            return null;
        String lower = filename.toLowerCase();

        if (lower.endsWith(".pdf")) {
            return extractTextFromPdf(file.getInputStream());
        } else if (lower.endsWith(".docx")) {
            return extractTextFromDocx(file.getInputStream());
        } else {
            // fallback: try to read as plain text
            return readAsText(file.getInputStream());
        }
    }

    private String extractTextFromPdf(InputStream in) throws Exception {
        // PDFBox 3.x API - use Loader.loadPDF instead of PDDocument.load
        try (PDDocument document = Loader.loadPDF(in.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            return cleanText(text);
        }
    }

    private String extractTextFromDocx(InputStream in) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(in)) {
            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph p : doc.getParagraphs()) {
                sb.append(p.getText()).append("\n");
            }
            return cleanText(sb.toString());
        }
    }

    private String readAsText(InputStream in) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        return cleanText(sb.toString());
    }

    // simple cleaning: remove excessive whitespace and repeated headers/footers
    // heuristics
    private String cleanText(String input) {
        if (input == null)
            return null;
        // collapse multiple blank lines
        String cleaned = input.replaceAll("\r", "\n");
        cleaned = cleaned.replaceAll("\n{2,}", "\n\n");
        // trim each line
        StringBuilder out = new StringBuilder();
        for (String line : cleaned.split("\n")) {
            String t = line.trim();
            if (!t.isEmpty()) {
                out.append(t).append('\n');
            }
        }
        return out.toString().trim();
    }
}
