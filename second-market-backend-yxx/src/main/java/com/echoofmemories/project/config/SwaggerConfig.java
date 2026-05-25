package com.echoofmemories.project.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * Knife4j配置类
 * 
 * @author Echo of Memories
 */
@Configuration
@ConditionalOnProperty(prefix = "knife4j", name = "enable", havingValue = "true")
@EnableSwagger2WebMvc
@EnableKnife4j
public class SwaggerConfig {
    
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("Echo of Memories Project API")
                        .description("Echo of Memories 项目后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact("Echo of Memories", "", "echoofmemories（wechat）"))
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.echoofmemories.project.controller"))
                .paths(PathSelectors.any())
                .build();
    }
    
}
