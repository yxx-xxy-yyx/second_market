package com.echoofmemories.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类统计DTO
 * 
 * @author EchoOfMemories
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatisticsDTO {
    
    private String category;
    
    private Long count;
}

