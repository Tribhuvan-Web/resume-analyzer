package com.resumeanalyzer.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "education_extractions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationExtraction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String degree;
    
    @Column(nullable = false)
    private String institution;
    
    private String fieldOfStudy;
    private LocalDate startDate;
    private LocalDate endDate;
    private String grade;
    private String location;
    
    @Column(columnDefinition = "TEXT")
    private String activities;
    
    @Column(columnDefinition = "TEXT")
    private String achievements;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;
}
