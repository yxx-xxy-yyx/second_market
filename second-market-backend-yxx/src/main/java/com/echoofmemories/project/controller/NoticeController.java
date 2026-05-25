package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.NoticeRequest;
import com.echoofmemories.project.entity.Notice;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 公告控制器
 * 
 * @author EchoOfMemories
 */
@Tag(name = "公告管理", description = "公告相关接口")
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "发布公告")
    @PostMapping("/add")
    public Result<Notice> addNotice(@RequestBody NoticeRequest request) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("401", "用户未认证");
            }

            Notice notice = noticeService.addNotice(request, userId);
            return Result.success("发布成功", notice);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "更新公告")
    @PutMapping("/update")
    public Result<String> updateNotice(@RequestBody NoticeRequest request) {
        try {
            boolean success = noticeService.updateNotice(request);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "删除公告")
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteNotice(@PathVariable Long id) {
        try {
            boolean success = noticeService.deleteNotice(id);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "公告列表")
    @PostMapping("/list")
    public Result<Page<Notice>> getNoticeList(@RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                               @RequestParam(required = false) Integer status,
                                               @RequestParam(required = false) Integer type) {
        try {
            Page<Notice> page = noticeService.getNoticePage(pageNum, pageSize, status, type);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "获取公告详情")
    @GetMapping("/{id}")
    public Result<Notice> getNotice(@PathVariable Long id) {
        try {
            Notice notice = noticeService.getNoticeById(id);
            return notice != null ? Result.success(notice) : Result.error("404", "公告未找到");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }
}

