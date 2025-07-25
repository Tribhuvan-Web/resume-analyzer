package com.resumeanalyzer.nlp.processors;

import com.resumeanalyzer.model.Resume;
import org.springframework.stereotype.Component;

@Component
public class SummaryProcessor {

    public void generateSummary(Resume resume, String text) {
        StringBuilder summary = new StringBuilder();
        
        // Add experience information
        if (resume.getTotalExperienceYears() != null && resume.getTotalExperienceYears() > 0) {
            summary.append(String.format("Professional with %d years of experience. ", 
                resume.getTotalExperienceYears()));
        }
        
        // Add seniority level
        if (resume.getSeniority() != null) {
            summary.append(String.format("%s level candidate. ", resume.getSeniority()));
        }
        
        // Count skills by category
        String skillsJson = resume.getSkillsJson();
        if (skillsJson != null && !skillsJson.equals("[]")) {
            long skillCount = skillsJson.chars().filter(ch -> ch == '{').count();
            summary.append(String.format("Skilled in %d technologies across multiple domains. ", skillCount));
        }
        
        // Add domain expertise if detected
        String domainExpertise = detectDomainExpertise(text);
        if (!domainExpertise.isEmpty()) {
            summary.append(String.format("Specializes in %s. ", domainExpertise));
        }
        
        // Add education summary
        String educationLevel = detectEducationLevel(text);
        if (!educationLevel.isEmpty()) {
            summary.append(String.format("Holds %s degree. ", educationLevel));
        }
        
        resume.setSummary(summary.toString().trim());
    }

    private String detectDomainExpertise(String text) {
        String lowerText = text.toLowerCase();
        
        if (countOccurrences(lowerText, new String[]{"web", "frontend", "react", "angular", "vue"}) >= 3) {
            return "Frontend Development";
        }
        if (countOccurrences(lowerText, new String[]{"backend", "api", "server", "database", "microservices"}) >= 3) {
            return "Backend Development";
        }
        if (countOccurrences(lowerText, new String[]{"fullstack", "full stack", "frontend", "backend"}) >= 2) {
            return "Full Stack Development";
        }
        if (countOccurrences(lowerText, new String[]{"mobile", "android", "ios", "react native", "flutter"}) >= 2) {
            return "Mobile Development";
        }
        if (countOccurrences(lowerText, new String[]{"data science", "machine learning", "ai", "analytics", "python"}) >= 3) {
            return "Data Science & Analytics";
        }
        if (countOccurrences(lowerText, new String[]{"devops", "cloud", "aws", "docker", "kubernetes"}) >= 3) {
            return "DevOps & Cloud";
        }
        
        return "";
    }

    private String detectEducationLevel(String text) {
        String lowerText = text.toLowerCase();
        
        if (lowerText.contains("phd") || lowerText.contains("doctorate")) {
            return "PhD/Doctorate";
        }
        if (lowerText.contains("master") || lowerText.contains("mba")) {
            return "Master's";
        }
        if (lowerText.contains("bachelor")) {
            return "Bachelor's";
        }
        
        return "";
    }

    private int countOccurrences(String text, String[] keywords) {
        int count = 0;
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                count++;
            }
        }
        return count;
    }

    public String generateDetailedSummary(Resume resume, String text) {
        StringBuilder detailed = new StringBuilder();
        
        // Personal info summary
        if (resume.getFullName() != null) {
            detailed.append(resume.getFullName()).append(" is a ");
        }
        
        // Experience and seniority
        if (resume.getSeniority() != null) {
            detailed.append(resume.getSeniority().toLowerCase()).append(" ");
        }
        
        String domain = detectDomainExpertise(text);
        if (!domain.isEmpty()) {
            detailed.append(domain.toLowerCase()).append(" professional ");
        } else {
            detailed.append("technology professional ");
        }
        
        if (resume.getTotalExperienceYears() != null && resume.getTotalExperienceYears() > 0) {
            detailed.append(String.format("with %d years of experience. ", resume.getTotalExperienceYears()));
        }
        
        // Skills summary
        String skillsJson = resume.getSkillsJson();
        if (skillsJson != null && !skillsJson.equals("[]")) {
            long skillCount = skillsJson.chars().filter(ch -> ch == '{').count();
            detailed.append(String.format("Demonstrates proficiency in %d technologies ", skillCount));
            detailed.append("spanning multiple technical domains. ");
        }
        
        return detailed.toString().trim();
    }

    public double calculateOverallScore(Resume resume, String text) {
        double score = 0.0;
        
        // Experience weight: 30%
        if (resume.getTotalExperienceYears() != null) {
            score += Math.min(resume.getTotalExperienceYears() * 0.05, 0.3);
        }
        
        // Skills weight: 40%
        String skillsJson = resume.getSkillsJson();
        if (skillsJson != null && !skillsJson.equals("[]")) {
            long skillCount = skillsJson.chars().filter(ch -> ch == '{').count();
            score += Math.min(skillCount * 0.02, 0.4);
        }
        
        // Education weight: 20%
        String educationLevel = detectEducationLevel(text);
        switch (educationLevel) {
            case "PhD/Doctorate": score += 0.2; break;
            case "Master's": score += 0.15; break;
            case "Bachelor's": score += 0.1; break;
            default: score += 0.05;
        }
        
        // Completeness weight: 10%
        double completeness = 0.0;
        if (resume.getFullName() != null) completeness += 0.02;
        if (resume.getEmail() != null) completeness += 0.02;
        if (resume.getPhoneNumber() != null) completeness += 0.02;
        if (resume.getLinkedinUrl() != null) completeness += 0.02;
        if (resume.getGithubUrl() != null) completeness += 0.02;
        score += completeness;
        
        return Math.min(score, 1.0);
    }
}
