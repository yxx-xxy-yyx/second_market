package com.echoofmemories.project.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("school")
public class School implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String schoolCode;

    private String nameZh;

    private String nameKo;

    private String nameEn;

    private String cityZh;

    private String cityKo;

    private String cityEn;

    private String logoUrl;

    private Integer status;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Integer deleted;

    private static final long serialVersionUID = 1L;
}
