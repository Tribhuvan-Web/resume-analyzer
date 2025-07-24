package com.resumeanalyzer.nlp;

import com.resumeanalyzer.model.Resume;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NLPProcessor {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("\\b(?:\\+?1[-\\s]?)?\\(?([0-9]{3})\\)?[-\\s]?([0-9]{3})[-\\s]?([0-9]{4})\\b");
    
    private static final Pattern LINKEDIN_PATTERN = 
        Pattern.compile("(?:https?://)?(?:www\\.)?linkedin\\.com/in/[\\w-]+/?", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern GITHUB_PATTERN = 
        Pattern.compile("(?:https?://)?(?:www\\.)?github\\.com/[\\w-]+/?", Pattern.CASE_INSENSITIVE);

    private static final List<String> TECHNICAL_SKILLS = Arrays.asList(
        "Java", "Python", "JavaScript", "TypeScript", "C++", "C#", "SQL", "HTML", "CSS",
        "React", "Angular", "Vue", "Spring", "Django", "Flask", "Node.js", "Express",
        "MongoDB", "PostgreSQL", "MySQL", "Redis", "Docker", "Kubernetes", "AWS", "Azure",
        "Git", "Jenkins", "Maven", "Gradle", "JUnit", "Selenium", "REST", "GraphQL",
        "Microservices", "Agile", "Scrum", "Machine Learning", "AI", "Data Science"
    );

    public void processResume(Resume resume) {
        String text = resume.getOriginalText();
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        extractPersonalInfo(resume, text);
        
        extractSkills(resume, text);
        
        calculateExperience(resume, text);
        
        resume.setProcessedText(cleanText(text));
    }

    private void extractPersonalInfo(Resume resume, String text) {
        Matcher emailMatcher = EMAIL_PATTERN.matcher(text);
        if (emailMatcher.find()) {
            resume.setEmail(emailMatcher.group());
        }

        Matcher phoneMatcher = PHONE_PATTERN.matcher(text);
        if (phoneMatcher.find()) {
            resume.setPhoneNumber(phoneMatcher.group());
        }

        Matcher linkedinMatcher = LINKEDIN_PATTERN.matcher(text);
        if (linkedinMatcher.find()) {
            resume.setLinkedinUrl(linkedinMatcher.group());
        }

        Matcher githubMatcher = GITHUB_PATTERN.matcher(text);
        if (githubMatcher.find()) {
            resume.setGithubUrl(githubMatcher.group());
        }

        extractName(resume, text);
    }

    private void extractName(Resume resume, String text) {
        String[] lines = text.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (!line.isEmpty() && 
                !line.contains("@") && 
                !line.matches(".*\\d{3}.*") && 
                line.length() > 2 && line.length() < 50) {
                if (line.split("\\s+").length >= 2) {
                    resume.setFullName(line);
                    break;
                }
            }
        }
    }

    private void extractSkills(Resume resume, String text) {
        StringBuilder skillsJson = new StringBuilder("[");
        boolean first = true;
        
        for (String skill : TECHNICAL_SKILLS) {
            if (text.toLowerCase().contains(skill.toLowerCase())) {
                if (!first) {
                    skillsJson.append(",");
                }
                skillsJson.append(String.format(
                    "{\"name\":\"%s\",\"category\":\"Technical\",\"confidence\":0.8}", 
                    skill
                ));
                first = false;
            }
        }
        skillsJson.append("]");
        resume.setSkillsJson(skillsJson.toString());
    }

    private void calculateExperience(Resume resume, String text) {
        int currentYear = java.time.Year.now().getValue();
        int experienceYears = 0;
        
        Pattern yearPattern = Pattern.compile("\\b(19|20)\\d{2}\\b");
        Matcher yearMatcher = yearPattern.matcher(text);
        
        int earliestYear = currentYear;
        while (yearMatcher.find()) {
            int year = Integer.parseInt(yearMatcher.group());
            if (year >= 1990 && year <= currentYear) {
                earliestYear = Math.min(earliestYear, year);
            }
        }
        
        if (earliestYear < currentYear) {
            experienceYears = currentYear - earliestYear;
        }
        
        resume.setTotalExperienceYears(experienceYears);
        
        if (experienceYears < 2) {
            resume.setSeniority("Junior");
        } else if (experienceYears < 5) {
            resume.setSeniority("Mid");
        } else {
            resume.setSeniority("Senior");
        }
    }

    private String cleanText(String text) {
        return text.replaceAll("\\s+", " ").trim();
    }

    public Double calculateSkillMatch(Resume resume, List<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return 0.0;
        }

        String resumeText = resume.getOriginalText().toLowerCase();
        int matchCount = 0;

        for (String skill : requiredSkills) {
            if (resumeText.contains(skill.toLowerCase())) {
                matchCount++;
            }
        }

        return (double) matchCount / requiredSkills.size();
    }
}
