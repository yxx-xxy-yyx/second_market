package com.echoofmemories.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器配置
 * 
 * @author Echo of Memories
 */
@Configuration
public class PasswordConfig {

    /**
     * 密码编码器Bean
     * 从SecurityConfig中移出，避免循环依赖
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
