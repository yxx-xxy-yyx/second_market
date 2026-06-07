package com.echoofmemories.project.dto;

import lombok.Data;

@Data
public class AiSmartSearchRequest {
    private String query;
    private String voiceQuery;
    private String category;
    private Double minPrice;
    private Double maxPrice;
    private Integer conditionMin;
    private Integer conditionMax;
    private Boolean schoolPriority = true;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
