package com.resumeanalyzer.nlp.processors;

import com.resumeanalyzer.model.Resume;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EducationProcessor {

    // Education-related keywords
    private static final List<String> EDUCATION_KEYWORDS = Arrays.asList(
        "education", "degree", "university", "college", "bachelor", "master", 
        "phd", "doctorate", "graduated", "certification", "course", "diploma",
        "institute", "school", "academic", "studied"
    );

    // Degree patterns
    private static final List<String> DEGREE_TYPES = Arrays.asList(
        "Bachelor", "Master", "PhD", "Doctorate", "Associate", "Certificate",
        "B.S.", "B.A.", "M.S.", "M.A.", "MBA", "B.E.", "B.Tech", "M.Tech"
    );

    // Field of study patterns
    private static final List<String> STUDY_FIELDS = Arrays.asList(
        "Computer Science", "Engineering", "Business", "Mathematics", "Physics",
        "Chemistry", "Biology", "Economics", "Finance", "Marketing", "Psychology",
        "Information Technology", "Software Engineering", "Data Science"
    );

    public void extractEducation(Resume resume, String text) {
        List<String> educationInfo = new ArrayList<>();
        boolean inEducationSection = false;
        
        // Try NLP processing if available
        
        String[] lines = text.split("\\n");
        for (String line : lines) {
            String lowerLine = line.toLowerCase();
            
            // Check if we're in education section
            if (EDUCATION_KEYWORDS.stream().anyMatch(lowerLine::contains)) {
                inEducationSection = true;
            }
            
            // Look for degree patterns
            if (inEducationSection || 
                containsEducationKeywords(lowerLine) ||
                containsDegreePattern(line)) {
                
                if (!line.trim().isEmpty() && line.length() > 5) {
                    educationInfo.add(line.trim());
                }
            }
            
            // Stop education section when we hit other sections
            if (lowerLine.contains("experience") || lowerLine.contains("work") ||
                lowerLine.contains("skills") || lowerLine.contains("projects")) {
                inEducationSection = false;
            }
        }
        
        // Store education information
        if (!educationInfo.isEmpty()) {
            // You can add an education field to the Resume model
            // For now, we'll just log that education was found
            System.out.println("Found education information: " + educationInfo.size() + " entries");
        }
    }

    private boolean containsEducationKeywords(String line) {
        return EDUCATION_KEYWORDS.stream().anyMatch(line::contains);
    }

    private boolean containsDegreePattern(String line) {
        return DEGREE_TYPES.stream().anyMatch(degree -> 
            line.toLowerCase().contains(degree.toLowerCase()));
    }

    public String extractHighestDegree(String text) {
        String lowerText = text.toLowerCase();
        
        // Check for advanced degrees first
        if (lowerText.contains("phd") || lowerText.contains("doctorate")) {
            return "PhD/Doctorate";
        }
        if (lowerText.contains("master") || lowerText.contains("m.s.") || 
            lowerText.contains("m.a.") || lowerText.contains("mba") ||
            lowerText.contains("m.tech")) {
            return "Master's";
        }
        if (lowerText.contains("bachelor") || lowerText.contains("b.s.") || 
            lowerText.contains("b.a.") || lowerText.contains("b.e.") ||
            lowerText.contains("b.tech")) {
            return "Bachelor's";
        }
        if (lowerText.contains("associate") || lowerText.contains("diploma")) {
            return "Associate/Diploma";
        }
        
        return "Not Specified";
    }

    public List<String> extractFieldsOfStudy(String text) {
        List<String> foundFields = new ArrayList<>();
        String lowerText = text.toLowerCase();
        
        for (String field : STUDY_FIELDS) {
            if (lowerText.contains(field.toLowerCase())) {
                foundFields.add(field);
            }
        }
        
        return foundFields;
    }

    public List<String> extractInstitutions(String text) {
        List<String> institutions = new ArrayList<>();
        
        // Look for university/college patterns
        Pattern institutionPattern = Pattern.compile(
            "(?:University|College|Institute|School)\\s+of\\s+([A-Za-z\\s]+)|" +
            "([A-Za-z\\s]+)\\s+(?:University|College|Institute|School)",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = institutionPattern.matcher(text);
        while (matcher.find()) {
            String institution = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            if (institution != null && institution.trim().length() > 2) {
                institutions.add(institution.trim());
            }
        }
        
        return institutions;
    }

    public List<String> extractCertifications(String text) {
        List<String> certifications = new ArrayList<>();
        String lowerText = text.toLowerCase();
        
        // Common certification patterns
        String[] certKeywords = {
            "certified", "certification", "certificate", "aws certified",
            "microsoft certified", "google certified", "oracle certified",
            "cisco certified", "comptia", "pmp", "cissp", "cisa"
        };
        
        for (String keyword : certKeywords) {
            if (lowerText.contains(keyword)) {
                // Extract the line containing the certification
                String[] lines = text.split("\\n");
                for (String line : lines) {
                    if (line.toLowerCase().contains(keyword)) {
                        certifications.add(line.trim());
                        break;
                    }
                }
            }
        }
        
        return certifications;
    }

    public double calculateEducationScore(String text) {
        double score = 0.0;
        
        String highestDegree = extractHighestDegree(text);
        switch (highestDegree) {
            case "PhD/Doctorate":
                score += 1.0;
                break;
            case "Master's":
                score += 0.8;
                break;
            case "Bachelor's":
                score += 0.6;
                break;
            case "Associate/Diploma":
                score += 0.4;
                break;
            default:
                score += 0.2;
        }
        
        // Add points for certifications
        List<String> certifications = extractCertifications(text);
        score += certifications.size() * 0.1;
        
        return Math.min(score, 1.0);
    }
}
