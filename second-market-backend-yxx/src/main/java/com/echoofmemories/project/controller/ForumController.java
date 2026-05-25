package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.ForumInteractionStatus;
import com.echoofmemories.project.entity.ForumComment;
import com.echoofmemories.project.entity.ForumPost;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.service.ForumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 论坛控制器
 */
@Tag(name = "校园论坛", description = "论坛帖子相关接口")
@RestController
@RequestMapping("/forum")
@RequiredArgsConstructor
public class ForumController {

    private final ForumService forumService;

    @Operation(summary = "获取帖子列表")
    @GetMapping("/list")
    public Result<IPage<ForumPost>> getForumList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) String sortBy) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            Page<ForumPost> page = new Page<>(current, size);
            return Result.success(forumService.getForumPage(page, category, schoolId, userId, sortBy));
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "获取帖子详情")
    @GetMapping("/detail/{id}")
    public Result<ForumPost> getForumDetail(@PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            return Result.success(forumService.getForumDetail(id, userId));
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "发布帖子")
    @PostMapping("/create")
    public Result<String> createPost(@RequestBody ForumPost post) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "请先登录");
            }
            post.setUserId(userId);
            boolean success = forumService.createPost(post);
            return success ? Result.success("发布成功") : Result.error("发布失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "发表评论")
    @PostMapping("/comment")
    public Result<String> addComment(@RequestBody ForumComment comment) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "请先登录");
            }
            comment.setUserId(userId);
            boolean success = forumService.addComment(comment);
            return success ? Result.success("评论成功") : Result.error("评论失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/delete/{id}")
    public Result<String> deletePost(@PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) return Result.error("401", "请先登录");
            boolean success = forumService.deletePost(id, userId);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/comment/delete/{id}")
    public Result<String> deleteComment(@PathVariable Long id) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) return Result.error("401", "请先登录");
            boolean success = forumService.deleteComment(id, userId);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "更新帖子")
    @PutMapping("/update")
    public Result<String> updatePost(@RequestBody ForumPost post) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) return Result.error("401", "请先登录");
            boolean success = forumService.updatePost(post, userId);
            if (!success) return Result.error("403", "无权修改");
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "获取帖子评论列表")
    @GetMapping("/comments/{postId}")
    public Result<List<ForumComment>> getComments(@PathVariable Long postId) {
        try {
            return Result.success(forumService.getComments(postId));
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "点赞/取消点赞")
    @PostMapping("/like/{postId}")
    public Result<ForumInteractionStatus> toggleLike(@PathVariable Long postId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) return Result.error("401", "请先登录");
            return Result.success(forumService.toggleLike(postId, userId));
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "收藏/取消收藏")
    @PostMapping("/favorite/{postId}")
    public Result<ForumInteractionStatus> toggleFavorite(@PathVariable Long postId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) return Result.error("401", "请先登录");
            return Result.success(forumService.toggleFavorite(postId, userId));
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "我的收藏帖子")
    @GetMapping("/favorite/my")
    public Result<IPage<ForumPost>> getMyFavorites(@RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) return Result.error("401", "请先登录");
            Page<ForumPost> page = new Page<>(current, size);
            return Result.success(forumService.getMyFavoritePage(page, userId));
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }
}
