package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.entity.Report;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 举报数据访问层
 * 
 * @author EchoOfMemories
 */
@Mapper
public interface ReportMapper extends BaseMapper<Report> {
    
    Page<Report> selectReportPageWithDetails(Page<Report> page, @Param("status") Integer status);
}

