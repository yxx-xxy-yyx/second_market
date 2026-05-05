package com.echoofmemories.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.echoofmemories.project.dto.PageRequest;
import com.echoofmemories.project.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 * 
 * @author Echo of Memories
 */
public interface FileService extends IService<FileInfo> {
    
    /**
     * 上传文件
     */
    FileInfo uploadFile(MultipartFile file, Long userId);
    
    /**
     * 上传头像
     */
    FileInfo uploadAvatar(MultipartFile file, Long userId);
    
    /**
     * 根据MD5查询文件
     */
    FileInfo getByMd5(String md5);
    
    /**
     * 分页查询文件
     */
    Page<FileInfo> getFilePage(PageRequest pageRequest, Long userId, String fileType);
    
    /**
     * 删除文件
     */
    boolean deleteFile(Long fileId, Long userId);
    
    /**
     * 获取文件下载URL
     */
    String getDownloadUrl(Long fileId);
    
}