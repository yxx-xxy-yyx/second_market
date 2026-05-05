package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.echoofmemories.project.entity.School;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SchoolMapper extends BaseMapper<School> {
    List<School> selectSchoolOptions(@Param("language") String language);
}
