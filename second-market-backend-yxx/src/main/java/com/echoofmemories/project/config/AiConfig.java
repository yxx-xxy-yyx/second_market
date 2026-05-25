package com.echoofmemories.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * AI功能配置类
 * 支持OpenAI兼容格式的多种AI服务商
 * 
 * @author echo
 * @since 2025-01-27
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiConfig {

    /**
     * AI功能总开关
     */
    private Boolean enabled = true;

    /**
     * API配置
     */
    private ApiConfig api = new ApiConfig();

    /**
     * 功能配置
     */
    private FeaturesConfig features = new FeaturesConfig();

    /**
     * API服务商配置
     */
    @Data
    public static class ApiConfig {
        /**
         * API端点地址
         */
        private String baseUrl = "https://api.openai.com/v1/chat/completions";

        /**
         * API密钥
         */
        private String apiKey = "your-api-key-here";

        /**
         * 模型名称
         */
        private String model = "gpt-3.5-turbo";

        /**
         * 最大token数
         */
        private Integer maxTokens = 1000;

        /**
         * 创造性参数(0.0-2.0)
         */
        private Double temperature = 0.7;

        /**
         * 请求超时时间(毫秒)
         */
        private Integer timeout = 30000;
    }

    /**
     * 功能配置
     */
    @Data
    public static class FeaturesConfig {
        /**
         * 每分钟请求限制
         */
        private Integer rateLimit = 10;

        /**
         * 内容过滤开关
         */
        private Boolean contentFilter = true;
    }

    /**
     * 检查AI功能是否可用
     * 
     * @return true-可用，false-不可用
     */
    public boolean isAiAvailable() {
        return enabled &&
                StringUtils.hasText(api.getApiKey()) &&
                !api.getApiKey().equals("your-api-key-here") &&
                StringUtils.hasText(api.getBaseUrl()) &&
                StringUtils.hasText(api.getModel());
    }

    /**
     * 验证配置完整性
     * 
     * @return 验证结果信息
     */
    public String validateConfig() {
        if (!enabled) {
            return "AI功能已禁用";
        }

        if (!StringUtils.hasText(api.getApiKey()) || api.getApiKey().equals("your-api-key-here")) {
            return "AI API密钥未配置或使用默认值";
        }

        if (!StringUtils.hasText(api.getBaseUrl())) {
            return "AI API地址未配置";
        }

        if (!StringUtils.hasText(api.getModel())) {
            return "AI模型未配置";
        }

        if (api.getMaxTokens() == null || api.getMaxTokens() <= 0) {
            return "最大token数配置无效";
        }

        if (api.getTemperature() == null || api.getTemperature() < 0 || api.getTemperature() > 2) {
            return "temperature参数配置无效，应在0-2之间";
        }

        if (api.getTimeout() == null || api.getTimeout() <= 0) {
            return "超时时间配置无效";
        }

        return "配置验证通过";
    }

    /**
     * 获取配置摘要信息（隐藏敏感信息）
     * 
     * @return 配置摘要
     */
    public String getConfigSummary() {
        String maskedApiKey = StringUtils.hasText(api.getApiKey()) && !api.getApiKey().equals("your-api-key-here")
                ? api.getApiKey().substring(0, Math.min(8, api.getApiKey().length())) + "***"
                : "未配置";

        return String.format("AI功能: %s, 服务地址: %s, 模型: %s, API密钥: %s",
                enabled ? "启用" : "禁用",
                api.getBaseUrl(),
                api.getModel(),
                maskedApiKey);
    }
}
