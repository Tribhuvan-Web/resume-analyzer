package com.resumeanalyzer.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfoDTO {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String linkedinUrl;
    private String githubUrl;
}
