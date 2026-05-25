package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.entity.ForumPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 论坛帖子Mapper接口
 */
@Mapper
public interface ForumPostMapper extends BaseMapper<ForumPost> {

    /**
     * 分页查询帖子列表
     */
    IPage<ForumPost> selectForumPage(Page<ForumPost> page, 
                                    @Param("category") String category, 
                                    @Param("schoolId") Long schoolId,
                                    @Param("currentUserId") Long currentUserId,
                                    @Param("sortBy") String sortBy);

    /**
     * 查询帖子详情
     */
    ForumPost selectForumDetail(@Param("id") Long id, @Param("currentUserId") Long currentUserId);

    IPage<ForumPost> selectMyFavoritePage(Page<ForumPost> page, @Param("userId") Long userId);

    /**
     * 更新计数
     */
    int updateViewCount(@Param("id") Long id);
    int updateCommentCount(@Param("id") Long id, @Param("delta") int delta);
}
