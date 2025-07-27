package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.ATSAnalysisRequest;
import com.resumeanalyzer.dto.ATSAnalysisResponse;
import com.resumeanalyzer.model.Resume;

public interface ATSService {
    ATSAnalysisResponse analyzeATS(Resume resume, ATSAnalysisRequest request);
    Double calculateATSScore(Resume resume, ATSAnalysisRequest request);
}
