package com.echoofmemories.project.dto;

import lombok.Data;

/**
 * 分页请求参数
 * 
 * @author Echo of Memories
 */
@Data
public class PageRequest {
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 页面大小
     */
    private Integer pageSize = 10;
    
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 用户角色
     */
    private String role;
    
    /**
     * 用户状态
     */
    private Integer status;
    
}