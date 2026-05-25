package com.echoofmemories.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.echoofmemories.project.dto.ReportHandleRequest;
import com.echoofmemories.project.dto.ReportRequest;
import com.echoofmemories.project.entity.Report;

/**
 * 举报服务接口
 * 
 * @author EchoOfMemories
 */
public interface ReportService extends IService<Report> {
    
    Report addReport(ReportRequest reportRequest, Long userId);
    
    Page<Report> getReportPage(Integer pageNum, Integer pageSize, Integer status);
    
    boolean handleReport(ReportHandleRequest handleRequest);
}

