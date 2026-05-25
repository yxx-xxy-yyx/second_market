package com.echoofmemories.project.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 
 * @author EchoOfMemories
 */
@Data
@TableName("orders")
public class Orders implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long buyerId;

    private Long sellerId;

    private Long productId;

    private BigDecimal amount;

    private Integer status;

    private String remark;

    private LocalDateTime payTime;

    private LocalDateTime finishTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Integer deleted;

    @TableField(exist = false)
    private String buyerNickname;

    @TableField(exist = false)
    private String buyerAvatar;

    @TableField(exist = false)
    private String sellerNickname;

    @TableField(exist = false)
    private String sellerAvatar;

    @TableField(exist = false)
    private String productTitle;

    @TableField(exist = false)
    private String productImages;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

