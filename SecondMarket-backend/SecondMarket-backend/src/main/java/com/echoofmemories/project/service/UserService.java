package com.echoofmemories.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.echoofmemories.project.dto.PageRequest;
import com.echoofmemories.project.entity.User;

/**
 * 用户服务接口
 * 
 * @author Echo of Memories
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户登录
     */
    User login(String username, String password);
    
    /**
     * 用户注册
     */
    User register(User user);
    
    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);
    
    /**
     * 根据邮箱查询用户
     */
    User getByEmail(String email);
    
    /**
     * 分页查询用户
     */
    Page<User> getUserPage(PageRequest pageRequest);
    
    /**
     * 更新用户密码
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     */
    boolean resetPassword(Long userId, String newPassword);
    
    /**
     * 更新用户状态
     */
    boolean updateStatus(Long userId, Integer status);
    
    /**
     * 更新用户信息
     */
    boolean updateUserInfo(Long userId, com.echoofmemories.project.dto.UserInfoUpdateRequest request);
    
    /**
     * 更新用户头像
     */
    boolean updateAvatar(Long userId, String avatarUrl);
    
    /**
     * 管理员查询用户详细信息列表
     */
    Page<com.echoofmemories.project.dto.UserDetailDTO> getUserDetailPage(com.echoofmemories.project.dto.AdminUserListRequest request);
    
    /**
     * 封禁用户
     */
    boolean banUser(Long userId);
    
    /**
     * 解封用户
     */
    boolean unbanUser(Long userId);
    
}