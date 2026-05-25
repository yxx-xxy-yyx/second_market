package com.echoofmemories.project.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.dto.PageRequest;
import com.echoofmemories.project.entity.FileInfo;
import com.echoofmemories.project.exception.CustomException;
import com.echoofmemories.project.mapper.FileInfoMapper;
import com.echoofmemories.project.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 文件服务实现类
 * 
 * @author Echo of Memories
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileService {

    private final FileInfoMapper fileInfoMapper;

    @Value("${project.file.upload-path:${java.io.tmpdir}/uploads}")
    private String uploadPath;

    @Value("${project.supabase.url:${SUPABASE_URL:}}")
    private String supabaseUrl;

    @Value("${project.supabase.service-role-key:${SUPABASE_SERVICE_ROLE_KEY:}}")
    private String supabaseServiceRoleKey;

    @Value("${project.supabase.storage.bucket:${SUPABASE_STORAGE_BUCKET:images}}")
    private String supabaseBucket;

    @Value("${project.file.allowed-types:jpg,jpeg,png,gif,bmp,webp,pdf,doc,docx,xls,xlsx,ppt,pptx,txt}")
    private String allowedTypes;

    @Value("${project.file.max-size:100MB}")
    private String maxSize;

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();

    // 支持的图片类型
    private static final List<String> IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");

    // 支持的文档类型
    private static final List<String> DOCUMENT_TYPES = Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "txt");

    private boolean isSupabaseEnabled() {
        return StrUtil.isNotBlank(supabaseUrl) && StrUtil.isNotBlank(supabaseServiceRoleKey) && StrUtil.isNotBlank(supabaseBucket);
    }

    private String normalizeBaseUrl(String url) {
        if (url == null) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private String buildSupabasePublicUrl(String objectKey) {
        String base = normalizeBaseUrl(supabaseUrl);
        String encoded = Arrays.stream(objectKey.split("/"))
                .map(seg -> URLEncoder.encode(seg, StandardCharsets.UTF_8))
                .collect(java.util.stream.Collectors.joining("/"));
        return base + "/storage/v1/object/public/" + supabaseBucket + "/" + encoded;
    }

    private void uploadToSupabase(String objectKey, byte[] bytes, String contentType) {
        String base = normalizeBaseUrl(supabaseUrl);
        String encoded = Arrays.stream(objectKey.split("/"))
                .map(seg -> URLEncoder.encode(seg, StandardCharsets.UTF_8))
                .collect(java.util.stream.Collectors.joining("/"));
        String url = base + "/storage/v1/object/" + supabaseBucket + "/" + encoded;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + supabaseServiceRoleKey)
                .header("apikey", supabaseServiceRoleKey)
                .header("x-upsert", "true")
                .header("Content-Type", StrUtil.blankToDefault(contentType, MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .PUT(HttpRequest.BodyPublishers.ofByteArray(bytes))
                .build();

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            int code = response.statusCode();
            if (code >= 200 && code < 300) {
                return;
            }
            throw new CustomException("上传到对象存储失败: " + code);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CustomException("上传到对象存储失败: " + e.getMessage());
        } catch (IOException e) {
            throw new CustomException("上传到对象存储失败: " + e.getMessage());
        }
    }

    private void deleteFromSupabase(String objectKey) {
        String base = normalizeBaseUrl(supabaseUrl);
        String encoded = Arrays.stream(objectKey.split("/"))
                .map(seg -> URLEncoder.encode(seg, StandardCharsets.UTF_8))
                .collect(java.util.stream.Collectors.joining("/"));
        String url = base + "/storage/v1/object/" + supabaseBucket + "/" + encoded;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + supabaseServiceRoleKey)
                .header("apikey", supabaseServiceRoleKey)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            int code = response.statusCode();
            if (code >= 200 && code < 300) {
                return;
            }
            log.warn("删除对象存储文件失败: {}, status={}", objectKey, code);
        } catch (Exception e) {
            log.warn("删除对象存储文件失败: {}, err={}", objectKey, e.getMessage());
        }
    }

    @Override
    public FileInfo uploadFile(MultipartFile file, Long userId) {
        if (file.isEmpty()) {
            throw new CustomException("文件不能为空");
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new CustomException("文件名不能为空");
        }

        String extension = FileUtil.extName(originalFilename).toLowerCase();
        List<String> allowedTypeList = Arrays.asList(allowedTypes.split(","));
        if (!allowedTypeList.contains(extension)) {
            throw new CustomException("不支持的文件类型: " + extension);
        }

        // 验证文件大小（这里简化处理，实际应该解析maxSize）
        if (file.getSize() > 100 * 1024 * 1024) { // 100MB
            throw new CustomException("文件大小超出限制");
        }

        try {
            // 计算文件MD5
            byte[] fileBytes = file.getBytes();
            String md5 = DigestUtil.md5Hex(fileBytes);

            // 检查文件是否已存在
            FileInfo existingFile = fileInfoMapper.selectByMd5(md5);
            if (existingFile != null) {
                log.info("文件已存在，返回已有记录: {}", existingFile.getFileName());
                return existingFile;
            }

            // 生成新的文件名
            String newFileName = IdUtil.simpleUUID() + "." + extension;

            // 按日期创建目录结构
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String filePath;
            String fileUrl;

            if (isSupabaseEnabled()) {
                String objectKey = dateDir + "/" + newFileName;
                uploadToSupabase(objectKey, fileBytes, file.getContentType());
                filePath = objectKey;
                fileUrl = buildSupabasePublicUrl(objectKey);
                log.info("文件上传到对象存储成功: {}", objectKey);
            } else {
                String dirPath = uploadPath + File.separator + dateDir;
                File directory = new File(dirPath);
                if (!directory.exists()) {
                    boolean created = directory.mkdirs();
                    if (!created) {
                        log.error("创建上传目录失败: {}", dirPath);
                        throw new CustomException("创建上传目录失败");
                    }
                }

                String localPath = dirPath + File.separator + newFileName;
                File targetFile = new File(localPath);
                file.transferTo(targetFile);
                log.info("文件保存成功: {}", localPath);
                filePath = localPath;
                fileUrl = "/uploads/" + dateDir + "/" + newFileName;
            }

            // 创建文件信息记录
            FileInfo fileInfo = new FileInfo();
            fileInfo.setOriginalName(originalFilename);
            fileInfo.setFileName(newFileName);
            fileInfo.setFilePath(filePath);
            fileInfo.setFileSize(file.getSize());
            fileInfo.setMimeType(file.getContentType());
            fileInfo.setExtension(extension);
            fileInfo.setMd5(md5);
            fileInfo.setUserId(userId);
            fileInfo.setCreateTime(LocalDateTime.now());
            fileInfo.setDeleted(0);

            // 设置文件类型
            if (IMAGE_TYPES.contains(extension)) {
                fileInfo.setFileType("image");
            } else if (DOCUMENT_TYPES.contains(extension)) {
                fileInfo.setFileType("document");
            } else {
                fileInfo.setFileType("other");
            }

            fileInfo.setFileUrl(fileUrl);

            // 保存到数据库
            fileInfoMapper.insert(fileInfo);

            log.info("文件上传成功: {} -> {}", originalFilename, newFileName);
            return fileInfo;

        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new CustomException("文件上传失败");
        }
    }

    @Override
    public FileInfo uploadAvatar(MultipartFile file, Long userId) {
        // 验证是否为图片类型
        if (file.isEmpty()) {
            throw new CustomException("头像文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new CustomException("文件名不能为空");
        }

        String extension = FileUtil.extName(originalFilename).toLowerCase();
        if (!IMAGE_TYPES.contains(extension)) {
            throw new CustomException("头像必须是图片格式");
        }

        return uploadFile(file, userId);
    }

    @Override
    public FileInfo getByMd5(String md5) {
        return fileInfoMapper.selectByMd5(md5);
    }

    @Override
    public Page<FileInfo> getFilePage(PageRequest pageRequest, Long userId, String fileType) {
        Page<FileInfo> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);

        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }

        if (StrUtil.isNotBlank(fileType)) {
            queryWrapper.eq("file_type", fileType);
        }

        if (StrUtil.isNotBlank(pageRequest.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like("original_name", pageRequest.getKeyword())
                    .or()
                    .like("file_name", pageRequest.getKeyword()));
        }

        queryWrapper.orderByDesc("create_time");
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean deleteFile(Long fileId, Long userId) {
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new CustomException("文件不存在");
        }

        // 检查权限（管理员或文件所有者可以删除）
        if (!fileInfo.getUserId().equals(userId)) {
            throw new CustomException("无权限删除此文件");
        }

        // 软删除
        fileInfo.setDeleted(1);
        fileInfoMapper.updateById(fileInfo);

        if (isSupabaseEnabled() && StrUtil.isNotBlank(fileInfo.getFilePath())) {
            deleteFromSupabase(fileInfo.getFilePath());
            return true;
        }

        try {
            FileUtil.del(fileInfo.getFilePath());
            log.info("删除文件成功: {}", fileInfo.getFileName());
        } catch (Exception e) {
            log.error("删除物理文件失败: {}", fileInfo.getFileName(), e);
        }

        return true;
    }

    @Override
    public String getDownloadUrl(Long fileId) {
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if (fileInfo == null || fileInfo.getDeleted() == 1) {
            throw new CustomException("文件不存在");
        }
        return fileInfo.getFileUrl();
    }

}
