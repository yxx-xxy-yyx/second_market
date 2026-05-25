package com.echoofmemories.project.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security配置类
 * 
 * @author Echo of Memories
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
        private final CorsConfigurationSource corsConfigurationSource;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Value("${project.security.permit-all:${SECURITY_PERMIT_ALL:true}}")
        private boolean permitAll;

        /**
         * 安全过滤器链配置
         */
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                // 禁用CSRF，使用JWT不需要CSRF保护
                                .csrf().disable()

                                // CORS配置
                                .cors().configurationSource(corsConfigurationSource)
                                .and()

                                // 配置异常处理
                                .exceptionHandling()
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .and()

                                // 配置会话管理为无状态
                                .sessionManagement()
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                .and();

                if (permitAll) {
                        http.authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
                } else {
                        http.authorizeHttpRequests(authz -> authz
                                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                                        .antMatchers("/health", "/api/health").permitAll()
                                        .antMatchers("/auth/**", "/api/auth/**").permitAll()

                                        .antMatchers(
                                                        "/doc.html",
                                                        "/webjars/**",
                                                        "/v2/api-docs",
                                                        "/v3/api-docs/**",
                                                        "/swagger-resources/**",
                                                        "/configuration/**",
                                                        "/favicon.ico")
                                        .permitAll()

                                        .antMatchers(
                                                        "/uploads/**",
                                                        "/api/uploads/**",
                                                        "/file/download/**",
                                                        "/api/file/download/**")
                                        .permitAll()

                                        .antMatchers("/security-demo/public", "/api/security-demo/public").permitAll()

                                        .antMatchers(
                                                        "/admin/**",
                                                        "/api/admin/**",
                                                        "/role/**",
                                                        "/api/role/**",
                                                        "/user/admin/**",
                                                        "/api/user/admin/**")
                                        .hasRole("ADMIN")

                                        .antMatchers(HttpMethod.GET, "/**").permitAll()
                                        .anyRequest().authenticated());
                }

                http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                // 原权限配置（已注释，生产环境需要恢复）
                // .authorizeHttpRequests(authz -> authz
                // // 公开访问的端点
                // .antMatchers(
                // "/auth/**", // 认证相关接口
                // "/doc.html", // Knife4j文档
                // "/webjars/**", // Knife4j静态资源
                // "/v2/api-docs", // Swagger文档
                // "/v3/api-docs/**", // OpenAPI文档
                // "/swagger-resources/**", // Swagger资源
                // "/configuration/**", // Swagger配置
                // "/favicon.ico", // 图标
                // "/uploads/**", // 静态文件访问
                // "/file/download/**", // 文件下载
                // "/health", // 健康检查
                // "/actuator/**", // 监控端点
                // "/security-demo/public" // 权限演示的公开接口
                // ).permitAll()
                //
                // // 管理员专用接口
                // .antMatchers(
                // "/admin/**", // 管理端接口
                // "/role/**", // 角色管理
                // "/user/admin/**" // 用户管理的管理员功能
                // ).hasRole("ADMIN")
                //
                // // 需要认证的接口
                // .anyRequest().authenticated())
                //
                // 开发模式：注释掉JWT过滤器
                // .addFilterBefore(jwtAuthenticationFilter,
                // UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

}
