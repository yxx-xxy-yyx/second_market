package com.echoofmemories.project.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 举报处理请求DTO
 * 
 * @author EchoOfMemories
 */
@Data
public class ReportHandleRequest {
    
    @NotNull(message = "举报ID不能为空")
    private Long id;
    
    @NotNull(message = "处理状态不能为空")
    private Integer status;
    
    private String handleResult;
}

