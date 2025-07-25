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
        
        if (file.isEmpty()) {
            throw new Exception("File is empty");
        }
        
        try (InputStream inputStream = file.getInputStream()) {
            if (contentType != null && contentType.contains("pdf")) {
                return extractFromPdf(inputStream);
            } else if (fileName.endsWith(".docx")) {
                return extractFromDocx(inputStream);
            } else if (contentType != null && contentType.contains("text")) {
                return extractFromText(inputStream);
            } else {
                // Fallback to text extraction for unknown types
                return extractFromText(inputStream);
            }
        } catch (IOException e) {
            throw new Exception("Failed to extract text from file: " + e.getMessage(), e);
        }
    }

    private String extractFromPdf(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            if (document.isEncrypted()) {
                throw new IOException("PDF is encrypted and cannot be processed");
            }
            
            PDFTextStripper stripper = new PDFTextStripper();
            
            // Configure stripper to handle problematic characters better
            stripper.setSortByPosition(true);
            stripper.setAddMoreFormatting(false);
            
            String text = stripper.getText(document);
            
            // Clean up the extracted text to remove problematic characters
            return cleanPdfText(text);
        } catch (IOException e) {
            throw new IOException("Failed to extract text from PDF: " + e.getMessage(), e);
        }
    }
    
    private String cleanPdfText(String text) {
        if (text == null) return "";
        
        // Remove FontAwesome and other icon characters
        text = text.replaceAll("[\uE000-\uF8FF]", ""); // Private use area (FontAwesome)
        text = text.replaceAll("[\u2600-\u26FF]", ""); // Misc symbols
        text = text.replaceAll("[\u2700-\u27BF]", ""); // Dingbats
        
        // Remove other problematic Unicode ranges
        text = text.replaceAll("[\u0080-\u009F]", ""); // Control characters
        text = text.replaceAll("[\uFFF0-\uFFFF]", ""); // Specials
        
        // Replace common PDF extraction artifacts
        text = text.replace("", ""); // Replacement character
        text = text.replace("�", ""); // Another replacement character
        
        // Clean up excessive whitespace
        text = text.replaceAll("\\s+", " ").trim();
        
        return text;
    }

    private String extractFromDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            String text = extractor.getText();
            return text != null ? text.trim() : "";
        } catch (IOException e) {
            throw new IOException("Failed to extract text from DOCX: " + e.getMessage(), e);
        }
    }

    private String extractFromText(InputStream inputStream) throws IOException {
        try {
            String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return text != null ? text.trim() : "";
        } catch (IOException e) {
            throw new IOException("Failed to extract text from file: " + e.getMessage(), e);
        }
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
