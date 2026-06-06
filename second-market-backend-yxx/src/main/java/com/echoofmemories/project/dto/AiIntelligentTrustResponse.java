package com.echoofmemories.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiIntelligentTrustResponse {
    private Boolean enabled;
    private Integer autoReplyCount;
    private Integer negotiationCount;
    private Integer priceAdjustmentCount;
    private String status;
    private String lastActivity;
}
