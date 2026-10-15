package com.chinatower.platform.annotation;

import java.lang.annotation.*;

/**
 * 数据权限过滤注解
 *
 * @author chinatower
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermissions
{

    /**
     * 租户字段名称
     */
    String tenantColumn() default "";


    /**
     * 数据源
     */
    String dbType() default "";


    /**
     * 行业编码
     * 2022.08.03新增
     */
    String industryCode() default "";

    /**
     * 权限类型字段名称
     */
    String layerColumn() default "";


    /**
     * 受理节点
     * @return
     */
    String acceptColumn() default  "";
}
