package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.PageRequest;
import com.echoofmemories.project.entity.FileInfo;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件管理控制器
 * 
 * @author Echo of Memories
 */
@Tag(name = "文件管理", description = "文件上传下载相关接口")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<FileInfo> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Long currentUserId = SecurityUtils.getCurrentUserId();
            if (currentUserId == null) {
                return Result.error("401", "用户未登录或登录已过期");
            }

            FileInfo fileInfo = fileService.uploadFile(file, currentUserId);
            return Result.success("文件上传成功", fileInfo);
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "上传头像")
    @PostMapping("/upload/avatar")
    public Result<FileInfo> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            Long currentUserId = SecurityUtils.getCurrentUserId();
            if (currentUserId == null) {
                return Result.error("401", "用户未登录或登录已过期");
            }

            FileInfo fileInfo = fileService.uploadAvatar(file, currentUserId);
            return Result.success("头像上传成功", fileInfo);
        } catch (Exception e) {
            return Result.error("头像上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "文件下载")
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            // 通过ID查询文件信息，而不是直接使用文件名
            FileInfo fileInfo = fileService.getById(id);
            if (fileInfo == null || fileInfo.getDeleted() == 1) {
                return ResponseEntity.notFound().build();
            }

            File file = new File(fileInfo.getFilePath());
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(file);

            String encodedFileName = URLEncoder.encode(
                fileInfo.getOriginalName(), 
                StandardCharsets.UTF_8
            ).replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                            encodedFileName + "\"")
                    .contentType(MediaType.parseMediaType(
                        fileInfo.getMimeType() != null ? 
                        fileInfo.getMimeType() : 
                        MediaType.APPLICATION_OCTET_STREAM_VALUE
                    ))
                    .body(resource);

        } catch (Exception e) {
            log.error("文件下载失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "获取文件信息")
    @GetMapping("/{id}")
    public Result<FileInfo> getFileInfo(@PathVariable Long id) {
        FileInfo fileInfo = fileService.getById(id);
        return Result.success(fileInfo);
    }

    @Operation(summary = "分页查询文件")
    @GetMapping("/page")
    public Result<Page<FileInfo>> getFilePage(PageRequest pageRequest,
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "fileType", required = false) String fileType) {
        Page<FileInfo> filePage = fileService.getFilePage(pageRequest, userId, fileType);
        return Result.success(filePage);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    public Result<String> deleteFile(@PathVariable Long id) {
        try {
            Long currentUserId = SecurityUtils.getCurrentUserId();
            if (currentUserId == null) {
                return Result.error("401", "用户未登录或登录已过期");
            }

            boolean deleted = fileService.deleteFile(id, currentUserId);
            if (deleted) {
                return Result.success("文件删除成功");
            } else {
                return Result.error("文件删除失败");
            }
        } catch (Exception e) {
            return Result.error("文件删除失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取文件下载链接")
    @GetMapping("/{id}/download-url")
    public Result<String> getDownloadUrl(@PathVariable Long id) {
        String downloadUrl = fileService.getDownloadUrl(id);
        return Result.success(downloadUrl);
    }

}