package com.echoofmemories.project.security.annotation;

import java.lang.annotation.*;

/**
 * 角色权限注解
 * 用于标记需要特定角色才能访问的方法或类
 * 
 * @author Echo of Memories
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /**
     * 需要的角色
     * 
     * @return 角色数组
     */
    String[] value() default {};

    /**
     * 逻辑关系：AND-需要所有角色，OR-需要其中一个角色
     * 
     * @return 逻辑关系
     */
    LogicalOperator logical() default LogicalOperator.OR;

    enum LogicalOperator {
        AND, OR
    }
}
