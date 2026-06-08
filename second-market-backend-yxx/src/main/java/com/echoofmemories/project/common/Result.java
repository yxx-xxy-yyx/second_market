package com.echoofmemories.project.common;

import lombok.Data;

/**
 * 统一返回结果类
 * 
 * @param <T> 数据类型
 * @author Echo of Memories
 */
@Data
public class Result<T> {

    private static final String SUCCESS_CODE = "200";
    private static final String ERROR_CODE = "500";
    private static final String SUCCESS_MSG = "操作成功";
    private static final String ERROR_MSG = "操作失败";

    private String code;
    private String message;
    private T data;

    /**
     * 成功标识，根据code字段自动计算
     */
    public Boolean getSuccess() {
        return SUCCESS_CODE.equals(this.code);
    }

    public Result() {
    }

    public Result(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     */
    public static <T> Result<T> success() {
        return new Result<>(SUCCESS_CODE, SUCCESS_MSG, null);
    }

    /**
     * 成功返回结果
     * 
     * @param data 返回数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, SUCCESS_MSG, data);
    }

    /**
     * 成功返回结果
     * 
     * @param message 返回消息
     * @param data    返回数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(SUCCESS_CODE, message, data);
    }

    /**
     * 失败返回结果
     */
    public static <T> Result<T> error() {
        return new Result<>(ERROR_CODE, ERROR_MSG, null);
    }

    /**
     * 失败返回结果
     * 
     * @param message 错误消息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ERROR_CODE, message, null);
    }

    /**
     * 失败返回结果
     * 
     * @param code    错误码
     * @param message 错误消息
     */
    public static <T> Result<T> error(String code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败返回结果（通过ResultCode枚举）
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return SUCCESS_CODE.equals(this.code);
    }
}