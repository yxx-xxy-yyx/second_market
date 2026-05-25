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
 * 论坛帖子实体类
 */
@Data
@TableName("forum_post")
public class ForumPost implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long schoolId;

    private String category;

    private String title;

    private String content;

    private String images;

    private Boolean isSchoolOnly;

    private Boolean isAnonymous;

    private Integer viewCount;

    private Integer commentCount;

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
    private String schoolName;

    @TableField(exist = false)
    private Long likeCount;

    @TableField(exist = false)
    private Integer liked;

    @TableField(exist = false)
    private Long favoriteCount;

    @TableField(exist = false)
    private Integer favorited;

    private static final long serialVersionUID = 1L;
}
