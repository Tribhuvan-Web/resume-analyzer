package com.resumeanalyzer.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "resumes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resume {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String fileType;
    
    @Column(columnDefinition = "LONGTEXT")
    private String originalText;
    
    @Column(columnDefinition = "LONGTEXT")
    private String processedText;
    
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String linkedinUrl;
    private String githubUrl;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(columnDefinition = "JSON")
    private String experienceJson;
    
    @Column(columnDefinition = "JSON")
    private String educationJson;
    
    @Column(columnDefinition = "JSON")
    private String skillsJson;
    
    private Integer totalExperienceYears;
    private String seniority;
    private Double skillMatchScore;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SkillExtraction> skillExtractions;
    
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExperienceExtraction> experienceExtractions;
    
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EducationExtraction> educationExtractions;
}
