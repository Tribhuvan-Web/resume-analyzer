package com.resumeanalyzer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceDTO {
    private String jobTitle;
    private String company;
    private String startDate;
    private String endDate;
    private Boolean isCurrent;
    private Integer durationMonths;
    private String description;
    private String responsibilities;
    private String location;
    private String employmentType;
}
