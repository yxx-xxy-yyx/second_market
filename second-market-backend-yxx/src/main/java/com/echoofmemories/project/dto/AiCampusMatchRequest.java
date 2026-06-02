package com.echoofmemories.project.dto;

import lombok.Data;

@Data
public class AiCampusMatchRequest {
    private Long productId;
    private Boolean schoolOnly = true;
    private Integer distanceLimit = 5;
    private Boolean majorRelevant = true;
}
