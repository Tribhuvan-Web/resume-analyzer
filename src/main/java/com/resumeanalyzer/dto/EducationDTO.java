package com.resumeanalyzer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationDTO {
    private String degree;
    private String institution;
    private String fieldOfStudy;
    private String startDate;
    private String endDate;
    private String grade;
    private String location;
    private String activities;
    private String achievements;
}
