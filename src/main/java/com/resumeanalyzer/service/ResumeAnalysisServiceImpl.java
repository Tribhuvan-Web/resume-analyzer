package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.ATSAnalysisRequest;
import com.resumeanalyzer.dto.ATSAnalysisResponse;
import com.resumeanalyzer.dto.ResumeAnalysisResponse;
import com.resumeanalyzer.dto.PersonalInfoDTO;
import com.resumeanalyzer.model.Resume;
import com.resumeanalyzer.repository.ResumeRepository;
import com.resumeanalyzer.nlp.NLPProcessor;
import com.resumeanalyzer.utils.FileProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeAnalysisServiceImpl implements ResumeAnalysisService {

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private NLPProcessor nlpProcessor;

    @Autowired
    private FileProcessor fileProcessor;

    @Autowired
    private ATSService atsService;

    @Override
    public ResumeAnalysisResponse analyzeResume(MultipartFile file) throws Exception {
        return analyzeResume(file, null);
    }

    @Override
    public ResumeAnalysisResponse analyzeResume(MultipartFile file, ATSAnalysisRequest atsRequest) throws Exception {
        String extractedText = fileProcessor.extractText(file);
        
        // Create Resume entity
        Resume resume = new Resume();
        resume.setFileName(file.getOriginalFilename());
        resume.setFileType(file.getContentType());
        resume.setOriginalText(extractedText);
        resume.setCreatedAt(LocalDateTime.now());
        
        // Process with NLP
        nlpProcessor.processResume(resume);
        
        // Save to database
        resume = resumeRepository.save(resume);
        
        // Convert to response DTO
        ResumeAnalysisResponse response = convertToResponse(resume);
        
        // Perform ATS analysis if job description provided
        if (atsRequest != null && atsRequest.getJobDescription() != null && !atsRequest.getJobDescription().trim().isEmpty()) {
            ATSAnalysisResponse atsAnalysis = atsService.analyzeATS(resume, atsRequest);
            response.setAtsAnalysis(atsAnalysis);
        }
        
        return response;
    }

    @Override
    public ResumeAnalysisResponse getResumeAnalysis(Long id) throws Exception {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new Exception("Resume not found"));
        return convertToResponse(resume);
    }

    @Override
    public List<ResumeAnalysisResponse> getAllResumes() {
        return resumeRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Double calculateSkillMatch(Long resumeId, List<String> requiredSkills) throws Exception {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new Exception("Resume not found"));
        
        return nlpProcessor.calculateSkillMatch(resume, requiredSkills);
    }

    @Override
    public ATSAnalysisResponse performATSAnalysis(Long resumeId, ATSAnalysisRequest request) throws Exception {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new Exception("Resume not found"));
        
        return atsService.analyzeATS(resume, request);
    }

    @Override
    public void deleteResume(Long id) throws Exception {
        if (!resumeRepository.existsById(id)) {
            throw new Exception("Resume not found");
        }
        resumeRepository.deleteById(id);
    }

    private ResumeAnalysisResponse convertToResponse(Resume resume) {
        ResumeAnalysisResponse response = new ResumeAnalysisResponse();
        response.setId(resume.getId());
        response.setFileName(resume.getFileName());
        response.setSummary(resume.getSummary());
        response.setTotalExperienceYears(resume.getTotalExperienceYears());
        response.setSeniority(resume.getSeniority());
        response.setSkillMatchScore(resume.getSkillMatchScore());
        
        PersonalInfoDTO personalInfo = new PersonalInfoDTO();
        personalInfo.setFullName(resume.getFullName());
        personalInfo.setEmail(resume.getEmail());
        personalInfo.setPhoneNumber(resume.getPhoneNumber());
        personalInfo.setAddress(resume.getAddress());
        personalInfo.setLinkedinUrl(resume.getLinkedinUrl());
        personalInfo.setGithubUrl(resume.getGithubUrl());
        response.setPersonalInfo(personalInfo);
        
        
        return response;
    }
}
