package com.resumeanalyzer.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "experience_extractions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceExtraction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String jobTitle;
    
    @Column(nullable = false)
    private String company;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private Integer durationMonths;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String responsibilities;
    
    private String location;
    private String employmentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;
}
