package com.resumeanalyzer.nlp.processors;

import com.resumeanalyzer.model.Resume;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PersonalInfoProcessor {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("\\b(?:\\+?1[-\\s]?)?\\(?([0-9]{3})\\)?[-\\s]?([0-9]{3})[-\\s]?([0-9]{4})\\b");
    
    private static final Pattern LINKEDIN_PATTERN = 
        Pattern.compile("(?:https?://)?(?:www\\.)?linkedin\\.com/in/[\\w-]+/?", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern GITHUB_PATTERN = 
        Pattern.compile("(?:https?://)?(?:www\\.)?github\\.com/[\\w-]+/?", Pattern.CASE_INSENSITIVE);
    
    // Enhanced name patterns
    private static final Pattern NAME_PATTERN = 
        Pattern.compile("^[A-Z][a-zA-Z]+(?:\\s+[a-zA-Z]+){1,3}$");

    public void extractPersonalInfo(Resume resume, String text) {
        // Use enhanced pattern matching (more reliable than heavy NLP libraries)
        extractPersonalInfoWithPatterns(resume, text);
        
        // Try OpenNLP if available as backup
        if (resume.getFullName() == null) {
            extractNameWithOpenNLP(resume, text);
        }
        
        // Final fallback with improved name extraction
        if (resume.getFullName() == null) {
            extractNameFromText(resume, text);
        }
    }

    private void extractNameWithOpenNLP(Resume resume, String text) {
        try {
            // Try to use OpenNLP person name finder
            // Note: This requires downloading the person model, which we'll handle gracefully
            SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
            tokenizer.tokenize(text); // Basic tokenization to validate OpenNLP is working
            
            // For now, we'll use a simple approach since OpenNLP models need to be downloaded
            // This is more reliable than Stanford CoreNLP for this use case
            extractNameFromFirstLines(resume, text);
            
        } catch (Exception e) {
            System.err.println("OpenNLP name extraction failed, using fallback: " + e.getMessage());
        }
    }
    
    private void extractNameFromFirstLines(Resume resume, String text) {
        String[] lines = text.split("\\n");
        for (int i = 0; i < Math.min(5, lines.length); i++) {
            String line = lines[i].trim();
            if (isLikelyName(line)) {
                resume.setFullName(line);
                break;
            }
        }
    }
    
    private boolean isLikelyName(String line) {
        if (line == null || line.isEmpty()) return false;
        
        // Skip lines with common non-name patterns
        if (line.contains("@") || 
            line.contains("http") || 
            line.contains("www") ||
            line.matches(".*\\d{3,}.*") ||
            line.length() < 4 || 
            line.length() > 50) {
            return false;
        }
        
        // Check if it matches name pattern
        return NAME_PATTERN.matcher(line).matches();
    }

    private void extractPersonalInfoWithPatterns(Resume resume, String text) {
        // Extract email
        if (resume.getEmail() == null) {
            Matcher emailMatcher = EMAIL_PATTERN.matcher(text);
            if (emailMatcher.find()) {
                resume.setEmail(emailMatcher.group());
            }
        }

        // Extract phone
        if (resume.getPhoneNumber() == null) {
            Matcher phoneMatcher = PHONE_PATTERN.matcher(text);
            if (phoneMatcher.find()) {
                resume.setPhoneNumber(phoneMatcher.group());
            }
        }

        // Extract LinkedIn
        if (resume.getLinkedinUrl() == null) {
            Matcher linkedinMatcher = LINKEDIN_PATTERN.matcher(text);
            if (linkedinMatcher.find()) {
                resume.setLinkedinUrl(linkedinMatcher.group());
            }
        }

        // Extract GitHub
        if (resume.getGithubUrl() == null) {
            Matcher githubMatcher = GITHUB_PATTERN.matcher(text);
            if (githubMatcher.find()) {
                resume.setGithubUrl(githubMatcher.group());
            }
        }
    }

    private void extractNameFromText(Resume resume, String text) {
        String[] lines = text.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && 
                !line.contains("@") && 
                !line.contains("http") &&
                !line.matches(".*\\d{3,}.*") && 
                line.length() > 2 && line.length() < 50) {
                String[] words = line.split("\\s+");
                if (words.length >= 2 && words.length <= 4) {
                    boolean isName = true;
                    for (int i = 0; i < words.length; i++) {
                        String word = words[i];
                        if (word.length() < 2) {
                            isName = false;
                            break;
                        }
                        // First word must be capitalized, others can be lowercase (like "nath")
                        if (i == 0 && !Character.isUpperCase(word.charAt(0))) {
                            isName = false;
                            break;
                        }
                        // Check if word contains only letters (no numbers or special chars except hyphens)
                        if (!word.matches("^[a-zA-Z-]+$")) {
                            isName = false;
                            break;
                        }
                    }
                    if (isName) {
                        resume.setFullName(line);
                        break;
                    }
                }
            }
        }
    }

    public void extractAddress(Resume resume, String text) {
        // Enhanced address extraction logic
        String[] lines = text.split("\\n");
        Pattern addressPattern = Pattern.compile(
            "\\b\\d+\\s+[A-Za-z\\s,]+(?:Street|St|Avenue|Ave|Road|Rd|Lane|Ln|Drive|Dr|Boulevard|Blvd)\\b",
            Pattern.CASE_INSENSITIVE
        );
        
        for (String line : lines) {
            Matcher addressMatcher = addressPattern.matcher(line);
            if (addressMatcher.find() && resume.getAddress() == null) {
                resume.setAddress(line.trim());
                break;
            }
        }
    }
}
