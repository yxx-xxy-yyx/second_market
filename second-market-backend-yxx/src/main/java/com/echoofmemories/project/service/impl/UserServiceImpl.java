package com.echoofmemories.project.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.common.Constants;
import com.echoofmemories.project.dto.AdminUserListRequest;
import com.echoofmemories.project.dto.PageRequest;
import com.echoofmemories.project.dto.UserDetailDTO;
import com.echoofmemories.project.dto.UserInfoUpdateRequest;
import com.echoofmemories.project.entity.User;
import com.echoofmemories.project.exception.CustomException;
import com.echoofmemories.project.mapper.UserMapper;
import com.echoofmemories.project.service.UserService;
import com.echoofmemories.project.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户服务实现类
 * 
 * @author Echo of Memories
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User login(String username, String password) {
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            throw new CustomException("用户名和密码不能为空");
        }

        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new CustomException("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new CustomException("账户已被禁用");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException("密码错误");
        }

        // 生成JWT token
        String token = JwtUtils.generateToken(user.getId().toString(), user.getUsername());
        user.setToken(token);
        user.setPassword(null); // 清除密码字段

        return user;
    }

    @Override
    public User register(User user) {
        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            throw new CustomException("用户名和密码不能为空");
        }

        // 检查用户名是否已存在
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new CustomException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (StrUtil.isNotBlank(user.getEmail()) && userMapper.selectByEmail(user.getEmail()) != null) {
            throw new CustomException("邮箱已存在");
        }

        // 设置默认值
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Constants.ROLE_USER);
        user.setStatus(1);
        user.setAvatar(Constants.DEFAULT_AVATAR);
        user.setSchoolId(user.getSchoolId());
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setDeleted(0);

        if (userMapper.insert(user) > 0) {
            user.setPassword(null); // 清除密码字段
            return user;
        } else {
            throw new CustomException("注册失败");
        }
    }

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User getByEmail(String email) {
        return userMapper.selectByEmail(email);
    }

    @Override
    public Page<User> getUserPage(PageRequest pageRequest) {
        Page<User> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);

        if (StrUtil.isNotBlank(pageRequest.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like("username", pageRequest.getKeyword())
                    .or()
                    .like("nickname", pageRequest.getKeyword())
                    .or()
                    .like("email", pageRequest.getKeyword()));
        }

        if (StrUtil.isNotBlank(pageRequest.getRole())) {
            queryWrapper.eq("role", pageRequest.getRole());
        }

        if (pageRequest.getStatus() != null) {
            queryWrapper.eq("status", pageRequest.getStatus());
        }

        queryWrapper.orderByDesc("create_time");
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new CustomException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new CustomException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());

        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new CustomException("用户不存在");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());

        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean updateStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new CustomException("用户不存在");
        }

        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());

        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean updateUserInfo(Long userId, UserInfoUpdateRequest request) {
        User user = this.getById(userId);
        if (user == null) {
            throw new CustomException("用户不存在");
        }

        if (StrUtil.isNotBlank(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StrUtil.isNotBlank(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        if (StrUtil.isNotBlank(request.getAddress())) {
            user.setAddress(request.getAddress());
        }
        if (StrUtil.isNotBlank(request.getAvatar())) {
            user.setAvatar(request.getAvatar());
        }
        if (StrUtil.isNotBlank(request.getBio())) {
            user.setBio(request.getBio());
        }

        return this.updateById(user);
    }

    @Override
    public Page<UserDetailDTO> getUserDetailPage(AdminUserListRequest request) {
        Page<UserDetailDTO> page = new Page<>(request.getPageNum(), request.getPageSize());
        return userMapper.selectUserDetailPage(page, request.getSchoolId(), request.getKeyword(), request.getUsername(),
                request.getNickname(), request.getPhone(), request.getStatus(), request.getLanguage());
    }

    @Override
    public boolean banUser(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new CustomException("用户不存在");
        }

        if ("admin".equals(user.getRole())) {
            throw new CustomException("不能封禁管理员账号");
        }

        user.setStatus(0);
        return this.updateById(user);
    }

    @Override
    public boolean unbanUser(Long userId) {
        User user = this.getById(userId);
        if (user == null) {
            throw new CustomException("用户不存在");
        }

        user.setStatus(1);
        return this.updateById(user);
    }

    @Override
    public boolean updateAvatar(Long userId, String avatarUrl) {
        if (StrUtil.isBlank(avatarUrl)) {
            throw new CustomException("头像URL不能为空");
        }

        User user = this.getById(userId);
        if (user == null) {
            throw new CustomException("用户不存在");
        }

        user.setAvatar(avatarUrl);
        return this.updateById(user);
    }

    @Override
    public Long getUserIdByNicknameOrUsername(String value) {
        if (StrUtil.isBlank(value)) return null;
        User byUsername = userMapper.selectByUsername(value);
        if (byUsername != null) return byUsername.getId();

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("id");
        wrapper.eq("deleted", 0);
        wrapper.eq("nickname", value);
        wrapper.last("limit 1");
        List<User> list = userMapper.selectList(wrapper);
        if (list == null || list.isEmpty()) return null;
        return list.get(0).getId();
    }

}
