package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.entity.CampusService;
import com.echoofmemories.project.service.CampusServiceService;
import com.echoofmemories.project.security.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 校园周边服务控制器
 */
@RestController
@RequestMapping("/campus-service")
public class CampusServiceController {

    @Resource
    private CampusServiceService campusServiceService;

    /**
     * 分页查询校园周边服务
     */
    @GetMapping("/list")
    public Result<Page<CampusService>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Long schoolId) {
        Page<CampusService> page = new Page<>(pageNum, pageSize);
        return Result.success(campusServiceService.getPage(page, type, schoolId));
    }

    /**
     * 获取详情
     */
    @GetMapping("/{id}")
    public Result<CampusService> getById(@PathVariable Long id) {
        return Result.success(campusServiceService.getDetail(id));
    }

    /**
     * 发布服务
     */
    @PostMapping("/publish")
    public Result<Boolean> publish(@RequestBody CampusService campusService) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) return Result.error("401", "未登录");
        
        campusService.setUserId(userId);
        campusService.setStatus(1); // 招募中/待接单
        campusService.setCurrentCount(0);
        return Result.success(campusServiceService.save(campusService));
    }

    /**
     * 接单/加入服务
     */
    @PostMapping("/accept/{id}")
    public Result<Boolean> accept(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) return Result.error("401", "未登录");
        
        CampusService service = campusServiceService.getById(id);
        if (service == null) {
            return Result.error("服务不存在");
        }
        if (service.getUserId().equals(userId)) {
            return Result.error("不能接自己的单");
        }
        if (service.getStatus() != 1) {
            return Result.error("当前状态不可操作");
        }

        if (service.getType() == 1) { // 跑腿
            service.setAccepterId(userId);
            service.setStatus(2); // 进行中
        } else if (service.getType() == 2) { // 拼单
            if (service.getLimitCount() != null && service.getCurrentCount() >= service.getLimitCount()) {
                return Result.error("人数已满");
            }
            service.setCurrentCount(service.getCurrentCount() + 1);
            if (service.getLimitCount() != null && service.getCurrentCount().equals(service.getLimitCount())) {
                service.setStatus(2); // 人满变进行中
            }
        } else if (service.getType() == 3) { // 资源共享
            service.setAccepterId(userId);
            service.setStatus(2); // 已借出/进行中
        }

        return Result.success(campusServiceService.updateById(service));
    }

    /**
     * 完成服务
     */
    @PostMapping("/complete/{id}")
    public Result<Boolean> complete(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) return Result.error("401", "未登录");
        
        CampusService service = campusServiceService.getById(id);
        if (service == null) {
            return Result.error("服务不存在");
        }
        // 只有发布者或接单人可以点击完成 (取决于具体业务逻辑，通常发布者确认)
        if (!service.getUserId().equals(userId) && (service.getAccepterId() == null || !service.getAccepterId().equals(userId))) {
            return Result.error("无权操作");
        }
        service.setStatus(3); // 已完成
        return Result.success(campusServiceService.updateById(service));
    }

    /**
     * 取消服务
     */
    @PostMapping("/cancel/{id}")
    public Result<Boolean> cancel(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) return Result.error("401", "未登录");
        
        CampusService service = campusServiceService.getById(id);
        if (service == null) {
            return Result.error("服务不存在");
        }
        if (!service.getUserId().equals(userId)) {
            return Result.error("只有发布者可以取消");
        }
        service.setStatus(4); // 已取消
        return Result.success(campusServiceService.updateById(service));
    }
}
