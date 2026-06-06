package com.echoofmemories.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiMarketTrendResponse {
    private Double averagePrice;
    private Double lowestPrice;
    private Double highestPrice;
    private Integer totalSold;
    private List<Map<String, Object>> priceTrend;
    private List<Map<String, Object>> similarProducts;
    private String recommendation;
    private String priceSuggestion;
    private String marketOutlook;
}
