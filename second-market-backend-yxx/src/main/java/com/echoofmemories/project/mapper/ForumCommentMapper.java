package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.echoofmemories.project.entity.ForumComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 论坛评论Mapper接口
 */
@Mapper
public interface ForumCommentMapper extends BaseMapper<ForumComment> {

    /**
     * 查询帖子评论列表
     */
    List<ForumComment> selectByPostId(@Param("postId") Long postId);
}
