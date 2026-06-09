package com.echoofmemories.project.service.impl;

import com.echoofmemories.project.config.AiConfig;
import com.echoofmemories.project.dto.*;
import com.echoofmemories.project.service.AiService;
import com.echoofmemories.project.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.echoofmemories.project.service.BrowseHistoryService;
import com.echoofmemories.project.service.SearchHistoryService;
import com.echoofmemories.project.service.ProductService;
import com.echoofmemories.project.mapper.ProductMapper;

/**
 * AI服务实现类
 * 
 * @author echo
 * @since 2025-01-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final AiConfig aiConfig;
    private final BrowseHistoryService browseHistoryService;
    private final SearchHistoryService searchHistoryService;
    private final ProductService productService;
    private final ProductMapper productMapper;
    private RestTemplate restTemplate;

    @Value("${project.server.base-url:http://localhost:8001/api}")
    private String serverBaseUrl;

    @Value("${project.file.upload-path:./uploads/}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        restTemplate = createRestTemplate();
    }

    private RestTemplate createRestTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(aiConfig.getApi().getTimeout() != null ? aiConfig.getApi().getTimeout() : 120000);
        factory.setConnectionRequestTimeout(10000);
        return new RestTemplate(factory);
    }

    private String convertToFullUrl(String imageUrl) {
        log.debug("开始转换图片URL: {}", imageUrl);

        if (imageUrl == null || imageUrl.isEmpty()) {
            log.warn("图片URL为空");
            throw new CustomException("图片URL不能为空");
        }

        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            log.debug("图片URL是HTTP URL，尝试下载并转换为base64");
            try {
                return downloadAndConvertToBase64(imageUrl);
            } catch (Exception e) {
                log.warn("下载HTTP图片失败，尝试直接使用URL: {}, 错误: {}", imageUrl, e.getMessage());
                return imageUrl;
            }
        }

        if (imageUrl.startsWith("data:image/")) {
            log.debug("图片URL已经是base64格式，直接返回");
            return imageUrl;
        }

        try {
            String filePath = imageUrl;
            if (filePath.startsWith("/uploads/")) {
                filePath = uploadPath + filePath.substring("/uploads/".length());
            } else if (filePath.startsWith("/")) {
                filePath = uploadPath + filePath.substring(1);
            } else {
                filePath = uploadPath + filePath;
            }

            log.debug("计算后的文件路径: {} (uploadPath: {})", filePath, uploadPath);

            File imageFile = new File(filePath);
            log.debug("文件是否存在: {}, 是否为文件: {}", imageFile.exists(), imageFile.isFile());

            if (imageFile.exists() && imageFile.isFile()) {
                long fileSize = imageFile.length();
                log.debug("文件大小: {} bytes ({}MB)", fileSize, fileSize / (1024 * 1024));

                log.debug("开始读取文件并转换为base64");
                byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
                String base64 = Base64.getEncoder().encodeToString(imageBytes);
                log.debug("Base64编码完成，长度: {}", base64.length());

                String mimeType = getMimeType(imageUrl);
                
                // 如果文件过大，尝试压缩
                if (fileSize > 2 * 1024 * 1024) {
                    log.info("图片文件过大（{}MB），尝试压缩", fileSize / (1024 * 1024));
                    try {
                        String compressedBase64 = compressBase64Image(base64, mimeType);
                        long compressedSize = (compressedBase64.length() * 3) / 4;
                        if (compressedSize <= 2 * 1024 * 1024) {
                            log.info("图片压缩成功，大小从{}MB减少到{}MB", 
                                fileSize / (1024 * 1024), compressedSize / (1024 * 1024));
                            base64 = compressedBase64;
                        } else {
                            log.warn("图片压缩后仍过大（{}MB），使用原始图片", compressedSize / (1024 * 1024));
                        }
                    } catch (Exception e) {
                        log.warn("图片压缩失败，使用原始图片：{}", e.getMessage());
                    }
                }
                
                String result = "data:image/" + mimeType + ";base64," + base64;
                log.debug("转换完成，结果格式: data:image/{}, base64长度: {}", mimeType, base64.length());
                return result;
            } else {
                log.error("图片文件不存在或不是文件: {} (绝对路径: {})", filePath, imageFile.getAbsolutePath());
                throw new CustomException("图片文件不存在: " + imageUrl);
            }
        } catch (IOException e) {
            log.error("读取图片文件失败: {}, 错误: {}", imageUrl, e.getMessage(), e);
            throw new CustomException("读取图片文件失败: " + e.getMessage());
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("处理图片URL失败: {}, 错误: {}", imageUrl, e.getMessage(), e);
            throw new CustomException("处理图片URL失败: " + e.getMessage());
        }
    }

    private String downloadAndConvertToBase64(String imageUrl) throws IOException {
        log.debug("开始下载图片: {}", imageUrl);

        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(30000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("下载图片失败，HTTP状态码: " + responseCode);
        }

        long contentLength = connection.getContentLengthLong();
        log.debug("图片内容长度: {} bytes", contentLength);

        try (InputStream inputStream = connection.getInputStream()) {
            byte[] imageBytes = inputStream.readAllBytes();
            log.debug("图片下载完成，大小: {} bytes ({}MB)", imageBytes.length, imageBytes.length / (1024 * 1024));

            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            String mimeType = getMimeTypeFromContentType(connection.getContentType());
            if (mimeType == null) {
                mimeType = getMimeType(imageUrl);
            }

            // 如果图片过大，尝试压缩
            if (imageBytes.length > 2 * 1024 * 1024) {
                log.info("下载的图片过大（{}MB），尝试压缩", imageBytes.length / (1024 * 1024));
                try {
                    String compressedBase64 = compressBase64Image(base64, mimeType);
                    long compressedSize = (compressedBase64.length() * 3) / 4;
                    if (compressedSize <= 2 * 1024 * 1024) {
                        log.info("图片压缩成功，大小从{}MB减少到{}MB", 
                            imageBytes.length / (1024 * 1024), compressedSize / (1024 * 1024));
                        base64 = compressedBase64;
                    } else {
                        log.warn("图片压缩后仍过大（{}MB），使用原始图片", compressedSize / (1024 * 1024));
                    }
                } catch (Exception e) {
                    log.warn("图片压缩失败，使用原始图片：{}", e.getMessage());
                }
            }

            log.debug("图片转换为base64完成，MIME类型: {}, base64长度: {}", mimeType, base64.length());
            return "data:image/" + mimeType + ";base64," + base64;
        } finally {
            connection.disconnect();
        }
    }

    private String getMimeTypeFromContentType(String contentType) {
        if (contentType == null || contentType.isEmpty()) {
            return null;
        }
        if (contentType.contains("image/jpeg") || contentType.contains("image/jpg")) {
            return "jpeg";
        } else if (contentType.contains("image/png")) {
            return "png";
        } else if (contentType.contains("image/gif")) {
            return "gif";
        } else if (contentType.contains("image/webp")) {
            return "webp";
        } else if (contentType.contains("image/bmp")) {
            return "bmp";
        }
        return null;
    }

    private String getMimeType(String imageUrl) {
        String lowerUrl = imageUrl.toLowerCase();
        if (lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg")) {
            return "jpeg";
        } else if (lowerUrl.endsWith(".png")) {
            return "png";
        } else if (lowerUrl.endsWith(".gif")) {
            return "gif";
        } else if (lowerUrl.endsWith(".webp")) {
            return "webp";
        } else if (lowerUrl.endsWith(".bmp")) {
            return "bmp";
        }
        return "jpeg";
    }

    private String compressBase64Image(String base64Data, String mimeType) {
        try {
            if (base64Data == null || base64Data.isEmpty()) {
                return base64Data;
            }
            
            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
            ByteArrayInputStream bais = new ByteArrayInputStream(decodedBytes);
            BufferedImage image = ImageIO.read(bais);
            
            if (image == null) {
                log.warn("无法读取图片，返回原始数据");
                return base64Data;
            }

            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            int maxWidth = 1200;
            int maxHeight = 1200;
            
            if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
                long estimatedSize = (base64Data.length() * 3) / 4;
                if (estimatedSize <= 1 * 1024 * 1024) {
                    log.debug("图片大小已在限制内，无需压缩");
                    return base64Data;
                }
            }

            int newWidth = originalWidth;
            int newHeight = originalHeight;
            
            if (originalWidth > maxWidth || originalHeight > maxHeight) {
                double ratio = Math.min((double) maxWidth / originalWidth, (double) maxHeight / originalHeight);
                newWidth = (int) (originalWidth * ratio);
                newHeight = (int) (originalHeight * ratio);
            }

            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = resizedImage.createGraphics();
            g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            String formatName = mimeType;
            if (formatName == null || formatName.isEmpty()) {
                formatName = "jpeg";
            } else if (formatName.equals("jpeg")) {
                formatName = "jpg";
            }
            
            ImageIO.write(resizedImage, formatName, baos);
            
            byte[] compressedBytes = baos.toByteArray();
            String compressedBase64 = Base64.getEncoder().encodeToString(compressedBytes);
            
            long originalSize = (base64Data.length() * 3) / 4;
            long compressedSize = (compressedBase64.length() * 3) / 4;
            log.info("图片压缩完成：{}x{} -> {}x{}, 大小：{}MB -> {}MB", 
                originalWidth, originalHeight, newWidth, newHeight,
                originalSize / (1024 * 1024), compressedSize / (1024 * 1024));
            
            return compressedBase64;
        } catch (Exception e) {
            log.warn("图片压缩失败，使用原始数据：{}", e.getMessage(), e);
            return base64Data;
        }
    }

    private java.util.List<String> optimizeImageUrls(java.util.List<String> imageUrls) {
        java.util.List<String> optimized = new java.util.ArrayList<>();

        int maxImages = 1;
        long maxTotalSize = 3 * 1024 * 1024;
        long totalSize = 0;
        int processedCount = 0;

        for (String imageUrl : imageUrls) {
            if (processedCount >= maxImages) {
                log.info("已达到最大图片数量限制（{}），跳过剩余图片", maxImages);
                break;
            }

            log.info("处理图片 {}/{}: {}", processedCount + 1, imageUrls.size(), imageUrl);
            try {
                String convertedUrl = convertToFullUrl(imageUrl);
                log.info("图片转换成功，格式: {}", convertedUrl.startsWith("data:image/") ? "base64"
                        : (convertedUrl.startsWith("http") ? "HTTP URL" : "其他"));

                if (convertedUrl.startsWith("http://") || convertedUrl.startsWith("https://")) {
                    log.info("使用HTTP URL格式的图片: {}", convertedUrl);
                    optimized.add(convertedUrl);
                    processedCount++;
                    continue;
                }

                if (!convertedUrl.startsWith("data:image/")) {
                    log.warn("图片URL格式不正确（不是base64格式也不是HTTP URL），跳过: {}", imageUrl);
                    continue;
                }

                int commaIndex = convertedUrl.indexOf(",");
                if (commaIndex == -1) {
                    log.warn("Base64图片URL格式不正确，缺少逗号分隔符，跳过: {}", imageUrl);
                    continue;
                }
                
                String base64Data = convertedUrl.substring(commaIndex + 1);
                if (base64Data.isEmpty()) {
                    log.warn("Base64图片数据为空，跳过: {}", imageUrl);
                    continue;
                }
                
                long base64Size = base64Data.length();
                long estimatedSize = (base64Size * 3) / 4;
                log.debug("Base64数据长度: {}, 估算大小: {}MB", base64Size, estimatedSize / (1024 * 1024));

                if (estimatedSize > 2 * 1024 * 1024) {
                    log.info("单张图片过大（{}MB），尝试压缩", estimatedSize / (1024 * 1024));
                    try {
                        int mimeTypeStart = convertedUrl.indexOf("image/");
                        if (mimeTypeStart == -1) {
                            log.warn("无法从图片URL中提取MIME类型，跳过该图片");
                            continue;
                        }
                        mimeTypeStart += 6; // 跳过 "image/"
                        
                        int mimeTypeEnd = convertedUrl.indexOf(";", mimeTypeStart);
                        if (mimeTypeEnd == -1) {
                            mimeTypeEnd = convertedUrl.indexOf(",", mimeTypeStart);
                        }
                        if (mimeTypeEnd == -1 || mimeTypeEnd <= mimeTypeStart) {
                            log.warn("无法从图片URL中提取MIME类型，跳过该图片");
                            continue;
                        }
                        
                        String mimeType = convertedUrl.substring(mimeTypeStart, mimeTypeEnd);
                        if (mimeType.isEmpty()) {
                            mimeType = "jpeg";
                        }
                        
                        String compressedBase64 = compressBase64Image(base64Data, mimeType);
                        convertedUrl = "data:image/" + mimeType + ";base64," + compressedBase64;
                        base64Data = compressedBase64;
                        base64Size = base64Data.length();
                        estimatedSize = (base64Size * 3) / 4;
                        
                        if (estimatedSize > 2 * 1024 * 1024) {
                            log.warn("压缩后图片仍过大（{}MB），跳过该图片", estimatedSize / (1024 * 1024));
                            continue;
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        log.warn("图片URL格式错误，跳过该图片：{}", e.getMessage());
                        continue;
                    } catch (Exception e) {
                        log.warn("图片压缩过程出错，跳过该图片：{}", e.getMessage());
                        continue;
                    }
                }

                if (totalSize + estimatedSize > maxTotalSize) {
                    log.warn("累计大小超过限制（{}MB），停止处理更多图片", maxTotalSize / (1024 * 1024));
                    break;
                }

                totalSize += estimatedSize;
                optimized.add(convertedUrl);
                processedCount++;
                log.info("已添加图片，当前数量：{}，累计大小：{}MB", processedCount, totalSize / (1024 * 1024));
            } catch (CustomException e) {
                log.warn("处理图片URL失败，跳过: {}，原因：{}", imageUrl, e.getMessage());
            } catch (Exception e) {
                log.warn("处理图片URL失败，跳过: {}，错误：{}", imageUrl, e.getMessage(), e);
            }
        }

        if (optimized.isEmpty()) {
            throw new CustomException("没有可用的图片，请检查图片格式和大小（建议单张图片小于2MB）");
        }

        if (totalSize > 0) {
            log.info("图片优化完成，最终使用{}张图片，总大小约{}MB", optimized.size(), totalSize / (1024 * 1024));
        } else {
            log.info("图片优化完成，最终使用{}张图片", optimized.size());
        }
        return optimized;
    }

    // 简单的内存限流器：用户ID -> 请求计数器
    private final ConcurrentHashMap<Long, UserRateLimit> userRateLimits = new ConcurrentHashMap<>();

    // 用户限流信息
    private static class UserRateLimit {
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private final AtomicLong lastResetTime = new AtomicLong(System.currentTimeMillis());

        public boolean canRequest(int limit) {
            long now = System.currentTimeMillis();
            long lastReset = lastResetTime.get();

            // 每分钟重置一次
            if (now - lastReset > 60000) {
                if (lastResetTime.compareAndSet(lastReset, now)) {
                    requestCount.set(0);
                }
            }

            return requestCount.incrementAndGet() <= limit;
        }
    }

    @Override
    public AiResponse generateContent(AiRequest request) {
        log.info("开始AI内容生成，用户ID：{}，类型：{}", request.getUserId(), request.getType());

        // 检查AI服务是否可用
        if (!isAvailable()) {
            throw new CustomException("AI服务当前不可用");
        }

        // 限流检查
        if (!checkRateLimit(request.getUserId())) {
            throw new CustomException("请求过于频繁，请稍后重试");
        }

        // 根据类型生成提示词
        String systemPrompt = buildSystemPrompt(request.getType());
        String userPrompt = buildUserPrompt(request);

        // 调用AI API
        return callAiApi(systemPrompt, userPrompt, request);
    }

    @Override
    public AiResponse generateSummary(String content, Long userId) {
        if (!StringUtils.hasText(content)) {
            throw new CustomException("内容不能为空");
        }

        AiRequest request = new AiRequest();
        request.setPrompt("请为以下内容生成一个简洁的摘要");
        request.setContext(content);
        request.setType(AiRequest.AiGenerateType.POST_SUMMARY);
        request.setUserId(userId);

        return generateContent(request);
    }

    @Override
    public AiResponse generateTags(String content, Long userId) {
        if (!StringUtils.hasText(content)) {
            throw new CustomException("内容不能为空");
        }

        AiRequest request = new AiRequest();
        request.setPrompt("请为以下内容生成3-5个相关标签");
        request.setContext(content);
        request.setType(AiRequest.AiGenerateType.POST_TAGS);
        request.setUserId(userId);

        return generateContent(request);
    }

    @Override
    public boolean testConnection() {
        try {
            AiRequest testRequest = new AiRequest();
            testRequest.setPrompt("Hello");
            testRequest.setType(AiRequest.AiGenerateType.GENERAL_CONTENT);
            testRequest.setMaxTokens(10);
            testRequest.setUserId(0L); // 测试请求使用特殊用户ID

            AiResponse response = generateContent(testRequest);
            return response != null && StringUtils.hasText(response.getContent());
        } catch (Exception e) {
            log.warn("AI服务连接测试失败：{}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isAvailable() {
        return aiConfig.isAiAvailable();
    }

    @Override
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
     * 检查用户请求限流
     */
    private boolean checkRateLimit(Long userId) {
        if (userId == 0L) { // 测试请求跳过限流
            return true;
        }

        UserRateLimit userLimit = userRateLimits.computeIfAbsent(userId, k -> new UserRateLimit());
        return userLimit.canRequest(aiConfig.getFeatures().getRateLimit());
    }

    /**
     * 构建系统提示词
     */
    private String buildSystemPrompt(AiRequest.AiGenerateType type) {
        switch (type) {
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
    private String buildUserPrompt(AiRequest request) {
        StringBuilder promptBuilder = new StringBuilder();

        // 添加用户的提示词
        if (StringUtils.hasText(request.getPrompt())) {
            promptBuilder.append(request.getPrompt());
        }

        // 如果有上下文内容，添加到提示词中
        if (StringUtils.hasText(request.getContext())) {
            promptBuilder.append("\n\n参考内容：\n").append(request.getContext());
        }

        return promptBuilder.toString();
    }

    /**
     * 调用AI API
     */
    private AiResponse callAiApi(String systemPrompt, String userPrompt, AiRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            // 构建请求
            OpenAiCompatibleRequest apiRequest = OpenAiCompatibleRequest.createSystemUserRequest(
                    aiConfig.getApi().getModel(),
                    systemPrompt,
                    userPrompt,
                    request.getMaxTokens() != null ? request.getMaxTokens() : aiConfig.getApi().getMaxTokens(),
                    request.getTemperature() != null ? request.getTemperature() : aiConfig.getApi().getTemperature());

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiConfig.getApi().getApiKey());

            HttpEntity<OpenAiCompatibleRequest> requestEntity = new HttpEntity<>(apiRequest, headers);

            log.info("调用AI API，模型：{}，用户：{}", apiRequest.getModel(), request.getUserId());

            // 发送请求
            ResponseEntity<OpenAiCompatibleResponse> response = restTemplate.exchange(
                    aiConfig.getApi().getBaseUrl(),
                    HttpMethod.POST,
                    requestEntity,
                    OpenAiCompatibleResponse.class);

            // 处理响应
            OpenAiCompatibleResponse apiResponse = response.getBody();
            if (apiResponse == null || !apiResponse.isSuccessful()) {
                throw new CustomException("AI API返回异常响应");
            }

            String content = apiResponse.getFirstChoiceContent();
            if (!StringUtils.hasText(content)) {
                throw new CustomException("AI生成内容为空");
            }

            // 内容过滤
            if (aiConfig.getFeatures().getContentFilter() && containsInappropriateContent(content)) {
                throw new CustomException("生成内容包含不适当信息，请修改提示词后重试");
            }

            long duration = System.currentTimeMillis() - startTime;

            log.info("AI生成完成，用户：{}，耗时：{}ms，token使用：{}",
                    request.getUserId(), duration,
                    apiResponse.getUsage() != null ? apiResponse.getUsage().getTotalTokens() : "未知");

            return AiResponse.success(
                    content.trim(),
                    apiResponse.getModel(),
                    request.getType(),
                    duration,
                    apiResponse.toTokenUsage());

        } catch (ResourceAccessException e) {
            log.error("AI API网络异常，用户：{}，错误：{}", request.getUserId(), e.getMessage());
            throw new CustomException("AI服务连接超时，请稍后重试");
        } catch (Exception e) {
            log.error("AI API调用失败，用户：{}，错误：{}", request.getUserId(), e.getMessage(), e);

            if (e instanceof CustomException) {
                throw e;
            }

            // 根据错误信息判断具体原因
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
     * 简单的内容过滤
     */
    private boolean containsInappropriateContent(String content) {
        if (!StringUtils.hasText(content)) {
            return false;
        }

        // 这里可以实现更复杂的内容过滤逻辑
        // 目前只做基本的敏感词检查
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

    @Override
    public ProductAnalysisDTO analyzeProductImages(
            List<String> imageUrls,
            String additionalInfo,
            Long userId,
            String language) {

        log.info("开始AI商品分析，用户ID：{}，图片数量：{}", userId, imageUrls.size());

        if (!isAvailable()) {
            throw new CustomException("AI服务当前不可用");
        }

        if (!checkRateLimit(userId)) {
            throw new CustomException("请求过于频繁，请稍后重试");
        }

        if (imageUrls == null || imageUrls.isEmpty()) {
            throw new CustomException("图片URL列表不能为空");
        }

        java.util.List<String> processedImageUrls = optimizeImageUrls(imageUrls);
        log.info("优化后图片数量：{}（原始：{}）", processedImageUrls.size(), imageUrls.size());

        String systemPrompt = buildProductAnalysisSystemPrompt(language);
        String userPrompt = buildProductAnalysisUserPrompt(additionalInfo, language);

        long startTime = System.currentTimeMillis();

        try {
            OpenAiCompatibleRequest apiRequest = buildMultimodalRequest(
                    systemPrompt, userPrompt, processedImageUrls);

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

    private OpenAiCompatibleRequest buildMultimodalRequest(
            String systemPrompt, String userPrompt, java.util.List<String> imageUrls) {

        java.util.List<OpenAiCompatibleRequest.Message> messages = new java.util.ArrayList<>();

        messages.add(new OpenAiCompatibleRequest.Message("system", systemPrompt));

        java.util.List<Object> userContent = new java.util.ArrayList<>();
        userContent.add(java.util.Map.of("type", "text", "text", userPrompt));

        for (String imageUrl : imageUrls) {
            String fullUrl = convertToFullUrl(imageUrl);
            log.debug("转换图片URL: {} -> {}", imageUrl, fullUrl);
            userContent.add(java.util.Map.of(
                    "type", "image_url",
                    "image_url", java.util.Map.of("url", fullUrl)));
        }

        OpenAiCompatibleRequest.Message userMessage = new OpenAiCompatibleRequest.Message();
        userMessage.setRole("user");
        userMessage.setContentList(userContent);
        messages.add(userMessage);

        OpenAiCompatibleRequest request = new OpenAiCompatibleRequest();
        request.setModel(aiConfig.getApi().getModel());
        request.setMessages(messages);
        request.setMaxTokens(aiConfig.getApi().getMaxTokens());
        request.setTemperature(aiConfig.getApi().getTemperature());

        return request;
    }

    private com.echoofmemories.project.dto.ProductAnalysisDTO parseProductAnalysisResult(String content) {
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
            return mapper.readValue(content, com.echoofmemories.project.dto.ProductAnalysisDTO.class);

        } catch (Exception e) {
            log.error("解析AI返回结果失败：{}", e.getMessage());
            throw new CustomException("AI返回格式错误，请重试");
        }
    }

    @Override
    public com.echoofmemories.project.dto.AiIntelligentTrustResponse intelligentTrust(
            com.echoofmemories.project.dto.AiIntelligentTrustRequest request,
            Long userId) {
        log.info("智能托管服务 - 用户ID: {}, 商品ID: {}", userId, request.getProductId());
        
        if (!isAvailable()) {
            throw new CustomException("AI服务当前不可用");
        }

        if (!checkRateLimit(userId)) {
            throw new CustomException("请求过于频繁，请稍后重试");
        }
        
        try {
            String systemPrompt = "你是一个专业的二手交易智能托管助手。你需要为用户配置智能托管服务，包括自动回复、智能议价和动态调价等功能。" +
                    "请根据用户选择的配置生成详细的托管策略，并返回JSON格式的配置结果。";
            
            StringBuilder userPrompt = new StringBuilder();
            userPrompt.append("用户请求开启智能托管服务，配置如下：\n");
            userPrompt.append("- 商品ID: ").append(request.getProductId()).append("\n");
            userPrompt.append("- 自动回复: ").append(request.getEnableAutoReply() != null && request.getEnableAutoReply() ? "开启" : "关闭").append("\n");
            userPrompt.append("- 智能议价: ").append(request.getEnableAutoNegotiate() != null && request.getEnableAutoNegotiate() ? "开启" : "关闭").append("\n");
            userPrompt.append("- 动态调价: ").append(request.getEnableDynamicPricing() != null && request.getEnableDynamicPricing() ? "开启" : "关闭").append("\n");
            if (request.getMinPrice() != null) {
                userPrompt.append("- 最低价格: ").append(request.getMinPrice()).append("\n");
            }
            if (request.getMaxPrice() != null) {
                userPrompt.append("- 最高价格: ").append(request.getMaxPrice()).append("\n");
            }
            userPrompt.append("\n请返回一个JSON配置，包含：enabled(是否启用), autoReplyCount(自动回复次数), negotiationCount(议价次数), priceAdjustmentCount(调价次数), status(状态), lastActivity(最后活动时间)。");
            
            AiRequest aiRequest = new AiRequest();
            aiRequest.setPrompt(userPrompt.toString());
            aiRequest.setType(AiRequest.AiGenerateType.GENERAL_CONTENT);
            aiRequest.setUserId(userId);
            
            AiResponse aiResponse = callAiApi(systemPrompt, userPrompt.toString(), aiRequest);
            String content = aiResponse.getContent();
            
            com.echoofmemories.project.dto.AiIntelligentTrustResponse response = 
                parseIntelligentTrustResponse(content);
            response.setEnabled(true);
            response.setLastActivity(java.time.LocalDateTime.now().toString());
            
            return response;
            
        } catch (Exception e) {
            log.error("智能托管服务失败：{}", e.getMessage(), e);
            throw new CustomException("智能托管服务失败：" + e.getMessage());
        }
    }
    
    private com.echoofmemories.project.dto.AiIntelligentTrustResponse parseIntelligentTrustResponse(String content) {
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
            com.echoofmemories.project.dto.AiIntelligentTrustResponse response = 
                mapper.readValue(content, com.echoofmemories.project.dto.AiIntelligentTrustResponse.class);
            return response;
        } catch (Exception e) {
            log.warn("解析智能托管响应失败，使用默认值：{}", e.getMessage());
            com.echoofmemories.project.dto.AiIntelligentTrustResponse response = 
                new com.echoofmemories.project.dto.AiIntelligentTrustResponse();
            response.setEnabled(true);
            response.setAutoReplyCount(0);
            response.setNegotiationCount(0);
            response.setPriceAdjustmentCount(0);
            response.setStatus("active");
            response.setLastActivity(java.time.LocalDateTime.now().toString());
            return response;
        }
    }

    @Override
    public com.echoofmemories.project.dto.AiAuthenticationResponse authenticateProduct(
            com.echoofmemories.project.dto.AiAuthenticationRequest request,
            Long userId) {
        log.info("鉴定质检服务 - 用户ID: {}, 图片数量: {}", userId, request.getImageUrls() != null ? request.getImageUrls().size() : 0);
        
        if (!isAvailable()) {
            throw new CustomException("AI服务当前不可用");
        }

        if (!checkRateLimit(userId)) {
            throw new CustomException("请求过于频繁，请稍后重试");
        }
        
        if (request.getImageUrls() == null || request.getImageUrls().isEmpty()) {
            throw new CustomException("图片URL列表不能为空");
        }
        
        try {
            // 处理图片URLs
            java.util.List<String> processedImageUrls = optimizeImageUrls(request.getImageUrls());
            log.info("优化后图片数量：{}（原始：{}）", processedImageUrls.size(), request.getImageUrls().size());
            
            String systemPrompt = buildAuthenticationSystemPrompt();
            String userPrompt = buildAuthenticationUserPrompt(request);
            
            // 使用多模态API调用
            OpenAiCompatibleRequest apiRequest = buildMultimodalRequest(systemPrompt, userPrompt, processedImageUrls);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(aiConfig.getApi().getApiKey());
            
            HttpEntity<OpenAiCompatibleRequest> requestEntity = new HttpEntity<>(apiRequest, headers);
            
            log.info("调用AI鉴定API，图片数量：{}", processedImageUrls.size());
            
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
            
            log.info("AI鉴定结果长度：{}", content.length());
            
            com.echoofmemories.project.dto.AiAuthenticationResponse result = 
                parseAuthenticationResponse(content);
            result.setReportTime(java.time.LocalDateTime.now());
            result.setReportId("AUTH-" + System.currentTimeMillis());
            
            return result;
            
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("鉴定质检服务失败：{}", e.getMessage(), e);
            throw new CustomException("鉴定质检服务失败：" + e.getMessage());
        }
    }
    
    private String buildAuthenticationSystemPrompt() {
        return "你是一个专业的二手商品鉴定专家。你需要根据用户上传的商品图片进行详细分析，" +
                "识别商品的类别、品牌、型号，评估商品的真伪和成色，并给出专业的鉴定报告。" +
                "请严格按照以下JSON格式返回结果（只返回JSON，不要包含任何其他文字）：\n" +
                "{\n" +
                "  \"isAuthentic\": true或false，表示商品是否为正品,\n" +
                "  \"authenticityScore\": 0.0到1.0之间的数字，表示真伪可信度,\n" +
                "  \"conditionGrade\": \"成色等级，如A+、A、B+等\",\n" +
                "  \"conditionScore\": 1到10之间的数字，表示成色评分,\n" +
                "  \"conditionDetails\": [\"成色细节描述1\", \"成色细节描述2\", ...],\n" +
                "  \"reportSummary\": \"鉴定报告摘要，一段话总结鉴定结果\",\n" +
                "  \"recommendations\": [\"建议1\", \"建议2\", ...]\n" +
                "}\n" +
                "注意：\n" +
                "1. 成色评分1-10分，10分为全新\n" +
                "2. 真伪判断要客观准确，不要轻易判定为假货\n" +
                "3. 建议要实用，有助于用户更好地出售商品";
    }
    
    private String buildAuthenticationUserPrompt(com.echoofmemories.project.dto.AiAuthenticationRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据商品图片进行真伪鉴定和成色评估。\n");
        
        if (StringUtils.hasText(request.getProductName())) {
            prompt.append("商品名称：").append(request.getProductName()).append("\n");
        }
        if (StringUtils.hasText(request.getBrand())) {
            prompt.append("品牌：").append(request.getBrand()).append("\n");
        }
        if (StringUtils.hasText(request.getModel())) {
            prompt.append("型号：").append(request.getModel()).append("\n");
        }
        if (StringUtils.hasText(request.getCategory())) {
            prompt.append("分类：").append(request.getCategory()).append("\n");
        }
        if (StringUtils.hasText(request.getAdditionalInfo())) {
            prompt.append("补充信息：").append(request.getAdditionalInfo()).append("\n");
        }
        
        prompt.append("\n请分析商品图片，给出专业的鉴定结果。");
        
        return prompt.toString();
    }
    
    private com.echoofmemories.project.dto.AiAuthenticationResponse parseAuthenticationResponse(String content) {
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
            com.echoofmemories.project.dto.AiAuthenticationResponse response = 
                mapper.readValue(content, com.echoofmemories.project.dto.AiAuthenticationResponse.class);
            return response;
        } catch (Exception e) {
            log.error("解析鉴定响应失败：{}", e.getMessage());
            throw new CustomException("AI返回格式错误，请重试");
        }
    }

    @Override
    public com.echoofmemories.project.dto.AiMarketTrendResponse getMarketTrend(
            com.echoofmemories.project.dto.AiMarketTrendRequest request,
            Long userId) {
        log.info("市场行情参考 - 用户ID: {}, 品类: {}", userId, request.getCategory());
        
        if (!isAvailable()) {
            throw new CustomException("AI服务当前不可用");
        }

        if (!checkRateLimit(userId)) {
            throw new CustomException("请求过于频繁，请稍后重试");
        }
        
        try {
            String systemPrompt = "你是一个专业的二手市场价格分析专家。你需要根据用户提供的商品信息，" +
                    "分析市场行情、价格趋势，并给出定价建议。请根据当前市场需求和供给情况给出专业的分析。" +
                    "请严格按照以下JSON格式返回结果（只返回JSON，不要包含任何其他文字）：\n" +
                    "{\n" +
                    "  \"averagePrice\": 平均价格（数字）,\n" +
                    "  \"lowestPrice\": 最低价格（数字）,\n" +
                    "  \"highestPrice\": 最高价格（数字）,\n" +
                    "  \"totalSold\": 总销量（整数）,\n" +
                    "  \"priceTrend\": [{\"date\": \"日期\", \"price\": 价格}, ...]（最近7天的价格趋势）,\n" +
                    "  \"similarProducts\": [{\"name\": \"商品名称\", \"price\": 价格, \"condition\": 成色分数}, ...]（5个相似商品参考）,\n" +
                    "  \"recommendation\": \"综合建议，一段话\",\n" +
                    "  \"priceSuggestion\": \"定价建议\",\n" +
                    "  \"marketOutlook\": \"市场展望\"\n" +
                    "}";
            
            StringBuilder userPrompt = new StringBuilder();
            userPrompt.append("请分析以下商品的市场行情：\n");
            if (StringUtils.hasText(request.getCategory())) {
                userPrompt.append("商品分类：").append(request.getCategory()).append("\n");
            }
            if (StringUtils.hasText(request.getBrand())) {
                userPrompt.append("品牌：").append(request.getBrand()).append("\n");
            }
            if (StringUtils.hasText(request.getModel())) {
                userPrompt.append("型号：").append(request.getModel()).append("\n");
            }
            if (StringUtils.hasText(request.getProductName())) {
                userPrompt.append("商品名称：").append(request.getProductName()).append("\n");
            }
            userPrompt.append("分析时间范围：最近").append(request.getDays() != null ? request.getDays() : 7).append("天\n");
            
            AiRequest aiRequest = new AiRequest();
            aiRequest.setPrompt(userPrompt.toString());
            aiRequest.setType(AiRequest.AiGenerateType.GENERAL_CONTENT);
            aiRequest.setUserId(userId);
            aiRequest.setMaxTokens(2000);
            
            AiResponse aiResponse = callAiApi(systemPrompt, userPrompt.toString(), aiRequest);
            String content = aiResponse.getContent();
            
            log.info("AI市场行情分析结果长度：{}", content.length());
            
            com.echoofmemories.project.dto.AiMarketTrendResponse response = 
                parseMarketTrendResponse(content);
            
            return response;
            
        } catch (Exception e) {
            log.error("获取市场行情失败：{}", e.getMessage(), e);
            throw new CustomException("获取市场行情失败：" + e.getMessage());
        }
    }
    
    private com.echoofmemories.project.dto.AiMarketTrendResponse parseMarketTrendResponse(String content) {
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
            return mapper.readValue(content, com.echoofmemories.project.dto.AiMarketTrendResponse.class);
        } catch (Exception e) {
            log.error("解析市场行情响应失败：{}", e.getMessage());
            throw new CustomException("AI返回格式错误，请重试");
        }
    }

    @Override
    public com.echoofmemories.project.dto.AiSmartSearchResponse smartSearch(
            com.echoofmemories.project.dto.AiSmartSearchRequest request,
            Long userId) {
        log.info("智能搜索 - 用户ID: {}, 查询: {}", userId, request.getQuery());
        
        if (!isAvailable()) {
            throw new CustomException("AI服务当前不可用");
        }

        if (!checkRateLimit(userId)) {
            throw new CustomException("请求过于频繁，请稍后重试");
        }
        
        try {
            String systemPrompt = "你是一个专业的二手商品智能搜索助手。你需要理解用户的搜索意图，" +
                    "对搜索词进行智能解析和扩展，生成更精准的搜索建议。请严格按照以下JSON格式返回结果（只返回JSON）：\n" +
                    "{\n" +
                    "  \"interpretedQuery\": \"理解后的搜索查询\",\n" +
                    "  \"suggestedTags\": [\"标签1\", \"标签2\", ...],\n" +
                    "  \"searchTip\": \"搜索提示语\",\n" +
                    "  \"totalCount\": 预估结果数量（整数）\n" +
                    "}";
            
            StringBuilder userPrompt = new StringBuilder();
            userPrompt.append("用户发起智能搜索：\n");
            if (StringUtils.hasText(request.getQuery())) {
                userPrompt.append("搜索词：").append(request.getQuery()).append("\n");
            }
            if (StringUtils.hasText(request.getVoiceQuery())) {
                userPrompt.append("语音搜索：").append(request.getVoiceQuery()).append("\n");
            }
            if (StringUtils.hasText(request.getCategory())) {
                userPrompt.append("分类：").append(request.getCategory()).append("\n");
            }
            if (request.getMinPrice() != null) {
                userPrompt.append("最低价格：").append(request.getMinPrice()).append("\n");
            }
            if (request.getMaxPrice() != null) {
                userPrompt.append("最高价格：").append(request.getMaxPrice()).append("\n");
            }
            if (request.getConditionMin() != null) {
                userPrompt.append("最低成色：").append(request.getConditionMin()).append("\n");
            }
            
            AiRequest aiRequest = new AiRequest();
            aiRequest.setPrompt(userPrompt.toString());
            aiRequest.setType(AiRequest.AiGenerateType.GENERAL_CONTENT);
            aiRequest.setUserId(userId);
            aiRequest.setMaxTokens(500);
            
            AiResponse aiResponse = callAiApi(systemPrompt, userPrompt.toString(), aiRequest);
            String content = aiResponse.getContent();
            
            log.info("AI智能搜索结果：{}", content);
            
            com.echoofmemories.project.dto.AiSmartSearchResponse response = 
                parseSmartSearchResponse(content);
            
            return response;
            
        } catch (Exception e) {
            log.error("智能搜索失败：{}", e.getMessage(), e);
            throw new CustomException("智能搜索失败：" + e.getMessage());
        }
    }
    
    private com.echoofmemories.project.dto.AiSmartSearchResponse parseSmartSearchResponse(String content) {
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
            return mapper.readValue(content, com.echoofmemories.project.dto.AiSmartSearchResponse.class);
        } catch (Exception e) {
            log.error("解析智能搜索响应失败：{}", e.getMessage());
            com.echoofmemories.project.dto.AiSmartSearchResponse response = 
                new com.echoofmemories.project.dto.AiSmartSearchResponse();
            response.setInterpretedQuery("");
            response.setSuggestedTags(java.util.Arrays.asList("二手", "校园"));
            response.setSearchTip("请尝试更具体的搜索词");
            response.setTotalCount(0);
            return response;
        }
    }

    @Override
    public com.echoofmemories.project.dto.AiCampusMatchResponse campusMatch(
            com.echoofmemories.project.dto.AiCampusMatchRequest request,
            Long userId) {
        log.info("校园匹配 - 用户ID: {}, 商品ID: {}", userId, request.getProductId());
        
        if (!isAvailable()) {
            throw new CustomException("AI服务当前不可用");
        }

        if (!checkRateLimit(userId)) {
            throw new CustomException("请求过于频繁，请稍后重试");
        }
        
        try {
            String systemPrompt = "你是一个专业的校园交易匹配助手。你需要根据商品信息，智能匹配校内和校外的潜在买家或卖家，" +
                    "并给出交易建议和安全提示。请严格按照以下JSON格式返回结果（只返回JSON）：\n" +
                    "{\n" +
                    "  \"matchedBuyers\": [{\"name\": \"买家昵称\", \"school\": \"学校\", \"distance\": 距离, \"matchScore\": 匹配度}, ...],\n" +
                    "  \"matchedSellers\": [{\"name\": \"卖家昵称\", \"school\": \"学校\", \"distance\": 距离, \"matchScore\": 匹配度}, ...],\n" +
                    "  \"meetupSuggestions\": \"见面交易建议\",\n" +
                    "  \"safetyTips\": \"安全交易提示\"\n" +
                    "}";
            
            StringBuilder userPrompt = new StringBuilder();
            userPrompt.append("请求校园匹配服务：\n");
            userPrompt.append("商品ID: ").append(request.getProductId()).append("\n");
            userPrompt.append("仅限校内: ").append(request.getSchoolOnly() != null && request.getSchoolOnly() ? "是" : "否").append("\n");
            if (request.getDistanceLimit() != null) {
                userPrompt.append("距离限制: ").append(request.getDistanceLimit()).append("公里\n");
            }
            userPrompt.append("专业相关: ").append(request.getMajorRelevant() != null && request.getMajorRelevant() ? "是" : "否").append("\n");
            
            AiRequest aiRequest = new AiRequest();
            aiRequest.setPrompt(userPrompt.toString());
            aiRequest.setType(AiRequest.AiGenerateType.GENERAL_CONTENT);
            aiRequest.setUserId(userId);
            aiRequest.setMaxTokens(1000);
            
            AiResponse aiResponse = callAiApi(systemPrompt, userPrompt.toString(), aiRequest);
            String content = aiResponse.getContent();
            
            log.info("AI校园匹配结果：{}", content);
            
            com.echoofmemories.project.dto.AiCampusMatchResponse response = 
                parseCampusMatchResponse(content);
            
            return response;
            
        } catch (Exception e) {
            log.error("校园匹配失败：{}", e.getMessage(), e);
            throw new CustomException("校园匹配失败：" + e.getMessage());
        }
    }
    
    private com.echoofmemories.project.dto.AiCampusMatchResponse parseCampusMatchResponse(String content) {
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
            return mapper.readValue(content, com.echoofmemories.project.dto.AiCampusMatchResponse.class);
        } catch (Exception e) {
            log.error("解析校园匹配响应失败：{}", e.getMessage());
            com.echoofmemories.project.dto.AiCampusMatchResponse response = 
                new com.echoofmemories.project.dto.AiCampusMatchResponse();
            response.setMeetupSuggestions("建议在学校公共场所见面交易");
            response.setSafetyTips("选择公共场所见面、验证商品后再付款");
            return response;
        }
    }

    @Override
    public com.echoofmemories.project.dto.AiDisputeResolutionResponse resolveDispute(
            com.echoofmemories.project.dto.AiDisputeResolutionRequest request,
            Long userId) {
        log.info("纠纷仲裁 - 用户ID: {}, 订单ID: {}", userId, request.getOrderId());
        
        if (!isAvailable()) {
            throw new CustomException("AI服务当前不可用");
        }

        if (!checkRateLimit(userId)) {
            throw new CustomException("请求过于频繁，请稍后重试");
        }
        
        try {
            String systemPrompt = "你是一个专业的交易纠纷仲裁助手。你需要根据订单信息和双方提供的证据，" +
                    "客观分析纠纷原因，给出公正的仲裁建议。请严格按照以下JSON格式返回结果（只返回JSON）：\n" +
                    "{\n" +
                    "  \"caseId\": \"案例ID\",\n" +
                    "  \"suggestedResolution\": \"建议解决方案\",\n" +
                    "  \"refundSuggestion\": 建议退款百分比（数字）,\n" +
                    "  \"keyFindings\": [\"关键发现1\", \"关键发现2\", ...],\n" +
                    "  \"riskLevel\": \"风险等级（low/medium/high）\",\n" +
                    "  \"nextSteps\": \"后续步骤建议\",\n" +
                    "  \"confidenceScore\": 置信度分数（0-100整数）\n" +
                    "}";
            
            StringBuilder userPrompt = new StringBuilder();
            userPrompt.append("请求纠纷仲裁服务：\n");
            userPrompt.append("订单ID: ").append(request.getOrderId()).append("\n");
            if (StringUtils.hasText(request.getDisputeDescription())) {
                userPrompt.append("纠纷描述：").append(request.getDisputeDescription()).append("\n");
            }
            if (request.getEvidenceUrls() != null && !request.getEvidenceUrls().isEmpty()) {
                userPrompt.append("证据URLs：").append(String.join(", ", request.getEvidenceUrls())).append("\n");
            }
            
            AiRequest aiRequest = new AiRequest();
            aiRequest.setPrompt(userPrompt.toString());
            aiRequest.setType(AiRequest.AiGenerateType.GENERAL_CONTENT);
            aiRequest.setUserId(userId);
            aiRequest.setMaxTokens(800);
            
            AiResponse aiResponse = callAiApi(systemPrompt, userPrompt.toString(), aiRequest);
            String content = aiResponse.getContent();
            
            log.info("AI纠纷仲裁结果：{}", content);
            
            com.echoofmemories.project.dto.AiDisputeResolutionResponse response = 
                parseDisputeResolutionResponse(content);
            response.setCaseId("DISPUTE-" + System.currentTimeMillis());
            
            return response;
            
        } catch (Exception e) {
            log.error("纠纷仲裁失败：{}", e.getMessage(), e);
            throw new CustomException("纠纷仲裁失败：" + e.getMessage());
        }
    }
    
    private com.echoofmemories.project.dto.AiDisputeResolutionResponse parseDisputeResolutionResponse(String content) {
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
            return mapper.readValue(content, com.echoofmemories.project.dto.AiDisputeResolutionResponse.class);
        } catch (Exception e) {
            log.error("解析纠纷仲裁响应失败：{}", e.getMessage());
            com.echoofmemories.project.dto.AiDisputeResolutionResponse response = 
                new com.echoofmemories.project.dto.AiDisputeResolutionResponse();
            response.setCaseId("DISPUTE-" + System.currentTimeMillis());
            response.setSuggestedResolution("建议双方协商解决");
            response.setRiskLevel("medium");
            return response;
        }
    }

    @Override
    public com.echoofmemories.project.dto.AiCampusServiceResponse campusService(
            com.echoofmemories.project.dto.AiCampusServiceRequest request,
            Long userId) {
        log.info("校园专属服务 - 用户ID: {}, 类型: {}", userId, request.getServiceType());
        
        if (!isAvailable()) {
            throw new CustomException("AI服务当前不可用");
        }

        if (!checkRateLimit(userId)) {
            throw new CustomException("请求过于频繁，请稍后重试");
        }
        
        try {
            String systemPrompt = "你是一个专业的校园服务匹配助手。你需要根据用户的需求类型，" +
                    "匹配校园内的服务资源，包括教材循环、闲置置换、跑腿服务等。请严格按照以下JSON格式返回结果（只返回JSON）：\n" +
                    "{\n" +
                    "  \"serviceType\": \"服务类型\",\n" +
                    "  \"matches\": [{\"title\": \"服务标题\", \"price\": 价格, \"rating\": 评分}, ...],\n" +
                    "  \"suggestion\": \"服务建议\",\n" +
                    "  \"tips\": [\"提示1\", \"提示2\", ...],\n" +
                    "  \"priceEstimate\": 预估价格（数字）,\n" +
                    "  \"priceCurrency\": \"货币单位（CNY/USD等）\"\n" +
                    "}";
            
            StringBuilder userPrompt = new StringBuilder();
            userPrompt.append("请求校园专属服务：\n");
            if (StringUtils.hasText(request.getServiceType())) {
                userPrompt.append("服务类型：").append(request.getServiceType()).append("\n");
            }
            if (StringUtils.hasText(request.getRequest())) {
                userPrompt.append("服务需求：").append(request.getRequest()).append("\n");
            }
            if (StringUtils.hasText(request.getAdditionalInfo())) {
                userPrompt.append("补充信息：").append(request.getAdditionalInfo()).append("\n");
            }
            
            AiRequest aiRequest = new AiRequest();
            aiRequest.setPrompt(userPrompt.toString());
            aiRequest.setType(AiRequest.AiGenerateType.GENERAL_CONTENT);
            aiRequest.setUserId(userId);
            aiRequest.setMaxTokens(800);
            
            AiResponse aiResponse = callAiApi(systemPrompt, userPrompt.toString(), aiRequest);
            String content = aiResponse.getContent();
            
            log.info("AI校园服务结果：{}", content);
            
            com.echoofmemories.project.dto.AiCampusServiceResponse response = 
                parseCampusServiceResponse(content);
            response.setServiceType(request.getServiceType());
            
            return response;
            
        } catch (Exception e) {
            log.error("校园专属服务失败：{}", e.getMessage(), e);
            throw new CustomException("校园专属服务失败：" + e.getMessage());
        }
    }
    
    private com.echoofmemories.project.dto.AiCampusServiceResponse parseCampusServiceResponse(String content) {
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
            return mapper.readValue(content, com.echoofmemories.project.dto.AiCampusServiceResponse.class);
        } catch (Exception e) {
            log.error("解析校园服务响应失败：{}", e.getMessage());
            com.echoofmemories.project.dto.AiCampusServiceResponse response = 
                new com.echoofmemories.project.dto.AiCampusServiceResponse();
            response.setSuggestion("请稍后再试");
            response.setPriceCurrency("CNY");
            return response;
        }
    }
    
    @Override
    public com.echoofmemories.project.dto.AiRecommendationResponse getRecommendations(
            com.echoofmemories.project.dto.AiRecommendationRequest request,
            Long userId) {
        
        long startTime = System.currentTimeMillis();
        com.echoofmemories.project.dto.AiRecommendationResponse response = 
                new com.echoofmemories.project.dto.AiRecommendationResponse();
        
        try {
            log.info("AI推荐 - 用户ID: {}, 类型: {}", userId, request.getType());
            
            // 获取用户数据
            java.util.List<com.echoofmemories.project.entity.BrowseHistory> browseHistories = 
                    getRecentBrowseHistory(userId, 30);
            java.util.List<com.echoofmemories.project.entity.SearchHistory> searchHistories = 
                    searchHistoryService.getVisibleHistory(userId, 20);
            
            // 提取用户兴趣
            java.util.Set<String> userCategories = new java.util.HashSet<>();
            java.util.Set<String> userKeywords = new java.util.HashSet<>();
            java.util.Set<Long> viewedProductIds = new java.util.HashSet<>();
            
            for (com.echoofmemories.project.entity.BrowseHistory bh : browseHistories) {
                if (bh.getProduct() != null) {
                    userCategories.add(bh.getProduct().getCategory());
                    viewedProductIds.add(bh.getProductId());
                }
            }
            
            for (com.echoofmemories.project.entity.SearchHistory sh : searchHistories) {
                userKeywords.add(sh.getKeyword());
            }
            
            // 获取候选商品
            java.util.List<com.echoofmemories.project.entity.Product> candidates = 
                    getCandidateProducts(userId, userCategories, viewedProductIds, request.getLimit() + 20);
            
            // 对商品进行评分和排序
            java.util.List<com.echoofmemories.project.dto.AiRecommendationResponse.ProductWithReason> scoredProducts = 
                    scoreAndRankProducts(candidates, userCategories, userKeywords, browseHistories, request);
            
            // 限制数量
            if (scoredProducts.size() > request.getLimit()) {
                scoredProducts = scoredProducts.subList(0, request.getLimit());
            }
            
            // 构建响应
            response.setSuccess(true);
            response.setProducts(scoredProducts);
            response.setInterestTags(new java.util.ArrayList<>(userCategories));
            response.setExplanation(generateExplanation(userCategories, userKeywords));
            response.setResponseTime(System.currentTimeMillis() - startTime);
            
            // 如果AI可用，生成AI推理说明
            if (isAvailable()) {
                try {
                    response.setAiReasoning(generateAiReasoning(userCategories, userKeywords, scoredProducts));
                } catch (Exception e) {
                    log.warn("AI推理生成失败，使用默认说明", e);
                    response.setAiReasoning("基于您的浏览和搜索历史进行个性化推荐");
                }
            } else {
                response.setAiReasoning("基于您的浏览和搜索历史进行个性化推荐");
            }
            
            log.info("AI推荐完成 - 用户ID: {}, 推荐数量: {}, 耗时: {}ms", 
                    userId, scoredProducts.size(), System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            log.error("AI推荐失败 - 用户ID: {}", userId, e);
            response.setSuccess(false);
            response.setExplanation("推荐服务暂时不可用，请稍后重试");
            response.setResponseTime(System.currentTimeMillis() - startTime);
        }
        
        return response;
    }
    
    private java.util.List<com.echoofmemories.project.entity.BrowseHistory> getRecentBrowseHistory(Long userId, int limit) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.echoofmemories.project.entity.BrowseHistory> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(com.echoofmemories.project.entity.BrowseHistory::getUserId, userId)
               .orderByDesc(com.echoofmemories.project.entity.BrowseHistory::getCreateTime)
               .last("LIMIT " + limit);
        return browseHistoryService.list(wrapper);
    }
    
    private java.util.List<com.echoofmemories.project.entity.Product> getCandidateProducts(
            Long userId, 
            java.util.Set<String> categories, 
            java.util.Set<Long> excludeIds,
            int limit) {
        
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.echoofmemories.project.entity.Product> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(com.echoofmemories.project.entity.Product::getStatus, 1)
               .eq(com.echoofmemories.project.entity.Product::getDeleted, 0);
        
        if (!categories.isEmpty()) {
            wrapper.in(com.echoofmemories.project.entity.Product::getCategory, categories);
        }
        
        if (!excludeIds.isEmpty()) {
            wrapper.notIn(com.echoofmemories.project.entity.Product::getId, excludeIds);
        }
        
        if (userId != null) {
            wrapper.ne(com.echoofmemories.project.entity.Product::getUserId, userId);
        }
        
        wrapper.orderByDesc(com.echoofmemories.project.entity.Product::getViewCount)
               .orderByDesc(com.echoofmemories.project.entity.Product::getFavoriteCount)
               .last("LIMIT " + limit);
        
        return productService.list(wrapper);
    }
    
    private java.util.List<com.echoofmemories.project.dto.AiRecommendationResponse.ProductWithReason> scoreAndRankProducts(
            java.util.List<com.echoofmemories.project.entity.Product> products,
            java.util.Set<String> userCategories,
            java.util.Set<String> userKeywords,
            java.util.List<com.echoofmemories.project.entity.BrowseHistory> browseHistories,
            com.echoofmemories.project.dto.AiRecommendationRequest request) {
        
        java.util.List<com.echoofmemories.project.dto.AiRecommendationResponse.ProductWithReason> result = new java.util.ArrayList<>();
        
        for (com.echoofmemories.project.entity.Product product : products) {
            com.echoofmemories.project.dto.AiRecommendationResponse.ProductWithReason item = 
                    new com.echoofmemories.project.dto.AiRecommendationResponse.ProductWithReason();
            item.setProduct(product);
            item.setId(product.getId());
            item.setTitle(product.getTitle());
            item.setDescription(product.getDescription());
            item.setCategory(product.getCategory());
            item.setPrice(product.getPrice());
            item.setImages(product.getImages());
            item.setViewCount(product.getViewCount());
            
            double score = 0.0;
            String reasonType = "hot";
            String reason = "热门推荐";
            
            // 类别匹配
            if (userCategories.contains(product.getCategory())) {
                score += 30.0;
                reasonType = "browse_history";
                reason = "根据您浏览的" + product.getCategory() + "商品推荐";
            }
            
            // 关键词匹配
            for (String keyword : userKeywords) {
                if ((product.getTitle() != null && product.getTitle().contains(keyword)) || 
                    (product.getDescription() != null && product.getDescription().contains(keyword))) {
                    score += 25.0;
                    reasonType = "search";
                    reason = "与您搜索的\"" + keyword + "\"相关";
                    break;
                }
            }
            
            // 热门度评分
            int viewCount = product.getViewCount() != null ? product.getViewCount() : 0;
            int favoriteCount = product.getFavoriteCount() != null ? product.getFavoriteCount() : 0;
            score += Math.min(viewCount * 0.1, 20.0);
            score += Math.min(favoriteCount * 0.5, 15.0);
            
            // 成新度加分
            int conditionScore = product.getConditionScore() != null ? product.getConditionScore() : 5;
            score += conditionScore * 1.0;
            
            // 随机因素（增加多样性）
            score += Math.random() * 10.0;
            
            item.setMatchScore(Math.min(score, 100.0));
            item.setReasonType(reasonType);
            item.setReason(reason);
            
            result.add(item);
        }
        
        // 按分数排序
        result.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        
        return result;
    }
    
    private String generateExplanation(java.util.Set<String> categories, java.util.Set<String> keywords) {
        StringBuilder sb = new StringBuilder();
        sb.append("为您推荐 ");
        
        if (!categories.isEmpty()) {
            sb.append(String.join("、", categories));
            sb.append(" 等类别的商品");
        }
        
        if (!keywords.isEmpty()) {
            sb.append("，包含 ");
            sb.append(String.join("、", keywords.stream().limit(3).collect(java.util.stream.Collectors.toList())));
            sb.append(" 等关键词");
        }
        
        return sb.toString();
    }
    
    private String generateAiReasoning(
            java.util.Set<String> categories, 
            java.util.Set<String> keywords,
            java.util.List<com.echoofmemories.project.dto.AiRecommendationResponse.ProductWithReason> products) {
        
        if (!isAvailable()) {
            return "基于您的浏览和搜索历史进行个性化推荐";
        }
        
        try {
            StringBuilder prompt = new StringBuilder();
            prompt.append("请用中文生成一段简短的推荐说明（不超过100字）。\n");
            prompt.append("用户兴趣类别: ").append(String.join("、", categories)).append("\n");
            prompt.append("用户搜索关键词: ").append(String.join("、", keywords)).append("\n");
            prompt.append("请说明是基于用户的浏览和搜索历史进行推荐。");
            
            com.echoofmemories.project.dto.AiRequest aiRequest = new com.echoofmemories.project.dto.AiRequest();
            aiRequest.setPrompt(prompt.toString());
            aiRequest.setType(com.echoofmemories.project.dto.AiRequest.AiGenerateType.GENERAL_CONTENT);
            aiRequest.setMaxTokens(150);
            
            com.echoofmemories.project.dto.AiResponse aiResponse = generateContent(aiRequest);
            return aiResponse.getContent();
            
        } catch (Exception e) {
            log.warn("AI推理说明生成失败", e);
            return "基于您的浏览和搜索历史进行个性化推荐";
        }
    }
}
