package com.echoofmemories.project.dto;

import lombok.Data;

@Data
public class AiMarketTrendRequest {
    private String category;
    private String brand;
    private String model;
    private String productName;
    private Integer days = 7;
}
