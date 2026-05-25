package com.echoofmemories.project.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品AI分析结果DTO
 * 
 * @author EchoOfMemories
 */
@Data
public class ProductAnalysisDTO {
    
    private String productName;
    
    private String brand;
    
    private String model;
    
    private Integer conditionScore;
    
    private String conditionDescription;
    
    private BigDecimal minPrice;
    
    private BigDecimal maxPrice;
    
    private String description;
    
    private String category;
}

