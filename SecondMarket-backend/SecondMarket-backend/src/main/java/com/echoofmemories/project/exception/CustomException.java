package com.echoofmemories.project.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常类
 * 
 * @author Echo of Memories
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomException extends RuntimeException {
    
    private String code;
    private String message;
    
    public CustomException(String message) {
        super(message);
        this.code = "500";
        this.message = message;
    }
    
    public CustomException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public CustomException(String message, Throwable cause) {
        super(message, cause);
        this.code = "500";
        this.message = message;
    }
    
}