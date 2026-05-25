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
 * 校园周边服务实体类
 */
@Data
@TableName("campus_service")
public class CampusService implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long schoolId;

    /**
     * 服务类型 (1-跑腿互助, 2-拼单拼车, 3-资源共享)
     */
    private Integer type;

    private String title;

    private String content;

    private String location;

    private BigDecimal reward;

    /**
     * 人数上限 (针对拼单)
     */
    private Integer limitCount;

    private Integer currentCount;

    private LocalDateTime deadline;

    /**
     * 状态 (1-待接单/招募中, 2-已接单/进行中, 3-已完成, 4-已取消)
     */
    private Integer status;

    private Long accepterId;

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
    private String accepterNickname;

    @TableField(exist = false)
    private String accepterAvatar;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
