package com.echoofmemories.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiCampusServiceResponse {
    private String serviceType;
    private List<Map<String, Object>> matches;
    private String suggestion;
    private List<String> tips;
    private Double priceEstimate;
    private String priceCurrency;
}
