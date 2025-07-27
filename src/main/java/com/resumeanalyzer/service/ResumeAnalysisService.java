package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.ATSAnalysisRequest;
import com.resumeanalyzer.dto.ATSAnalysisResponse;
import com.resumeanalyzer.dto.ResumeAnalysisResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ResumeAnalysisService {
    
    ResumeAnalysisResponse analyzeResume(MultipartFile file) throws Exception;
    
    ResumeAnalysisResponse analyzeResume(MultipartFile file, ATSAnalysisRequest atsRequest) throws Exception;
    
    ResumeAnalysisResponse getResumeAnalysis(Long id) throws Exception;
    
    List<ResumeAnalysisResponse> getAllResumes();
    
    Double calculateSkillMatch(Long resumeId, List<String> requiredSkills) throws Exception;
    
    ATSAnalysisResponse performATSAnalysis(Long resumeId, ATSAnalysisRequest request) throws Exception;
    
    void deleteResume(Long id) throws Exception;
}
