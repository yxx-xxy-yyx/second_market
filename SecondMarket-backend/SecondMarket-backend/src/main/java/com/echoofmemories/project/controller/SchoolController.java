package com.echoofmemories.project.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.common.Result;
import com.echoofmemories.project.dto.SchoolRequest;
import com.echoofmemories.project.entity.School;
import com.echoofmemories.project.service.SchoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "学校管理", description = "学校相关接口")
@RestController
@RequestMapping("/school")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @Operation(summary = "新增学校")
    @PostMapping("/add")
    public Result<School> addSchool(@RequestBody SchoolRequest request) {
        try {
            School school = schoolService.addSchool(request);
            return Result.success("新增成功", school);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "更新学校")
    @PutMapping("/update")
    public Result<String> updateSchool(@RequestBody SchoolRequest request) {
        try {
            boolean success = schoolService.updateSchool(request);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "删除学校")
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteSchool(@PathVariable Long id) {
        try {
            boolean success = schoolService.deleteSchool(id);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "学校分页列表")
    @GetMapping("/page")
    public Result<Page<School>> getSchoolPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                              @RequestParam(required = false) String keyword) {
        try {
            Page<School> page = schoolService.getSchoolPage(pageNum, pageSize, keyword);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "获取学校详情")
    @GetMapping("/{id}")
    public Result<School> getSchool(@PathVariable Long id) {
        try {
            School school = schoolService.getSchoolById(id);
            return school != null ? Result.success(school) : Result.error("404", "学校不存在");
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "学校下拉框列表")
    @GetMapping("/list")
    public Result<List<School>> getSchoolList(@RequestParam(defaultValue = "zh") String language) {
        try {
            List<School> list = schoolService.getSchoolList(language);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("500", e.getMessage());
        }
    }
}