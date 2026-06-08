package com.echoofmemories.project.aspect;

import com.echoofmemories.project.annotation.AuditLog;
import com.echoofmemories.project.entity.AuditLog;
import com.echoofmemories.project.mapper.AuditLogMapper;
import com.echoofmemories.project.security.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;

    @Pointcut("@annotation(com.echoofmemories.project.annotation.AuditLog)")
    public void logPointcut() {
    }

    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        com.echoofmemories.project.entity.AuditLog auditLog = new com.echoofmemories.project.entity.AuditLog();
        
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                auditLog.setIp(getIpAddress(request));
            }

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            AuditLog annotation = signature.getMethod().getAnnotation(AuditLog.class);
            
            auditLog.setOperation(annotation.value());
            auditLog.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
            
            Object[] args = joinPoint.getArgs();
            try {
                auditLog.setParams(objectMapper.writeValueAsString(args));
            } catch (Exception e) {
                auditLog.setParams(Arrays.toString(args));
            }

            Long userId = SecurityUtils.getCurrentUserId();
            if (userId != null) {
                auditLog.setUserId(userId);
            }

            Object result = joinPoint.proceed();
            
            auditLog.setStatus(1);
            auditLog.setCreateTime(LocalDateTime.now());
            
            return result;
        } catch (Exception e) {
            auditLog.setStatus(0);
            auditLog.setErrorMsg(e.getMessage());
            auditLog.setCreateTime(LocalDateTime.now());
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            auditLog.setExecutionTime(executionTime);
            
            try {
                auditLogMapper.insert(auditLog);
            } catch (Exception e) {
                log.error("保存审计日志失败", e);
            }
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
