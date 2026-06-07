package com.echoofmemories.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户搜索历史
 */
@Data
@TableName("search_history")
public class SearchHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String keyword;

    /**
     * 删除状态：0-未删除，1-已清空
     */
    private Integer deleted;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
