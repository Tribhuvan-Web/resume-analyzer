package com.resumeanalyzer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO {
    private String name;
    private String category;
    private Double confidence;
    private Integer mentionCount;
    private String context;
}
