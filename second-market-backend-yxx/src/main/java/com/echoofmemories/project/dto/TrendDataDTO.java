package com.echoofmemories.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 趋势数据DTO
 * 
 * @author EchoOfMemories
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendDataDTO {
    
    private String date;
    
    private Long count;
}

