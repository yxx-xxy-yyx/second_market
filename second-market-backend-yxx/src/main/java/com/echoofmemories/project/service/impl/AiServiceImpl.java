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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.echoofmemories.project.service.BrowseHistoryService;
import com.echoofmemories.project.service.SearchHistoryService;
import com.echoofmemories.project.service.ProductService;
import com.echoofmemories.project.service.UserService;
import com.echoofmemories.project.mapper.ProductMapper;
import com.echoofmemories.project.mapper.UserMapper;
import com.echoofmemories.project.mapper.OrdersMapper;
import com.echoofmemories.project.mapper.MessageMapper;
import com.echoofmemories.project.mapper.SchoolMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

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
    private final UserMapper userMapper;
    private final OrdersMapper ordersMapper;
    private final MessageMapper messageMapper;
    private final SchoolMapper schoolMapper;
    private final UserService userService;
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

    private void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new CustomException("图片URL不能为空");
        }
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            throw new CustomException("图片URL不安全：仅支持 http 或 https 协议");
        }
        try {
            java.net.URL url = new java.net.URL(imageUrl);
            String host = url.getHost();
            if (host == null || host.isEmpty()) {
                throw new CustomException("图片URL不安全：无法解析主机");
            }
            String lowerHost = host.toLowerCase();
            if ("localhost".equals(lowerHost) || "127.0.0.1".equals(lowerHost) || "::1".equals(lowerHost)) {
                throw new CustomException("图片URL不安全：禁止访问本地服务");
            }
            if (lowerHost.startsWith("10.") || lowerHost.startsWith("192.168.")
                    || lowerHost.startsWith("127.") || lowerHost.startsWith("169.254.")) {
                throw new CustomException("图片URL不安全：禁止访问内网地址");
            }
            if (lowerHost.matches("^172\\.(1[6-9]|2[0-9]|3[0-1])\\..*")) {
                throw new CustomException("图片URL不安全：禁止访问内网地址");
            }
            java.net.InetAddress inet = java.net.InetAddress.getByName(host);
            if (inet.isLoopbackAddress() || inet.isSiteLocalAddress()) {
                throw new CustomException("图片URL不安全：禁止访问内网地址");
            }
        } catch (CustomException ce) {
            throw ce;
        } catch (Exception e) {
            log.warn("图片URL校验失败: {}, 错误: {}", imageUrl, e.getMessage());
            throw new CustomException("图片URL不安全");
        }
    }

    private String convertToFullUrl(String imageUrl) {
        log.debug("开始转换图片URL: {}", imageUrl);

        if (imageUrl == null || imageUrl.isEmpty()) {
            log.warn("图片URL为空");
            throw new CustomException("图片URL不能为空");
        }

        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            validateImageUrl(imageUrl);
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
        validateImageUrl(imageUrl);

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

        com.echoofmemories.project.dto.AiIntelligentTrustResponse response =
                new com.echoofmemories.project.dto.AiIntelligentTrustResponse();

        Product product = null;
        if (request.getProductId() != null) {
            product = productMapper.selectById(request.getProductId());
        }

        User seller = null;
        if (product != null && product.getUserId() != null) {
            seller = userMapper.selectById(product.getUserId());
        } else if (userId != null) {
            seller = userMapper.selectById(userId);
        }

        int autoReplyCount = 0;
        try {
            if (userId != null) {
                QueryWrapper<com.echoofmemories.project.entity.Message> mw = new QueryWrapper<>();
                mw.eq("receiver_id", userId);
                mw.ge("create_time", LocalDateTime.now().minusDays(7));
                Long c = messageMapper.selectCount(mw);
                autoReplyCount = c == null ? 0 : c.intValue();
            }
        } catch (Exception e) {
            log.warn("消息回复数统计失败: {}", e.getMessage());
            autoReplyCount = 0;
        }

        int negotiationCount = 0;
        try {
            if (userId != null) {
                QueryWrapper<Orders> ow = new QueryWrapper<>();
                ow.eq("buyer_id", userId).eq("deleted", 0);
                Long c = ordersMapper.selectCount(ow);
                negotiationCount = c == null ? 0 : c.intValue();
            }
        } catch (Exception e) {
            log.warn("协商次数统计失败: {}", e.getMessage());
        }

        int priceAdjustmentCount = 0;
        try {
            if (product != null) {
                int vc = product.getViewCount() == null ? 0 : product.getViewCount();
                priceAdjustmentCount = vc > 50 ? Math.min(10, vc / 20) : 0;
            }
        } catch (Exception e) {
            priceAdjustmentCount = 0;
        }

        String status = "limited";
        boolean enabled = false;
        if (seller != null && seller.getCreditScore() != null && seller.getCreditScore() >= 90) {
            status = "active";
            enabled = true;
        } else if (seller != null && seller.getCreditScore() != null && seller.getCreditScore() >= 60) {
            status = "normal";
            enabled = true;
        }

        String lastActivity = LocalDateTime.now().toString();
        try {
            if (userId != null) {
                QueryWrapper<Orders> ow = new QueryWrapper<>();
                ow.in("buyer_id", userId).or(w -> w.in("seller_id", userId));
                ow.eq("deleted", 0);
                ow.orderByDesc("create_time");
                ow.last("LIMIT 1");
                Orders last = ordersMapper.selectOne(ow);
                if (last != null && last.getCreateTime() != null) {
                    lastActivity = last.getCreateTime().toString();
                }
            }
        } catch (Exception e) {
            log.warn("最近活动时间查询失败: {}", e.getMessage());
        }

        response.setEnabled(enabled);
        response.setAutoReplyCount(autoReplyCount);
        response.setNegotiationCount(negotiationCount);
        response.setPriceAdjustmentCount(priceAdjustmentCount);
        response.setStatus(status);
        response.setLastActivity(lastActivity);

        log.info("智能托管结果 - productId={}, enabled={}, status={}, autoReply={}, negotiation={}, priceAdj={}",
                request.getProductId(), enabled, status, autoReplyCount, negotiationCount, priceAdjustmentCount);

        return response;
    }

    @Override
    public com.echoofmemories.project.dto.AiAuthenticationResponse authenticateProduct(
            com.echoofmemories.project.dto.AiAuthenticationRequest request,
            Long userId) {
        log.info("鉴定质检服务 - 用户ID: {}", userId);

        com.echoofmemories.project.dto.AiAuthenticationResponse response =
                new com.echoofmemories.project.dto.AiAuthenticationResponse();

        Long productId = request.getProductId();
        Product product = null;
        if (productId != null) {
            product = productMapper.selectById(productId);
        }

        if (product == null) {
            response.setIsAuthentic(false);
            response.setAuthenticityScore(0.3);
            response.setConditionGrade("C");
            response.setConditionScore(0.0);
            response.setConditionDetails(java.util.Collections.emptyList());
            response.setReportSummary("未查询到商品信息，无法完成鉴定");
            response.setRecommendations(java.util.Arrays.asList("请确认商品ID是否正确", "建议补充商品详情与实拍图"));
            response.setReportTime(LocalDateTime.now());
            response.setReportId("AUTH-" + System.currentTimeMillis());
            log.info("鉴定结果 - 未查询到商品, productId={}", productId);
            return response;
        }

        String images = product.getImages();
        int imageCount = 0;
        if (images != null && !images.isEmpty()) {
            String[] parts = images.split(",");
            for (String p : parts) {
                if (p != null && !p.trim().isEmpty()) imageCount++;
            }
        }

        String desc = product.getDescription();
        int descLen = desc == null ? 0 : desc.length();

        Integer aiAnalyzed = product.getAiAnalyzed();
        String aiSuggestions = product.getAiSuggestions();

        Integer condScore = product.getConditionScore();
        int condInt = condScore == null ? 0 : condScore;

        double authenticityScore = 0.35;
        if (imageCount > 2) authenticityScore += 0.15;
        if (descLen > 100) authenticityScore += 0.10;
        if (aiAnalyzed != null && aiAnalyzed == 1 && aiSuggestions != null && !aiSuggestions.isEmpty()) {
            authenticityScore += 0.20;
        }
        if (condInt >= 8) authenticityScore += 0.15;

        User seller = null;
        if (product.getUserId() != null) {
            seller = userMapper.selectById(product.getUserId());
        }
        if (seller != null && seller.getCreditScore() != null && seller.getCreditScore() >= 90) {
            authenticityScore += 0.15;
        }
        if (authenticityScore > 1.0) authenticityScore = 1.0;

        String grade;
        if (authenticityScore >= 0.85) grade = "A+";
        else if (authenticityScore >= 0.7) grade = "A";
        else if (authenticityScore >= 0.55) grade = "B";
        else grade = "C";

        boolean isAuthentic = authenticityScore >= 0.55;

        java.util.List<String> conditionDetails = new java.util.ArrayList<>();
        if (condInt > 0) {
            conditionDetails.add("成色评分：" + condInt + "/10");
        }
        if (product.getConditionDesc() != null && !product.getConditionDesc().isEmpty()) {
            conditionDetails.add("成色描述：" + product.getConditionDesc());
        }
        if (imageCount > 0) {
            conditionDetails.add("含实拍图片 " + imageCount + " 张");
        }
        if (descLen > 100) {
            conditionDetails.add("商品描述详细（" + descLen + "字）");
        }
        if (aiAnalyzed != null && aiAnalyzed == 1) {
            conditionDetails.add("已通过AI智能分析");
        }
        if (seller != null && seller.getCreditScore() != null) {
            conditionDetails.add("卖家信用分：" + seller.getCreditScore());
        }
        if (conditionDetails.isEmpty()) {
            conditionDetails.add("暂无详情信息");
        }

        BigDecimal avgPrice = null;
        BigDecimal lowestPrice = null;
        BigDecimal highestPrice = null;
        int categoryCount = 0;
        try {
            QueryWrapper<Product> qw = new QueryWrapper<>();
            qw.eq("deleted", 0);
            if (product.getCategory() != null) {
                qw.eq("category", product.getCategory());
            }
            qw.isNotNull("price");
            java.util.List<Product> sameCat = productMapper.selectList(qw);
            if (!sameCat.isEmpty()) {
                categoryCount = sameCat.size();
                java.math.BigDecimal total = java.math.BigDecimal.ZERO;
                java.math.BigDecimal min = sameCat.get(0).getPrice();
                java.math.BigDecimal max = sameCat.get(0).getPrice();
                for (Product p : sameCat) {
                    if (p.getPrice() == null) continue;
                    total = total.add(p.getPrice());
                    if (p.getPrice().compareTo(min) < 0) min = p.getPrice();
                    if (p.getPrice().compareTo(max) > 0) max = p.getPrice();
                }
                avgPrice = total.divide(new java.math.BigDecimal(sameCat.size()), 2, java.math.RoundingMode.HALF_UP);
                lowestPrice = min;
                highestPrice = max;
            }
        } catch (Exception e) {
            log.warn("同类商品统计失败: {}", e.getMessage());
        }

        java.util.List<String> recommendations = new java.util.ArrayList<>();
        if (imageCount <= 2) recommendations.add("建议添加更多细节图片（至少3张）");
        if (descLen <= 100) recommendations.add("建议补充更详细的商品描述（>100字）");
        if (condInt < 8) recommendations.add("成色一般，可考虑如实说明细节问题");
        if (aiAnalyzed == null || aiAnalyzed != 1) recommendations.add("建议使用AI商品分析增强可信度");
        if (product.getPrice() != null && avgPrice != null) {
            if (product.getPrice().compareTo(avgPrice.multiply(new java.math.BigDecimal("0.7"))) < 0) {
                recommendations.add("当前价格低于同类均价较多，请确认商品是否存在潜在问题");
            } else if (product.getPrice().compareTo(avgPrice.multiply(new java.math.BigDecimal("1.3"))) > 0) {
                recommendations.add("当前价格高于同类均价，建议优化定价促进成交");
            } else {
                recommendations.add("当前价格接近同类均价，定价合理");
            }
        }
        if (recommendations.isEmpty()) recommendations.add("商品信息完整，建议维持现状");

        String summary = "鉴定完成，真实度评分 " + String.format("%.2f", authenticityScore)
                + "，评级 " + grade + "。";
        if (avgPrice != null) {
            summary += " 同类商品参考均价 ¥" + avgPrice + "（共" + categoryCount + "件）。";
        }
        String priceSuggestion = null;
        if (product.getPrice() != null && avgPrice != null) {
            double diff = product.getPrice().doubleValue() - avgPrice.doubleValue();
            if (Math.abs(diff) < 1.0) {
                priceSuggestion = "与同类均价一致，建议保持当前价格";
            } else if (diff > 0) {
                priceSuggestion = "高于同类均价 ¥" + String.format("%.2f", diff) + "，建议适当下调";
            } else {
                priceSuggestion = "低于同类均价 ¥" + String.format("%.2f", -diff) + "，可考虑小幅上调";
            }
        }

        response.setIsAuthentic(isAuthentic);
        response.setAuthenticityScore(authenticityScore);
        response.setConditionGrade(grade);
        response.setConditionScore((double) condInt);
        response.setConditionDetails(conditionDetails);
        response.setReportSummary(summary);
        response.setRecommendations(recommendations);
        response.setReportTime(LocalDateTime.now());
        response.setReportId("AUTH-" + System.currentTimeMillis());

        log.info("鉴定结果 - productId={}, grade={}, score={}, avgPrice={}, 同品类数量={}",
                productId, grade, authenticityScore, avgPrice, categoryCount);

        return response;
    }

    @Override
    public com.echoofmemories.project.dto.AiMarketTrendResponse getMarketTrend(
            com.echoofmemories.project.dto.AiMarketTrendRequest request,
            Long userId) {
        log.info("市场行情参考 - 用户ID: {}, 品类: {}", userId, request.getCategory());

        com.echoofmemories.project.dto.AiMarketTrendResponse response =
                new com.echoofmemories.project.dto.AiMarketTrendResponse();

        String category = request.getCategory();
        int days = request.getDays() == null || request.getDays() <= 0 ? 7 : request.getDays();

        double avgPrice = 0.0;
        double lowPrice = 0.0;
        double highPrice = 0.0;
        int productCount = 0;
        try {
            QueryWrapper<Product> pw = new QueryWrapper<>();
            pw.eq("deleted", 0);
            if (category != null && !category.isEmpty()) {
                pw.eq("category", category);
            }
            pw.isNotNull("price");
            java.util.List<Product> products = productMapper.selectList(pw);
            if (!products.isEmpty()) {
                productCount = products.size();
                java.math.BigDecimal total = java.math.BigDecimal.ZERO;
                java.math.BigDecimal min = products.get(0).getPrice();
                java.math.BigDecimal max = products.get(0).getPrice();
                for (Product p : products) {
                    if (p.getPrice() == null) continue;
                    total = total.add(p.getPrice());
                    if (p.getPrice().compareTo(min) < 0) min = p.getPrice();
                    if (p.getPrice().compareTo(max) > 0) max = p.getPrice();
                }
                avgPrice = total.divide(new java.math.BigDecimal(products.size()), 2, java.math.RoundingMode.HALF_UP).doubleValue();
                lowPrice = min.doubleValue();
                highPrice = max.doubleValue();
            }
        } catch (Exception e) {
            log.warn("品类商品价格聚合失败: {}", e.getMessage());
        }
        response.setAveragePrice(avgPrice);
        response.setLowestPrice(lowPrice);
        response.setHighestPrice(highPrice);

        int totalSold = 0;
        try {
            QueryWrapper<Orders> ow = new QueryWrapper<>();
            ow.eq("deleted", 0);
            if (category != null && !category.isEmpty()) {
                ow.inSql("product_id", "SELECT id FROM product WHERE deleted=0 AND category='"
                        + category.replace("'", "") + "'");
            }
            ow.eq("status", 4);
            Long count = ordersMapper.selectCount(ow);
            totalSold = count == null ? 0 : count.intValue();
        } catch (Exception e) {
            log.warn("成交数量统计失败: {}", e.getMessage());
        }
        response.setTotalSold(totalSold);

        java.util.List<java.util.Map<String, Object>> priceTrend = new java.util.ArrayList<>();
        try {
            LocalDate today = LocalDate.now();
            java.util.Map<LocalDate, java.math.BigDecimal> dayTotal = new java.util.HashMap<>();
            java.util.Map<LocalDate, Integer> dayCount = new java.util.HashMap<>();
            QueryWrapper<Orders> ow2 = new QueryWrapper<>();
            ow2.eq("deleted", 0).eq("status", 4);
            if (category != null && !category.isEmpty()) {
                ow2.inSql("product_id", "SELECT id FROM product WHERE deleted=0 AND category='"
                        + category.replace("'", "") + "'");
            }
            ow2.ge("create_time", today.minusDays(days).atStartOfDay());
            ow2.le("create_time", today.atTime(23, 59, 59));
            java.util.List<Orders> recentOrders = ordersMapper.selectList(ow2);
            for (Orders o : recentOrders) {
                if (o.getCreateTime() == null || o.getAmount() == null) continue;
                LocalDate d = o.getCreateTime().toLocalDate();
                dayTotal.put(d, dayTotal.getOrDefault(d, java.math.BigDecimal.ZERO).add(o.getAmount()));
                dayCount.put(d, dayCount.getOrDefault(d, 0) + 1);
            }
            for (int i = days - 1; i >= 0; i--) {
                LocalDate d = today.minusDays(i);
                java.util.Map<String, Object> row = new java.util.HashMap<>();
                row.put("date", d.toString());
                java.math.BigDecimal t = dayTotal.get(d);
                Integer c = dayCount.get(d);
                if (t != null && c != null && c > 0) {
                    row.put("price", t.divide(new java.math.BigDecimal(c), 2, java.math.RoundingMode.HALF_UP).doubleValue());
                    row.put("count", c);
                } else {
                    row.put("price", 0);
                    row.put("count", 0);
                }
                priceTrend.add(row);
            }
        } catch (Exception e) {
            log.warn("价格走势统计失败: {}", e.getMessage());
        }
        response.setPriceTrend(priceTrend);

        java.util.List<java.util.Map<String, Object>> similarProducts = new java.util.ArrayList<>();
        try {
            QueryWrapper<Product> pw2 = new QueryWrapper<>();
            pw2.eq("deleted", 0);
            if (category != null && !category.isEmpty()) {
                pw2.eq("category", category);
            }
            pw2.orderByDesc("(view_count + favorite_count)");
            pw2.last("LIMIT 5");
            java.util.List<Product> top = productMapper.selectList(pw2);
            for (Product p : top) {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("id", p.getId());
                m.put("name", p.getTitle());
                m.put("price", p.getPrice() == null ? 0 : p.getPrice().doubleValue());
                m.put("condition", p.getConditionScore() == null ? 0 : p.getConditionScore());
                m.put("viewCount", p.getViewCount() == null ? 0 : p.getViewCount());
                m.put("favoriteCount", p.getFavoriteCount() == null ? 0 : p.getFavoriteCount());
                similarProducts.add(m);
            }
        } catch (Exception e) {
            log.warn("热门同类商品查询失败: {}", e.getMessage());
        }
        response.setSimilarProducts(similarProducts);

        StringBuilder recommendation = new StringBuilder();
        recommendation.append("品类").append(category == null ? "全部" : category)
                .append(" - 当前均价 ¥").append(String.format("%.2f", avgPrice))
                .append("（").append(productCount).append("件在售，近").append(days).append("天成交")
                .append(totalSold).append("单）。");
        if (avgPrice > 0) {
            if (totalSold >= productCount * 0.3) {
                recommendation.append(" 成交活跃，建议关注热门同类商品。");
            } else if (totalSold >= productCount * 0.1) {
                recommendation.append(" 需求稳定，价格可参考均价调整。");
            } else {
                recommendation.append(" 成交一般，建议优化商品描述或适度降价。");
            }
        }
        response.setRecommendation(recommendation.toString());

        String priceSuggestion;
        if (avgPrice <= 0) {
            priceSuggestion = "暂无同类成交数据，建议参考商品成色与市场评估定价";
        } else {
            priceSuggestion = "推荐定价参考同类均价 ¥" + String.format("%.2f", avgPrice)
                    + "，区间 ¥" + String.format("%.2f", lowPrice) + " ~ ¥" + String.format("%.2f", highPrice);
        }
        response.setPriceSuggestion(priceSuggestion);

        StringBuilder outlook = new StringBuilder();
        outlook.append("未来").append(days).append("天");
        if (totalSold > productCount * 0.2 && avgPrice > 0) {
            outlook.append("市场需求旺盛，价格预计保持平稳或小幅上涨，建议尽快发布或上架");
        } else if (totalSold > 0) {
            outlook.append("需求一般，建议关注同类TOP商品走势并优化商品详情页");
        } else {
            outlook.append("近期无成交数据，建议先完善商品信息并适当降低价格以吸引买家");
        }
        response.setMarketOutlook(outlook.toString());

        log.info("行情结果 - 品类={}, days={}, 均价={}, 最低={}, 最高={}, 成交={}, similarProducts={}",
                category, days, avgPrice, lowPrice, highPrice, totalSold, similarProducts.size());

        return response;
    }

    @Override
    public com.echoofmemories.project.dto.AiSmartSearchResponse smartSearch(
            com.echoofmemories.project.dto.AiSmartSearchRequest request,
            Long userId) {
        log.info("智能搜索 - 用户ID: {}, 查询: {}", userId, request.getQuery());

        com.echoofmemories.project.dto.AiSmartSearchResponse response =
                new com.echoofmemories.project.dto.AiSmartSearchResponse();

        String keyword = request.getQuery();
        String category = request.getCategory();
        Long userSchoolId = null;
        if (userId != null) {
            User u = userMapper.selectById(userId);
            if (u != null) userSchoolId = u.getSchoolId();
        }

        java.util.List<Product> hits = new java.util.ArrayList<>();
        try {
            QueryWrapper<Product> qw = new QueryWrapper<>();
            qw.eq("deleted", 0);
            if (category != null && !category.isEmpty()) {
                qw.eq("category", category);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim().replace("%", "\\%") + "%";
                qw.and(w -> w.like("title", kw).or().like("description", kw));
            }
            if (request.getMinPrice() != null) {
                qw.ge("price", BigDecimal.valueOf(request.getMinPrice()));
            }
            if (request.getMaxPrice() != null) {
                qw.le("price", BigDecimal.valueOf(request.getMaxPrice()));
            }
            if (request.getConditionMin() != null) {
                qw.ge("condition_score", request.getConditionMin());
            }
            if (request.getConditionMax() != null) {
                qw.le("condition_score", request.getConditionMax());
            }
            qw.orderByDesc("view_count");
            qw.last("LIMIT 200");
            hits = productMapper.selectList(qw);
        } catch (Exception e) {
            log.warn("智能搜索失败: {}", e.getMessage());
        }

        java.util.List<java.util.Map<String, Object>> products = new java.util.ArrayList<>();
        for (Product p : hits) {
            double titleMatch = 0.0;
            double descMatch = 0.0;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String k = keyword.trim().toLowerCase();
                if (p.getTitle() != null && p.getTitle().toLowerCase().contains(k)) {
                    titleMatch = 0.5;
                }
                if (p.getDescription() != null && p.getDescription().toLowerCase().contains(k)) {
                    descMatch = 0.3;
                }
            }
            double schoolBonus = 0.0;
            if (Boolean.TRUE.equals(request.getSchoolPriority())
                    && userSchoolId != null && p.getSchoolId() != null
                    && userSchoolId.equals(p.getSchoolId())) {
                schoolBonus = 0.3;
            }
            double matchScore = Math.min(1.0, titleMatch + descMatch + schoolBonus + 0.2);

            java.util.Map<String, Object> m = new java.util.HashMap<>();
            m.put("id", p.getId());
            m.put("title", p.getTitle());
            m.put("price", p.getPrice() == null ? 0 : p.getPrice().doubleValue());
            m.put("conditionScore", p.getConditionScore() == null ? 0 : p.getConditionScore());
            m.put("schoolId", p.getSchoolId());
            m.put("matchScore", matchScore);
            m.put("category", p.getCategory());
            products.add(m);
        }

        products.sort((a, b) -> Double.compare((Double) b.get("matchScore"), (Double) a.get("matchScore")));

        int pageSize = request.getPageSize() == null || request.getPageSize() <= 0 ? 20 : request.getPageSize();
        int pageNum = request.getPageNum() == null || request.getPageNum() <= 0 ? 1 : request.getPageNum();
        int totalCount = products.size();
        int from = Math.min(totalCount, (pageNum - 1) * pageSize);
        int to = Math.min(totalCount, from + pageSize);
        java.util.List<java.util.Map<String, Object>> pageList =
                from >= to ? new java.util.ArrayList<>() : new java.util.ArrayList<>(products.subList(from, to));

        java.util.Set<String> suggestedTags = new java.util.LinkedHashSet<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            String k = keyword.trim();
            suggestedTags.add(k);
            if (k.length() >= 2) {
                suggestedTags.add(k.substring(0, Math.min(4, k.length())));
            }
            for (String part : k.split("[,\\s，、]+")) {
                if (!part.isEmpty()) suggestedTags.add(part);
            }
        }
        if (category != null && !category.isEmpty()) suggestedTags.add(category);

        response.setInterpretedQuery(keyword == null ? "" : keyword);
        response.setSuggestedTags(new java.util.ArrayList<>(suggestedTags));
        response.setSearchTip(totalCount > 0 ? "共找到 " + totalCount + " 条相关商品" : "未找到相关商品，可尝试放宽筛选条件");
        response.setProducts(pageList);
        response.setTotalCount(totalCount);

        response.setSimilarSearches(java.util.Arrays.asList(
                java.util.Map.of("query", (category == null ? "热门" : category + " 相关"), "count", totalCount),
                java.util.Map.of("query", "校园二手", "count", totalCount)
        ));

        log.info("搜索结果 - keyword={}, total={}, 返回={}", keyword, totalCount, pageList.size());

        return response;
    }

    @Override
    public com.echoofmemories.project.dto.AiCampusMatchResponse campusMatch(
            com.echoofmemories.project.dto.AiCampusMatchRequest request,
            Long userId) {
        log.info("校园匹配 - 用户ID: {}, 商品ID: {}", userId, request.getProductId());

        com.echoofmemories.project.dto.AiCampusMatchResponse response =
                new com.echoofmemories.project.dto.AiCampusMatchResponse();

        Long schoolId = null;
        String category = null;
        if (request.getProductId() != null) {
            Product product = productMapper.selectById(request.getProductId());
            if (product != null) {
                schoolId = product.getSchoolId();
                category = product.getCategory();
            }
        }
        if (schoolId == null && userId != null) {
            User u = userMapper.selectById(userId);
            if (u != null) schoolId = u.getSchoolId();
        }

        String schoolName = "";
        if (schoolId != null) {
            com.echoofmemories.project.entity.School s = schoolMapper.selectById(schoolId);
            if (s != null) {
                if (s.getNameZh() != null) schoolName = s.getNameZh();
                else if (s.getNameEn() != null) schoolName = s.getNameEn();
            }
        }

        java.util.List<java.util.Map<String, Object>> matchedBuyers = new java.util.ArrayList<>();
        java.util.List<java.util.Map<String, Object>> matchedSellers = new java.util.ArrayList<>();

        if (schoolId != null) {
            try {
                java.time.LocalDateTime since = java.time.LocalDateTime.now().minusDays(30);
                java.util.Map<Long, Integer> buyerMap = new java.util.HashMap<>();
                java.util.Map<Long, Integer> sellerMap = new java.util.HashMap<>();

                QueryWrapper<Orders> ow = new QueryWrapper<>();
                ow.eq("deleted", 0);
                ow.ge("create_time", since);
                if (category != null && !category.isEmpty()) {
                    ow.inSql("product_id", "SELECT id FROM product WHERE deleted=0 AND school_id=" + schoolId
                            + " AND category='" + category.replace("'", "") + "'");
                } else {
                    ow.inSql("product_id", "SELECT id FROM product WHERE deleted=0 AND school_id=" + schoolId);
                }
                java.util.List<Orders> orders = ordersMapper.selectList(ow);
                for (Orders o : orders) {
                    if (o.getBuyerId() != null) {
                        buyerMap.put(o.getBuyerId(), buyerMap.getOrDefault(o.getBuyerId(), 0) + 1);
                    }
                    if (o.getSellerId() != null) {
                        sellerMap.put(o.getSellerId(), sellerMap.getOrDefault(o.getSellerId(), 0) + 1);
                    }
                }

                QueryWrapper<Product> pw = new QueryWrapper<>();
                pw.eq("deleted", 0).eq("school_id", schoolId);
                pw.isNotNull("user_id");
                if (category != null && !category.isEmpty()) {
                    pw.eq("category", category);
                }
                java.util.List<Product> prods = productMapper.selectList(pw);
                for (Product p : prods) {
                    if (p.getUserId() == null) continue;
                    sellerMap.put(p.getUserId(), sellerMap.getOrDefault(p.getUserId(), 0) + 1);
                }

                java.util.List<java.util.Map.Entry<Long, Integer>> buyerList =
                        new java.util.ArrayList<>(buyerMap.entrySet());
                buyerList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
                int buyerLimit = 0;
                for (java.util.Map.Entry<Long, Integer> e : buyerList) {
                    if (buyerLimit >= 5) break;
                    User u = userMapper.selectById(e.getKey());
                    if (u == null) continue;
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("id", u.getId());
                    m.put("name", u.getNickname() == null ? "校园用户" : u.getNickname());
                    m.put("school", schoolName);
                    m.put("matchScore", Math.min(1.0, 0.5 + 0.1 * e.getValue()));
                    m.put("tradeCount", e.getValue());
                    matchedBuyers.add(m);
                    buyerLimit++;
                }

                java.util.List<java.util.Map.Entry<Long, Integer>> sellerList =
                        new java.util.ArrayList<>(sellerMap.entrySet());
                sellerList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
                int sellerLimit = 0;
                for (java.util.Map.Entry<Long, Integer> e : sellerList) {
                    if (sellerLimit >= 5) break;
                    User u = userMapper.selectById(e.getKey());
                    if (u == null) continue;
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("id", u.getId());
                    m.put("name", u.getNickname() == null ? "校园用户" : u.getNickname());
                    m.put("school", schoolName);
                    m.put("matchScore", Math.min(1.0, 0.5 + 0.1 * e.getValue()));
                    m.put("tradeCount", e.getValue());
                    matchedSellers.add(m);
                    sellerLimit++;
                }
            } catch (Exception e) {
                log.warn("校园匹配计算失败: {}", e.getMessage());
            }
        }

        response.setMatchedBuyers(matchedBuyers);
        response.setMatchedSellers(matchedSellers);

        if (!schoolName.isEmpty()) {
            response.setMeetupSuggestions("建议在" + schoolName + "图书馆/食堂/快递点等公开场所当面交易");
            response.setSafetyTips("1. 选择" + schoolName + "校内公共场所见面 2. 当面检查商品后再付款 3. 建议使用平台担保交易 4. 保留聊天记录与支付凭证");
        } else {
            response.setMeetupSuggestions("建议在校园内或其他公共场所见面交易");
            response.setSafetyTips("1. 选择公共场所见面 2. 交易时检查商品 3. 建议使用平台担保交易");
        }

        log.info("校园匹配结果 - schoolId={}, schoolName={}, 买家数={}, 卖家数={}",
                schoolId, schoolName, matchedBuyers.size(), matchedSellers.size());

        return response;
    }

    @Override
    public com.echoofmemories.project.dto.AiDisputeResolutionResponse resolveDispute(
            com.echoofmemories.project.dto.AiDisputeResolutionRequest request,
            Long userId) {
        log.info("纠纷仲裁 - 用户ID: {}, 订单ID: {}", userId, request.getOrderId());

        com.echoofmemories.project.dto.AiDisputeResolutionResponse response =
                new com.echoofmemories.project.dto.AiDisputeResolutionResponse();

        Orders order = null;
        if (request.getOrderId() != null) {
            order = ordersMapper.selectById(request.getOrderId());
        }

        java.util.List<String> keyFindings = new java.util.ArrayList<>();
        double refundSuggestion = 0.0;
        String riskLevel = "low";
        String suggestedResolution;
        String nextSteps;
        int confidenceScore = 80;

        if (order == null) {
            keyFindings.add("未查询到订单信息，请确认订单ID");
            suggestedResolution = "无法自动仲裁，建议联系客服介入";
            nextSteps = "1. 确认订单ID是否正确  2. 提供纠纷描述与证据  3. 等待客服人工处理";
            confidenceScore = 60;
        } else {
            if (order.getAmount() != null) {
                keyFindings.add("订单金额：¥" + order.getAmount());
            }
            if (order.getStatus() != null) {
                keyFindings.add("订单状态：" + orderStatusText(order.getStatus()));
            }
            if (order.getCreateTime() != null) {
                keyFindings.add("下单时间：" + order.getCreateTime());
            }
            if (request.getDisputeDescription() != null && !request.getDisputeDescription().isEmpty()) {
                keyFindings.add("纠纷描述：" + request.getDisputeDescription());
            }
            if (request.getEvidenceUrls() != null && !request.getEvidenceUrls().isEmpty()) {
                keyFindings.add("已提供 " + request.getEvidenceUrls().size() + " 条证据");
            }

            if (order.getStatus() != null && order.getStatus() >= 3) {
                keyFindings.add("订单已进入完成/后序流程，可启动退款判断");
            }

            double amount = order.getAmount() == null ? 0.0 : order.getAmount().doubleValue();
            if (amount > 1000) {
                riskLevel = "high";
                refundSuggestion = Math.min(100.0, 70.0);
                confidenceScore = 75;
            } else if (amount > 200) {
                riskLevel = "medium";
                refundSuggestion = 50.0;
                confidenceScore = 85;
            } else {
                riskLevel = "low";
                refundSuggestion = 30.0;
                confidenceScore = 90;
            }

            String desc = request.getDisputeDescription();
            if (desc != null) {
                String lower = desc.toLowerCase();
                if (lower.contains("质量") || lower.contains("假") || lower.contains("损坏")) {
                    refundSuggestion = Math.min(100.0, refundSuggestion + 20.0);
                    keyFindings.add("涉及商品质量/真伪问题，建议卖家提供证明");
                }
                if (lower.contains("未发货") || lower.contains("没收到") || lower.contains("未收到")) {
                    refundSuggestion = 100.0;
                    keyFindings.add("涉及物流未送达，建议优先全额退款");
                }
                if (lower.contains("描述不符") || lower.contains("不符")) {
                    refundSuggestion = Math.max(refundSuggestion, 60.0);
                    keyFindings.add("存在描述不符争议，建议双方提供图文证据");
                }
            }

            suggestedResolution = "根据订单金额与纠纷描述，建议卖家承担 "
                    + String.format("%.0f", refundSuggestion) + "% 退款责任，双方协商解决";
            nextSteps = "1. 双方确认解决方案  2. 卖家执行退款操作  3. 买家确认收到退款  4. 完成纠纷处理";
        }

        response.setCaseId("DISPUTE-" + System.currentTimeMillis());
        response.setSuggestedResolution(suggestedResolution);
        response.setRefundSuggestion(refundSuggestion);
        response.setKeyFindings(keyFindings);
        response.setRiskLevel(riskLevel);
        response.setNextSteps(nextSteps);
        response.setConfidenceScore(confidenceScore);

        log.info("纠纷仲裁结果 - orderId={}, refund={}%, risk={}, confidence={}",
                request.getOrderId(), refundSuggestion, riskLevel, confidenceScore);

        return response;
    }

    private String orderStatusText(int status) {
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付/待发货";
            case 2: return "已发货/待收货";
            case 3: return "交易完成";
            case 4: return "已成交";
            case -1: return "已取消";
            default: return "状态-" + status;
        }
    }

    @Override
    public com.echoofmemories.project.dto.AiCampusServiceResponse campusService(
            com.echoofmemories.project.dto.AiCampusServiceRequest request,
            Long userId) {
        log.info("校园专属服务 - 用户ID: {}, 类型: {}", userId, request.getServiceType());
        
        com.echoofmemories.project.dto.AiCampusServiceResponse response = 
            new com.echoofmemories.project.dto.AiCampusServiceResponse();
        response.setServiceType(request.getServiceType());
        
        java.util.List<java.util.Map<String, Object>> matches = new java.util.ArrayList<>();
        for (int i = 0; i < 3; i++) {
            java.util.Map<String, Object> match = new java.util.HashMap<>();
            match.put("id", i + 1);
            match.put("title", request.getServiceType() + "服务 " + (i + 1));
            match.put("price", 10 + Math.random() * 50);
            match.put("rating", 4.0 + Math.random());
            matches.add(match);
        }
        response.setMatches(matches);
        
        response.setSuggestion("建议选择评分最高的服务");
        response.setTips(java.util.Arrays.asList(
            "提前确认服务时间",
            "选择有评价的服务商",
            "注意保管好个人物品"
        ));
        response.setPriceEstimate(25.0);
        response.setPriceCurrency("CNY");
        
        return response;
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
