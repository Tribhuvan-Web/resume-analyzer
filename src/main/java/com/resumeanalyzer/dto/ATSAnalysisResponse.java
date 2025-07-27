package com.resumeanalyzer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ATSAnalysisResponse {
    private Double atsScore;
    private List<String> matchingSkills;
    private List<String> missingSkills;
    private List<String> keywordMatches;
    private List<String> missingKeywords;
    private List<ATSRecommendation> recommendations;
    private String overallFeedback;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ATSRecommendation {
        private String category;
        private String issue;
        private String suggestion;
        private String priority; // HIGH, MEDIUM, LOW
    }
}
