package com.echoofmemories.project.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户评分DTO
 * 
 * @author EchoOfMemories
 */
@Data
public class UserRatingDTO {
    
    private BigDecimal avgRating;
    
    private Long reviewCount;
}

