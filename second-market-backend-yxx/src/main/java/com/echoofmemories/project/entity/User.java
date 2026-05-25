package com.echoofmemories.project.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * @author Echo of Memories
 */
@Data
@TableName("sys_user")
public class User implements Serializable {

    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 用户昵称
     */
    @NotBlank(message = "用户昵称不能为空")
    private String nickname;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 地址
     */
    private String address;

    /**
     * 性别 (M-男, F-女, U-未知)
     */
    private String gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 用户角色 (admin-管理员, user-普通用户)
     */
    private String role;

    /**
     * 账户状态 (1-启用, 0-禁用)
     */
    private Integer status;

    /**
     * 信用评分
     */
    private Integer creditScore;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除 (0-未删除, 1-已删除)
     */
    private Integer deleted;

    // 临时字段，不映射到数据库
    @TableField(exist = false)
    private String token;

    @TableField(exist = false)
    private String verifyCode;

    @TableField(exist = false)
    private String newPassword;

    @TableField(exist = false)
    private String confirmPassword;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    private Long schoolId;
}