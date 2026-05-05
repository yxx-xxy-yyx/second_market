package com.echoofmemories.project.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 平台统计概览DTO
 * 
 * @author EchoOfMemories
 */
@Data
public class StatisticsOverviewDTO {
    
    private Long totalUsers;
    
    private Long totalProducts;
    
    private Long totalOrders;
    
    private BigDecimal totalAmount;
}

