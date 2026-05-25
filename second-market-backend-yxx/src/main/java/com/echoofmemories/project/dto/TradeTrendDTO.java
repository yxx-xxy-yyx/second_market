package com.echoofmemories.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 交易趋势DTO
 * 
 * @author EchoOfMemories
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeTrendDTO {
    
    private String date;
    
    private Long orderCount;
    
    private BigDecimal totalAmount;
}

