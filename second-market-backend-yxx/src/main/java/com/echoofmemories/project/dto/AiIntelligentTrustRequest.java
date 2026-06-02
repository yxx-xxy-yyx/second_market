package com.echoofmemories.project.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AiIntelligentTrustRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    private Boolean enableAutoReply = true;
    private Boolean enableAutoNegotiate = true;
    private Boolean enableDynamicPricing = true;
    private Double minPrice;
    private Double maxPrice;
    private String autoReplyTemplate;
}
