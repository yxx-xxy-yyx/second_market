package com.echoofmemories.project.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.dto.ReportHandleRequest;
import com.echoofmemories.project.dto.ReportRequest;
import com.echoofmemories.project.entity.Product;
import com.echoofmemories.project.entity.Report;
import com.echoofmemories.project.entity.User;
import com.echoofmemories.project.exception.CustomException;
import com.echoofmemories.project.mapper.ReportMapper;
import com.echoofmemories.project.service.ProductService;
import com.echoofmemories.project.service.ReportService;
import com.echoofmemories.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 举报服务实现类
 * 
 * @author EchoOfMemories
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {

    private final ReportMapper reportMapper;
    private final ProductService productService;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Report addReport(ReportRequest reportRequest, Long userId) {
        if (reportRequest.getTargetType() == 1) {
            Product product = productService.getById(reportRequest.getTargetId());
            if (product == null || product.getDeleted() == 1) {
                throw new CustomException("举报的商品不存在");
            }
        } else if (reportRequest.getTargetType() == 2) {
            User user = userService.getById(reportRequest.getTargetId());
            if (user == null) {
                throw new CustomException("举报的用户不存在");
            }
            if (user.getId().equals(userId)) {
                throw new CustomException("不能举报自己");
            }
        } else {
            throw new CustomException("无效的举报类型");
        }

        Report report = new Report();
        report.setReporterId(userId);
        report.setTargetType(reportRequest.getTargetType());
        report.setTargetId(reportRequest.getTargetId());
        report.setReason(reportRequest.getReason());
        report.setStatus(0);
        report.setDeleted(0);

        this.save(report);
        return report;
    }

    @Override
    public Page<Report> getReportPage(Integer pageNum, Integer pageSize, Integer status) {
        Page<Report> page = new Page<>(pageNum, pageSize);
        return reportMapper.selectReportPageWithDetails(page, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleReport(ReportHandleRequest handleRequest) {
        Report report = this.getById(handleRequest.getId());
        
        if (report == null) {
            throw new CustomException("举报记录不存在");
        }

        if (report.getStatus() != 0) {
            throw new CustomException("该举报已处理");
        }

        report.setStatus(handleRequest.getStatus());
        report.setHandleResult(handleRequest.getHandleResult());
        report.setHandleTime(LocalDateTime.now());

        return this.updateById(report);
    }
}

