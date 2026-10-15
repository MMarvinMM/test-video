package com.chinatower.platform.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限过滤注解
 *
 * @author chinatower
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IndustryAuth
{
    /**
     * 租户字段名称字段
     */
    String tenantColumn() default "t.tenant_id";

    /**
     * 行业版本编码字段
     */
    String appVerCodeColumn() default "t.app_ver_code";
}
