package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.ResumeAnalysisResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ResumeAnalysisService {
    
    ResumeAnalysisResponse analyzeResume(MultipartFile file) throws Exception;
    
    ResumeAnalysisResponse getResumeAnalysis(Long id) throws Exception;
    
    List<ResumeAnalysisResponse> getAllResumes();
    
    Double calculateSkillMatch(Long resumeId, List<String> requiredSkills) throws Exception;
    
    void deleteResume(Long id) throws Exception;
}
