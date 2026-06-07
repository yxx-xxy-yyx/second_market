package com.echoofmemories.project.controller;

import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.AiRequest;
import com.echoofmemories.project.dto.AiResponse;
import com.echoofmemories.project.dto.ProductAnalysisDTO;
import com.echoofmemories.project.dto.ProductAnalysisRequest;
import com.echoofmemories.project.dto.AiRecommendationRequest;
import com.echoofmemories.project.dto.AiRecommendationResponse;
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

@Tag(name = "AI功能管理", description = "AI内容生成及商品分析相关接口")
@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @Operation(summary = "图片智能分析", description = "利用AI分析商品图片，提取标题、描述和成色建议")
    @PostMapping("/analyze/image")
    public Result<ProductAnalysisDTO> analyzeProductImage(@Valid @RequestBody ProductAnalysisRequest request) {
        log.info("收到AI商品分析请求，图片数量：{}", request.getImageUrls() != null ? request.getImageUrls().size() : 0);
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                userId = 0L;
            }
            
            log.info("开始处理AI商品分析请求，用户ID：{}，图片URLs：{}", userId, request.getImageUrls());

            String language = request.getLanguage();
            if (language == null || language.isEmpty()) {
                language = "zh";
            }

            ProductAnalysisDTO result = aiService.analyzeProductImages(
                    request.getImageUrls(),
                    request.getAdditionalInfo(),
                    userId,
                    language
            );
            
            log.info("AI商品分析请求处理成功");
            return Result.success("分析成功", result);
        } catch (Exception e) {
            log.error("AI商品分析请求处理失败：{}", e.getMessage(), e);
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "通用AI内容生成", description = "根据用户提示词生成内容")
    @PostMapping("/generate")
    @RequireRole({ "admin", "user" })
    public Result<AiResponse> generateContent(@Valid @RequestBody AiRequest request) {
        try {
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

            Map<String, String> generateTypes = new HashMap<>();
            generateTypes.put("POST_CONTENT", "帖子内容生成 - 根据提示词创作文章内容");
            generateTypes.put("POST_SUMMARY", "帖子摘要生成 - 为文章内容生成简洁摘要");
            generateTypes.put("POST_TAGS", "帖子标签生成 - 为文章内容生成相关标签");
            generateTypes.put("GENERAL_CONTENT", "通用内容生成 - 通用的内容创作");

            Map<String, String> examples = new HashMap<>();
            examples.put("文章创作", "请帮我写一篇关于Vue 3新特性的技术文章，包含组合式API、响应式系统等内容");
            examples.put("摘要生成", "为你的长文章内容生成200字以内的摘要");
            examples.put("标签生成", "为文章内容生成3-5个相关标签，便于分类和检索");

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

    @Operation(summary = "智能托管", description = "开启商品智能托管，自动回复、议价、调价")
    @PostMapping("/trust")
    @RequireRole({ "admin", "user" })
    public Result<com.echoofmemories.project.dto.AiIntelligentTrustResponse> intelligentTrust(
            @Valid @RequestBody com.echoofmemories.project.dto.AiIntelligentTrustRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("用户{}请求智能托管服务", userId);

            com.echoofmemories.project.dto.AiIntelligentTrustResponse response = 
                aiService.intelligentTrust(request, userId);
            return Result.success("智能托管开启成功", response);

        } catch (Exception e) {
            log.error("智能托管服务失败：{}", e.getMessage(), e);
            return Result.error("智能托管服务失败：" + e.getMessage());
        }
    }

    @Operation(summary = "鉴定质检", description = "上传商品图片进行AI鉴定和成色评估")
    @PostMapping("/authenticate")
    @RequireRole({ "admin", "user" })
    public Result<com.echoofmemories.project.dto.AiAuthenticationResponse> authenticateProduct(
            @Valid @RequestBody com.echoofmemories.project.dto.AiAuthenticationRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("用户{}请求商品鉴定服务", userId);

            com.echoofmemories.project.dto.AiAuthenticationResponse response = 
                aiService.authenticateProduct(request, userId);
            return Result.success("商品鉴定完成", response);

        } catch (Exception e) {
            log.error("商品鉴定服务失败：{}", e.getMessage(), e);
            return Result.error("商品鉴定服务失败：" + e.getMessage());
        }
    }

    @Operation(summary = "市场行情参考", description = "获取商品市场行情、价格趋势和同款最低价")
    @PostMapping("/market")
    @RequireRole({ "admin", "user" })
    public Result<com.echoofmemories.project.dto.AiMarketTrendResponse> getMarketTrend(
            @RequestBody com.echoofmemories.project.dto.AiMarketTrendRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("用户{}请求市场行情参考", userId);

            com.echoofmemories.project.dto.AiMarketTrendResponse response = 
                aiService.getMarketTrend(request, userId);
            return Result.success("获取市场行情成功", response);

        } catch (Exception e) {
            log.error("获取市场行情失败：{}", e.getMessage(), e);
            return Result.error("获取市场行情失败：" + e.getMessage());
        }
    }

    @Operation(summary = "智能搜索", description = "AI智能搜索，支持模糊和语音搜索")
    @PostMapping("/search")
    public Result<com.echoofmemories.project.dto.AiSmartSearchResponse> smartSearch(
            @RequestBody com.echoofmemories.project.dto.AiSmartSearchRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                userId = 0L;
            }
            log.info("用户{}请求智能搜索", userId);

            com.echoofmemories.project.dto.AiSmartSearchResponse response = 
                aiService.smartSearch(request, userId);
            return Result.success("搜索完成", response);

        } catch (Exception e) {
            log.error("智能搜索失败：{}", e.getMessage(), e);
            return Result.error("智能搜索失败：" + e.getMessage());
        }
    }

    @Operation(summary = "校园匹配", description = "按学校、距离、专业匹配相关商品和用户")
    @PostMapping("/match")
    @RequireRole({ "admin", "user" })
    public Result<com.echoofmemories.project.dto.AiCampusMatchResponse> campusMatch(
            @RequestBody com.echoofmemories.project.dto.AiCampusMatchRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("用户{}请求校园匹配服务", userId);

            com.echoofmemories.project.dto.AiCampusMatchResponse response = 
                aiService.campusMatch(request, userId);
            return Result.success("校园匹配完成", response);

        } catch (Exception e) {
            log.error("校园匹配失败：{}", e.getMessage(), e);
            return Result.error("校园匹配失败：" + e.getMessage());
        }
    }

    @Operation(summary = "纠纷仲裁", description = "AI辅助纠纷判断和处理建议")
    @PostMapping("/dispute")
    @RequireRole({ "admin", "user" })
    public Result<com.echoofmemories.project.dto.AiDisputeResolutionResponse> resolveDispute(
            @Valid @RequestBody com.echoofmemories.project.dto.AiDisputeResolutionRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("用户{}请求纠纷仲裁服务", userId);

            com.echoofmemories.project.dto.AiDisputeResolutionResponse response = 
                aiService.resolveDispute(request, userId);
            return Result.success("纠纷分析完成", response);

        } catch (Exception e) {
            log.error("纠纷仲裁服务失败：{}", e.getMessage(), e);
            return Result.error("纠纷仲裁服务失败：" + e.getMessage());
        }
    }

    @Operation(summary = "校园专属服务", description = "AI教材循环、闲置置换、校园跑腿等服务")
    @PostMapping("/campus-service")
    @RequireRole({ "admin", "user" })
    public Result<com.echoofmemories.project.dto.AiCampusServiceResponse> campusService(
            @RequestBody com.echoofmemories.project.dto.AiCampusServiceRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("用户{}请求校园专属服务", userId);

            com.echoofmemories.project.dto.AiCampusServiceResponse response = 
                aiService.campusService(request, userId);
            return Result.success("校园服务匹配完成", response);

        } catch (Exception e) {
            log.error("校园服务匹配失败：{}", e.getMessage(), e);
            return Result.error("校园服务匹配失败：" + e.getMessage());
        }
    }

    @Operation(summary = "AI智能推荐商品", description = "根据用户浏览和搜索历史进行个性化推荐")
    @PostMapping("/recommendations")
    public Result<AiRecommendationResponse> getRecommendations(
            @RequestBody AiRecommendationRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("AI推荐请求 - 用户ID: {}, 类型: {}", userId, request.getType());
            
            AiRecommendationResponse response = aiService.getRecommendations(request, userId);
            
            if (response.getSuccess()) {
                return Result.success(response);
            } else {
                return Result.error("500", response.getExplanation());
            }
        } catch (Exception e) {
            log.error("AI推荐失败", e);
            return Result.error("500", "推荐服务暂时不可用");
        }
    }
}
