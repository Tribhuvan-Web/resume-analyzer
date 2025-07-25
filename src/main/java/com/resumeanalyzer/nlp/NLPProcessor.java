package com.resumeanalyzer.nlp;

import com.resumeanalyzer.model.Resume;
import com.resumeanalyzer.nlp.processors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class NLPProcessor {

    @Autowired
    private TextPreProcessor textPreProcessor;

    @Autowired
    private PersonalInfoProcessor personalInfoProcessor;

    @Autowired
    private SkillsProcessor skillsProcessor;

    @Autowired
    private ExperienceProcessor experienceProcessor;

    @Autowired
    private EducationProcessor educationProcessor;

    @Autowired
    private SummaryProcessor summaryProcessor;

    @PostConstruct
    public void initializeProcessor() {
        System.out.println("🔧 Initializing lightweight NLP processor...");
        System.out.println("✅ Using OpenNLP and pattern-based processing for better performance");
        System.out.println("✅ No heavy dependencies - faster startup and processing");
    }

    public void processResume(Resume resume) {
        String text = resume.getOriginalText();
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        // 1. Clean and preprocess text
        String cleanedText = textPreProcessor.preprocessText(text);
        resume.setProcessedText(cleanedText);

        // 2. Extract personal information using enhanced pattern matching
        personalInfoProcessor.extractPersonalInfo(resume, cleanedText);
        
        // 3. Extract skills with pattern-based processing
        skillsProcessor.extractSkills(resume, cleanedText);
        
        // 4. Calculate experience with improved parsing
        experienceProcessor.calculateExperience(resume, cleanedText);
        
        // 5. Extract education information
        educationProcessor.extractEducation(resume, cleanedText);
        
        // 6. Generate summary
        summaryProcessor.generateSummary(resume, cleanedText);
    }

    public Double calculateSkillMatch(Resume resume, List<String> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return 0.0;
        }

        String resumeText = resume.getOriginalText().toLowerCase();
        double totalScore = 0.0;

        for (String skill : requiredSkills) {
            String lowerSkill = skill.toLowerCase();
            if (isSkillPresentInContext(resumeText, lowerSkill)) {
                // Use a simple confidence calculation here
                double confidence = calculateBasicSkillConfidence(resume.getOriginalText(), skill);
                totalScore += confidence;
            }
        }

        // Return weighted average
        return requiredSkills.size() > 0 ? totalScore / requiredSkills.size() : 0.0;
    }

    private boolean isSkillPresentInContext(String text, String skill) {
        // Look for skill with word boundaries and context
        Pattern skillPattern = Pattern.compile("\\b" + Pattern.quote(skill) + "\\b", Pattern.CASE_INSENSITIVE);
        return skillPattern.matcher(text).find();
    }

    private double calculateBasicSkillConfidence(String text, String skill) {
        String lowerText = text.toLowerCase();
        String lowerSkill = skill.toLowerCase();
        
        double confidence = 0.6; // Base confidence
        
        // Increase confidence based on context
        if (lowerText.contains("experience " + lowerSkill) || 
            lowerText.contains(lowerSkill + " experience")) {
            confidence += 0.2;
        }
        
        return Math.min(confidence, 1.0);
    }
}
