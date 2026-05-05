package com.echoofmemories.project.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评价实体类
 * 
 * @author EchoOfMemories
 */
@Data
@TableName("review")
public class Review implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    private Long reviewerId;

    private Long revieweeId;

    private Long productId;

    @Min(value = 1, message = "评分最低1分")
    @Max(value = 5, message = "评分最高5分")
    @NotNull(message = "评分不能为空")
    private Integer rating;

    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private Integer deleted;

    @TableField(exist = false)
    private String reviewerNickname;

    @TableField(exist = false)
    private String reviewerAvatar;

    @TableField(exist = false)
    private String productTitle;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

