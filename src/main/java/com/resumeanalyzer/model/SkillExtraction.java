package com.resumeanalyzer.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "skill_extractions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillExtraction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String skillName;
    
    @Column(nullable = false)
    private String category;

    private Double confidence;
    private Integer mentionCount;
    private String context;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;
}
