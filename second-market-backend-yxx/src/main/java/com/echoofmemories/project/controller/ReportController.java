package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.ReportHandleRequest;
import com.echoofmemories.project.dto.ReportRequest;
import com.echoofmemories.project.entity.Report;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 举报控制器
 * 
 * @author EchoOfMemories
 */
@Tag(name = "举报管理", description = "举报相关接口")
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "提交举报")
    @PostMapping("/add")
    public Result<Report> addReport(@Valid @RequestBody ReportRequest reportRequest) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }
            
            Report report = reportService.addReport(reportRequest, userId);
            return Result.success("举报提交成功", report);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "查看举报列表")
    @PostMapping("/list")
    public Result<Page<Report>> getReportList(@RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                               @RequestParam(required = false) Integer status) {
        try {
            Page<Report> page = reportService.getReportPage(pageNum, pageSize, status);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "处理举报")
    @PutMapping("/handle/{id}")
    public Result<String> handleReport(@PathVariable Long id, @Valid @RequestBody ReportHandleRequest handleRequest) {
        try {
            handleRequest.setId(id);
            boolean success = reportService.handleReport(handleRequest);
            return success ? Result.success("处理成功") : Result.error("处理失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }
}

