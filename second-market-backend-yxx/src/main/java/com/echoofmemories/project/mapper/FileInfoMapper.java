package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.echoofmemories.project.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 文件信息数据访问层
 * 
 * @author Echo of Memories
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {
    
    /**
     * 根据MD5查询文件
     */
    FileInfo selectByMd5(@Param("md5") String md5);
    
}