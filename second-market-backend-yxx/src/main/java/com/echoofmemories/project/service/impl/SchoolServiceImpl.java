package com.echoofmemories.project.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.echoofmemories.project.dto.SchoolRequest;
import com.echoofmemories.project.entity.School;
import com.echoofmemories.project.exception.CustomException;
import com.echoofmemories.project.mapper.SchoolMapper;
import com.echoofmemories.project.service.SchoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements SchoolService {

    private final SchoolMapper schoolMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public School addSchool(SchoolRequest request) {
        if (StrUtil.isBlank(request.getNameZh()) || StrUtil.isBlank(request.getNameKo())) {
            throw new CustomException("学校名称不能为空");
        }

        School school = new School();
        school.setSchoolCode(request.getSchoolCode());
        school.setNameZh(request.getNameZh());
        school.setNameKo(request.getNameKo());
        school.setNameEn(request.getNameEn());
        school.setCityZh(request.getCityZh());
        school.setCityKo(request.getCityKo());
        school.setCityEn(request.getCityEn());
        school.setLogoUrl(request.getLogoUrl());
        school.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        school.setDeleted(0);

        this.save(school);
        return school;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSchool(SchoolRequest request) {
        if (request.getId() == null) {
            throw new CustomException("学校ID不能为空");
        }

        School school = this.getById(request.getId());
        if (school == null || school.getDeleted() == 1) {
            throw new CustomException("学校不存在");
        }

        school.setSchoolCode(request.getSchoolCode());
        school.setNameZh(request.getNameZh());
        school.setNameKo(request.getNameKo());
        school.setNameEn(request.getNameEn());
        school.setCityZh(request.getCityZh());
        school.setCityKo(request.getCityKo());
        school.setCityEn(request.getCityEn());
        school.setLogoUrl(request.getLogoUrl());

        if (request.getStatus() != null) {
            school.setStatus(request.getStatus());
        }

        return this.updateById(school);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSchool(Long id) {
        School school = this.getById(id);
        if (school == null || school.getDeleted() == 1) {
            throw new CustomException("学校不存在");
        }
        return this.removeById(id);
    }

    @Override
    public School getSchoolById(Long id) {
        return this.getById(id);
    }

    @Override
    public Page<School> getSchoolPage(Integer pageNum, Integer pageSize, String keyword) {
        Page<School> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<School> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(School::getDeleted, 0);

        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(School::getNameZh, keyword)
                    .or()
                    .like(School::getNameKo, keyword)
                    .or()
                    .like(School::getNameEn, keyword));
        }

        wrapper.orderByDesc(School::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public List<School> getSchoolList(String language) {
        return schoolMapper.selectSchoolOptions(language);
    }
}
