package com.resumeanalyzer.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class FileProcessor {

    public String extractText(MultipartFile file) throws Exception {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        
        if (fileName == null) {
            throw new Exception("File name cannot be null");
        }
        
        try (InputStream inputStream = file.getInputStream()) {
            if (contentType != null && contentType.contains("pdf")) {
                return extractFromPdf(inputStream);
            } else if (fileName.endsWith(".docx")) {
                return extractFromDocx(inputStream);
            } else if (contentType != null && contentType.contains("text")) {
                return extractFromText(inputStream);
            } else {
                return extractFromText(inputStream);
            }
        }
    }

    private String extractFromPdf(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractFromDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    private String extractFromText(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    public boolean isSupportedFileType(String fileName, String contentType) {
        if (fileName == null) return false;
        
        return fileName.endsWith(".pdf") ||
               fileName.endsWith(".docx") ||
               fileName.endsWith(".txt") ||
               (contentType != null && (
                   contentType.contains("pdf") ||
                   contentType.contains("word") ||
                   contentType.contains("text")
               ));
    }
}
