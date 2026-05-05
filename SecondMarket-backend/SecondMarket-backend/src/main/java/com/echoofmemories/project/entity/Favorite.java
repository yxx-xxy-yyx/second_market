package com.echoofmemories.project.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏实体类
 * 
 * @author EchoOfMemories
 */
@Data
@TableName("favorite")
public class Favorite implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long productId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private Integer deleted;

    @TableField(exist = false)
    private String productTitle;

    @TableField(exist = false)
    private String productImages;

    @TableField(exist = false)
    private java.math.BigDecimal productPrice;

    @TableField(exist = false)
    private Integer productStatus;

    @TableField(exist = false)
    private java.math.BigDecimal productOriginalPrice;

    @TableField(exist = false)
    private Integer productConditionScore;

    @TableField(exist = false)
    private Integer productViewCount;

    @TableField(exist = false)
    private Integer productFavoriteCount;

    @TableField(exist = false)
    private Integer productAiAnalyzed;

    @TableField(exist = false)
    private String productCategory;

    private static final long serialVersionUID = 1L;
}

