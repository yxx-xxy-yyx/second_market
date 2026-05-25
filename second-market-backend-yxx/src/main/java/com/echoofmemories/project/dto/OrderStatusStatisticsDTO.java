package com.echoofmemories.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单状态统计DTO
 * 
 * @author EchoOfMemories
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusStatisticsDTO {
    
    private Integer status;
    
    private String statusName;
    
    private Long count;
}

