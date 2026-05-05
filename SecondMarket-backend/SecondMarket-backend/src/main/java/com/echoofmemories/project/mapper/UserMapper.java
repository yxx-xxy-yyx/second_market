package com.echoofmemories.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.echoofmemories.project.dto.UserDetailDTO;
import com.echoofmemories.project.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户数据访问层
 * 
 * @author EchoOfMemories
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    Page<UserDetailDTO> selectUserDetailPage(Page<UserDetailDTO> page,
            @Param("schoolId") Long schoolId,
            @Param("keyword") String keyword,
            @Param("username") String username,
            @Param("nickname") String nickname,
            @Param("phone") String phone,
            @Param("status") String status,
            @Param("language") String language);

    User selectByUsername(@Param("username") String username);

    User selectByEmail(@Param("email") String email);
}
