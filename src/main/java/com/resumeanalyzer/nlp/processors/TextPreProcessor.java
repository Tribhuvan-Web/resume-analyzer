package com.resumeanalyzer.nlp.processors;

import org.springframework.stereotype.Component;

@Component
public class TextPreProcessor {

    public String preprocessText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        
        // Remove non-printable characters and special Unicode characters
        text = text.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")
                  .replaceAll("[\u0080-\u009F]", "") // Remove control characters
                  .replaceAll("[\uFFF0-\uFFFF]", "") // Remove special Unicode ranges
                  .replaceAll("[\uE000-\uF8FF]", "") // Remove private use area (FontAwesome icons)
                  .replaceAll("[\u2600-\u26FF]", "") // Remove misc symbols
                  .replaceAll("[\u2700-\u27BF]", "") // Remove dingbats
                  .replaceAll("[\uFE00-\uFE0F]", "") // Remove variation selectors
                  .replaceAll("[\u200B-\u200D\uFEFF]", ""); // Remove zero-width characters
        
        // Normalize whitespace
        text = text.replaceAll("\\s+", " ")
                  .replaceAll("\\n+", "\n")
                  .replaceAll("\\r", "")
                  .trim();
        
        // Remove or replace common problematic characters
        text = text.replace("", "") // Remove replacement character
                  .replace("�", "") // Remove another replacement character
                  .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", ""); // Remove other control chars
        
        return text;
    }

    public String[] splitIntoLines(String text) {
        return text.split("\\n");
    }

    public String cleanLine(String line) {
        return line.trim().replaceAll("\\s+", " ");
    }
}
