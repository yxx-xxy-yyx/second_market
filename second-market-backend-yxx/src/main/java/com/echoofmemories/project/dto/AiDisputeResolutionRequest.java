package com.echoofmemories.project.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AiDisputeResolutionRequest {
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    private String disputeDescription;
    private List<String> evidenceUrls;
}
