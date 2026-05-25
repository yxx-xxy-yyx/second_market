package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.entity.CampusService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 校园周边服务 Mapper
 */
@Mapper
public interface CampusServiceMapper extends BaseMapper<CampusService> {
    Page<CampusService> selectCampusServicePage(@Param("page") Page<CampusService> page, 
                                               @Param("type") Integer type, 
                                               @Param("schoolId") Long schoolId);

    CampusService selectCampusServiceDetail(@Param("id") Long id);
}
