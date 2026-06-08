package com.echoofmemories.project.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用响应码枚举
 * 
 * @author Echo of Memories
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS("200", "操作成功"),

    BAD_REQUEST("400", "请求参数错误"),
    UNAUTHORIZED("401", "未授权，请先登录"),
    FORBIDDEN("403", "无权限访问"),
    NOT_FOUND("404", "请求资源不存在"),
    METHOD_NOT_ALLOWED("405", "请求方法不允许"),

    INTERNAL_ERROR("500", "系统内部错误"),
    SERVICE_UNAVAILABLE("503", "服务暂不可用"),

    USER_NOT_FOUND("1001", "用户不存在"),
    USER_ALREADY_EXISTS("1002", "用户已存在"),
    USERNAME_OR_PASSWORD_ERROR("1003", "用户名或密码错误"),
    USER_ACCOUNT_DISABLED("1004", "用户账号已禁用"),

    PRODUCT_NOT_FOUND("2001", "商品不存在"),
    PRODUCT_ALREADY_OFFLINE("2002", "商品已下架"),
    PRODUCT_STATUS_ERROR("2003", "商品状态异常"),

    ORDER_NOT_FOUND("3001", "订单不存在"),
    ORDER_STATUS_ERROR("3002", "订单状态异常"),
    ORDER_CANNOT_CANCEL("3003", "订单不可取消"),

    FILE_UPLOAD_ERROR("4001", "文件上传失败"),
    FILE_TYPE_ERROR("4002", "文件类型不支持"),
    FILE_SIZE_EXCEEDED("4003", "文件大小超出限制"),

    AI_SERVICE_ERROR("5001", "AI服务异常"),
    AI_RATE_LIMIT("5002", "AI调用频率过高，请稍后重试"),

    DATABASE_ERROR("6001", "数据库操作异常"),
    NETWORK_ERROR("6002", "网络请求异常"),

    VALIDATION_ERROR("7001", "参数验证失败"),
    DUPLICATE_SUBMIT("7002", "请勿重复提交");

    private final String code;
    private final String message;
}

