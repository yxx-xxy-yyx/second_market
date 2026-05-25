package com.echoofmemories.project.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 热门商品DTO
 * 
 * @author EchoOfMemories
 */
@Data
public class HotProductDTO {
    
    private Long id;
    
    private String title;
    
    private String category;
    
    private BigDecimal price;
    
    private Integer viewCount;
    
    private String images;
}

