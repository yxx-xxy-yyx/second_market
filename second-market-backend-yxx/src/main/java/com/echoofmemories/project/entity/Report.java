package com.echoofmemories.project.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 举报实体类
 * 
 * @author EchoOfMemories
 */
@Data
@TableName("report")
public class Report implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long reporterId;

    @NotNull(message = "举报类型不能为空")
    private Integer targetType;

    @NotNull(message = "举报对象不能为空")
    private Long targetId;

    @NotBlank(message = "举报原因不能为空")
    private String reason;

    private Integer status;

    private String handleResult;

    private LocalDateTime handleTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private Integer deleted;

    @TableField(exist = false)
    private String reporterNickname;

    @TableField(exist = false)
    private String reporterAvatar;

    @TableField(exist = false)
    private String targetName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

