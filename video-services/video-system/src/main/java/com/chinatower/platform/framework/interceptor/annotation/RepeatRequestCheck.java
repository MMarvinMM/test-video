package com.chinatower.platform.framework.interceptor.annotation;


import java.lang.annotation.*;

/**
 * @author LQ
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatRequestCheck {
    /**
     * 用于校验重复请求的字段名，支持多个字段
     * 如果为空，则默认使用所有参数进行校验
     */
    String[] fields() default {};

    /**
     * 重复请求间隔时间(毫秒)，默认3秒
     */
    long interval() default 3000;

    /**
     * Redis中存储的key的前缀
     */
    String prefix() default "REPEAT_SUBMIT:";
}
