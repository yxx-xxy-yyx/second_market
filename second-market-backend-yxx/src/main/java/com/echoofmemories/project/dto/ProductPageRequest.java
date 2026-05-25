package com.echoofmemories.project.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品分页查询请求参数
 * 
 * @author EchoOfMemories
 */
@Data
public class ProductPageRequest {
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 10;
    
    private String keyword;
    
    private String category;
    
    private BigDecimal minPrice;
    
    private BigDecimal maxPrice;
    
    private Integer minCondition;
    
    private Integer maxCondition;
    
    private String sortBy;
    
    private Integer status;
    
    private Long userId;

    private Long schoolId;
}
