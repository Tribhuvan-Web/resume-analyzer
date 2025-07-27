package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.ATSAnalysisRequest;
import com.resumeanalyzer.dto.ATSAnalysisResponse;
import com.resumeanalyzer.dto.ResumeAnalysisResponse;
import com.resumeanalyzer.service.ResumeAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "*")
public class ResumeController {

    @Autowired
    private ResumeAnalysisService resumeAnalysisService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAndAnalyzeResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "jobDescription", required = false) String jobDescription,
            @RequestParam(value = "jobTitle", required = false) String jobTitle,
            @RequestParam(value = "companyName", required = false) String companyName) {
        try {
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No file uploaded");
                error.put("message", "Please select a file to upload");
                return ResponseEntity.badRequest().body(error);
            }
            
            ATSAnalysisRequest atsRequest = null;
            if (jobDescription != null && !jobDescription.trim().isEmpty()) {
                atsRequest = new ATSAnalysisRequest(jobDescription, jobTitle, companyName);
            }
            
            ResumeAnalysisResponse response = resumeAnalysisService.analyzeResume(file, atsRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "File processing failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/ats-analysis")
    public ResponseEntity<?> performATSAnalysis(
            @PathVariable Long id,
            @RequestBody ATSAnalysisRequest request) {
        try {
            ATSAnalysisResponse response = resumeAnalysisService.performATSAnalysis(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "ATS analysis failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

     @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<ResumeAnalysisResponse> getResumeAnalysis(@PathVariable Long id) {
        try {
            ResumeAnalysisResponse response = resumeAnalysisService.getResumeAnalysis(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/match")
    public ResponseEntity<Double> matchResumeWithJob(
            @RequestParam Long resumeId,
            @RequestBody List<String> requiredSkills) {
        try {
            Double matchScore = resumeAnalysisService.calculateSkillMatch(resumeId, requiredSkills);
            return ResponseEntity.ok(matchScore);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id:[0-9]+}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id) {
        try {
            resumeAnalysisService.deleteResume(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
