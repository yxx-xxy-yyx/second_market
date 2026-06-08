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
    private final JwtUtils jwtUtils;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Validated LoginRequest loginRequest) {
        try {
            // login() 内部已完成密码校验与 token 生成，返回的 User 已包含 token 字段
            User user = userService.login(loginRequest.getUid(), loginRequest.getPassword());

            // 构造返回数据：保持 { token, user } 结构，复用 service 已生成的 token
            String token = user.getToken();
            // 避免在返回的 user 对象中再次包含密码/token（token 放在外层）
            user.setToken(null);

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);

            return Result.success("登录成功", data);
        } catch (Exception e) {
            return Result.error(e.getMessage() != null ? e.getMessage() : "用户名或密码错误");
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

