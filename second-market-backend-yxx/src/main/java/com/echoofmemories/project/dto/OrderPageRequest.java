package com.echoofmemories.project.dto;

import lombok.Data;

/**
 * 订单分页查询请求参数
 * 
 * @author EchoOfMemories
 */
@Data
public class OrderPageRequest {
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 10;
    
    private Integer status;
}

