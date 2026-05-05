package com.echoofmemories.project.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 商品请求参数
 * 
 * @author EchoOfMemories
 */
@Data
public class ProductRequest {
    
    private Long id;
    
    @NotBlank(message = "商品标题不能为空")
    private String title;
    
    private String description;
    
    @NotBlank(message = "商品分类不能为空")
    private String category;

    private Long schoolId;
    
    @NotNull(message = "商品价格不能为空")
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private Integer conditionScore;
    
    private String conditionDesc;
    
    private String images;
    
    private Integer status;
    
    private Integer aiAnalyzed;
    
    private String aiSuggestions;
}

