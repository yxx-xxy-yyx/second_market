package com.echoofmemories.project.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 创建订单请求参数
 * 
 * @author EchoOfMemories
 */
@Data
public class OrderCreateRequest {
    
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    private String remark;
}

