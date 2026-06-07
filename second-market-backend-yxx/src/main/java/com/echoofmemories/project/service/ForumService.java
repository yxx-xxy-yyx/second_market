package com.echoofmemories.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.echoofmemories.project.dto.ForumInteractionStatus;
import com.echoofmemories.project.entity.ForumComment;
import com.echoofmemories.project.entity.ForumPost;

import java.util.List;

/**
 * 论坛服务接口
 */
public interface ForumService extends IService<ForumPost> {

    /**
     * 分页查询帖子列表
     */
    IPage<ForumPost> getForumPage(Page<ForumPost> page, String category, Long schoolId, Long currentUserId, String sortBy);

    /**
     * 获取帖子详情
     */
    ForumPost getForumDetail(Long id, Long currentUserId);

    /**
     * 发布帖子
     */
    boolean createPost(ForumPost post);

    /**
     * 更新帖子（包含 @ 提及通知）
     */
    boolean updatePost(ForumPost post, Long userId);

    /**
     * 发表评论
     */
    boolean addComment(ForumComment comment);

    /**
     * 删除帖子
     */
    boolean deletePost(Long id, Long userId);

    /**
     * 删除评论
     */
    boolean deleteComment(Long id, Long userId);

    /**
     * 获取帖子评论列表
     */
    List<ForumComment> getComments(Long postId);

    ForumInteractionStatus toggleLike(Long postId, Long userId);

    ForumInteractionStatus toggleFavorite(Long postId, Long userId);

    IPage<ForumPost> getMyFavoritePage(Page<ForumPost> page, Long userId);

    /**
     * 管理员获取所有帖子（不带权限过滤）
     */
    IPage<ForumPost> getAllPosts(Page<ForumPost> page, String category, Long schoolId, String keyword);

    /**
     * 管理员获取所有评论（不带权限过滤）
     */
    IPage<ForumComment> getAllComments(Page<ForumComment> page, Long postId, String keyword);

    /**
     * 管理员删除帖子（强制删除，绕过权限检查）
     */
    boolean adminDeletePost(Long id);

    /**
     * 管理员删除评论（强制删除，绕过权限检查）
     */
    boolean adminDeleteComment(Long id);
}
