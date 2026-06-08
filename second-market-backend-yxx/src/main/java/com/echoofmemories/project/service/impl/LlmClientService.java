package com.echoofmemories.project.service.impl;

import com.echoofmemories.project.config.AiConfig;
import com.echoofmemories.project.dto.*;
import com.echoofmemories.project.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * LLM API调用服务
 * 封装对外部大语言模型API的调用
 *
 * @author echo
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LlmClientService {

    private final AiConfig aiConfig;
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(aiConfig.getApi().getTimeout() != null ? aiConfig.getApi().getTimeout() : 120000);
        factory.setConnectionRequestTimeout(10000);
        restTemplate = new RestTemplate(factory);
    }

    /**
     * 生成AI内容
     */
    public AiResponse generateContent(String type, String prompt, String context) {
        log.info("开始AI内容生成，类型：{}", type);

        if (!isAvailable()) {
            throw new CustomException("AI服务当前不可用");
        }

        String systemPrompt = buildSystemPrompt(type);
        String userPrompt = buildUserPrompt(prompt, context);

        return callApi(systemPrompt, userPrompt, null, type);
    }

    /**
     * 分析商品图片（多模态调用）
     */
    public ProductAnalysisDTO analyzeImages(List<String> processedImageUrls, String additionalInfo, String language) {
        log.info("开始AI商品分析，图片数量：{}", processedImageUrls != null ? processedImageUrls.size() : 0);

        if (!isAvailable()) {
            throw new CustomException("AI服务当前不可用");
        }

        if (processedImageUrls == null || processedImageUrls.isEmpty()) {
            throw new CustomException("图片URL列表不能为空");
        }

        String systemPrompt = buildProductAnalysisSystemPrompt(language);
        String userPrompt = buildProductAnalysisUserPrompt(additionalInfo, language);

        long startTime = System.currentTimeMillis();

        try {
            OpenAiCompatibleRequest apiRequest = buildMultimodalRequest(systemPrompt, userPrompt, processedImageUrls);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiConfig.getApi().getApiKey());

            HttpEntity<OpenAiCompatibleRequest> requestEntity = new HttpEntity<>(apiRequest, headers);

            log.info("调用AI多模态API，模型：{}，图片数量：{}", apiRequest.getModel(), processedImageUrls.size());

            ResponseEntity<OpenAiCompatibleResponse> response = restTemplate.exchange(
                    aiConfig.getApi().getBaseUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    OpenAiCompatibleResponse.class);

            OpenAiCompatibleResponse apiResponse = response.getBody();
            if (apiResponse == null || !apiResponse.isSuccessful()) {
                throw new CustomException("AI API返回异常响应");
            }

            String content = apiResponse.getFirstChoiceContent();
            if (!StringUtils.hasText(content)) {
                throw new CustomException("AI生成内容为空");
            }

            long duration = System.currentTimeMillis() - startTime;
            log.info("AI商品分析完成，耗时：{}ms", duration);

            return parseProductAnalysisResult(content);

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("AI API客户端错误，状态码：{}，响应：{}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode().value() == 400) {
                String responseBody = e.getResponseBodyAsString();
                if (responseBody != null && responseBody.contains("Image url")) {
                    throw new CustomException("图片URL格式错误，请确保图片文件存在且小于2MB");
                }
                throw new CustomException("AI服务请求参数错误，请检查图片格式和大小");
            }
            throw new CustomException("AI服务暂时不可用，请稍后重试");
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            log.error("AI API服务器错误，状态码：{}，响应：{}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode().value() == 500) {
                throw new CustomException("AI服务处理失败，可能是图片过大，请尝试使用更小的图片（建议小于2MB）");
            }
            throw new CustomException("AI服务暂时不可用，请稍后重试");
        } catch (ResourceAccessException e) {
            log.error("AI API网络异常，错误：{}", e.getMessage());
            throw new CustomException("AI服务连接超时，请稍后重试");
        } catch (Exception e) {
            log.error("AI商品分析失败，错误：{}", e.getMessage(), e);
            if (e instanceof CustomException) {
                throw e;
            }
            throw new CustomException("AI服务暂时不可用，请稍后重试");
        }
    }

    /**
     * 生成摘要
     */
    public String generateSummary(String content, Long userId) {
        if (!StringUtils.hasText(content)) {
            throw new CustomException("内容不能为空");
        }

        String systemPrompt = buildSystemPrompt(AiRequest.AiGenerateType.POST_SUMMARY.name());
        String userPrompt = buildUserPrompt("请为以下内容生成一个简洁的摘要", content);

        AiResponse response = callApi(systemPrompt, userPrompt, null, AiRequest.AiGenerateType.POST_SUMMARY.name());
        return response != null ? response.getContent() : null;
    }

    /**
     * 生成标签
     */
    public String generateTags(String content, Long userId) {
        if (!StringUtils.hasText(content)) {
            throw new CustomException("内容不能为空");
        }

        String systemPrompt = buildSystemPrompt(AiRequest.AiGenerateType.POST_TAGS.name());
        String userPrompt = buildUserPrompt("请为以下内容生成3-5个相关标签", content);

        AiResponse response = callApi(systemPrompt, userPrompt, null, AiRequest.AiGenerateType.POST_TAGS.name());
        return response != null ? response.getContent() : null;
    }

    /**
     * 检查AI服务是否可用
     */
    public boolean isAvailable() {
        return aiConfig.isAiAvailable();
    }

    /**
     * 获取服务状态信息
     */
    public String getServiceStatus() {
        if (!aiConfig.getEnabled()) {
            return "AI功能已禁用";
        }

        String validationResult = aiConfig.validateConfig();
        if (!"配置验证通过".equals(validationResult)) {
            return "配置错误：" + validationResult;
        }

        return "AI服务正常运行 - " + aiConfig.getConfigSummary();
    }

    /**
     * 构建商品分析系统提示词
     */
    private String buildProductAnalysisSystemPrompt(String language) {
        if ("en".equalsIgnoreCase(language)) {
            return "You are a professional second-hand product evaluation expert. " +
                    "Please conduct a detailed analysis based on the product images uploaded by the user, " +
                    "identify the product category, brand, and model, evaluate the product condition and market value, " +
                    "and generate an attractive product description. " +
                    "Please strictly return the result in the following JSON format " +
                    "(return JSON only, do not include any other text):\n" +
                    "{\n" +
                    "  \"productName\": \"Product Name\",\n" +
                    "  \"brand\": \"Brand\",\n" +
                    "  \"model\": \"Model\",\n" +
                    "  \"category\": \"Product Category\",\n" +
                    "  \"conditionScore\": 8,\n" +
                    "  \"conditionDescription\": \"Detailed condition description, including wear, scratches, stains, and other conditions\",\n" +
                    "  \"minPrice\": 500,\n" +
                    "  \"maxPrice\": 800,\n" +
                    "  \"description\": \"Attractive product description copy, 100-200 words, highlighting selling points\"\n" +
                    "}";
        } else if ("ko".equalsIgnoreCase(language)) {
            return "당신은 전문 중고 상품 평가 전문가입니다. " +
                    "사용자가 업로드한 상품 이미지를 바탕으로 상세하게 분석하고, " +
                    "상품의 카테고리, 브랜드, 모델을 식별하며, " +
                    "상품의 상태와 시장 가치를 평가하고, 매력적인 상품 설명 문구를 생성해 주세요. " +
                    "반드시 아래 JSON 형식에 엄격하게 맞춰 결과를 반환하세요 " +
                    "(JSON만 반환하고 다른 문자는 포함하지 마세요):\n" +
                    "{\n" +
                    "  \"productName\": \"상품명\",\n" +
                    "  \"brand\": \"브랜드\",\n" +
                    "  \"model\": \"모델\",\n" +
                    "  \"category\": \"상품 카테고리\",\n" +
                    "  \"conditionScore\": 8,\n" +
                    "  \"conditionDescription\": \"마모, 스크래치, 오염 등을 포함한 상세 상태 설명\",\n" +
                    "  \"minPrice\": 500,\n" +
                    "  \"maxPrice\": 800,\n" +
                    "  \"description\": \"매력적인 상품 설명 문구, 100-200자, 주요 판매 포인트 강조\"\n" +
                    "}\n" +
                    "## 중요: 응답은 무조건 한국어로만 작성하고, JSON의 모든 내용을 한국어로 반환하세요.";
        } else {
            return "你是一个专业的二手商品评估专家。请根据用户上传的商品图片进行详细分析，" +
                    "识别商品的类别、品牌、型号，评估商品的成色和市场价值，并生成吸引人的商品描述。" +
                    "请严格按照以下JSON格式返回结果（只返回JSON，不要包含任何其他文字）：\n" +
                    "{\n" +
                    "  \"productName\": \"商品名称\",\n" +
                    "  \"brand\": \"品牌\",\n" +
                    "  \"model\": \"型号\",\n" +
                    "  \"category\": \"商品分类\",\n" +
                    "  \"conditionScore\": 8,\n" +
                    "  \"conditionDescription\": \"详细的成色描述，包括磨损、划痕、污渍等情况\",\n" +
                    "  \"minPrice\": 500,\n" +
                    "  \"maxPrice\": 800,\n" +
                    "  \"description\": \"吸引人的商品描述文案，100-200字，突出卖点\"\n" +
                    "}";
        }
    }

    /**
     * 构建商品分析用户提示词
     */
    private String buildProductAnalysisUserPrompt(String additionalInfo, String language) {
        StringBuilder prompt = new StringBuilder();

        if ("en".equalsIgnoreCase(language)) {
            prompt.append("Please analyze this second-hand product based on the images and provide a detailed evaluation result.");

            if (StringUtils.hasText(additionalInfo)) {
                prompt.append("\n\nAdditional Information: ").append(additionalInfo);
            }

            prompt.append("\n\nNotes:");
            prompt.append("\n1. Condition score ranges from 1 to 10, where 10 means brand new");
            prompt.append("\n2. Price suggestions must conform to the second-hand market conditions");
            prompt.append("\n3. Product description must be truthful and not exaggerated");
            prompt.append("\n4. If the exact model cannot be identified, please mark it as 'To Be Confirmed'");
        } else if ("ko".equalsIgnoreCase(language)) {
            prompt.append("이미지를 기반으로 이 중고 상품을 분석하고 상세한 평가 결과를 제공해 주세요.");

            if (StringUtils.hasText(additionalInfo)) {
                prompt.append("\n\n추가 정보: ").append(additionalInfo);
            }

            prompt.append("\n\n주의사항:");
            prompt.append("\n1. 상태 점수는 1점에서 10점까지이며, 10점은 새 상품을 의미합니다");
            prompt.append("\n2. 가격 제안은 중고 시장 시세에 부합해야 합니다");
            prompt.append("\n3. 상품 설명은 사실에 기반해야 하며 과장하지 마세요");
            prompt.append("\n4. 정확한 모델을 식별할 수 없는 경우 '확인 필요'로 표시하세요");
        } else {
            prompt.append("请根据图片分析这个二手商品，提供详细的评估结果。");

            if (StringUtils.hasText(additionalInfo)) {
                prompt.append("\n\n补充信息：").append(additionalInfo);
            }

            prompt.append("\n\n注意事项：");
            prompt.append("\n1. 成色评分范围1-10分，10分为全新");
            prompt.append("\n2. 价格建议需符合二手市场行情");
            prompt.append("\n3. 商品描述要真实，不要夸大");
            prompt.append("\n4. 如果无法识别具体型号，请标注为'待确认'");
        }

        return prompt.toString();
    }

    /**
     * 构建系统提示词（根据类型）
     */
    private String buildSystemPrompt(String type) {
        AiRequest.AiGenerateType generateType;
        try {
            generateType = AiRequest.AiGenerateType.valueOf(type);
        } catch (Exception e) {
            generateType = AiRequest.AiGenerateType.GENERAL_CONTENT;
        }

        switch (generateType) {
            case POST_CONTENT:
                return "你是一个专业的内容创作助手。请根据用户的要求，创作高质量、有价值的文章内容。" +
                        "内容应该结构清晰、逻辑性强，适合在论坛或博客上发布。使用中文回答。";

            case POST_SUMMARY:
                return "你是一个专业的内容摘要助手。请为用户提供的内容生成简洁、准确的摘要。" +
                        "摘要应该抓住内容的核心要点，长度控制在200字以内。使用中文回答。";

            case POST_TAGS:
                return "你是一个专业的标签生成助手。请为用户提供的内容生成3-5个相关标签。" +
                        "标签应该准确反映内容主题，有助于内容分类和检索。请只返回标签，用逗号分隔。使用中文回答。";

            case GENERAL_CONTENT:
            default:
                return "你是一个有帮助的助手。请根据用户的要求提供准确、有用的回答。使用中文回答。";
        }
    }

    /**
     * 构建用户提示词
     */
    private String buildUserPrompt(String prompt, String context) {
        StringBuilder promptBuilder = new StringBuilder();

        if (StringUtils.hasText(prompt)) {
            promptBuilder.append(prompt);
        }

        if (StringUtils.hasText(context)) {
            promptBuilder.append("\n\n参考内容：\n").append(context);
        }

        return promptBuilder.toString();
    }

    /**
     * 解析商品分析结果
     */
    private ProductAnalysisDTO parseProductAnalysisResult(String content) {
        try {
            content = content.trim();
            if (content.startsWith("```json")) {
                content = content.substring(7);
            }
            if (content.startsWith("```")) {
                content = content.substring(3);
            }
            if (content.endsWith("```")) {
                content = content.substring(0, content.length() - 3);
            }
            content = content.trim();

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(content, ProductAnalysisDTO.class);

        } catch (Exception e) {
            log.error("解析AI返回结果失败：{}", e.getMessage());
            throw new CustomException("AI返回格式错误，请重试");
        }
    }

    /**
     * 调用AI API（普通文本或多模态）
     */
    private AiResponse callApi(String systemPrompt, String userPrompt, List<String> imageUrls) {
        return callApi(systemPrompt, userPrompt, imageUrls, AiRequest.AiGenerateType.GENERAL_CONTENT.name());
    }

    private AiResponse callApi(String systemPrompt, String userPrompt, List<String> imageUrls, String type) {
        long startTime = System.currentTimeMillis();

        try {
            OpenAiCompatibleRequest apiRequest;
            if (imageUrls != null && !imageUrls.isEmpty()) {
                apiRequest = buildMultimodalRequest(systemPrompt, userPrompt, imageUrls);
            } else {
                apiRequest = OpenAiCompatibleRequest.createSystemUserRequest(
                        aiConfig.getApi().getModel(),
                        systemPrompt,
                        userPrompt,
                        aiConfig.getApi().getMaxTokens(),
                        aiConfig.getApi().getTemperature());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiConfig.getApi().getApiKey());

            HttpEntity<OpenAiCompatibleRequest> requestEntity = new HttpEntity<>(apiRequest, headers);

            log.info("调用AI API，模型：{}", apiRequest.getModel());

            ResponseEntity<OpenAiCompatibleResponse> response = restTemplate.exchange(
                    aiConfig.getApi().getBaseUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    OpenAiCompatibleResponse.class);

            OpenAiCompatibleResponse apiResponse = response.getBody();
            if (apiResponse == null || !apiResponse.isSuccessful()) {
                throw new CustomException("AI API返回异常响应");
            }

            String content = apiResponse.getFirstChoiceContent();
            if (!StringUtils.hasText(content)) {
                throw new CustomException("AI生成内容为空");
            }

            if (aiConfig.getFeatures().getContentFilter() && containsInappropriateContent(content)) {
                throw new CustomException("生成内容包含不适当信息，请修改提示词后重试");
            }

            long duration = System.currentTimeMillis() - startTime;

            log.info("AI生成完成，耗时：{}ms，token使用：{}",
                    duration,
                    apiResponse.getUsage() != null ? apiResponse.getUsage().getTotalTokens() : "未知");

            AiRequest.AiGenerateType generateType;
            try {
                generateType = AiRequest.AiGenerateType.valueOf(type);
            } catch (Exception e) {
                generateType = AiRequest.AiGenerateType.GENERAL_CONTENT;
            }

            return AiResponse.success(
                    content.trim(),
                    apiResponse.getModel(),
                    generateType,
                    duration,
                    apiResponse.toTokenUsage());

        } catch (ResourceAccessException e) {
            log.error("AI API网络异常，错误：{}", e.getMessage());
            throw new CustomException("AI服务连接超时，请稍后重试");
        } catch (Exception e) {
            log.error("AI API调用失败，错误：{}", e.getMessage(), e);

            if (e instanceof CustomException) {
                throw e;
            }

            String errorMessage = e.getMessage();
            if (errorMessage != null) {
                if (errorMessage.contains("401") || errorMessage.contains("Unauthorized")) {
                    throw new CustomException("AI服务认证失败，请检查API密钥配置");
                } else if (errorMessage.contains("429") || errorMessage.contains("rate limit")) {
                    throw new CustomException("AI服务请求过于频繁，请稍后重试");
                } else if (errorMessage.contains("400") || errorMessage.contains("Bad Request")) {
                    throw new CustomException("请求参数错误，请修改后重试");
                }
            }

            throw new CustomException("AI服务暂时不可用，请稍后重试");
        }
    }

    /**
     * 构建多模态请求
     */
    private OpenAiCompatibleRequest buildMultimodalRequest(
            String systemPrompt, String userPrompt, List<String> imageUrls) {

        List<OpenAiCompatibleRequest.Message> messages = new ArrayList<>();

        messages.add(OpenAiCompatibleRequest.Message.builder()
                .role("system")
                .content(systemPrompt)
                .build());

        List<Object> userContent = new ArrayList<>();
        userContent.add(Map.of("type", "text", "text", userPrompt));

        for (String imageUrl : imageUrls) {
            userContent.add(Map.of(
                    "type", "image_url",
                    "image_url", Map.of("url", imageUrl)));
        }

        OpenAiCompatibleRequest.Message userMessage = new OpenAiCompatibleRequest.Message();
        userMessage.setRole("user");
        userMessage.setContentList(userContent);
        messages.add(userMessage);

        return OpenAiCompatibleRequest.builder()
                .model(aiConfig.getApi().getModel())
                .messages(messages)
                .maxTokens(aiConfig.getApi().getMaxTokens())
                .temperature(aiConfig.getApi().getTemperature())
                .stream(false)
                .build();
    }

    /**
     * 内容过滤检查
     */
    private boolean containsInappropriateContent(String content) {
        if (!StringUtils.hasText(content)) {
            return false;
        }

        String[] inappropriateWords = {
                "违法", "犯罪", "暴力", "色情", "赌博",
                "政治敏感", "恶意攻击", "人身攻击"
        };

        String lowerContent = content.toLowerCase();
        for (String word : inappropriateWords) {
            if (lowerContent.contains(word.toLowerCase())) {
                log.warn("检测到不适当内容：{}", word);
                return true;
            }
        }

        return false;
    }
}
