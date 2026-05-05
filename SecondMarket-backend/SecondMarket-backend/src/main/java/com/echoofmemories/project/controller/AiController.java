package com.echoofmemories.project.controller;

import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.AiRequest;
import com.echoofmemories.project.dto.AiResponse;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.security.annotation.RequireRole;
import com.echoofmemories.project.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * AI功能控制器
 * 提供AI内容生成相关接口
 * 
 * @author echo
 * @since 2025-01-27
 */
@Tag(name = "AI功能管理", description = "AI内容生成相关接口")
@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @Operation(summary = "通用AI内容生成", description = "根据用户提示词生成内容")
    @PostMapping("/generate")
    @RequireRole({ "admin", "user" })
    public Result<AiResponse> generateContent(@Valid @RequestBody AiRequest request) {
        try {
            // 自动设置当前用户ID（安全性考虑，不允许前端传递用户ID）
            Long currentUserId = SecurityUtils.getCurrentUserId();
            request.setUserId(currentUserId);

            log.info("用户{}请求AI生成内容，类型：{}", currentUserId, request.getType());

            AiResponse response = aiService.generateContent(request);
            return Result.success(response);

        } catch (Exception e) {
            log.error("AI内容生成失败，用户：{}，错误：{}", SecurityUtils.getCurrentUserId(), e.getMessage(), e);
            return Result.error("AI内容生成失败：" + e.getMessage());
        }
    }

    @Operation(summary = "生成内容摘要", description = "为给定内容生成简洁的摘要")
    @PostMapping("/generate/summary")
    @RequireRole({ "admin", "user" })
    public Result<AiResponse> generateSummary(
            @Parameter(description = "需要生成摘要的内容", required = true) @RequestBody Map<String, String> requestBody) {
        try {
            String content = requestBody.get("content");
            if (!StringUtils.hasText(content)) {
                return Result.error("内容不能为空");
            }

            Long currentUserId = SecurityUtils.getCurrentUserId();
            log.info("用户{}请求生成摘要，内容长度：{}", currentUserId, content.length());

            AiResponse response = aiService.generateSummary(content, currentUserId);
            return Result.success(response);

        } catch (Exception e) {
            log.error("生成摘要失败，用户：{}，错误：{}", SecurityUtils.getCurrentUserId(), e.getMessage(), e);
            return Result.error("生成摘要失败：" + e.getMessage());
        }
    }

    @Operation(summary = "生成内容标签", description = "为给定内容生成相关标签")
    @PostMapping("/generate/tags")
    @RequireRole({ "admin", "user" })
    public Result<AiResponse> generateTags(
            @Parameter(description = "需要生成标签的内容", required = true) @RequestBody Map<String, String> requestBody) {
        try {
            String content = requestBody.get("content");
            if (!StringUtils.hasText(content)) {
                return Result.error("内容不能为空");
            }

            Long currentUserId = SecurityUtils.getCurrentUserId();
            log.info("用户{}请求生成标签，内容长度：{}", currentUserId, content.length());

            AiResponse response = aiService.generateTags(content, currentUserId);
            return Result.success(response);

        } catch (Exception e) {
            log.error("生成标签失败，用户：{}，错误：{}", SecurityUtils.getCurrentUserId(), e.getMessage(), e);
            return Result.error("生成标签失败：" + e.getMessage());
        }
    }

    @Operation(summary = "获取AI服务状态", description = "查看AI服务的运行状态和配置信息")
    @GetMapping("/status")
    public Result<Map<String, Object>> getAiStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("available", aiService.isAvailable());
            status.put("status", aiService.getServiceStatus());

            // 如果是管理员，返回更详细的信息
            if (SecurityUtils.isAdmin()) {
                status.put("connectionTest", aiService.testConnection());
            }

            return Result.success(status);

        } catch (Exception e) {
            log.error("获取AI服务状态失败：{}", e.getMessage(), e);
            return Result.error("获取AI服务状态失败");
        }
    }

    @Operation(summary = "测试AI服务连接", description = "测试AI服务是否正常工作（管理员专用）")
    @GetMapping("/test")
    @RequireRole("admin")
    public Result<Map<String, Object>> testAiConnection() {
        try {
            log.info("管理员{}执行AI服务连接测试", SecurityUtils.getCurrentUserId());

            boolean connectionOk = aiService.testConnection();
            String statusInfo = aiService.getServiceStatus();

            Map<String, Object> result = new HashMap<>();
            result.put("connectionTest", connectionOk);
            result.put("statusInfo", statusInfo);
            result.put("testResult", connectionOk ? "连接正常" : "连接失败");

            if (connectionOk) {
                return Result.success("AI服务连接测试通过", result);
            } else {
                return Result.error("AI服务连接测试失败");
            }

        } catch (Exception e) {
            log.error("AI服务连接测试异常：{}", e.getMessage(), e);

            Map<String, Object> result = new HashMap<>();
            result.put("connectionTest", false);
            result.put("error", e.getMessage());

            return Result.error("AI服务连接测试异常：" + e.getMessage());
        }
    }

    @Operation(summary = "获取AI功能使用说明", description = "获取AI功能的使用指南和示例")
    @GetMapping("/help")
    public Result<Map<String, Object>> getAiHelp() {
        try {
            Map<String, Object> help = new HashMap<>();

            // AI生成类型说明
            Map<String, String> generateTypes = new HashMap<>();
            generateTypes.put("POST_CONTENT", "帖子内容生成 - 根据提示词创作文章内容");
            generateTypes.put("POST_SUMMARY", "帖子摘要生成 - 为文章内容生成简洁摘要");
            generateTypes.put("POST_TAGS", "帖子标签生成 - 为文章内容生成相关标签");
            generateTypes.put("GENERAL_CONTENT", "通用内容生成 - 通用的内容创作");

            // 使用示例
            Map<String, String> examples = new HashMap<>();
            examples.put("文章创作", "请帮我写一篇关于Vue 3新特性的技术文章，包含组合式API、响应式系统等内容");
            examples.put("摘要生成", "为你的长文章内容生成200字以内的摘要");
            examples.put("标签生成", "为文章内容生成3-5个相关标签，便于分类和检索");

            // 使用建议
            String[] tips = {
                    "提示词要具体明确，避免过于模糊的描述",
                    "可以在提示词中指定期望的内容格式和风格",
                    "生成内容后建议进行适当的人工编辑和优化",
                    "请遵守内容规范，避免生成不当内容",
                    "每分钟请求次数有限制，请合理使用"
            };

            help.put("generateTypes", generateTypes);
            help.put("examples", examples);
            help.put("tips", tips);
            help.put("available", aiService.isAvailable());

            return Result.success(help);

        } catch (Exception e) {
            log.error("获取AI帮助信息失败：{}", e.getMessage(), e);
            return Result.error("获取AI帮助信息失败");
        }
    }
}
