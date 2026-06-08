package com.echoofmemories.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

/**
 * Echo of Memories Project - 主启动类
 * 
 * @author Echo of Memories
 * @version 1.0.0
 * @since 2024
 */
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
@MapperScan("com.echoofmemories.project.mapper")
public class Application {

    public static void main(String[] args) {
        String port = System.getenv("PORT");
        if (port != null && !port.isBlank() && System.getProperty("server.port") == null) {
            System.setProperty("server.port", port.trim());
        }
        if (System.getProperty("server.servlet.context-path") == null) {
            System.setProperty("server.servlet.context-path", "/api");
        }
        SpringApplication.run(Application.class, args);
        System.out.println("Echo of Memories Project Backend Started Successfully!");
    }

}
