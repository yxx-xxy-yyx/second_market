package com.echoofmemories.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.echoofmemories.project.dto.SchoolRequest;
import com.echoofmemories.project.entity.School;

import java.util.List;

public interface SchoolService extends IService<School> {

    School addSchool(SchoolRequest request);

    boolean updateSchool(SchoolRequest request);

    boolean deleteSchool(Long id);

    School getSchoolById(Long id);

    Page<School> getSchoolPage(Integer pageNum, Integer pageSize, String keyword);

    List<School> getSchoolList(String language);
}