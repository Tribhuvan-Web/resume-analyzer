package com.resumeanalyzer.nlp.processors;

import com.resumeanalyzer.model.Resume;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExperienceProcessor {

    // Experience-related keywords
    private static final List<String> EXPERIENCE_KEYWORDS = Arrays.asList(
            "experience", "work", "employment", "career", "professional", "job",
            "position", "role", "worked", "employed", "served", "years");

    // Job title patterns
    private static final List<String> JOB_TITLES = Arrays.asList(
            "Developer", "Engineer", "Manager", "Analyst", "Consultant", "Architect",
            "Designer", "Specialist", "Lead", "Senior", "Junior", "Associate",
            "Director", "VP", "CTO", "CEO", "Product Manager", "Project Manager");

    public void calculateExperience(Resume resume, String text) {
        int currentYear = java.time.Year.now().getValue();
        Set<Integer> years = new HashSet<>();

        try {
            // Extract years using regex patterns
            extractYearsWithRegex(text, years, currentYear);
            
            // Calculate experience more accurately
            int experienceYears = calculateYearsFromDates(years, currentYear);
            
            // Look for explicit experience mentions
            int explicitYears = extractExplicitExperience(text);
            experienceYears = Math.max(experienceYears, explicitYears);
            
            resume.setTotalExperienceYears(experienceYears);
            setSeniorityLevel(resume, experienceYears);
            
        } catch (Exception e) {
            System.err.println("Warning: Experience extraction failed: " + e.getMessage());
            resume.setTotalExperienceYears(0);
            resume.setSeniority("Entry Level");
        }
    }

    private void extractYearsWithRegex(String text, Set<Integer> years, int currentYear) {
        Pattern yearPattern = Pattern.compile("\\b(19|20)\\d{2}\\b");
        Matcher yearMatcher = yearPattern.matcher(text);
        while (yearMatcher.find()) {
            int year = Integer.parseInt(yearMatcher.group());
            if (year >= 1990 && year <= currentYear) {
                years.add(year);
            }
        }
    }

    private int calculateYearsFromDates(Set<Integer> years, int currentYear) {
        if (years.isEmpty()) {
            return 0;
        }
        
        int earliestYear = Collections.min(years);
        int latestYear = Collections.max(years);
        
        // If we have a range of years, use that
        if (latestYear - earliestYear > 0) {
            return latestYear - earliestYear;
        } else {
            return currentYear - earliestYear;
        }
    }

    private int extractExplicitExperience(String text) {
        // Look for explicit experience mentions like "5 years experience"
        Pattern expPattern = Pattern.compile("(\\d+)\\s+years?\\s+(?:of\\s+)?(?:experience|work)", Pattern.CASE_INSENSITIVE);
        Matcher expMatcher = expPattern.matcher(text);
        if (expMatcher.find()) {
            return Integer.parseInt(expMatcher.group(1));
        }
        return 0;
    }

    private void setSeniorityLevel(Resume resume, int experienceYears) {
        if (experienceYears < 1) {
            resume.setSeniority("Entry Level");
        } else if (experienceYears < 3) {
            resume.setSeniority("Junior");
        } else if (experienceYears < 6) {
            resume.setSeniority("Mid-Level");
        } else if (experienceYears < 10) {
            resume.setSeniority("Senior");
        } else {
            resume.setSeniority("Expert/Lead");
        }
    }

    public List<String> extractJobTitles(String text) {
        List<String> foundTitles = new ArrayList<>();
        String lowerText = text.toLowerCase();
        
        for (String title : JOB_TITLES) {
            if (lowerText.contains(title.toLowerCase())) {
                foundTitles.add(title);
            }
        }
        
        return foundTitles;
    }

    public List<String> extractCompanies(String text) {
        List<String> companies = new ArrayList<>();
        
        // Look for company patterns (this is a simplified approach)
        Pattern companyPattern = Pattern.compile("(?:at|@)\\s+([A-Z][a-zA-Z\\s&,.]+?)(?:\\s|,|\\.|$)", Pattern.MULTILINE);
        Matcher matcher = companyPattern.matcher(text);
        
        while (matcher.find()) {
            String company = matcher.group(1).trim();
            if (company.length() > 2 && company.length() < 50) {
                companies.add(company);
            }
        }
        
        return companies;
    }

    public boolean hasRemoteExperience(String text) {
        String lowerText = text.toLowerCase();
        return lowerText.contains("remote") || 
               lowerText.contains("work from home") || 
               lowerText.contains("telecommute") ||
               lowerText.contains("distributed team");
    }

    public boolean hasLeadershipExperience(String text) {
        String lowerText = text.toLowerCase();
        return lowerText.contains("lead") || 
               lowerText.contains("manage") || 
               lowerText.contains("supervise") ||
               lowerText.contains("mentor") ||
               lowerText.contains("team lead") ||
               lowerText.contains("project manager");
    }

    public boolean hasExperienceSection(String text) {
        String lowerText = text.toLowerCase();
        return EXPERIENCE_KEYWORDS.stream()
                .anyMatch(keyword -> lowerText.contains(keyword));
    }
}
