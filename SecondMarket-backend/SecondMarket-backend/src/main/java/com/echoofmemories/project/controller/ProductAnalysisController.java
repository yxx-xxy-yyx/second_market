package com.echoofmemories.project.controller;

import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.ProductAnalysisDTO;
import com.echoofmemories.project.dto.ProductAnalysisRequest;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * AI商品分析控制器
 * 
 * @author EchoOfMemories
 */
@Slf4j
@Tag(name = "AI商品分析", description = "AI智能商品分析接口")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ProductAnalysisController {

    private final AiService aiService;

    @Operation(summary = "图片智能分析")
    @PostMapping("/analyze/image")
    public Result<ProductAnalysisDTO> analyzeProductImage(@Valid @RequestBody ProductAnalysisRequest request) {
        log.info("收到AI分析请求，图片数量：{}", request.getImageUrls() != null ? request.getImageUrls().size() : 0);
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                userId = 0L;
            }
            
            log.info("开始处理AI分析请求，用户ID：{}，图片URLs：{}", userId, request.getImageUrls());

            // 默认语言（防止前端不传）
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
            
            log.info("AI分析请求处理成功");
            return Result.success("分析成功", result);
        } catch (Exception e) {
            log.error("AI分析请求处理失败：{}", e.getMessage(), e);
            return Result.error("500", e.getMessage());
        }
    }
}

