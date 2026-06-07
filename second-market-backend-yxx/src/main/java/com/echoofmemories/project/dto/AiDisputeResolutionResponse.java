package com.echoofmemories.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiDisputeResolutionResponse {
    private String caseId;
    private String suggestedResolution;
    private Double refundSuggestion;
    private List<String> keyFindings;
    private String riskLevel;
    private String nextSteps;
    private Integer confidenceScore;
}
