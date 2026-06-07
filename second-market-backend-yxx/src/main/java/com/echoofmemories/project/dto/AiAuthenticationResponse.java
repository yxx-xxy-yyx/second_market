package com.echoofmemories.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiAuthenticationResponse {
    private Boolean isAuthentic;
    private Double authenticityScore;
    private String conditionGrade;
    private Double conditionScore;
    private List<String> conditionDetails;
    private String reportSummary;
    private List<String> recommendations;
    private LocalDateTime reportTime;
    private String reportId;
}
