package com.resumeanalyzer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAnalysisResponse {

    private Long id;
    private String fileName;
    private PersonalInfoDTO personalInfo;
    private List<SkillDTO> skills;
    private List<ExperienceDTO> experiences;
    private List<EducationDTO> education;
    private String summary;
    private Integer totalExperienceYears;
    private String seniority;
    private Double skillMatchScore;
}
