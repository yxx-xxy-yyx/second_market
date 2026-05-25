package com.echoofmemories.project.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 举报请求DTO
 * 
 * @author EchoOfMemories
 */
@Data
public class ReportRequest {
    
    @NotNull(message = "举报类型不能为空")
    private Integer targetType;
    
    @NotNull(message = "举报对象不能为空")
    private Long targetId;
    
    @NotBlank(message = "举报原因不能为空")
    private String reason;
}

