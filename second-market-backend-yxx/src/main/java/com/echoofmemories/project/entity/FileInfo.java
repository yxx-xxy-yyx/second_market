package com.echoofmemories.project.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件信息实体类
 * 
 * @author Echo of Memories
 */
@Data
@TableName("sys_file_info")
public class FileInfo implements Serializable {
    
    /**
     * 文件ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 文件原始名称
     */
    private String originalName;
    
    /**
     * 文件存储名称
     */
    private String fileName;
    
    /**
     * 文件路径
     */
    private String filePath;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * MIME类型
     */
    private String mimeType;
    
    /**
     * 文件扩展名
     */
    private String extension;
    
    /**
     * 文件MD5值
     */
    private String md5;
    
    /**
     * 文件URL
     */
    private String fileUrl;
    
    /**
     * 上传用户ID
     */
    private Long userId;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 是否删除 (0-未删除, 1-已删除)
     */
    private Integer deleted;
    
    private static final long serialVersionUID = 1L;
    
}