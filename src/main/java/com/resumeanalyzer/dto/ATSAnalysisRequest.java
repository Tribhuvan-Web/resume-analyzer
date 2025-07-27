package com.resumeanalyzer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ATSAnalysisRequest {
    private String jobDescription;
    private String jobTitle;
    private String companyName;
}
