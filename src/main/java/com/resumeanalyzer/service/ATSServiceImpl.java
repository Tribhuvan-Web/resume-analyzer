package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.ATSAnalysisRequest;
import com.resumeanalyzer.dto.ATSAnalysisResponse;
import com.resumeanalyzer.dto.ATSAnalysisResponse.ATSRecommendation;
import com.resumeanalyzer.model.Resume;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ATSServiceImpl implements ATSService {

    @Override
    public ATSAnalysisResponse analyzeATS(Resume resume, ATSAnalysisRequest request) {
        ATSAnalysisResponse response = new ATSAnalysisResponse();
        
        // Extract keywords from job description
        List<String> jobKeywords = extractKeywords(request.getJobDescription());
        List<String> jobSkills = extractSkills(request.getJobDescription());
        
        // Extract resume content
        String resumeText = resume.getOriginalText() != null ? resume.getOriginalText().toLowerCase() : "";
        List<String> resumeSkills = resume.getSkillExtractions() != null ? 
                resume.getSkillExtractions().stream()
                    .map(skill -> skill.getSkillName().toLowerCase())
                    .collect(Collectors.toList()) : 
                new ArrayList<>();
        
        // Calculate matches
        List<String> matchingSkills = findMatchingSkills(resumeSkills, jobSkills);
        List<String> missingSkills = findMissingSkills(resumeSkills, jobSkills);
        List<String> keywordMatches = findKeywordMatches(resumeText, jobKeywords);
        List<String> missingKeywords = findMissingKeywords(resumeText, jobKeywords);
        
        // Calculate ATS score
        Double atsScore = calculateATSScore(resume, request);
        
        // Generate recommendations
        List<ATSRecommendation> recommendations = generateRecommendations(
                missingSkills, missingKeywords, resume, request);
        
        // Generate overall feedback
        String overallFeedback = generateOverallFeedback(atsScore, matchingSkills.size(), 
                missingSkills.size());
        
        response.setAtsScore(atsScore);
        response.setMatchingSkills(matchingSkills);
        response.setMissingSkills(missingSkills);
        response.setKeywordMatches(keywordMatches);
        response.setMissingKeywords(missingKeywords);
        response.setRecommendations(recommendations);
        response.setOverallFeedback(overallFeedback);
        
        return response;
    }

    @Override
    public Double calculateATSScore(Resume resume, ATSAnalysisRequest request) {
        if (request.getJobDescription() == null || request.getJobDescription().trim().isEmpty()) {
            return 0.0;
        }
        
        String jobDesc = request.getJobDescription().toLowerCase();
        String resumeText = resume.getOriginalText() != null ? resume.getOriginalText().toLowerCase() : "";
        
        // Extract important keywords and skills
        List<String> jobKeywords = extractKeywords(jobDesc);
        List<String> jobSkills = extractSkills(jobDesc);
        
        List<String> resumeSkills = resume.getSkillExtractions() != null ? 
                resume.getSkillExtractions().stream()
                    .map(skill -> skill.getSkillName().toLowerCase())
                    .collect(Collectors.toList()) : 
                new ArrayList<>();
        
        // Calculate skill match percentage
        double skillMatchScore = calculateSkillMatchPercentage(resumeSkills, jobSkills);
        
        // Calculate keyword match percentage
        double keywordMatchScore = calculateKeywordMatchPercentage(resumeText, jobKeywords);
        
        // Calculate experience relevance (simplified)
        double experienceScore = calculateExperienceRelevance(resume, jobDesc);
        
        // Weighted average (skills 50%, keywords 30%, experience 20%)
        double finalScore = (skillMatchScore * 0.5) + (keywordMatchScore * 0.3) + (experienceScore * 0.2);
        
        return Math.round(finalScore * 100.0) / 100.0;
    }
    
    private List<String> extractKeywords(String text) {
        // Common important keywords in job descriptions
        String[] commonKeywords = {
            "experience", "management", "leadership", "team", "project", "development",
            "analysis", "design", "implementation", "testing", "debugging", "optimization",
            "collaboration", "communication", "problem solving", "agile", "scrum",
            "bachelor", "master", "degree", "certification", "years"
        };
        
        List<String> keywords = new ArrayList<>();
        String lowerText = text.toLowerCase();
        
        for (String keyword : commonKeywords) {
            if (lowerText.contains(keyword)) {
                keywords.add(keyword);
            }
        }
        
        return keywords;
    }
    
    private List<String> extractSkills(String text) {
        // Common technical skills to look for
        String[] techSkills = {
            "java", "python", "javascript", "react", "angular", "vue", "spring", "hibernate",
            "sql", "mysql", "postgresql", "mongodb", "redis", "docker", "kubernetes",
            "aws", "azure", "gcp", "git", "jenkins", "maven", "gradle", "junit",
            "rest", "api", "microservices", "html", "css", "bootstrap", "node.js",
            "express", "django", "flask", "laravel", "php", "c++", "c#", ".net",
            "machine learning", "ai", "data science", "pandas", "numpy", "tensorflow"
        };
        
        List<String> skills = new ArrayList<>();
        String lowerText = text.toLowerCase();
        
        for (String skill : techSkills) {
            if (lowerText.contains(skill)) {
                skills.add(skill);
            }
        }
        
        return skills;
    }
    
    private List<String> findMatchingSkills(List<String> resumeSkills, List<String> jobSkills) {
        return jobSkills.stream()
                .filter(jobSkill -> resumeSkills.stream()
                        .anyMatch(resumeSkill -> resumeSkill.contains(jobSkill) || jobSkill.contains(resumeSkill)))
                .collect(Collectors.toList());
    }
    
    private List<String> findMissingSkills(List<String> resumeSkills, List<String> jobSkills) {
        return jobSkills.stream()
                .filter(jobSkill -> resumeSkills.stream()
                        .noneMatch(resumeSkill -> resumeSkill.contains(jobSkill) || jobSkill.contains(resumeSkill)))
                .collect(Collectors.toList());
    }
    
    private List<String> findKeywordMatches(String resumeText, List<String> jobKeywords) {
        return jobKeywords.stream()
                .filter(keyword -> resumeText.contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    private List<String> findMissingKeywords(String resumeText, List<String> jobKeywords) {
        return jobKeywords.stream()
                .filter(keyword -> !resumeText.contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    private double calculateSkillMatchPercentage(List<String> resumeSkills, List<String> jobSkills) {
        if (jobSkills.isEmpty()) return 100.0;
        
        long matchingSkills = jobSkills.stream()
                .mapToLong(jobSkill -> resumeSkills.stream()
                        .anyMatch(resumeSkill -> resumeSkill.contains(jobSkill) || jobSkill.contains(resumeSkill)) ? 1 : 0)
                .sum();
        
        return (double) matchingSkills / jobSkills.size() * 100.0;
    }
    
    private double calculateKeywordMatchPercentage(String resumeText, List<String> jobKeywords) {
        if (jobKeywords.isEmpty()) return 100.0;
        
        long matchingKeywords = jobKeywords.stream()
                .mapToLong(keyword -> resumeText.contains(keyword.toLowerCase()) ? 1 : 0)
                .sum();
        
        return (double) matchingKeywords / jobKeywords.size() * 100.0;
    }
    
    private double calculateExperienceRelevance(Resume resume, String jobDescription) {
        // Simple relevance calculation based on experience years and job requirements
        if (resume.getExperienceExtractions() == null || resume.getExperienceExtractions().isEmpty()) return 50.0;
        
        // Calculate total experience in years
        int totalExperienceMonths = resume.getExperienceExtractions().stream()
                .mapToInt(exp -> exp.getDurationMonths() != null ? exp.getDurationMonths() : 0)
                .sum();
        
        int totalExperienceYears = totalExperienceMonths / 12;
        
        // Look for experience requirements in job description
        String jobDesc = jobDescription.toLowerCase();
        if (jobDesc.contains("entry level") || jobDesc.contains("0-2 years")) {
            return totalExperienceYears >= 0 ? 100.0 : 50.0;
        } else if (jobDesc.contains("mid level") || jobDesc.contains("3-5 years")) {
            return totalExperienceYears >= 3 ? 100.0 : Math.max(50.0, totalExperienceYears * 20.0);
        } else if (jobDesc.contains("senior") || jobDesc.contains("5+ years")) {
            return totalExperienceYears >= 5 ? 100.0 : Math.max(30.0, totalExperienceYears * 15.0);
        }
        
        return Math.min(100.0, totalExperienceYears * 10.0 + 50.0);
    }
    
    private List<ATSRecommendation> generateRecommendations(List<String> missingSkills, 
            List<String> missingKeywords, Resume resume, ATSAnalysisRequest request) {
        List<ATSRecommendation> recommendations = new ArrayList<>();
        
        // Skills recommendations
        if (!missingSkills.isEmpty()) {
            recommendations.add(new ATSRecommendation(
                "Skills",
                "Missing key technical skills: " + String.join(", ", missingSkills.subList(0, Math.min(5, missingSkills.size()))),
                "Consider adding these skills to your resume if you have experience with them, or highlight similar/related technologies",
                "HIGH"
            ));
        }
        
        // Keywords recommendations
        if (!missingKeywords.isEmpty()) {
            recommendations.add(new ATSRecommendation(
                "Keywords",
                "Missing important keywords from job description",
                "Incorporate relevant keywords naturally throughout your resume, especially in summary and experience sections",
                "MEDIUM"
            ));
        }
        
        // Experience recommendations
        if (resume.getExperienceExtractions() == null || resume.getExperienceExtractions().isEmpty()) {
            recommendations.add(new ATSRecommendation(
                "Experience",
                "No work experience detected",
                "Add internships, projects, or volunteer work that demonstrates relevant skills",
                "HIGH"
            ));
        }
        
        // Education recommendations
        if (resume.getEducationExtractions() == null || resume.getEducationExtractions().isEmpty()) {
            recommendations.add(new ATSRecommendation(
                "Education",
                "Education information not clearly formatted",
                "Ensure education section is clearly labeled with degree, institution, and graduation year",
                "MEDIUM"
            ));
        }
        
        // Format recommendations
        recommendations.add(new ATSRecommendation(
            "Format",
            "Optimize for ATS scanning",
            "Use standard section headings (Summary, Experience, Education, Skills), avoid complex formatting, tables, or graphics",
            "MEDIUM"
        ));
        
        return recommendations;
    }
    
    private String generateOverallFeedback(Double atsScore, int matchingSkills, int missingSkills) {
        StringBuilder feedback = new StringBuilder();
        
        if (atsScore >= 80) {
            feedback.append("Excellent ATS compatibility! Your resume aligns well with the job requirements. ");
        } else if (atsScore >= 60) {
            feedback.append("Good ATS score with room for improvement. ");
        } else if (atsScore >= 40) {
            feedback.append("Moderate ATS compatibility. Consider significant improvements. ");
        } else {
            feedback.append("Low ATS score. Major improvements needed for better job matching. ");
        }
        
        feedback.append(String.format("You match %d key requirements", matchingSkills));
        if (missingSkills > 0) {
            feedback.append(String.format(" and could strengthen your profile by adding %d missing skills.", missingSkills));
        } else {
            feedback.append(".");
        }
        
        return feedback.toString();
    }
}
