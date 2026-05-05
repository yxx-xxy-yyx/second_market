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
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 * 
 * @author EchoOfMemories
 */
@Data
@TableName("product")
public class Product implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private Long schoolId;

    @NotBlank(message = "商品标题不能为空")
    private String title;

    private String description;

    @NotBlank(message = "商品分类不能为空")
    private String category;

    @NotNull(message = "商品价格不能为空")
    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer conditionScore;

    private String conditionDesc;

    private String images;

    private Integer status;

    private Integer viewCount;

    private Integer favoriteCount;

    private Integer aiAnalyzed;

    private String aiSuggestions;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Integer deleted;

    @TableField(exist = false)
    private String userNickname;

    @TableField(exist = false)
    private String userAvatar;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

