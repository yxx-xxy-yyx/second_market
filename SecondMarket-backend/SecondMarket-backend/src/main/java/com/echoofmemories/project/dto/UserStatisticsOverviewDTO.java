package com.echoofmemories.project.dto;

import lombok.Data;

/**
 * 用户统计概览DTO
 * 
 * @author EchoOfMemories
 */
@Data
public class UserStatisticsOverviewDTO {
    
    private Long productCount;
    
    private Long soldCount;
    
    private Long buyCount;
    
    private Long favoriteCount;
    
    private Double creditScore;
    
    private Long reviewCount;
}

