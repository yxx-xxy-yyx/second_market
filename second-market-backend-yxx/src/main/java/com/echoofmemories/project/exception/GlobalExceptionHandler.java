package com.echoofmemories.project.exception;

import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
<<<<<<< HEAD

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 
 * @author Echo of Memories
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Object> handleValidationException(MethodArgumentNotValidException e) {
        log.error("参数验证异常: {}", e.getMessage());

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

<<<<<<< HEAD
        // 返回第一个验证错误信息
        String firstError = errors.values().iterator().next();
        return Result.error("400", "参数验证失败：" + firstError);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error("请求体解析失败: {}", e.getMessage());
        return Result.error("400", "请求体格式错误");
    }

    /**
     * 处理请求体解析异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Object> handleHttpMessageNotRead(HttpMessageNotReadableException e) {
        log.error("请求体解析失败", e);
        return Result.error(ResultCode.BAD_REQUEST);
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<Object> handleNotFound(NoHandlerFoundException e) {
        log.error("请求资源不存在: {}", e.getRequestURL());
        return Result.error(ResultCode.NOT_FOUND);
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Object> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.error("请求方法不支持: {}", e.getMethod());
        return Result.error(ResultCode.METHOD_NOT_ALLOWED);
    }


    /**
     * 处理自定义异常
     */
    @ExceptionHandler(CustomException.class)
    public Result<Object> handleCustomException(CustomException e) {
        log.error("自定义异常: {}", e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Object> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return Result.error("运行时异常: " + e.getMessage());
    }

    /**
     * 处理所有异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.error("系统异常，请联系管理员");
    }

}
