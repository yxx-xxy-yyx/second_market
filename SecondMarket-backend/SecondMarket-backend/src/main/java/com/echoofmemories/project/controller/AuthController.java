package com.echoofmemories.project.controller;

import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.LoginRequest;
import com.echoofmemories.project.dto.RegisterRequest;
import com.echoofmemories.project.entity.User;
import com.echoofmemories.project.service.UserService;
import com.echoofmemories.project.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 
 * @author Echo of Memories
 */
@Tag(name = "用户认证", description = "用户登录注册相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Validated LoginRequest loginRequest) {
        try {
            User user = userService.login(loginRequest.getUid(), loginRequest.getPassword());

            // 生成JWT Token
            String token = JwtUtils.createToken(user.getId().toString());

            // 构造返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);

            return Result.success("登录成功", data);
        } catch (Exception e) {
            return Result.error("用户名或密码错误");
        }
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<User> register(@RequestBody @Validated RegisterRequest request) {
        try {
            User user = new User();
            user.setUsername(request.getUid());
            user.setNickname(request.getNickname());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setSchoolId(request.getSchoolId());

            User newUser = userService.register(user);
            // 清除密码字段
            newUser.setPassword(null);
            return Result.success("注册成功", newUser);
        } catch (Exception e) {
            return Result.error("注册失败：" + e.getMessage());
        }
    }
}

