package com.echoofmemories.project.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 商品AI分析请求DTO
 * 
 * @author EchoOfMemories
 */
@Data
public class ProductAnalysisRequest {
    
    @NotEmpty(message = "图片URL不能为空")
    private List<String> imageUrls;
    
    private String additionalInfo;

    private String language; // zh / en / ko
}

