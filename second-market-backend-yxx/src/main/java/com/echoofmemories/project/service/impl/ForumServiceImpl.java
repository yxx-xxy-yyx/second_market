package com.echoofmemories.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.dto.ForumInteractionStatus;
import com.echoofmemories.project.dto.MessageSendRequest;
import com.echoofmemories.project.entity.ForumComment;
import com.echoofmemories.project.entity.ForumFavorite;
import com.echoofmemories.project.entity.ForumLike;
import com.echoofmemories.project.entity.ForumPost;
import com.echoofmemories.project.mapper.ForumCommentMapper;
import com.echoofmemories.project.mapper.ForumFavoriteMapper;
import com.echoofmemories.project.mapper.ForumLikeMapper;
import com.echoofmemories.project.mapper.ForumPostMapper;
import com.echoofmemories.project.service.ForumService;
import com.echoofmemories.project.service.MessageService;
import com.echoofmemories.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 论坛服务实现类
 */
@Service
public class ForumServiceImpl extends ServiceImpl<ForumPostMapper, ForumPost> implements ForumService {

    private static final Pattern MENTION_PATTERN = Pattern.compile("@([\\u4e00-\\u9fa5A-Za-z0-9_]{2,20})");

    @Autowired
    private ForumPostMapper forumPostMapper;

    @Autowired
    private ForumCommentMapper forumCommentMapper;

    @Autowired
    private ForumLikeMapper forumLikeMapper;

    @Autowired
    private ForumFavoriteMapper forumFavoriteMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    public IPage<ForumPost> getForumPage(Page<ForumPost> page, String category, Long schoolId, Long currentUserId, String sortBy) {
        return forumPostMapper.selectForumPage(page, category, schoolId, currentUserId, sortBy);
    }

    @Override
    public ForumPost getForumDetail(Long id, Long currentUserId) {
        forumPostMapper.updateViewCount(id);
        return forumPostMapper.selectForumDetail(id, currentUserId);
    }

    @Override
    @Transactional
    public boolean createPost(ForumPost post) {
        boolean success = save(post);
        if (success) {
            notifyMentionUsers(post.getContent(), post.getUserId(), post.getId());
        }
        return success;
    }

    @Override
    @Transactional
    public boolean updatePost(ForumPost post, Long userId) {
        ForumPost oldPost = getById(post.getId());
        if (oldPost == null || oldPost.getUserId() == null) return false;
        if (!oldPost.getUserId().equals(userId)) return false;

        post.setUserId(oldPost.getUserId());
        boolean success = updateById(post);
        if (success) {
            notifyMentionUsers(post.getContent(), userId, post.getId());
        }
        return success;
    }

    @Override
    @Transactional
    public boolean addComment(ForumComment comment) {
        int result = forumCommentMapper.insert(comment);
        if (result > 0) {
            forumPostMapper.updateCommentCount(comment.getPostId(), 1);
            notifyPostAuthorComment(comment);
            notifyMentionUsers(comment.getContent(), comment.getUserId(), comment.getPostId());
        }
        return result > 0;
    }

    @Override
    @Transactional
    public boolean deletePost(Long id, Long userId) {
        ForumPost post = getById(id);
        if (post != null && post.getUserId().equals(userId)) {
            post.setDeleted(1);
            return updateById(post);
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteComment(Long id, Long userId) {
        ForumComment comment = forumCommentMapper.selectById(id);
        if (comment != null) {
            // 获取帖子作者ID
            ForumPost post = getById(comment.getPostId());
            // 只有评论作者或帖子作者可以删除
            if (comment.getUserId().equals(userId) || (post != null && post.getUserId().equals(userId))) {
                int res = forumCommentMapper.deleteById(id);
                if (res > 0) {
                    forumPostMapper.updateCommentCount(comment.getPostId(), -1);
                }
                return res > 0;
            }
        }
        return false;
    }

    @Override
    public List<ForumComment> getComments(Long postId) {
        return forumCommentMapper.selectByPostId(postId);
    }

    @Override
    @Transactional
    public ForumInteractionStatus toggleLike(Long postId, Long userId) {
        LambdaQueryWrapper<ForumLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ForumLike::getPostId, postId).eq(ForumLike::getUserId, userId);
        ForumLike existed = forumLikeMapper.selectOne(queryWrapper);
        boolean liked;
        if (existed != null) {
            forumLikeMapper.deleteById(existed.getId());
            liked = false;
        } else {
            ForumLike like = new ForumLike();
            like.setPostId(postId);
            like.setUserId(userId);
            forumLikeMapper.insert(like);
            liked = true;
            notifyPostAuthorLike(postId, userId);
        }

        ForumInteractionStatus status = new ForumInteractionStatus();
        status.setLiked(liked);
        status.setLikeCount(getLikeCount(postId));
        status.setFavorited(isFavorited(postId, userId));
        status.setFavoriteCount(getFavoriteCount(postId));
        return status;
    }

    @Override
    @Transactional
    public ForumInteractionStatus toggleFavorite(Long postId, Long userId) {
        LambdaQueryWrapper<ForumFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ForumFavorite::getPostId, postId).eq(ForumFavorite::getUserId, userId);
        ForumFavorite existed = forumFavoriteMapper.selectOne(queryWrapper);
        boolean favorited;
        if (existed != null) {
            forumFavoriteMapper.deleteById(existed.getId());
            favorited = false;
        } else {
            ForumFavorite favorite = new ForumFavorite();
            favorite.setPostId(postId);
            favorite.setUserId(userId);
            forumFavoriteMapper.insert(favorite);
            favorited = true;
        }

        ForumInteractionStatus status = new ForumInteractionStatus();
        status.setLiked(isLiked(postId, userId));
        status.setLikeCount(getLikeCount(postId));
        status.setFavorited(favorited);
        status.setFavoriteCount(getFavoriteCount(postId));
        return status;
    }

    @Override
    public IPage<ForumPost> getMyFavoritePage(Page<ForumPost> page, Long userId) {
        return forumPostMapper.selectMyFavoritePage(page, userId);
    }

    private long getLikeCount(Long postId) {
        LambdaQueryWrapper<ForumLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ForumLike::getPostId, postId);
        return forumLikeMapper.selectCount(wrapper);
    }

    private boolean isLiked(Long postId, Long userId) {
        LambdaQueryWrapper<ForumLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ForumLike::getPostId, postId).eq(ForumLike::getUserId, userId);
        return forumLikeMapper.selectCount(wrapper) > 0;
    }

    private long getFavoriteCount(Long postId) {
        LambdaQueryWrapper<ForumFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ForumFavorite::getPostId, postId);
        return forumFavoriteMapper.selectCount(wrapper);
    }

    private boolean isFavorited(Long postId, Long userId) {
        LambdaQueryWrapper<ForumFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ForumFavorite::getPostId, postId).eq(ForumFavorite::getUserId, userId);
        return forumFavoriteMapper.selectCount(wrapper) > 0;
    }

    private void notifyPostAuthorComment(ForumComment comment) {
        ForumPost post = forumPostMapper.selectById(comment.getPostId());
        if (post == null) return;
        if (post.getUserId() == null) return;
        if (post.getUserId().equals(comment.getUserId())) return;

        MessageSendRequest req = new MessageSendRequest();
        req.setReceiverId(post.getUserId());
        req.setType(101);
        req.setTitle("帖子被评论");
        req.setContent("你的帖子《" + safe(post.getTitle()) + "》收到新评论，点击查看：/user/forum/detail/" + post.getId());
        messageService.sendMessage(req);
    }

    private void notifyPostAuthorLike(Long postId, Long likerId) {
        ForumPost post = forumPostMapper.selectById(postId);
        if (post == null) return;
        if (post.getUserId() == null) return;
        if (post.getUserId().equals(likerId)) return;

        MessageSendRequest req = new MessageSendRequest();
        req.setReceiverId(post.getUserId());
        req.setType(102);
        req.setTitle("帖子被点赞");
        req.setContent("你的帖子《" + safe(post.getTitle()) + "》收到点赞，点击查看：/user/forum/detail/" + post.getId());
        messageService.sendMessage(req);
    }

    private void notifyMentionUsers(String text, Long fromUserId, Long postId) {
        if (text == null || text.isBlank()) return;

        Matcher matcher = MENTION_PATTERN.matcher(text);
        Set<String> names = matcher.results()
                .map(mr -> mr.group(1))
                .collect(Collectors.toSet());
        if (names.isEmpty()) return;

        for (String name : names) {
            Long targetId = userService.getUserIdByNicknameOrUsername(name);
            if (targetId == null) continue;
            if (targetId.equals(fromUserId)) continue;
            MessageSendRequest req = new MessageSendRequest();
            req.setReceiverId(targetId);
            req.setType(103);
            req.setTitle("有人@你");
            req.setContent("有人在帖子中提及你，点击查看：/user/forum/detail/" + postId);
            messageService.sendMessage(req);
        }
    }

    private String safe(String v) {
        return v == null ? "" : v;
    }
}
