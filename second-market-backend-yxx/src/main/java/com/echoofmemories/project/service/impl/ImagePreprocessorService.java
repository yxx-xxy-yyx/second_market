package com.echoofmemories.project.service.impl;

import com.echoofmemories.project.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * 图片预处理服务
 * 负责图片下载、Base64转换、压缩及SSRF防护
 *
 * @author echo
 * @since 2025-01-27
 */
@Slf4j
@Service
public class ImagePreprocessorService {

    @Value("${project.file.upload-path:./uploads/}")
    private String uploadPath;

    /**
     * 优化图片URL列表（最多1张，转换为base64或HTTP URL格式）
     */
    public List<String> optimizeImageUrls(List<String> imageUrls) {
        List<String> optimized = new ArrayList<>();

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
                        mimeTypeStart += 6;

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

    /**
     * 将图片URL转换为完整格式（HTTP URL / Base64 / 本地文件）
     */
    public String convertToFullUrl(String imageUrl) {
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

    /**
     * 下载图片并转换为Base64
     */
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

    /**
     * 从Content-Type中提取MIME类型
     */
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

    /**
     * 从URL后缀推断MIME类型
     */
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

    /**
     * 压缩Base64图片数据
     */
    private String compressBase64Image(String base64Data, String mimeType) {
        try {
            if (base64Data == null || base64Data.isEmpty()) {
                return base64Data;
            }

            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(decodedBytes);
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

            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
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

    /**
     * 验证图片URL，防止SSRF攻击
     */
    public void validateImageUrl(String imageUrl) {
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
}
