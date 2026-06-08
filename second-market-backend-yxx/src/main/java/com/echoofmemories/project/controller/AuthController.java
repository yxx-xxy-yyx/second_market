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
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 *
 * @author EchoOfMemories
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
            // 从 service 层获取已认证的用户对象（内部包含 token）
            User authenticated = userService.login(loginRequest.getUid(), loginRequest.getPassword());

            // 用局部变量保存 token，然后克隆一个新对象给前端返回，避免修改 service 层对象
            String token = authenticated.getToken();

            // 构造一个新 User 对象给前端返回（密码/内部字段已脱敏）
            User dto = new User();
            BeanUtils.copyProperties(authenticated, dto, "password", "token");

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", dto);

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

            // 克隆返回对象，避免污染 service 层对象
            User dto = new User();
            BeanUtils.copyProperties(newUser, dto, "password");

            return Result.success("注册成功", dto);
        } catch (Exception e) {
            return Result.error("注册失败：" + e.getMessage());
        }
    }
}

