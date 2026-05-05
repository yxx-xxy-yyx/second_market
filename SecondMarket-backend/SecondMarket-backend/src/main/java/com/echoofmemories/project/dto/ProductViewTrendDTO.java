package com.echoofmemories.project.dto;

import lombok.Data;

/**
 * 商品浏览趋势DTO
 * 
 * @author EchoOfMemories
 */
@Data
public class ProductViewTrendDTO {
    
    private Long productId;
    
    private String productTitle;
    
    private Integer viewCount;
}

