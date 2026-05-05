package com.echoofmemories.project.security.aspect;

import com.echoofmemories.project.exception.CustomException;
import com.echoofmemories.project.security.SecurityUtils;
import com.echoofmemories.project.security.annotation.RequireRole;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 角色权限检查切面
 * 
 * @author Echo of Memories
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class RoleCheckAspect {

    @Pointcut("@annotation(com.echoofmemories.project.security.annotation.RequireRole) || " +
            "@within(com.echoofmemories.project.security.annotation.RequireRole)")
    public void requireRolePointcut() {
    }

    @Around("requireRolePointcut()")
    public Object checkRole(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        // 首先检查方法级别的注解
        RequireRole methodAnnotation = method.getAnnotation(RequireRole.class);
        RequireRole annotation = methodAnnotation;

        // 如果方法没有注解，检查类级别的注解
        if (annotation == null) {
            Class<?> targetClass = point.getTarget().getClass();
            annotation = targetClass.getAnnotation(RequireRole.class);
        }

        if (annotation != null) {
            String[] requiredRoles = annotation.value();
            RequireRole.LogicalOperator logical = annotation.logical();

            // 检查用户是否已认证
            if (!SecurityUtils.isAuthenticated()) {
                log.warn("权限检查失败：用户未认证 - Method: {}", method.getName());
                throw new CustomException("未登录，请先登录");
            }

            // 检查角色权限
            if (requiredRoles.length > 0) {
                boolean hasPermission = checkRolePermission(requiredRoles, logical);

                if (!hasPermission) {
                    String currentRole = SecurityUtils.getCurrentUserRole();
                    log.warn("权限检查失败：用户角色[{}]不满足要求[{}] - Method: {}",
                            currentRole, Arrays.toString(requiredRoles), method.getName());
                    throw new CustomException("权限不足，无法访问该资源");
                }

                log.debug("权限检查通过：用户角色[{}]满足要求[{}] - Method: {}",
                        SecurityUtils.getCurrentUserRole(), Arrays.toString(requiredRoles), method.getName());
            }
        }

        return point.proceed();
    }

    /**
     * 检查角色权限
     */
    private boolean checkRolePermission(String[] requiredRoles, RequireRole.LogicalOperator logical) {
        if (logical == RequireRole.LogicalOperator.AND) {
            // 需要具有所有角色
            return SecurityUtils.hasAllRoles(requiredRoles);
        } else {
            // 需要具有任意一个角色
            return SecurityUtils.hasAnyRole(requiredRoles);
        }
    }
}
